package de.msdevs.einschlafhilfe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.msdevs.einschlafhilfe.databinding.ActivityAboutBinding
import net.cachapa.expandablelayout.ExpandableLayout


class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding
    private lateinit var btnPrivacy : Button
    private lateinit var btnSource : Button
    private lateinit var btnLicenses : Button
    private lateinit var expandableLayout : ExpandableLayout

    //TODO: RecyclerView für die Libraries nutzen -> Neue Libraries können besser hinzugefügt werden
    private lateinit var btnOkHttp : Button
    private lateinit var btnGlide : Button
    private lateinit var btnCoOkHttp : Button
    private lateinit var btnExpandableLayout : Button
    private lateinit var btnAppIntro : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnPrivacy = binding.btnPrivacy
        btnSource = binding.btnQuellcode
        btnLicenses = binding.btnLicense
        expandableLayout = binding.expandableLayout

        //TODO: RecyclerView für die Libraries nutzen -> Neue Libraries können besser hinzugefügt werden
        btnCoOkHttp = binding.included.btnCoroutinesOkhttp
        btnExpandableLayout = binding.included.btnExLayout
        btnGlide = binding.included.btnGlide
        btnAppIntro = binding.included.btnAppIntro
        btnOkHttp = binding.included.btnOkhttp

        btnPrivacy.setOnClickListener{
            opennUrl("https://ddf-einschlafhilfe.de/files/app_privacy.php")
        }
        btnSource.setOnClickListener{
            opennUrl("https://github.com/MarvinStelter/DieDreiFragezeichenEinschlafhilfe")
        }
        btnLicenses.setOnClickListener{
           if(expandableLayout.isExpanded){
               expandableLayout.collapse()
           }else{
               expandableLayout.expand()
           }
        }
        //TODO: RecyclerView für die Libraries nutzen -> Neue Libraries können besser hinzugefügt werden
        btnOkHttp.setOnClickListener{
            opennUrl("https://github.com/square/okhttp")
        }
        btnCoOkHttp.setOnClickListener{
            opennUrl("https://github.com/gildor/kotlin-coroutines-okhttp")
        }
        btnGlide.setOnClickListener{
            opennUrl("https://github.com/bumptech/glide")
        }
        btnExpandableLayout.setOnClickListener{
            opennUrl("https://github.com/cachapa/ExpandableLayout")
        }
        btnAppIntro.setOnClickListener{
            opennUrl("https://github.com/AppIntro/AppIntro")
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
