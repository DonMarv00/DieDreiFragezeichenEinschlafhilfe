package de.msdevs.einschlafhilfe


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import de.msdevs.einschlafhilfe.databinding.ActivityFilterBinding
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await


class FilterActivity : BaseActivity(){

    private lateinit var binding : ActivityFilterBinding
    private lateinit var tvInput : MaterialAutoCompleteTextView
    private val episodeList = ArrayList<JsonResponse>()
    private lateinit var networkUtils: NetworkUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        iniViews()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                apiCall()
            }
        }

    }
    private suspend fun apiCall() {
        val urlExtraParameter = "folgen.json"
        episodeList.clear()
        try {
            if(networkUtils.isConnected(this)){
                val client = OkHttpClient.Builder().build()
                val request =
                    Request.Builder().url(getString(R.string.base_url) + urlExtraParameter)
                        .build()

                val folgenListe = client.newCall(request).await().body()?.string().toString()
                val jsonObject = JSONObject(folgenListe)
                val jsonArray = jsonObject.optJSONArray("folgen")
                if (jsonArray != null) {
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        episodeList.add(
                            JsonResponse(
                                name = jsonObject.optString("name"),
                                beschreibung = jsonObject.optString("beschreibung"),
                                spotify = jsonObject.optString("spotify")
                            )
                        )
                    }
                    runOnUiThread {
                        iniAutoComplete()
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

        }
    }
    private fun iniViews(){
        networkUtils = NetworkUtils()
        tvInput = binding.tvFolge
    }
}