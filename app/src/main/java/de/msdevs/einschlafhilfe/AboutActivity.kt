package de.msdevs.einschlafhilfe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import de.msdevs.einschlafhilfe.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity() {

    private lateinit var binding : ActivityAboutBinding
    private lateinit var btnPrivacy : Button
    private lateinit var btnSource : Button
    private lateinit var btnLicenses : Button
    private lateinit var btnContact : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnPrivacy = binding.btnPrivacy
        btnSource = binding.btnQuellcode
        btnLicenses = binding.btnLicense
        btnContact = binding.btnContact


        btnPrivacy.setOnClickListener{
            openUrl("https://ddf-einschlafhilfe.de/files/app_privacy.php")
        }
        btnSource.setOnClickListener{
            openUrl("https://github.com/MarvinStelter/DieDreiFragezeichenEinschlafhilfe")
        }
        btnLicenses.setOnClickListener{
            startActivity(Intent(this@AboutActivity, AboutLibrariesActivity::class.java))

        }
        btnContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:"))
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("stelter.developer@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Kontaktanfrage: DDF Folgenauswahl")
            intent.putExtra(Intent.EXTRA_TEXT, "Deine Nachricht hier...")
            startActivity(intent)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun openUrl(url : String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
