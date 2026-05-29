package com.android.rickmortyandroid.feature.characters.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_remote_keys")
data class CharacterRemoteKeys(
    @PrimaryKey val id: Int,
    val prevPage: Int?,
    val nextPage: Int?
)
