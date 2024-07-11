package de.msdevs.einschlafhilfe.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import de.msdevs.einschlafhilfe.models.JsonResponse


class DatabaseHelper(private val c: Context) {
    fun insertFilterFolge(
        database: SQLiteDatabase,
        name: String?,
        nummer: String?,
        type: String?
    ) {
        if (!alreadyAdded(database, name)) {
            val initialValues = ContentValues()
            initialValues.put("name", name)
            initialValues.put("nummer", nummer)
            initialValues.put("type", type)
            database.insert("filter", null, initialValues)
        }
    }

    fun createTables(database: SQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS filter(name VARCHAR, nummer VARCHAR, type VARCHAR);")
    }

    fun removeFromList(database: SQLiteDatabase, name: String) {
        database.delete("filter", "name=?", arrayOf(name))
    }

    fun getFilterList(database: SQLiteDatabase): ArrayList<JsonResponse> {
        val app_names = ArrayList<JsonResponse>()
        var cursor: Cursor? = null
        try {
            cursor = database.rawQuery("Select * from filter", null)
            if (cursor.moveToFirst()) {
                do {
                    try {
                        app_names.add(JsonResponse(cursor.getString(0),"N/A","N/A",cursor.getString(1), cursor.getString(2)))
                    } catch (e: Exception) {
                        Log.e("DatabaseHelper", "Error: " + e.message)
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error: " + e.message)
        } finally {
            cursor?.close()
        }
        return app_names
    }
    fun alreadyAdded(database: SQLiteDatabase, name: String?): Boolean {
        var cursor: Cursor? = null
        try {
            cursor = database.rawQuery("SELECT * FROM filter WHERE name=?", arrayOf(name))
            return cursor.moveToFirst()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error checking if episode exists: " + e.message)
        } finally {
            cursor?.close()
        }
        return false
    }

}