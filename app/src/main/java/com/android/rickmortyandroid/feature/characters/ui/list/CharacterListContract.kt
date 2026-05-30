package com.android.rickmortyandroid.feature.characters.ui.list

import androidx.paging.PagingData
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterFilter
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

sealed interface CharacterListIntent {
    data object Refresh : CharacterListIntent
    data class OnCharacterClick(val characterId: Int) : CharacterListIntent
    data class OnSearchQueryChange(val query: String) : CharacterListIntent
    data class OnStatusFilterChange(val status: String) : CharacterListIntent
    data class OnSpeciesFilterChange(val species: String) : CharacterListIntent
    data object ClearFilters : CharacterListIntent
}

data class CharacterListUiState(
    val characters: Flow<PagingData<CharacterModel>> = emptyFlow(),
    val isOnline: Boolean = true,
    val searchQuery: String = "",
    val filter: CharacterFilter = CharacterFilter(),
    val isSearchActive: Boolean = false
)

sealed interface CharacterListEffect {
    data class NavigateToCharacterDetail(val characterId: Int) : CharacterListEffect
}
