package com.android.rickmortyandroid.feature.characters.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharacterRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<CharacterRemoteKeys>)

    @Query("SELECT * FROM character_remote_keys WHERE id = :characterId")
    suspend fun remoteKeyByCharacterId(characterId: Int): CharacterRemoteKeys?

    @Query("DELETE FROM character_remote_keys")
    suspend fun clearAll()
}
