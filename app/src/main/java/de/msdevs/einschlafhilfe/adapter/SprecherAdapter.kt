package de.msdevs.einschlafhilfe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.msdevs.einschlafhilfe.data.remote.ApiSprechrolle
import de.msdevs.einschlafhilfe.R

class SprecherAdapter(
    private val items: List<ApiSprechrolle>
) : RecyclerView.Adapter<SprecherAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sprecher, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val rolle: TextView = view.findViewById(R.id.text_rolle)
        private val sprecher: TextView = view.findViewById(R.id.text_sprecher)
        fun bind(s: ApiSprechrolle) {
            rolle.text = s.rolle
            sprecher.text = s.sprecher
        }
    }
}