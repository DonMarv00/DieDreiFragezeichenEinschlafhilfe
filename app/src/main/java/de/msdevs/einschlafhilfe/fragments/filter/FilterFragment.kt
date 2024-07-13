package de.msdevs.einschlafhilfe.fragments.filter

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.msdevs.einschlafhilfe.R
import de.msdevs.einschlafhilfe.adapter.FilterListeAdapter
import de.msdevs.einschlafhilfe.database.DatabaseHelper
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException

class FilterFragment() : Fragment(), FilterListeAdapter.OnFilterAdded{

    private val episodeList = ArrayList<JsonResponse>()
    private lateinit var rvEpisodes : RecyclerView
    private lateinit var pgLoadEpisodes : ProgressBar
    private lateinit var folgenListe : String
    private var position: Int? = null
    private lateinit var folgen_database: SQLiteDatabase
    private lateinit var databaseHelper: DatabaseHelper
    private var loadCounter = 0

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): FilterFragment {
            val fragment = FilterFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    private suspend fun loadEpisodes(){
        var type = ""
        requireActivity().runOnUiThread {
            if(loadCounter == 0){
                pgLoadEpisodes.visibility = View.VISIBLE
            }
        }

        val sharedPreferences = requireContext().getSharedPreferences(requireActivity().packageName,0)
        val updateList = sharedPreferences.getBoolean("update_list", false)
        if (updateList && NetworkUtils().isConnected(requireContext())) {
            when (position) {
                2 -> {
                    episodeList.clear()
                    folgenListe = loadJsonFiles(getString(R.string.base_url) + "folgen_diedrei.json")
                    type = "d3"
                }
                1 -> {
                    episodeList.clear()
                    folgenListe = loadJsonFiles(getString(R.string.base_url) + "folgen_kids.json")
                    type = "kids"
                }
                0 -> {
                    if(loadCounter == 0){
                        episodeList.clear()
                        folgenListe = loadJsonFiles(getString(R.string.base_url) + "folgen.json")
                        type = "ddf"
                    }else if(loadCounter == 1){
                        folgenListe = loadJsonFiles(getString(R.string.base_url) + "sonderfolgen_ddf.json")
                        type = "sonderfolgen"
                    }else{
                        folgenListe = loadJsonFiles(getString(R.string.base_url) + "hoerbuecher.json")
                        type = "hoerbuecher"
                        loadCounter = 0
                    }
                    loadCounter++
                }
            }
        }else{
            when (position) {
                2 -> {
                    episodeList.clear()
                    folgenListe = requireActivity().assets.open("offline_list_dd.txt").bufferedReader().use(BufferedReader::readText)
                    type = "d3"
                }
                1 -> {
                    episodeList.clear()
                    folgenListe = requireActivity().assets.open("offline_list_kids.txt").bufferedReader().use(BufferedReader::readText)
                    type = "kids"
                }
                0 -> {
                    if(loadCounter == 0){
                        episodeList.clear()
                        folgenListe = requireActivity().assets.open("offline_list.txt").bufferedReader().use(BufferedReader::readText)
                        type = "ddf"
                    }else if(loadCounter == 1){
                        folgenListe = requireActivity().assets.open("offline_list_sonderfolgen_ddf.txt").bufferedReader().use(BufferedReader::readText)
                        type = "sonderfolgen"
                    }else{
                        folgenListe = requireActivity().assets.open("offline_list_hoerbuecher.txt").bufferedReader().use(BufferedReader::readText)
                        type = "hoerbuecher"
                        loadCounter = 0
                    }
                    loadCounter++
                }
            }
        }
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
                    type = type))
            }
        }
        requireActivity().runOnUiThread {
            pgLoadEpisodes.visibility = View.GONE
            rvEpisodes.adapter = FilterListeAdapter(episodeList,this,requireContext())
        }

        if(type == "ddf" || type == "sonderfolgen"){
            loadEpisodes()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folgen_database = requireContext().openOrCreateDatabase("app_list", MODE_PRIVATE,null)
        databaseHelper = DatabaseHelper(requireContext())
        databaseHelper.createTables(folgen_database)

        rvEpisodes = view.findViewById(R.id.rv_folgen_liste)
        pgLoadEpisodes = view.findViewById(R.id.pg_load_episodes)
        rvEpisodes.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                loadEpisodes()
            }
        }

    }

    override fun onFilterAdded(name : String, nummer : String, type : String) {
       if(!databaseHelper.alreadyAdded(folgen_database,name)){
           databaseHelper.insertFilterFolge(folgen_database,name,nummer,type)
       }else{
           databaseHelper.removeFromList(folgen_database,name)
       }
    }
    private suspend fun loadJsonFiles(url: String) : String = withContext(Dispatchers.IO) {
        var data = ""
        try {
            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
               data = response.body()?.string().toString()
            }
        } catch (e: IOException) {
            Log.e("MainActivity", "Error loading episodes from server: ", e)
        }
        data
    }
}
