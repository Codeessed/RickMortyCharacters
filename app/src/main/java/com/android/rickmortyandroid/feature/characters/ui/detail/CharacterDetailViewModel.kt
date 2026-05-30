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
            _uiState.value = CharacterDetailUiState.Loading
            getCharacterDetailUseCase(characterId)
                .onSuccess { detail ->
                    _uiState.value = CharacterDetailUiState.Success(detail)
                }
                .onFailure { error ->
                    _uiState.value = CharacterDetailUiState.Error(
                        error.localizedMessage ?: "Failed to load character"
                    )
                }
        }
    }
}
