package de.msdevs.einschlafhilfe.prefs

import android.content.Context
import de.msdevs.einschlafhilfe.domain.FilterPresets

class PrefsManager(context: Context) {

    private val prefs = context.getSharedPreferences("ddf_prefs", Context.MODE_PRIVATE)

    var activeFilterId: String
        get() = prefs.getString(KEY_FILTER, FilterPresets.default.id) ?: FilterPresets.default.id
        set(value) { prefs.edit().putString(KEY_FILTER, value).apply() }

    companion object {
        private const val KEY_FILTER = "active_filter_id"
    }
}