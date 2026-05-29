package com.android.rickmortyandroid.feature.characters.domain.usecase

import androidx.paging.PagingData
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import com.android.rickmortyandroid.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(): Flow<PagingData<CharacterModel>> {
        return repository.getCharacters()
    }
}
