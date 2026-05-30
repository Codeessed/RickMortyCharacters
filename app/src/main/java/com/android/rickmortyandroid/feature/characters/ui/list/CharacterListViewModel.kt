package com.android.rickmortyandroid.feature.characters.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.rickmortyandroid.core.data.util.NetworkMonitor
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterFilter
import com.android.rickmortyandroid.feature.characters.domain.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterListUiState())
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    private val _effect = Channel<CharacterListEffect>()
    val effect = _effect.receiveAsFlow()

    // Internal filter state that drives the paging flow via debounce + flatMapLatest
    private val _filter = MutableStateFlow(CharacterFilter())

    init {
        // Observe filter changes: debounce 500ms to avoid unnecessary API calls,
        // then create a fresh PagingData flow for each new filter value
        _filter
            .debounce(500L)
            .distinctUntilChanged()
            .flatMapLatest { filter ->
                getCharactersUseCase(filter).cachedIn(viewModelScope)
            }
            .onEach { pagingData ->
                // Wrap the latest PagingData in a single-emission flow for the UI state
                _uiState.update {
                    it.copy(
                        characters = kotlinx.coroutines.flow.flowOf(pagingData)
                    )
                }
            }
            .launchIn(viewModelScope)

        // Monitor network status
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

            is CharacterListIntent.OnSearchQueryChange -> {
                val newFilter = _filter.value.copy(name = intent.query)
                _filter.value = newFilter
                _uiState.update {
                    it.copy(
                        searchQuery = intent.query,
                        filter = newFilter,
                        isSearchActive = newFilter.isActive
                    )
                }
            }

            is CharacterListIntent.OnStatusFilterChange -> {
                // Toggle: if same status is tapped again, clear it
                val currentStatus = _filter.value.status
                val newStatus = if (currentStatus == intent.status) "" else intent.status
                val newFilter = _filter.value.copy(status = newStatus)
                _filter.value = newFilter
                _uiState.update {
                    it.copy(
                        filter = newFilter,
                        isSearchActive = newFilter.isActive
                    )
                }
            }

            is CharacterListIntent.OnSpeciesFilterChange -> {
                val currentSpecies = _filter.value.species
                val newSpecies = if (currentSpecies == intent.species) "" else intent.species
                val newFilter = _filter.value.copy(species = newSpecies)
                _filter.value = newFilter
                _uiState.update {
                    it.copy(
                        filter = newFilter,
                        isSearchActive = newFilter.isActive
                    )
                }
            }

            is CharacterListIntent.ClearFilters -> {
                val emptyFilter = CharacterFilter()
                _filter.value = emptyFilter
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        filter = emptyFilter,
                        isSearchActive = false
                    )
                }
            }
        }
    }
}
