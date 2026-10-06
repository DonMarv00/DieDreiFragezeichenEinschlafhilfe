package de.msdevs.einschlafhilfe.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun kategorieToString(k: Kategorie): String = k.name

    @TypeConverter
    fun stringToKategorie(s: String): Kategorie = Kategorie.valueOf(s)
}