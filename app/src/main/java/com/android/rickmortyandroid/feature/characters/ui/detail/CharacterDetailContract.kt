package com.android.rickmortyandroid.feature.characters.ui.detail

import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel

sealed interface CharacterDetailIntent {
    data object Retry : CharacterDetailIntent
    data object NavigateBack : CharacterDetailIntent
}

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    /**
     * [isRefreshing] is true while a stale-cache background network refresh is in progress.
     * The UI can show a subtle loading indicator without replacing the visible content.
     */
    data class Success(
        val character: CharacterDetailModel,
        val isRefreshing: Boolean = false
    ) : CharacterDetailUiState
    data class Error(val message: String) : CharacterDetailUiState
}

sealed interface CharacterDetailEffect {
    data object NavigateBack : CharacterDetailEffect
}
