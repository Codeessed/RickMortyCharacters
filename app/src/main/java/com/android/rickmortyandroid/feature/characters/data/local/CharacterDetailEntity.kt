package com.android.rickmortyandroid.feature.characters.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity that caches the full detail data for a character.
 * [cachedAt] is a Unix-epoch millisecond timestamp used to determine staleness.
 */
@Entity(tableName = "character_detail_cache")
data class CharacterDetailEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val totalEpisodeCount: Int,
    val cachedAt: Long
)
