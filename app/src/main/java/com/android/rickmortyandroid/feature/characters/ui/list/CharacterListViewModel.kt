package com.android.rickmortyandroid.feature.characters.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.rickmortyandroid.core.data.util.NetworkMonitor
import com.android.rickmortyandroid.feature.characters.domain.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    getCharactersUseCase: GetCharactersUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterListUiState())
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    private val _effect = Channel<CharacterListEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _uiState.update {
            it.copy(
                characters = getCharactersUseCase().cachedIn(viewModelScope)
            )
        }

        networkMonitor.isOnline.onEach { isOnline ->
            _uiState.update { it.copy(isOnline = isOnline) }
        }.launchIn(viewModelScope)
    }

    fun onIntent(intent: CharacterListIntent) {
        when (intent) {
            is CharacterListIntent.Refresh -> {
                // Paging3 handles refresh at the UI level via LazyPagingItems.refresh()
            }
            is CharacterListIntent.OnCharacterClick -> {
                _effect.trySend(CharacterListEffect.NavigateToCharacterDetail(intent.characterId))
            }
        }
    }
}
