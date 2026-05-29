package com.android.rickmortyandroid.feature.characters.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.android.rickmortyandroid.core.ui.components.NetworkBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val characters = uiState.characters.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Rick & Morty") }
                )
                NetworkBanner(isOnline = uiState.isOnline)
            }
        }
    ) { innerPadding ->
        CharacterListContent(
            characters = characters,
            onCharacterClick = { id -> 
                viewModel.onIntent(CharacterListIntent.OnCharacterClick(id)) 
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
