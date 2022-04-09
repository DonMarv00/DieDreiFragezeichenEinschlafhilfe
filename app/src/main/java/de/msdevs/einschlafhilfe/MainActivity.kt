package de.msdevs.einschlafhilfe

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import de.msdevs.einschlafhilfe.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var urlExtraParameter = "folgen.json"
    private var episodeNumber: Int = 0
    private val episodeList = ArrayList<JsonResponse>()
    /*
        Copyright 2022 by Marvin Stelter
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                when (binding.bottomBarViewFlipper.displayedChild) {
                    0 -> episodeNumber = (1..50).random()
                    1 -> episodeNumber = (1..100).random()
                    2 -> episodeNumber = (1..150).random()
                    3 -> episodeNumber = (1..215).random()
                }
                apiCall()
            }
        }

        binding.btnLeft.setOnClickListener {
            binding.bottomBarViewFlipper.setOutAnimation(this, R.anim.anim_flipper_item_in_right)
            binding.bottomBarViewFlipper.setInAnimation(this, R.anim.anim_flipper_item_out_left)
            binding.bottomBarViewFlipper.showPrevious()
        }
        binding.btnRight.setOnClickListener {
            binding.bottomBarViewFlipper.setInAnimation(this, R.anim.anim_flipper_item_in_right)
            binding.bottomBarViewFlipper.setOutAnimation(this, R.anim.anim_flipper_item_out_left)
            binding.bottomBarViewFlipper.showNext()
        }
        binding.btnSpotify.setOnClickListener {
            try {
                var id = episodeList[episodeNumber].spotify
                when {
                    id.contains("https://") -> {
                        val separated: Array<String> =
                            id.split(getString(R.string.spotify_base_url).toRegex()).toTypedArray()
                        val pathOneRemoved = separated[1]
                        val separatedLastPath =
                            pathOneRemoved.split("\\?si=".toRegex()).toTypedArray()
                        id = separatedLastPath[0]
                    }
                }
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("spotify:album:$id")
                intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://$packageName"))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Snackbar.make(
                    binding.relativeLayout,
                    getString(R.string.not_installed),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        binding.fabRefresh.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    when (binding.bottomBarViewFlipper.displayedChild) {
                        0 -> episodeNumber = (1..50).random()
                        1 -> episodeNumber = (1..100).random()
                        2 -> episodeNumber = (1..150).random()
                        3 -> episodeNumber = (1..215).random()
                    }
                    apiCall()
                }
            }
        }
        binding.fabDescription.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle(
                getString(
                    R.string.output,
                    (episodeNumber + 1).toString(),
                    episodeList[episodeNumber].name
                )
            )
            alert.setMessage(episodeList[episodeNumber].beschreibung)
            alert.setNeutralButton(getString(R.string.close)) { dlg: DialogInterface, _: Int -> dlg.dismiss() }
            alert.show()
        }
        binding.fabLinks.setOnClickListener {
            val neuvertonungList = listOf("11", "1", "10", "8", "22", "18", "5", "24", "12", "14", "19", "3", "28", "73", "74", "76", "77", "78", "86", "90", "92", "95", "97", "100", "101", "103", "109", "107","121", "122", "123", "124", "125", "126", "127", "128", "128", "129", "130", "131", "135", "140")
            val liste: Array<String> = when (binding.bottomBarViewFlipper.displayedChild) {
                4 -> {
                    arrayOf(getString(R.string.informationen))
                }
                else -> {
                    var neuvertonung = 0
                    for (i in neuvertonungList.indices) {
                        if (episodeNumber.toString() == neuvertonungList[i]) {
                            neuvertonung++
                        }
                    }
                    when {
                        neuvertonung != 0 -> arrayOf(
                            getString(R.string.informationen),
                            getString(R.string.neuvertonung)
                        )
                        else -> arrayOf(getString(R.string.informationen))
                    }
                }
            }


            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Links:")
            builder.setItems(liste) { _: DialogInterface?, which: Int ->
                var i = Intent(Intent.ACTION_VIEW)
                when (which) {
                    0 -> {
                        i.data = Uri.parse(getRockyBeachLink((episodeNumber + 1).toString()))
                        startActivity(i)
                    }
                    1 -> {
                        i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse("http://fragezeichen.neuvertonung.de/")
                        startActivity(i)
                    }
                }
            }

            val dialog = builder.create()
            dialog.show()

        }
    }

    private suspend fun apiCall() {
        urlExtraParameter =
            if (binding.bottomBarViewFlipper.displayedChild == 4) "folgen_diedrei.json" else "folgen.json"
        try {
            when {
                episodeList.isEmpty() -> {
                    val client = OkHttpClient.Builder().build()
                    val request =
                        Request.Builder().url(getString(R.string.base_url) + urlExtraParameter)
                            .build()
                    val result = client.newCall(request).await().body()?.string()
                    val jsonObject = JSONObject(result.toString())
                    val jsonArray = jsonObject.optJSONArray("user")
                    if (jsonArray != null) {
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            episodeList.add(
                                JsonResponse(
                                    name = jsonObject.optString("name"),
                                    beschreibung = jsonObject.optString("beschreibung"),
                                    spotify = jsonObject.optString("spotify")
                                )
                            )
                        }
                    }
                }
            }
            runOnUiThread {
                binding.tvDetails.text = getString(
                    R.string.output,
                    (episodeNumber + 1).toString(),
                    episodeList[episodeNumber].name
                )
                try {
                    when {
                        binding.bottomBarViewFlipper.displayedChild == 4 -> Glide.with(this)
                            .load(getString(R.string.cover_citroncode_dd_url) + (episodeNumber + 1) + ".jpg")
                            .into(binding.ivCover)
                        episodeNumber > 200 -> Glide.with(this)
                            .load(getString(R.string.external_cover_url) + (episodeNumber + 1) + ".jpg")
                            .into(binding.ivCover)
                        else -> Glide.with(this)
                            .load(getString(R.string.cover_citroncode_url) + (episodeNumber + 1) + ".jpg")
                            .into(binding.ivCover)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Snackbar.make(
                        binding.relativeLayout,
                        getString(R.string.cover_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: IOException) {
            Toast.makeText(this,getString(R.string.fehler_glide),Toast.LENGTH_SHORT).show()
        }

    }

    private fun getRockyBeachLink(nummer: String): String? {
        var url = ""
        when (nummer.length) {
            3 -> url = "https://www.rocky-beach.com/hoerspiel/folgen/$nummer.html"
            2 -> url = "https://www.rocky-beach.com/hoerspiel/folgen/0$nummer.html"
            1 -> url = "https://www.rocky-beach.com/hoerspiel/folgen/00$nummer.html"
        }
        when (binding.bottomBarViewFlipper.displayedChild) {
            4 -> {
                url = "https://www.rocky-beach.com/hoerspiel/folgen/50$nummer.html"
            }
        }
        return url
    }

    class JsonResponse(
        val name: String,
        val beschreibung: String,
        val spotify: String
    )

}

