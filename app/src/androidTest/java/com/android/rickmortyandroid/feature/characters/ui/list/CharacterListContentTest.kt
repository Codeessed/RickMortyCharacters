package com.android.rickmortyandroid.feature.characters.ui.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import com.android.rickmortyandroid.ui.theme.RickMortyAndroidTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI test for [CharacterListContent].
 */
class CharacterListContentTest {

    // This rule provides the Compose test environment
    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Helpers ─────────────────────────────────────────────────────────

    private fun fakeCharacter(id: Int, name: String) = CharacterModel(
        id = id,
        name = name,
        status = "Alive",
        species = "Human",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg"
    )

    // ── Tests ───────────────────────────────────────────────────────────

    @Test
    fun characterListContent_displaysCharacterNames() {
        // GIVEN a list of characters wrapped in PagingData
        val characters = listOf(
            fakeCharacter(1, "Rick Sanchez"),
            fakeCharacter(2, "Morty Smith")
        )

        // Create a Flow of PagingData (simulating what the ViewModel provides)
        val pagingDataFlow = MutableStateFlow(PagingData.from(characters))

        // WHEN we render the composable
        composeTestRule.setContent {
            RickMortyAndroidTheme {
                val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
                CharacterListContent(
                    characters = lazyPagingItems,
                    onCharacterClick = {}
                )
            }
        }

        // THEN character names should be visible on screen
        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morty Smith").assertIsDisplayed()
    }

    @Test
    fun characterListContent_showsEmptyMessageWhenNoCharacters() {
        // GIVEN an empty PagingData
        val pagingDataFlow = MutableStateFlow(PagingData.from(emptyList<CharacterModel>()))

        // WHEN we render the composable
        composeTestRule.setContent {
            RickMortyAndroidTheme {
                val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
                CharacterListContent(
                    characters = lazyPagingItems,
                    onCharacterClick = {}
                )
            }
        }

        // THEN the empty state message should be displayed
        composeTestRule.onNodeWithText("No characters found").assertIsDisplayed()
    }
}
