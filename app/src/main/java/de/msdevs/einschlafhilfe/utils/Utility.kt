package de.msdevs.einschlafhilfe.utils

import android.content.Context
import android.preference.PreferenceManager
import de.msdevs.einschlafhilfe.R

object Utility {
    fun setTheme(context: Context, theme: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putInt(context.getString(R.string.prefs_theme_key), theme).apply()
    }

    fun getTheme(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(context.getString(R.string.prefs_theme_key), -1)
    }
}