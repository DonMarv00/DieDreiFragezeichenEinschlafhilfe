package de.msdevs.einschlafhilfe.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.msdevs.einschlafhilfe.databinding.ItemFolgeBinding
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.utils.NetworkUtils
import de.msdevs.einschlafhilfe.R

class FilterListeAdapter(private val folgeList: List<JsonResponse>, private val onDeleteItemListener: OnDeleteItemListener? = null) :
    RecyclerView.Adapter<FilterListeAdapter.FolgeViewHolder>() {
    private var networkUtils = NetworkUtils()

    inner class FolgeViewHolder(private val binding: ItemFolgeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folgeTitle: String, nummer : String, type : String) {
            binding.tvFolgenName.text = folgeTitle

            if(nummer.contains("x")){
                binding.tvFolgenNummer.text = binding.tvFolgenNummer.context.getString(R.string.nummer, "$nummer (Sonderfolge)")
            }else{
                binding.tvFolgenNummer.text = binding.tvFolgenNummer.context.getString(R.string.nummer, nummer)
            }
            loadCover(nummer,type, binding.ivCover)
            binding.ivDelete.setOnClickListener{
                onDeleteItemListener?.onDeleteItem(folgeTitle)
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
        holder.bind(item.name,item.nummer.toString(), item.type)
    }

    override fun getItemCount(): Int {
        return folgeList.size
    }

    private fun loadCover(nummer : String, type : String, iv : ImageView){
        val context : Context = iv.context
        var prefix = ""
        if(type == "ddf"){
            prefix = ""
        }else if(type == "d3"){
            prefix = "dd"
        }else if(type == "kids"){
            prefix = "k"
        }else if(type == "sonderfolgen"){
            prefix = "x"
        }else{
            prefix = "s"
        }
        val url : String = if(prefix == "dd"){
            context.getString(R.string.cover_citroncode_dd_url) + (nummer) + ".png"
        }else{
            context.getString(R.string.cover_citroncode_url)  + (nummer) + ".png"
        }
        Log.e("FilterListeAdapter", "Cover URL: $url")
        if(networkUtils.isConnected(context)){
            Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv)
        }
    }
    interface OnDeleteItemListener {
        fun onDeleteItem(name: String)
    }
}