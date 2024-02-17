package de.msdevs.einschlafhilfe


import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import de.msdevs.einschlafhilfe.adapter.FilterListeAdapter
import de.msdevs.einschlafhilfe.database.DatabaseHelper
import de.msdevs.einschlafhilfe.databinding.ActivityFilterBinding
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader

class FilterActivity : BaseActivity(), FilterListeAdapter.OnDeleteItemListener{
    private lateinit var rvFilterListe : RecyclerView
    private lateinit var tvNothingAdded : TextView
    private lateinit var binding : ActivityFilterBinding
    private lateinit var folgen_database: SQLiteDatabase
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tvInput : MaterialAutoCompleteTextView
    private val episodeList = ArrayList<JsonResponse>()
    private var filterList = ArrayList<JsonResponse>()
    private var jsonIndexSeperator = HashMap<String, Int>()
    private var apiCallCounter = 0
    private lateinit var networkUtils: NetworkUtils
    private lateinit var folgenListe : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        iniViews()
        refreshList()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                apiCall("ddf")
                apiCall("d3")
                apiCall("kids")
                apiCall("sonderfolgen")
            }
        }


    }
    private suspend fun apiCall(extraParameter : String) {
        try {
            if(networkUtils.isConnected(this)){

                if(extraParameter == "ddf"){
                    folgenListe = assets.open("offline_list.txt").bufferedReader().use(BufferedReader::readText)
                }else if (extraParameter == "d3"){
                    folgenListe = assets.open("offline_list_dd.txt").bufferedReader().use(BufferedReader::readText)
                }else if(extraParameter == "kids"){
                    folgenListe = assets.open("offline_list_kids.txt").bufferedReader().use(BufferedReader::readText)
                }else{
                    folgenListe = assets.open("offline_list_sonderfolgen_ddf.txt").bufferedReader().use(BufferedReader::readText)
                }
                val jsonObject = JSONObject(folgenListe)
                val jsonArray = jsonObject.optJSONArray("folgen")
                if (jsonArray != null) {
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        episodeList.add(
                            JsonResponse(
                                name = jsonObject.optString("name"),
                                beschreibung = jsonObject.optString("beschreibung"),
                                spotify = jsonObject.optString("spotify"),
                                nummer = jsonObject.optInt("nummer"),
                                type = extraParameter
                            )
                        )
                    }
                    jsonIndexSeperator[extraParameter] = jsonArray.length()
                    apiCallCounter++
                    if(apiCallCounter == 4){
                        runOnUiThread {
                            iniAutoComplete()
                        }
                    }
                }

            }
        }catch (e : Exception){
            Log.e("","Error: " + e.message)
        }
    }
    private fun iniAutoComplete(){
        val nameArray = episodeList.map { it.name }.toTypedArray()

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameArray)
        val textView : MaterialAutoCompleteTextView = binding.tvFolge
        textView.threshold = 3
        textView.setAdapter(adapter)

        textView.setOnItemClickListener { parent, view, position, id ->
            val selectedName = parent.getItemAtPosition(position) as String
            databaseHelper.insertFilterFolge(folgen_database,selectedName,getEpisodeNumber(selectedName),getEpisodeType(selectedName))
            textView.text = null
            refreshList()
        }
    }
    private fun refreshList(){
        filterList = databaseHelper.getFilterList(folgen_database)
        val adapter = FilterListeAdapter(filterList,this)
        val layoutManger = LinearLayoutManager(this)

        if(filterList.isNotEmpty()){
            tvNothingAdded.visibility = View.GONE
        }else{
            tvNothingAdded.visibility = View.VISIBLE
        }
        rvFilterListe.layoutManager = layoutManger
        rvFilterListe.adapter = adapter

    }
    private fun iniViews(){
        folgen_database = openOrCreateDatabase("app_list",MODE_PRIVATE,null)
        databaseHelper = DatabaseHelper(this)
        databaseHelper.createTables(folgen_database)

        networkUtils = NetworkUtils()
        tvInput = binding.tvFolge
        rvFilterListe = binding.rvFilterListe
        tvNothingAdded = binding.tvNoEpisodesAdded
    }

    private fun getEpisodeNumber(nameToFind: String): String? {
        for (jsonResponse in episodeList) {
            if (jsonResponse.name == nameToFind) {
                return jsonResponse.nummer.toString()
            }
        }
        return null
    }
    private fun getEpisodeType(nameToFind: String): String? {
        for (jsonResponse in episodeList) {
            if (jsonResponse.name == nameToFind) {
                return jsonResponse.type
            }
        }
        return null
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDeleteItem(name: String) {
        databaseHelper.removeFromList(folgen_database,name)
        refreshList()
    }
}