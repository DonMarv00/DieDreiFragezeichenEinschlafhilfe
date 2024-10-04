package de.msdevs.einschlafhilfe

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.msdevs.einschlafhilfe.database.DatabaseHelper
import de.msdevs.einschlafhilfe.databinding.ActivityMainBinding
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import de.msdevs.einschlafhilfe.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException


class MainActivity : BaseActivity(false) {

    private lateinit var binding: ActivityMainBinding
    private var episodeNumberExternal: Int = 0

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var networkUtils: NetworkUtils
    private var random : Int = 0
    private lateinit var folgen_database: SQLiteDatabase
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var toolbar : Toolbar
    private var alerterColor : Int = 0
    private var isWhite = false
    private val episodeListDDF = ArrayList<JsonResponse>()
    private val episodeListDD = ArrayList<JsonResponse>()
    private val episodeListKids = ArrayList<JsonResponse>()
    private val episodeListSonderfolgen = ArrayList<JsonResponse>()
    private val episodeListHoerbuecher = ArrayList<JsonResponse>()
    private lateinit var selectedEpisodeDescription : String
    private lateinit var selectedEpisodeSpotify : String
    private lateinit var selectedEpisodeName : String
    private var isFirstStart : Int = 0
    private var hasLoaded : Boolean = false
    /*
       Copyright 2017 - 2024 by Marvin Stelter
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        toolbarDesign()
        iniApp()

        binding.btnLeft.setOnClickListener {
            binding.bottomBarViewFlipper.setInAnimation(this, R.anim.anim_flipper_item_in_right)
            binding.bottomBarViewFlipper.setOutAnimation(this, R.anim.anim_flipper_item_out_left)
            binding.bottomBarViewFlipper.showPrevious()
            refresh()
            saveViewFlipperPostion()
        }
        binding.btnRight.setOnClickListener {
            binding.bottomBarViewFlipper.setInAnimation(this, R.anim.anim_flipper_item_in_left) //right
            binding.bottomBarViewFlipper.setOutAnimation(this, R.anim.anim_flipper_item_out_right)
            binding.bottomBarViewFlipper.showNext()
            refresh()
            saveViewFlipperPostion()
        }
        if(!isSpotifyInstalled()){
            binding.btnSpotify.visibility = View.GONE
        }
        if(!sharedPreferences.getBoolean("spotify",false)){
            binding.btnSpotify.visibility = View.GONE
        }
        binding.btnSpotify.setOnClickListener {
            try {
                var id = selectedEpisodeSpotify
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
                Utility.displayAlerter(getString(R.string.not_installed),alerterColor,isWhite,this@MainActivity)
            }
        }
        binding.fabRefresh.setOnClickListener {
           refresh()
        }
        binding.fabDescription.setOnClickListener {
            try {

                val alert : MaterialAlertDialogBuilder = if (Utility.getTheme(applicationContext) <= 2) {
                    MaterialAlertDialogBuilder(this, R.style.DialogThemeRed)
                } else if (Utility.getTheme(applicationContext) == 3) {
                    MaterialAlertDialogBuilder(this, R.style.DialogThemeBlue)
                } else if (Utility.getTheme(applicationContext) == 4) {
                    MaterialAlertDialogBuilder(this, R.style.DialogThemeWhite)
                }else{
                    MaterialAlertDialogBuilder(this, R.style.DialogThemeRed)
                }

                alert.setTitle(getString(R.string.output, (episodeNumberExternal + 1).toString(), selectedEpisodeName))
                alert.setMessage(selectedEpisodeDescription)
                alert.setNegativeButton(getString(R.string.close)) { dlg: DialogInterface, _: Int -> dlg.dismiss() }
                alert.show()
            }catch (e : Exception){
                Utility.displayAlerter(getString(R.string.folge_laedt_noch),alerterColor,isWhite,this@MainActivity)
            }
        }
        binding.fabLinks.setOnClickListener {
            val neuvertonungList = listOf("11", "1", "10", "8",
                "22", "18", "5", "24", "12", "14", "19",
                "3", "28", "73", "74", "76", "77", "78",
                "86", "90", "92", "95", "97", "100", "101",
                "103", "109", "107","121", "122", "123", "124",
                "125", "126", "127", "128", "128", "129", "130", "131", "135", "140")

            val liste: Array<String> = when (binding.bottomBarViewFlipper.displayedChild) {
                4 -> {
                    arrayOf(getString(R.string.informationen))
                }else -> {
                    var neuvertonung = 0
                    for (i in neuvertonungList.indices) {
                        if (episodeNumberExternal.toString() == neuvertonungList[i]) {
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

            val builder : MaterialAlertDialogBuilder = if (Utility.getTheme(applicationContext) <= 2) {
                MaterialAlertDialogBuilder(this, R.style.DialogThemeRed)
            } else if (Utility.getTheme(applicationContext) == 3) {
                MaterialAlertDialogBuilder(this, R.style.DialogThemeBlue)
            } else if (Utility.getTheme(applicationContext) == 4) {
                MaterialAlertDialogBuilder(this, R.style.DialogThemeWhite)
            }else{
                MaterialAlertDialogBuilder(this, R.style.DialogThemeRed)
            }

            builder.setTitle("Links:")
            builder.setItems(liste) { _: DialogInterface?, which: Int ->
                var i = Intent(Intent.ACTION_VIEW)
                when (which) {
                    0 -> {
                        i.data = Uri.parse(getRockyBeachLink((episodeNumberExternal + 1).toString(), random))
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
    private fun toolbarDesign() {
        if (Utility.getTheme(applicationContext) <= 2) {
            toolbar.setTitleTextColor(Color.WHITE)
            alerterColor = Color.parseColor("#d50000")
            isWhite = false
        } else if (Utility.getTheme(applicationContext) == 3) {
            toolbar.setTitleTextColor(Color.WHITE)
            alerterColor = Color.parseColor("#0048FF")
            isWhite = false
        } else if (Utility.getTheme(applicationContext) == 4) {
            toolbar.setTitleTextColor(Color.BLACK)
            alerterColor = Color.BLACK
            isWhite = true
        }
    }
    private fun iniApp(){
        sharedPreferences = getSharedPreferences(packageName,0)
        sharedPreferencesEditor = sharedPreferences.edit()
        networkUtils = NetworkUtils()
        restoreViewFlipperPostion()

        folgen_database = openOrCreateDatabase("app_list",MODE_PRIVATE,null)
        databaseHelper = DatabaseHelper(this)
        databaseHelper.createTables(folgen_database)

        isFirstStart = sharedPreferences.getInt("first",0)
        if(isFirstStart == 0){
            sharedPreferencesEditor.putBoolean("update_list", true)
            sharedPreferencesEditor.putBoolean("spotify", false)
            sharedPreferencesEditor.apply()
            startActivity(Intent(this@MainActivity, AppIntroActivity::class.java))
        }else{
            initializeEpisodeLists()
        }
    }
    private suspend fun apiCall() {
        try {
            val random = (1..5).random()
            runOnUiThread {
                if (binding.bottomBarViewFlipper.displayedChild == 4) {
                    binding.btnSpotify.visibility = View.GONE
                } else {
                    if (random != 3 && sharedPreferences.getBoolean("spotify", false)) {
                        binding.btnSpotify.visibility = View.VISIBLE
                    }
                }
            }

            val (episodeList, episodeNumber) = when (binding.bottomBarViewFlipper.displayedChild) {
                0-> episodeListDDF to (1..50).random() - 1
                1-> episodeListDDF to (1..100).random() - 1
                2 -> episodeListDDF to (1..150).random() - 1
                3 -> episodeListDDF to (1..episodeListDDF.size).random() - 1
                4 -> episodeListDD to (1..8).random() - 1
                6 -> episodeListKids to (1..episodeListKids.size).random() - 1
                7 -> episodeListHoerbuecher to (1..episodeListHoerbuecher.size).random() - 1
                5 -> when (random) {
                    1 -> episodeListDDF to (sharedPreferences.getInt("min", 1)..sharedPreferences.getInt("max", episodeListDDF.size)).random() - 1
                    2 -> episodeListSonderfolgen to (1..episodeListSonderfolgen.size).random() - 1
                    3 -> episodeListDD to  (sharedPreferences.getInt("minDr3i", 1)..sharedPreferences.getInt("maxDr3i", episodeListDDF.size)).random() - 1
                    4 -> episodeListKids to (sharedPreferences.getInt("minK", 1)..sharedPreferences.getInt("maxK", episodeListKids.size)).random() - 1
                    5 -> episodeListHoerbuecher to (1..episodeListHoerbuecher.size).random() - 1
                    else -> episodeListDDF to 0
                } else -> episodeListDDF to (1..episodeListDDF.size).random() - 1 //Error
            }

            episodeNumberExternal = episodeNumber
            val episode = episodeList[episodeNumber]

            if (!checkFilter(episode.name)) {
                runOnUiThread {
                    try {
                        binding.tvDetails.text = getString(R.string.output, (episodeNumber + 1).toString(), episode.name)
                        if (sharedPreferences.getBoolean("spotify", false) && random != 3 && binding.bottomBarViewFlipper.displayedChild != 4) {
                            binding.btnSpotify.visibility = View.VISIBLE
                        }
                        when (binding.bottomBarViewFlipper.displayedChild) {
                            4 ->
                                loadEpisodeCover(getString(R.string.cover_citroncode_dd_url) + (episodeNumber + 1) + ".png")
                            else ->
                                if(binding.bottomBarViewFlipper.displayedChild == 6) {
                                    loadEpisodeCover(getString(R.string.cover_citroncode_url) + "k" + (episodeNumber + 1) + ".png")
                                    binding.fabLinks.hide()
                                }else if(binding.bottomBarViewFlipper.displayedChild == 7){
                                    loadEpisodeCover(getString(R.string.cover_citroncode_url) + "h" +(episodeNumber + 1) + ".png")
                                    binding.fabLinks.hide()
                                }else if(binding.bottomBarViewFlipper.displayedChild != 5){
                                    loadEpisodeCover(getString(R.string.cover_citroncode_url) + (episodeNumber + 1) + ".png")
                                    binding.fabLinks.show()
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
                                        binding.btnSpotify.visibility = View.GONE
                                    }
                                    if(random == 4){
                                        loadEpisodeCover(getString(R.string.cover_citroncode_url) + "k" + (episodeNumber + 1) + ".png")
                                        binding.fabLinks.hide()
                                    }
                                    if(random == 5){
                                        loadEpisodeCover(getString(R.string.cover_citroncode_url) + "h" + (episodeNumber + 1) + ".png")
                                        binding.fabLinks.hide()
                                    }
                                }
                        }
                        selectedEpisodeName = episode.name
                        selectedEpisodeDescription = episode.beschreibung
                        selectedEpisodeSpotify = episode.spotify
                    } catch (e: Exception) {
                        e.printStackTrace()
                        refresh()
                    }
                }
            } else {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        apiCall()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error: $e")
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
       if(networkUtils.isConnected(this) && sharedPreferences.getBoolean("update_list",false) && isFirstStart != 0){
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
            return try {
                packageManager.getPackageInfo("com.spotify.music", PackageManager.GET_ACTIVITIES)
                true
            } catch (e : PackageManager.NameNotFoundException) {
                false
            }
        }
        return false
    }
    private fun refresh(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
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
    private fun checkFilter(folgenName : String) : Boolean{
        return databaseHelper.alreadyAdded(folgen_database, folgenName)
    }
    override fun onResume() {
        super.onResume()

        isFirstStart = sharedPreferences.getInt("first",0)
        if(isFirstStart != 0 && !hasLoaded){
           initializeEpisodeLists()
        }

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
    private fun restoreViewFlipperPostion(){
        binding.bottomBarViewFlipper.displayedChild = sharedPreferences.getInt("vf_pos",0)
    }
    private fun saveViewFlipperPostion(){
        sharedPreferencesEditor.putInt("vf_pos", binding.bottomBarViewFlipper.displayedChild)
        sharedPreferencesEditor.apply()
    }
    private suspend fun loadEpisodesFromServer(url: String): List<JsonResponse> = withContext(Dispatchers.IO) {
        val episodeList = ArrayList<JsonResponse>()
        try {
            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val folgenListe = response.body()?.string()
                if (!folgenListe.isNullOrEmpty()) {
                    val jsonObject = JSONObject(folgenListe)
                    val jsonArray = jsonObject.optJSONArray("folgen")
                    if (jsonArray != null) {
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            episodeList.add(JsonResponse(
                                name = jsonObject.optString("name"),
                                beschreibung = jsonObject.optString("beschreibung"),
                                spotify = jsonObject.optString("spotify"),
                                nummer = jsonObject.optString("nummer"),
                                type = ""
                            ))
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("MainActivity", "Error loading episodes from server: ", e)
        }
        episodeList
    }
    private fun initializeEpisodeLists() {
        lifecycleScope.launch {
            val updateList = sharedPreferences.getBoolean("update_list", false)
            if (updateList && networkUtils.isConnected(this@MainActivity)) {
                val episodesDDF = async { loadEpisodesFromServer(getString(R.string.base_url) + "folgen.json") }
                val episodesDD = async { loadEpisodesFromServer(getString(R.string.base_url) + "folgen_diedrei.json") }
                val episodesKids = async { loadEpisodesFromServer(getString(R.string.base_url) + "folgen_kids.json") }
                val episodesSonderfolgen = async { loadEpisodesFromServer(getString(R.string.base_url) + "sonderfolgen_ddf.json") }
                val episodesHoerbuecher = async { loadEpisodesFromServer(getString(R.string.base_url) + "hoerbuecher.json") }

                episodeListDDF.addAll(episodesDDF.await())
                episodeListDD.addAll(episodesDD.await())
                episodeListKids.addAll(episodesKids.await())
                episodeListSonderfolgen.addAll(episodesSonderfolgen.await())
                episodeListHoerbuecher.addAll(episodesHoerbuecher.await())
            } else {
                episodeListDDF.addAll(loadEpisodesFromAssets("offline_list.txt"))
                episodeListDD.addAll(loadEpisodesFromAssets("offline_list_dd.txt"))
                episodeListKids.addAll(loadEpisodesFromAssets("offline_list_kids.txt"))
                episodeListSonderfolgen.addAll(loadEpisodesFromAssets("offline_list_sonderfolgen_ddf.txt"))
                episodeListHoerbuecher.addAll(loadEpisodesFromAssets("offline_list_hoerbuecher.txt"))
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    apiCall()
                }
            }

            if(sharedPreferences.getInt("initialEpisodesLength",0) == 0){
                sharedPreferencesEditor.putInt("minD",1)
                sharedPreferencesEditor.putInt("maxD", episodeListDDF.size)
                sharedPreferencesEditor.putInt("minKD",1)
                sharedPreferencesEditor.putInt("maxKD", episodeListKids.size)

                sharedPreferencesEditor.putInt("min",1)
                sharedPreferencesEditor.putInt("max", episodeListDDF.size)

                sharedPreferencesEditor.putInt("minDr3i",1)
                sharedPreferencesEditor.putInt("maxDr3i", 8)

                sharedPreferencesEditor.putInt("minK",1)
                sharedPreferencesEditor.putInt("maxK", episodeListKids.size)
                sharedPreferencesEditor.putInt("initialEpisodesLength", 123)
                sharedPreferencesEditor.apply()
            }else{
                sharedPreferencesEditor.putInt("minD",1)
                sharedPreferencesEditor.putInt("maxD", episodeListDDF.size)
                sharedPreferencesEditor.putInt("minKD",1)
                sharedPreferencesEditor.putInt("maxKD", episodeListKids.size)
                sharedPreferencesEditor.apply()
            }
        }
        hasLoaded = true
    }

    private fun loadEpisodesFromAssets(filename: String): List<JsonResponse> {
        val episodeList = ArrayList<JsonResponse>()
        val folgenListe = assets.open(filename).bufferedReader().use(BufferedReader::readText)
        val jsonObject = JSONObject(folgenListe)
        val jsonArray = jsonObject.optJSONArray("folgen")
        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                episodeList.add(JsonResponse(
                    name = jsonObject.optString("name"),
                    beschreibung = jsonObject.optString("beschreibung"),
                    spotify = jsonObject.optString("spotify"),
                    nummer = jsonObject.optString("nummer"),
                    type = ""
                ))
            }
        }
        return episodeList
    }
}


