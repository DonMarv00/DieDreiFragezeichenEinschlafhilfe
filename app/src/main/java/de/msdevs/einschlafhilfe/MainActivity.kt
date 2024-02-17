package de.msdevs.einschlafhilfe



import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import de.msdevs.einschlafhilfe.databinding.ActivityMainBinding
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import de.msdevs.einschlafhilfe.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await
import java.io.BufferedReader
import java.io.IOException


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var urlExtraParameter = "folgen.json"
    private var episodeNumber: Int = 0
    private val episodeList = ArrayList<JsonResponse>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var folgenListe : String
    private var selectedTheme : Int = 0
    private lateinit var networkUtils: NetworkUtils
    private var random : Int = 0

    /*
       Copyright 2017 - 2024 by Marvin Stelter
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences(packageName,0)
        sharedPreferencesEditor = sharedPreferences.edit()
        networkUtils = NetworkUtils()

        if(sharedPreferences.getInt("first",0) == 0){
            sharedPreferencesEditor.putBoolean("update_list", true)
            sharedPreferencesEditor.putBoolean("spotify", false)
            sharedPreferencesEditor.apply()
            startActivity(Intent(this@MainActivity, AppIntroActivity::class.java))
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                when (binding.bottomBarViewFlipper.displayedChild) {
                    0 -> episodeNumber = (1..50).random()
                    1 -> episodeNumber = (1..100).random()
                    2 -> episodeNumber = (1..150).random()
                    3 -> episodeNumber = (1..216).random()
                    4 -> episodeNumber = (1..7).random()
                    5 -> episodeNumber = 0
                    6 -> episodeNumber = (1..87).random()
                }
                apiCall()
            }
        }

        binding.btnLeft.setOnClickListener {
            binding.bottomBarViewFlipper.setInAnimation(this, R.anim.anim_flipper_item_in_right)
            binding.bottomBarViewFlipper.setOutAnimation(this, R.anim.anim_flipper_item_out_left)
            binding.bottomBarViewFlipper.showPrevious()
            refresh()
        }
        binding.btnRight.setOnClickListener {
            binding.bottomBarViewFlipper.setInAnimation(this, R.anim.anim_flipper_item_in_left) //right
            binding.bottomBarViewFlipper.setOutAnimation(this, R.anim.anim_flipper_item_out_right)
            binding.bottomBarViewFlipper.showNext()
            refresh()
        }
        if(!isSpotifyInstalled()){
            binding.btnSpotify.visibility = View.GONE
        }
        if(!sharedPreferences.getBoolean("spotify",false)){
            binding.btnSpotify.visibility = View.GONE
        }
        binding.btnSpotify.setOnClickListener {
            try {
                var id = episodeList[episodeNumber].spotify
                when {
                    id.contains("https://") -> {
                        val separated: Array<String> = id.split(getString(R.string.spotify_base_url).toRegex()).toTypedArray()
                        val pathOneRemoved = separated[1]
                        val separatedLastPath = pathOneRemoved.split("\\?si=".toRegex()).toTypedArray()
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
           refresh()
        }
        binding.fabDescription.setOnClickListener {
           var alert : MaterialAlertDialogBuilder
           if(Utility.getTheme(this) == 4){
               alert = MaterialAlertDialogBuilder(this, R.style.DialogThemeWhite)
           }else{
               alert = MaterialAlertDialogBuilder(this, R.style.DialogTheme)
           }

            alert.setTitle(getString(R.string.output, (episodeNumber + 1).toString(), episodeList[episodeNumber].name)
            )
            alert.setMessage(episodeList[episodeNumber].beschreibung)
            alert.setNegativeButton(getString(R.string.close)) { dlg: DialogInterface, _: Int -> dlg.dismiss() }
            alert.show()
        }
        binding.fabLinks.setOnClickListener {
            val neuvertonungList = listOf("11", "1", "10", "8", "22", "18", "5", "24", "12", "14", "19", "3", "28", "73", "74", "76", "77", "78", "86", "90", "92", "95", "97", "100", "101", "103", "109", "107","121", "122", "123", "124", "125", "126", "127", "128", "128", "129", "130", "131", "135", "140")
            val liste: Array<String> = when (binding.bottomBarViewFlipper.displayedChild) {
                4 -> {
                    arrayOf(getString(R.string.informationen))
                }else -> {
                    var neuvertonung = 0
                    for (i in neuvertonungList.indices) {
                        if (episodeNumber.toString() == neuvertonungList[i]) {
                            neuvertonung++
                        }
                    }
                    if(binding.bottomBarViewFlipper.displayedChild == 5 && random == 3){
                            arrayOf(getString(R.string.informationen))
                    }else{
                        if(neuvertonung != 0){
                            arrayOf(
                                    getString(R.string.informationen),
                                    getString(R.string.neuvertonung))
                        }else{
                            arrayOf(getString(R.string.informationen))
                        }
                    }
                }
            }

            val builder : MaterialAlertDialogBuilder
            if(Utility.getTheme(this) == 4){
                builder = MaterialAlertDialogBuilder(this, R.style.DialogThemeWhite)
            }else{
                builder = MaterialAlertDialogBuilder(this, R.style.DialogTheme)
            }

            builder.setTitle("Links:")
            builder.setItems(liste) { _: DialogInterface?, which: Int ->
                var i = Intent(Intent.ACTION_VIEW)
                when (which) {
                    0 -> {
                        i.data = Uri.parse(getRockyBeachLink((episodeNumber + 1).toString(), random))
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
        random = (1..4).random()
        runOnUiThread {
            if(binding.bottomBarViewFlipper.displayedChild == 4){
                binding.btnSpotify.visibility = View.GONE
            }else{
                if(random != 3){
                    binding.btnSpotify.visibility = View.VISIBLE
                }
            }
        }
        episodeList.clear()

            if(binding.bottomBarViewFlipper.displayedChild == 4){
                urlExtraParameter = "folgen_diedrei.json"
            }else if(binding.bottomBarViewFlipper.displayedChild == 6){
                urlExtraParameter = "folgen_kids.json"
            }else if(binding.bottomBarViewFlipper.displayedChild != 5){
                urlExtraParameter = "folgen.json"
            }else{
                val randomDDF = (sharedPreferences.getInt("min",0)..sharedPreferences.getInt("max",0)).random()
                val randomKids = (sharedPreferences.getInt("minK",0)..sharedPreferences.getInt("maxK",0)).random()

                if(random == 1){
                    urlExtraParameter = "folgen.json"
                    episodeNumber = randomDDF - 1
                }
                if(random == 2){
                    urlExtraParameter = "sonderfolgen_ddf.json"
                    episodeNumber = (1..19).random() - 1
                }
                if(random == 3){
                    urlExtraParameter = "folgen_diedrei.json"
                    episodeNumber = (1..7).random() - 1
                }
                if(random == 4){
                    urlExtraParameter = "folgen_kids.json"
                    episodeNumber = randomKids - 1
                }
            }
            try {
                when {
                    episodeList.isEmpty() -> {

                        if(sharedPreferences.getBoolean("update_list",false) && networkUtils.isConnected(this)){
                            val client = OkHttpClient.Builder().build()
                            val request =
                                Request.Builder().url(getString(R.string.base_url) + urlExtraParameter)
                                    .build()
                             folgenListe = client.newCall(request).await().body()?.string().toString()
                        }else {
                                if (binding.bottomBarViewFlipper.displayedChild == 4) {
                                    folgenListe = assets.open("offline_list_dd.txt").bufferedReader().use(BufferedReader::readText)
                                } else if(binding.bottomBarViewFlipper.displayedChild == 6){
                                    folgenListe = assets.open("offline_list_kids.txt").bufferedReader().use(BufferedReader::readText)
                                } else if(binding.bottomBarViewFlipper.displayedChild != 5){
                                    folgenListe = assets.open("offline_list.txt").bufferedReader().use(BufferedReader::readText)
                                }else{
                                    if(random == 1){
                                        folgenListe = assets.open("offline_list.txt").bufferedReader().use(BufferedReader::readText)
                                    }
                                    if(random == 2){
                                        folgenListe = assets.open("offline_list_sonderfolgen_ddf.txt").bufferedReader().use(BufferedReader::readText)
                                    }
                                    if(random == 3){
                                        folgenListe = assets.open("offline_list_dd.txt").bufferedReader().use(BufferedReader::readText)
                                    }
                                    if(random == 4){
                                        folgenListe = assets.open("offline_list_kids.txt").bufferedReader().use(BufferedReader::readText)
                                    }
                                }
                            }
                        val jsonObject = JSONObject(folgenListe)
                        val jsonArray = jsonObject.optJSONArray("folgen")
                        if (jsonArray != null) {
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                episodeList.add(JsonResponse(name = jsonObject.optString("name"), beschreibung = jsonObject.optString("beschreibung"), spotify = jsonObject.optString("spotify"), nummer = jsonObject.optInt("nummer"), type = ""))
                            }
                        }
                    }
                }

                runOnUiThread {
                    try {
                        if(binding.bottomBarViewFlipper.displayedChild != 5){
                            binding.tvDetails.text = getString(R.string.output, (episodeNumber + 1).toString(), episodeList[episodeNumber].name)

                        }else{
                            if(random == 1){
                                binding.tvDetails.text = getString(R.string.output, (episodeNumber + 1).toString(), episodeList[episodeNumber].name)
                                binding.btnSpotify.visibility = View.VISIBLE
                            }
                            if(random == 2){
                                binding.tvDetails.text = episodeList[episodeNumber].name
                                binding.btnSpotify.visibility = View.VISIBLE
                            }
                            if(random == 3){
                                binding.tvDetails.text = getString(R.string.output, (episodeNumber + 1).toString(), episodeList[episodeNumber].name)
                                binding.btnSpotify.visibility = View.GONE
                            }
                            if(random == 4){
                                binding.tvDetails.text = getString(R.string.output, (episodeNumber + 1).toString(), episodeList[episodeNumber].name)
                            }
                        }
                        when (binding.bottomBarViewFlipper.displayedChild) {
                            4 ->
                                loadEpisodeCover(getString(R.string.cover_citroncode_dd_url) + (episodeNumber + 1) + ".png")
                            else ->
                                if(binding.bottomBarViewFlipper.displayedChild == 6) {
                                    loadEpisodeCover(getString(R.string.cover_citroncode_url) + "k" + (episodeNumber + 1) + ".png")
                                }else if(binding.bottomBarViewFlipper.displayedChild != 5){
                                  loadEpisodeCover(getString(R.string.cover_citroncode_url) + (episodeNumber + 1) + ".png")
                                }else{
                                    if(random == 1){
                                        loadEpisodeCover(getString(R.string.cover_citroncode_url) + (episodeNumber + 1) + ".png")
                                        binding.fabLinks.show()
                                    }
                                    if(random == 2){
                                        loadEpisodeCover(getString(R.string.cover_citroncode_url) + "x" + (episodeNumber + 1) + ".png")
                                        binding.fabLinks.hide()
                                    }
                                    if(random == 3){
                                        loadEpisodeCover(getString(R.string.cover_citroncode_dd_url) + (episodeNumber + 1) + ".png")
                                        binding.fabLinks.show()
                                    }
                                    if(random == 4){
                                        loadEpisodeCover(getString(R.string.cover_citroncode_url) + "k" + (episodeNumber + 1) + ".png")
                                        binding.fabLinks.hide()
                                    }
                                }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                       refresh()
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(this,getString(R.string.fehler_glide),Toast.LENGTH_SHORT).show()
            }
    }
    private fun getRockyBeachLink(nummer: String, random : Int): String {
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
        if(random == 3 && binding.bottomBarViewFlipper.displayedChild == 5){
            url = "https://www.rocky-beach.com/hoerspiel/folgen/50$nummer.html"
        }
        return url
    }
    private fun loadEpisodeCover(coverUrl : String){
       if(networkUtils.isConnected(this) && sharedPreferences.getBoolean("update_list",false)){
           Glide.with(this)
               .load(coverUrl)
               .diskCacheStrategy(DiskCacheStrategy.ALL)
               .into(binding.ivCover)
       }
    }
    private fun isSpotifyInstalled() : Boolean{
        val packageManager: PackageManager = packageManager
        val intent = Intent(Intent.ACTION_VIEW)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                packageManager.getPackageInfo("com.spotify.music", PackageManager.GET_ACTIVITIES)
                return true
            } catch (ignore: PackageManager.NameNotFoundException) {
            }
        }
        return false
    }
    private fun refresh(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                when (binding.bottomBarViewFlipper.displayedChild) {
                    0 -> episodeNumber = (1..50).random()
                    1 -> episodeNumber = (1..100).random()
                    2 -> episodeNumber = (1..150).random()
                    3 -> episodeNumber = (1..222).random()
                    4 -> episodeNumber = (1..7).random()
                    5 -> episodeNumber = 0
                    6 -> episodeNumber = (1..87).random()
                }
                apiCall()
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                true
            }
            R.id.action_about ->{
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        sharedPreferences = getSharedPreferences(packageName,0)
        sharedPreferencesEditor = sharedPreferences.edit()

        if (sharedPreferences.getInt("theme_changed",0) == 1) {
            val intent = intent
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)

            sharedPreferencesEditor.putInt("theme_changed",0)
            sharedPreferencesEditor.apply()
        }
    }

}

