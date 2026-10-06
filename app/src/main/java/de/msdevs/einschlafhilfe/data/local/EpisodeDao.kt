package de.msdevs.einschlafhilfe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpisodeDao {

    @Query("SELECT * FROM episodes")
    suspend fun getAll(): List<Episode>

    @Query("SELECT * FROM episodes WHERE id = :id")
    suspend fun getById(id: Long): Episode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(episodes: List<Episode>)

    @Query("DELETE FROM episodes")
    suspend fun clear()

    // endpoint_sync
    @Query("SELECT * FROM endpoint_sync WHERE endpoint = :endpoint")
    suspend fun getSync(endpoint: String): EndpointSync?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSync(sync: EndpointSync)

    // upcoming
    @Query("SELECT * FROM upcoming ORDER BY position ASC")
    suspend fun getUpcoming(): List<Upcoming>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcoming(items: List<Upcoming>)

    @Query("DELETE FROM upcoming")
    suspend fun clearUpcoming()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<EpisodeCharacter>)

    @Query("DELETE FROM episode_characters")
    suspend fun clearCharacters()

    @Query("""SELECT DISTINCT episodeId FROM episode_characters WHERE rolle LIKE :pattern COLLATE NOCASE""")
    suspend fun episodeIdsForCharacter(pattern: String): List<Long>
}