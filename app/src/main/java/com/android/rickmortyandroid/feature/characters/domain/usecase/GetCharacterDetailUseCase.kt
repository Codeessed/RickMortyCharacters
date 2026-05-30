package com.android.rickmortyandroid.feature.characters.domain.usecase

import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel
import com.android.rickmortyandroid.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(characterId: Int): Flow<Result<CharacterDetailModel>> {
        return repository.getCharacterDetail(characterId)
    }
}
