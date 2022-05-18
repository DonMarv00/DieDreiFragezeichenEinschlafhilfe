package de.msdevs.einschlafhilfe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.aboutlibraries.LibsBuilder

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.placeholder_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val libsBuilder = LibsBuilder()
        with(libsBuilder) {
            showLicense = true
            showLicenseDialog = true
            aboutDescription = getString(R.string.about_long)
            aboutAppName = getString(R.string.app_name)
            aboutShowIcon = true
            aboutShowVersion = true
            aboutShowVersionCode = true
        }
        supportFragmentManager.beginTransaction().replace(R.id.placeholder, libsBuilder.supportFragment()).commit()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}