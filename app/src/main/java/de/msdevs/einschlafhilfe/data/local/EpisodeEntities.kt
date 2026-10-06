package de.msdevs.einschlafhilfe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "endpoint_sync")
data class EndpointSync(
    @PrimaryKey val endpoint: String,
    val lastPulled: Long
)

@Entity(tableName = "upcoming")
data class Upcoming(
    @PrimaryKey val position: Int,
    val filterId: String,
    val episodeId: Long
)