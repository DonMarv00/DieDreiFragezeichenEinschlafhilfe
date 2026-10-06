package de.msdevs.einschlafhilfe.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiEpisode(
    val nummer: Int? = null,
    val titel: String,
    val autor: String? = null,
    @SerialName("hörspielskriptautor")
    val skriptautor: String? = null,
    val beschreibung: String? = null,
    @SerialName("veröffentlichungsdatum")
    val veroeffentlichungsdatum: String? = null,
    val gesamtdauer: Long = 0,
    val links: ApiLinks? = null,
    val kapitel: List<ApiKapitel>? = null,
    val sprechrollen: List<ApiSprechrolle>? = null
)

@Serializable
data class ApiLinks(
    val cover: String? = null,
    @SerialName("cover_dreifragezeichen")
    val coverDreifragezeichen: String? = null,
    @SerialName("cover_itunes")
    val coverItunes: String? = null,
    val artwork: String? = null,
    val dreifragezeichen: String? = null,
    val spotify: String? = null,
    val appleMusic: String? = null,
    val amazonMusic: String? = null,
    val youTubeMusic: String? = null,
    val deezer: String? = null
)

@Serializable
data class ApiKapitel(
    val titel: String,
    val start: Long = 0,
    val end: Long = 0
)

@Serializable
data class ApiSprechrolle(
    val rolle: String,
    val sprecher: String
)