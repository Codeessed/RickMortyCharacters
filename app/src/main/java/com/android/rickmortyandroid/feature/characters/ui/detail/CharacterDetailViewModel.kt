package com.android.rickmortyandroid.feature.characters.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.rickmortyandroid.feature.characters.domain.usecase.GetCharacterDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Int = savedStateHandle.get<Int>("characterId") ?: -1

    private val _uiState = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private val _effect = Channel<CharacterDetailEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadCharacterDetail()
    }

    fun onIntent(intent: CharacterDetailIntent) {
        when (intent) {
            is CharacterDetailIntent.Retry -> loadCharacterDetail()
            is CharacterDetailIntent.NavigateBack -> {
                _effect.trySend(CharacterDetailEffect.NavigateBack)
            }
        }
    }

    private fun loadCharacterDetail() {
        viewModelScope.launch {
            // Reset to Loading only if we don't already have data to show
            if (_uiState.value !is CharacterDetailUiState.Success) {
                _uiState.value = CharacterDetailUiState.Loading
            } else {
                // We have cached data — mark it as refreshing so the UI can show a subtle indicator
                val current = _uiState.value as CharacterDetailUiState.Success
                _uiState.value = current.copy(isRefreshing = true)
            }

            getCharacterDetailUseCase(characterId).collect { result ->
                result
                    .onSuccess { detail ->
                        _uiState.value = CharacterDetailUiState.Success(
                            character = detail,
                            isRefreshing = false
                        )
                    }
                    .onFailure { error ->
                        // Only surface an error if we have no data to show
                        if (_uiState.value !is CharacterDetailUiState.Success) {
                            _uiState.value = CharacterDetailUiState.Error(
                                error.localizedMessage ?: "Failed to load character"
                            )
                        }
                    }
            }
        }
    }
}
