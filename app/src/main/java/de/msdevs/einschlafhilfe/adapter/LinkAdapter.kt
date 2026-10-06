package de.msdevs.einschlafhilfe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.msdevs.einschlafhilfe.R

class LinkAdapter(
    private val items: List<Pair<String, String>>,  // Label to URL
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<LinkAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_link, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position + 1, onClick)
    }

    override fun getItemCount() = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val nummer: TextView = view.findViewById(R.id.text_link_nummer)
        private val titel: TextView = view.findViewById(R.id.text_link_titel)
        fun bind(item: Pair<String, String>, index: Int, onClick: (String) -> Unit) {
            nummer.text = index.toString()
            titel.text = item.first
            itemView.setOnClickListener { onClick(item.second) }
        }
    }
}