package de.msdevs.einschlafhilfe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.msdevs.einschlafhilfe.data.remote.ApiKapitel
import de.msdevs.einschlafhilfe.R

class KapitelAdapter(
    private val items: List<ApiKapitel>
) : RecyclerView.Adapter<KapitelAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kapitel, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position + 1)
    }

    override fun getItemCount() = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val nummer: TextView = view.findViewById(R.id.text_kapitel_nummer)
        private val titel: TextView = view.findViewById(R.id.text_kapitel_titel)
        fun bind(k: ApiKapitel, index: Int) {
            nummer.text = index.toString()
            titel.text = k.titel
        }
    }
}