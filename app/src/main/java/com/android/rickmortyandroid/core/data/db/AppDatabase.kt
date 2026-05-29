package com.android.rickmortyandroid.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.rickmortyandroid.feature.characters.data.local.CharacterDao
import com.android.rickmortyandroid.feature.characters.data.local.CharacterEntity
import com.android.rickmortyandroid.feature.characters.data.local.CharacterRemoteKeys
import com.android.rickmortyandroid.feature.characters.data.local.CharacterRemoteKeysDao

@Database(
    entities = [CharacterEntity::class, CharacterRemoteKeys::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun characterRemoteKeysDao(): CharacterRemoteKeysDao
}
