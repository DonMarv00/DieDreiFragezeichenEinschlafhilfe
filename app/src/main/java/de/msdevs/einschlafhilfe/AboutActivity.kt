package de.msdevs.einschlafhilfe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.msdevs.einschlafhilfe.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding
    private lateinit var btnPrivacy : Button
    private lateinit var btnSource : Button
    private lateinit var btnLicenses : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnPrivacy = binding.btnPrivacy
        btnSource = binding.btnQuellcode
        btnLicenses = binding.btnLicense

        btnPrivacy.setOnClickListener{
            opennUrl("https://ddf-einschlafhilfe.de/files/app_privacy.php")
        }
        btnSource.setOnClickListener{
         opennUrl("https://github.com/MarvinStelter/DieDreiFragezeichenEinschlafhilfe")
        }
        btnLicenses.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle(getString(R.string.third_party_libraries))
            val animals = arrayOf("AppIntro", "Glide", "OkHttp", "kotlin-coroutines-okhttp")
            builder.setItems(animals) { dialog, which ->
                when (which) {
                    0 -> { opennUrl("https://github.com/AppIntro/AppIntro") }
                    1 -> { opennUrl("https://github.com/bumptech/glide") }
                    2 -> { opennUrl("https://github.com/square/okhttp") }
                    3 -> { opennUrl("https://github.com/gildor/kotlin-coroutines-okhttp") }
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    fun opennUrl(url : String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}