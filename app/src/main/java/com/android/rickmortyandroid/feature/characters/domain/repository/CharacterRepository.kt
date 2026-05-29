package com.android.rickmortyandroid.feature.characters.domain.repository

import androidx.paging.PagingData
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacters(): Flow<PagingData<CharacterModel>>
}
