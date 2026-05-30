package com.android.rickmortyandroid.feature.characters.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for caching episodes associated with a cached character detail.
 * [characterId] is a foreign key referencing [CharacterDetailEntity.id].
 * On cascade delete, episodes are removed automatically when the character detail is deleted.
 */
@Entity(
    tableName = "character_detail_episodes",
    foreignKeys = [
        ForeignKey(
            entity = CharacterDetailEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("characterId")]
)
data class EpisodeEntity(
    @PrimaryKey val id: Int,
    val characterId: Int,
    val name: String,
    val airDate: String,
    val episodeCode: String
)
