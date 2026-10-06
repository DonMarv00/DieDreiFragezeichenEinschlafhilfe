package de.msdevs.einschlafhilfe.domain

import de.msdevs.einschlafhilfe.data.local.Episode
import de.msdevs.einschlafhilfe.data.remote.ApiEpisode
import kotlinx.serialization.json.Json

object EpisodeDetails {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun parse(episode: Episode): ApiEpisode =
        json.decodeFromString(ApiEpisode.serializer(), episode.rawJson)
}