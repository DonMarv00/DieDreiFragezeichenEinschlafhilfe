package de.msdevs.einschlafhilfe.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.msdevs.einschlafhilfe.models.FilterItem
import de.msdevs.einschlafhilfe.R
class FilterAdapter(
    private val items: List<FilterItem>,
    private val onEntryClick: (FilterItem.Entry) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ENTRY = 1
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is FilterItem.Header -> TYPE_HEADER
            is FilterItem.Entry -> TYPE_ENTRY
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderHolder(inflater.inflate(R.layout.item_filter_header, parent, false))
        } else {
            EntryHolder(inflater.inflate(R.layout.item_filter_entry, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is FilterItem.Header -> (holder as HeaderHolder).bind(item)
            is FilterItem.Entry -> (holder as EntryHolder).bind(item, onEntryClick)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.text_header)
        fun bind(item: FilterItem.Header) {
            title.text = item.title
        }
    }

    class EntryHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.text_title)
        private val subtitle: TextView = view.findViewById(R.id.text_subtitle)
        private val check: View = view.findViewById(R.id.icon_check)
        fun bind(item: FilterItem.Entry, onClick: (FilterItem.Entry) -> Unit) {
            title.text = item.title
            subtitle.text = item.subtitle
            check.visibility = if (item.selected) View.VISIBLE else View.GONE
            itemView.setOnClickListener { onClick(item) }
        }
    }
}