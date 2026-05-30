package com.android.rickmortyandroid.feature.characters.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDetailDao {

    // ── Queries ──────────────────────────────────────────────────────────────

    @Query("SELECT * FROM character_detail_cache WHERE id = :characterId")
    fun observeCharacterDetail(characterId: Int): Flow<CharacterDetailEntity?>

    @Query("SELECT * FROM character_detail_cache WHERE id = :characterId")
    suspend fun getCharacterDetail(characterId: Int): CharacterDetailEntity?

    @Query("SELECT * FROM character_detail_episodes WHERE characterId = :characterId")
    suspend fun getEpisodes(characterId: Int): List<EpisodeEntity>

    // ── Inserts ──────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterDetail(entity: CharacterDetailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    // ── Deletes ──────────────────────────────────────────────────────────────

    @Query("DELETE FROM character_detail_cache WHERE id = :characterId")
    suspend fun deleteCharacterDetail(characterId: Int)

    // Episodes are cascade-deleted automatically when the parent detail is removed.

    // ── Transactions ─────────────────────────────────────────────────────────

    /**
     * Atomically replaces character detail and its episodes.
     * The old episodes are implicitly removed by the CASCADE foreign key on delete.
     */
    @Transaction
    suspend fun upsertDetailWithEpisodes(
        detail: CharacterDetailEntity,
        episodes: List<EpisodeEntity>
    ) {
        deleteCharacterDetail(detail.id)
        insertCharacterDetail(detail)
        insertEpisodes(episodes)
    }
}
