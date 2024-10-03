package de.msdevs.einschlafhilfe

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import de.msdevs.einschlafhilfe.databinding.ActivityAboutBinding
import de.msdevs.einschlafhilfe.utils.Utility

class AboutActivity : BaseActivity(false) {

    private lateinit var binding : ActivityAboutBinding
    private lateinit var btnPrivacy : Button
    private lateinit var btnSource : Button
    private lateinit var btnLicenses : Button
    private lateinit var btnContact : Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        btnPrivacy = binding.btnPrivacy
        btnSource = binding.btnQuellcode
        btnLicenses = binding.btnLicense
        btnContact = binding.btnContact
        toolbarDesign()


        btnPrivacy.setOnClickListener{
            openUrl("https://privacy.citroncode.com/details.php?app_id=a0417")
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
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("contact@citroncode.com"))
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
    private fun toolbarDesign() {
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val nav = toolbar.navigationIcon
        if (Utility.getTheme(applicationContext) <= 2) {
            toolbar.setTitleTextColor(Color.WHITE)
            nav?.setTint(Color.WHITE)
        } else if (Utility.getTheme(applicationContext) == 3) {
            toolbar.setTitleTextColor(Color.WHITE)
            nav?.setTint(Color.WHITE)
        } else if (Utility.getTheme(applicationContext) == 4) {
            toolbar.setTitleTextColor(Color.BLACK)
            nav?.setTint(Color.BLACK)
        }
    }
}
