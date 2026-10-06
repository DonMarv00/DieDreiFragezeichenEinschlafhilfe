package de.msdevs.einschlafhilfe.data.repository

import android.content.Context
import de.msdevs.einschlafhilfe.data.local.AppDatabase
import de.msdevs.einschlafhilfe.data.local.EndpointSync
import de.msdevs.einschlafhilfe.data.local.Episode
import de.msdevs.einschlafhilfe.data.local.EpisodeCharacter
import de.msdevs.einschlafhilfe.data.local.Upcoming
import de.msdevs.einschlafhilfe.data.remote.ApiClient
import de.msdevs.einschlafhilfe.domain.EpisodeDetails
import de.msdevs.einschlafhilfe.domain.FilterEngine
import de.msdevs.einschlafhilfe.domain.FilterPreset
import de.msdevs.einschlafhilfe.domain.FilterPresets
import de.msdevs.einschlafhilfe.prefs.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EpisodeRepository(context: Context) {

    private val db = AppDatabase.get(context)
    private val dao = db.episodeDao()
    private val api = ApiClient()
    private val prefs = PrefsManager(context)

    private val vorratGroesse = 5
    private val syncIntervalMs = 24 * 60 * 60 * 1000L  // 24h

    /** True, wenn ein Sync nötig ist (letzter Pull > 24h oder nie). */
    suspend fun needsSync(): Boolean = withContext(Dispatchers.IO) {
        val last = dao.getSync(SYNC_KEY)?.lastPulled ?: return@withContext true
        System.currentTimeMillis() - last > syncIntervalMs
    }

    /** Lädt alle Endpoints neu in die DB. Wirft bei Netzfehler. */
    suspend fun sync() = withContext(Dispatchers.IO) {
        val episodes = api.fetchAll()
        if (episodes.isEmpty()) throw IllegalStateException("Keine Daten geladen")
        dao.clear()
        dao.clearCharacters()
        dao.clearUpcoming()          // Vorrat verwirft alte Episode-IDs, sonst verwaiste Referenzen
        dao.insertAll(episodes)
        dao.setSync(EndpointSync(SYNC_KEY, System.currentTimeMillis()))

        // Rollen extrahieren und in episode_characters ablegen
        val stored = dao.getAll()
        val characters = mutableListOf<EpisodeCharacter>()
        for (ep in stored) {
            val details = EpisodeDetails.parse(ep)
            details.sprechrollen?.forEach { sr ->
                val rolle = sr.rolle.trim()
                if (rolle.isNotEmpty()) {
                    characters += EpisodeCharacter(episodeId = ep.id, rolle = rolle)
                }
            }
        }
        if (characters.isNotEmpty()) dao.insertCharacters(characters)
    }

    /** Die aktuell anzuzeigende Folge (Vorrat-Position 0). Null, wenn Vorrat leer. */
    suspend fun current(): Episode? = withContext(Dispatchers.IO) {
        val upcoming = dao.getUpcoming()
        val first = upcoming.firstOrNull() ?: return@withContext null
        dao.getById(first.episodeId)
    }

    /** Prüft, ob der Vorrat zum aktiven Filter passt und gefüllt ist. */
    suspend fun isVorratValid(): Boolean = withContext(Dispatchers.IO) {
        val upcoming = dao.getUpcoming()
        if (upcoming.isEmpty()) return@withContext false
        if (upcoming.any { it.filterId != prefs.activeFilterId }) return@withContext false
        // Referenzen müssen in episodes noch existieren
        upcoming.all { dao.getById(it.episodeId) != null }
    }

    /** Verwirft den Vorrat und zieht neue Folgen für den aktiven Filter. */
    suspend fun refillVorrat() = withContext(Dispatchers.IO) {
        val preset = FilterPresets.byId(prefs.activeFilterId)
        val pool = filteredEpisodes(preset)
        if (pool.isEmpty()) {
            dao.clearUpcoming()
            return@withContext
        }
        val picked = pool.shuffled().take(vorratGroesse)
        dao.clearUpcoming()
        dao.insertUpcoming(
            picked.mapIndexed { index, ep -> Upcoming(index, preset.id, ep.id) }
        )
    }

    /** Verbraucht Position 0, rückt nach, füllt hinten auf. Gibt neue current() zurück. */
    suspend fun next(): Episode? = withContext(Dispatchers.IO) {
        val preset = FilterPresets.byId(prefs.activeFilterId)
        val pool = filteredEpisodes(preset)
        if (pool.isEmpty()) {
            dao.clearUpcoming()
            return@withContext null
        }

        val current = dao.getUpcoming().toMutableList()
        if (current.isNotEmpty()) current.removeAt(0)

        // hinten auffüllen bis vorratGroesse
        val usedIds = current.map { it.episodeId }.toMutableSet()
        while (current.size < vorratGroesse) {
            val candidate = pool.filter { it.id !in usedIds }.randomOrNull() ?: break
            usedIds += candidate.id
            current += Upcoming(0, preset.id, candidate.id)
        }

        // Positionen neu vergeben
        val renumbered = current.mapIndexed { index, u ->
            u.copy(position = index, filterId = preset.id)
        }
        dao.clearUpcoming()
        dao.insertUpcoming(renumbered)

        renumbered.firstOrNull()?.let { dao.getById(it.episodeId) }
    }

    /** Die IDs des aktuellen Vorrats – für Glide-Preload der Cover. */
    suspend fun vorratCoverUrls(): List<String> = withContext(Dispatchers.IO) {
        dao.getUpcoming().mapNotNull { dao.getById(it.episodeId)?.coverUrl }
    }

    fun setActiveFilter(id: String) { prefs.activeFilterId = id }
    fun activeFilterId(): String = prefs.activeFilterId

    companion object {
        private const val SYNC_KEY = "all"
    }

    /** Liefert die zum Preset passenden Episodes. Charakter-Filter läuft über die DB. */
    private suspend fun filteredEpisodes(preset: FilterPreset): List<Episode> {
        val all = dao.getAll()

        // Charakter-Filter: episodeIds über die Rollen-Tabelle sammeln
        if (!preset.characterAliases.isNullOrEmpty()) {
            val matchingIds = mutableSetOf<Long>()
            for (alias in preset.characterAliases) {
                matchingIds += dao.episodeIdsForCharacter("%$alias%")
            }
            return all.filter { it.id in matchingIds }
        }

        // sonst die normale Feld-Filterung
        return FilterEngine.apply(all, preset)
    }
}