package com.android.rickmortyandroid.feature.characters.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.android.rickmortyandroid.core.ui.components.CharacterCard
import com.android.rickmortyandroid.core.ui.components.EmptyView
import com.android.rickmortyandroid.core.ui.components.ErrorView
import com.android.rickmortyandroid.core.ui.components.PagingFooter
import com.android.rickmortyandroid.core.ui.components.ShimmerCard
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel

@Composable
fun CharacterListContent(
    characters: LazyPagingItems<CharacterModel>,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshState = characters.loadState.refresh

    when {
        refreshState is LoadState.Loading && characters.itemCount == 0 -> {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                modifier = modifier.fillMaxSize()
            ) {
                items(6) {
                    ShimmerCard()
                }
            }
        }

        refreshState is LoadState.Error && characters.itemCount == 0 -> {
            ErrorView(
                message = refreshState.error.localizedMessage ?: "Unknown error",
                onRetry = { characters.retry() },
                modifier = modifier
            )
        }

        refreshState is LoadState.NotLoading && characters.itemCount == 0 -> {
            EmptyView(
                message = "No characters found",
                modifier = modifier
            )
        }

        else -> {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                modifier = modifier.fillMaxSize()
            ) {
                items(
                    count = characters.itemCount,
                    key = { index -> characters.peek(index)?.id ?: index }
                ) { index ->
                    characters[index]?.let { character ->
                        CharacterCard(character = character)
                    }
                }
                
                item(span = StaggeredGridItemSpan.FullLine) {
                    PagingFooter(
                        isLoading = characters.loadState.append is LoadState.Loading,
                        isError = characters.loadState.append is LoadState.Error,
                        onRetry = { characters.retry() }
                    )
                }
            }
        }
    }
}
