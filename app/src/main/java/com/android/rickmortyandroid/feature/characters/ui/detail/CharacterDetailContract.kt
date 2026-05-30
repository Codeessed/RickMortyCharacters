package com.android.rickmortyandroid.feature.characters.ui.detail

import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel

sealed interface CharacterDetailIntent {
    data object Retry : CharacterDetailIntent
    data object NavigateBack : CharacterDetailIntent
}

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    data class Success(val character: CharacterDetailModel) : CharacterDetailUiState
    data class Error(val message: String) : CharacterDetailUiState
}

sealed interface CharacterDetailEffect {
    data object NavigateBack : CharacterDetailEffect
}
