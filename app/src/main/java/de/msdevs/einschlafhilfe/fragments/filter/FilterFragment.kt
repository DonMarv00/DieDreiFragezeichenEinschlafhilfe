package de.msdevs.einschlafhilfe.fragments.filter

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.msdevs.einschlafhilfe.R
import de.msdevs.einschlafhilfe.adapter.FilterListeAdapter
import de.msdevs.einschlafhilfe.database.DatabaseHelper
import de.msdevs.einschlafhilfe.models.JsonResponse
import org.json.JSONObject
import java.io.BufferedReader

class FilterFragment : Fragment(), FilterListeAdapter.OnFilterAdded{

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

    private fun loadEpisodes(){
        var type = ""
        pgLoadEpisodes.visibility = View.VISIBLE

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
                }else{
                    folgenListe = requireActivity().assets.open("offline_list_sonderfolgen_ddf.txt").bufferedReader().use(BufferedReader::readText)
                    type = "sonderfolgen"
                    loadCounter = 0
                }
                loadCounter++
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
        pgLoadEpisodes.visibility = View.GONE
        rvEpisodes.adapter = FilterListeAdapter(episodeList,this,requireContext())

        if(type == "ddf"){
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

        loadEpisodes()
    }

    override fun onFilterAdded(name : String, nummer : String, type : String) {
       if(!databaseHelper.alreadyAdded(folgen_database,name)){
           databaseHelper.insertFilterFolge(folgen_database,name,nummer,type)
       }else{
           databaseHelper.removeFromList(folgen_database,name)
       }
    }
}
