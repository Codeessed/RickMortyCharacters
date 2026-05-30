package com.android.rickmortyandroid.feature.characters.domain.usecase

import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel
import com.android.rickmortyandroid.feature.characters.domain.repository.CharacterRepository
import javax.inject.Inject

class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(characterId: Int): Result<CharacterDetailModel> {
        return repository.getCharacterDetail(characterId)
    }
}
