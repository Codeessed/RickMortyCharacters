package com.android.rickmortyandroid.feature.characters.domain.repository

import androidx.paging.PagingData
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterFilter
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacters(filter: CharacterFilter = CharacterFilter()): Flow<PagingData<CharacterModel>>

    /**
     * Returns a cold [Flow] that follows a stale-while-revalidate strategy:
     * 1. Emits cached data immediately if available.
     * 2. Fetches fresh data from the network when the cache is absent or stale.
     * 3. Persists fresh data and emits it via the same flow.
     */
    fun getCharacterDetail(characterId: Int): Flow<Result<CharacterDetailModel>>
}
