package de.msdevs.einschlafhilfe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Kategorie {
    SERIE, KIDS, DR3I, SPEZIAL, KURZGESCHICHTEN, HOERBUCH
}

@Entity(tableName = "episodes")
data class Episode(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nummer: Int?,          // null bei Kurzgeschichten
    val kategorie: Kategorie,
    val titel: String,
    val autor: String?,
    val skriptautor: String?,
    val beschreibung: String?,
    val jahr: Int?,            // aus veröffentlichungsdatum
    val dauerMs: Long,
    val coverUrl: String?,

    // Streaming-Links (für den Play-Intent später)
    val dreifragezeichenUrl: String? = null,
    val spotifyUrl: String? = null,
    val appleMusicUrl: String? = null,
    val amazonMusicUrl: String? = null,
    val youtubeMusicUrl: String? = null,
    val deezerUrl: String? = null,
    // komplette Roh-JSON dieser Folge, für die Detailansicht offline
    val rawJson: String
)