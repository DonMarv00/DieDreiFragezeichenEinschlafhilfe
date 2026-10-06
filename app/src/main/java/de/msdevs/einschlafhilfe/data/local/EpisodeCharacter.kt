package de.msdevs.einschlafhilfe.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "episode_characters",
    indices = [Index("episodeId"), Index("rolle")]
)
data class EpisodeCharacter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val episodeId: Long,
    val rolle: String
)