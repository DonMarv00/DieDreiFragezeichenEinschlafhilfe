package de.msdevs.einschlafhilfe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import de.msdevs.einschlafhilfe.models.JsonResponse
import de.msdevs.einschlafhilfe.R

class AutoCompleteTextViewAdapter(context: Context, resource: Int, objects: ArrayList<JsonResponse>) :
    ArrayAdapter<JsonResponse>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_autocomplete, parent, false)

        val textView: TextView = view.findViewById(R.id.tv_folgen_name)
        textView.text = item?.name

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}