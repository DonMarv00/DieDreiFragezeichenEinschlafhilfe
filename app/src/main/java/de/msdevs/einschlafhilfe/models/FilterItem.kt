package de.msdevs.einschlafhilfe.models


sealed class FilterItem {
    data class Header(val title: String) : FilterItem()
    data class Entry(
        val id: String,
        val title: String,
        val subtitle: String,
        val selected: Boolean = false
    ) : FilterItem()
}