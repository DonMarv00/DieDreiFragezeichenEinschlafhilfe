package de.msdevs.einschlafhilfe.data.remote

import de.msdevs.einschlafhilfe.data.local.Episode
import de.msdevs.einschlafhilfe.data.local.Kategorie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request


class ApiClient(
    private val client: OkHttpClient = OkHttpClient()
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val baseUrl = "https://api.citroncode.com/android/ddf/v7/api"

    private val endpoints = mapOf(
        "serie" to Kategorie.SERIE,
        "kids" to Kategorie.KIDS,
        "dr3i" to Kategorie.DR3I,
        "spezial" to Kategorie.SPEZIAL,
        "kurzgeschichten" to Kategorie.KURZGESCHICHTEN
    )

    // JSON-Key im Response-Objekt je Kategorie
    private val responseKey = mapOf(
        Kategorie.SERIE to "serie",
        Kategorie.KIDS to "kids",
        Kategorie.DR3I to "die_dr3i",
        Kategorie.SPEZIAL to "spezial",
        Kategorie.KURZGESCHICHTEN to "kurzgeschichten"
    )

    /** Lädt alle Endpoints und gibt alle Folgen als Episode-Liste zurück. */
    suspend fun fetchAll(): List<Episode> = withContext(Dispatchers.IO) {
        val result = mutableListOf<Episode>()
        for ((path, kategorie) in endpoints) {
            result += fetchEndpoint(path, kategorie)
        }
        result
    }

    private fun fetchEndpoint(path: String, kategorie: Kategorie): List<Episode> {
        val request = Request.Builder()
            .url("$baseUrl/$path")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            val body = response.body()?.string() ?: return emptyList()

            // 1) Ganzes Response-Objekt als JsonElement
            val root = json.parseToJsonElement(body).jsonObject
            val key = responseKey[kategorie] ?: return emptyList()
            val array = root[key] as? JsonArray ?: return emptyList()

            // 2) Pro Folge: rohes Objekt behalten + typisiert deserialisieren
            return array.mapNotNull { element ->
                val obj = element as? JsonObject ?: return@mapNotNull null
                val raw = obj.toString()                       // die einzelne Folgen-JSON
                val dto = json.decodeFromJsonElement(ApiEpisode.serializer(), obj)
                dto.toEpisode(kategorie, raw)
            }
        }
    }
    private fun String?.https(): String? = this?.replace("http://", "https://")

    private fun ApiEpisode.toEpisode(kategorie: Kategorie, raw: String): Episode =
        Episode(
            nummer = nummer,
            kategorie = kategorie,
            titel = titel,
            autor = autor,
            skriptautor = skriptautor,
            beschreibung = beschreibung,
            jahr = veroeffentlichungsdatum?.take(4)?.toIntOrNull(),
            dauerMs = gesamtdauer,
            coverUrl = (links?.cover
                ?: links?.coverDreifragezeichen
                ?: links?.coverItunes).https(),
            dreifragezeichenUrl = links?.dreifragezeichen.https(),
            spotifyUrl = links?.spotify.https(),
            appleMusicUrl = links?.appleMusic.https(),
            amazonMusicUrl = links?.amazonMusic.https(),
            youtubeMusicUrl = links?.youTubeMusic.https(),
            deezerUrl = links?.deezer.https(),
            rawJson = raw
        )
}