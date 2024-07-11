package de.msdevs.einschlafhilfe.adapter

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.msdevs.einschlafhilfe.databinding.ItemFolgeBinding
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import de.msdevs.einschlafhilfe.R
import de.msdevs.einschlafhilfe.database.DatabaseHelper

class FilterListeAdapter(private val folgeList: List<JsonResponse>, private val onAddToFilterList: OnFilterAdded? = null, private val context: Context) :
    RecyclerView.Adapter<FilterListeAdapter.FolgeViewHolder>() {

    private var networkUtils = NetworkUtils()
    private lateinit var folgen_database: SQLiteDatabase
    private lateinit var databaseHelper: DatabaseHelper

    inner class FolgeViewHolder(private val binding: ItemFolgeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folgeTitle: String, nummer : String, type : String) {
            binding.tvFolgenName.text = folgeTitle

            folgen_database = context.openOrCreateDatabase("app_list", MODE_PRIVATE,null)
            databaseHelper = DatabaseHelper(context)
            databaseHelper.createTables(folgen_database)


            if(nummer.contains("x")){
                binding.tvFolgenNummer.text = binding.tvFolgenNummer.context.getString(R.string.nummer, "$nummer (Sonderfolge)")
            }else{
                binding.tvFolgenNummer.text = binding.tvFolgenNummer.context.getString(R.string.nummer, nummer)
            }
            loadCover(nummer,type, binding.ivCover)

            binding.cbDelete.isChecked = isFiltered(folgeTitle)
            binding.cbDelete.setOnClickListener{
                onAddToFilterList?.onFilterAdded(folgeTitle,nummer,type)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolgeViewHolder {
        val binding =
            ItemFolgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolgeViewHolder, position: Int) {
        val item = folgeList[position]
        holder.bind(item.name,item.nummer, item.type)
    }

    override fun getItemCount(): Int {
        return folgeList.size
    }

    private fun loadCover(nummer : String, type : String, iv : ImageView){
        val context : Context = iv.context
        var prefix = ""
        if(type == "ddf"){
            prefix = "ddf"
        }else if(type == "d3"){
            prefix = "dd"
        }else if(type == "kids"){
            prefix = "k"
        }else if(type == "sonderfolgen"){
            prefix = "x"
        }else{
            prefix = "s"
        }

        var url : String = ""
        if(prefix == "dd"){
            url = context.getString(R.string.cover_citroncode_dd_url) + (nummer) + ".png"
        }else if(prefix == "ddf"){
            url = context.getString(R.string.cover_citroncode_url)  + (nummer) + ".png"
        }else if(prefix == "k"){
            url = context.getString(R.string.cover_citroncode_url)  + "k" + (nummer) + ".png"
        }
        else if(prefix == "x"){
            url = context.getString(R.string.cover_citroncode_url) + (nummer) + ".png"
        }

        if(networkUtils.isConnected(context)){
            Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv)
        }

    }
    private fun isFiltered(name : String) : Boolean{
        return !databaseHelper.alreadyAdded(folgen_database, name)
    }
    interface OnFilterAdded {
        fun onFilterAdded(name: String, nummer : String, type : String)
    }
}