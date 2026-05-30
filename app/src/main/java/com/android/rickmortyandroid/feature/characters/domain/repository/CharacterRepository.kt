package com.android.rickmortyandroid.feature.characters.domain.repository

import androidx.paging.PagingData
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterFilter
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacters(filter: CharacterFilter = CharacterFilter()): Flow<PagingData<CharacterModel>>
    suspend fun getCharacterDetail(characterId: Int): Result<CharacterDetailModel>
}
