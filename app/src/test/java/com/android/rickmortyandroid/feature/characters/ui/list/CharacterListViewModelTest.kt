package com.android.rickmortyandroid.feature.characters.ui.list

import app.cash.turbine.test          // Turbine's test {} block for Flow assertions
import com.android.rickmortyandroid.core.data.util.NetworkMonitor
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterFilter
import com.android.rickmortyandroid.feature.characters.domain.usecase.GetCharactersUseCase
import io.mockk.every                 // every = mock a non-suspend function
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests [CharacterListViewModel] intent handling and state management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {

    // This replaces Dispatchers.Main in all coroutines launched by the ViewModel
    private val testDispatcher = StandardTestDispatcher()

    // A controllable Flow that simulates NetworkMonitor.isOnline
    private val networkFlow = MutableStateFlow(true)

    // Mocks
    private val getCharactersUseCase = mockk<GetCharactersUseCase>()
    private val networkMonitor = mockk<NetworkMonitor>()

    @Before
    fun setup() {
        // Swap Dispatchers.Main with our test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Tell mocks what to return:
        // - UseCase returns an empty PagingData flow (we're testing state, not data)
        every { getCharactersUseCase(any()) } returns emptyFlow()
        // - NetworkMonitor exposes our controllable flow
        every { networkMonitor.isOnline } returns networkFlow
    }

    @After
    fun tearDown() {
        // Always reset Main dispatcher to avoid leaking state between tests
        Dispatchers.resetMain()
    }

    // Helper to create a fresh ViewModel for each test
    private fun createViewModel() = CharacterListViewModel(
        getCharactersUseCase = getCharactersUseCase,
        networkMonitor = networkMonitor
    )

    // ── Search query tests ──────────────────────────────────────────────

    @Test
    fun `search query intent updates UI state immediately`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()  // Let init{} complete

        // WHEN user types "Rick"
        viewModel.onIntent(CharacterListIntent.OnSearchQueryChange("Rick"))
        advanceUntilIdle()

        // THEN the UI state reflects the search query
        val state = viewModel.uiState.value
        assertEquals("Rick", state.searchQuery)
        assertEquals("Rick", state.filter.name)
        assertTrue(state.isSearchActive)
    }

    @Test
    fun `clearing search resets filter state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // GIVEN user has searched
        viewModel.onIntent(CharacterListIntent.OnSearchQueryChange("Rick"))
        advanceUntilIdle()

        // WHEN user clears all filters
        viewModel.onIntent(CharacterListIntent.ClearFilters)
        advanceUntilIdle()

        // THEN everything is reset
        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)
        assertEquals(CharacterFilter(), state.filter)
    }

    // ── Status filter tests ─────────────────────────────────────────────

    @Test
    fun `status filter toggles on and off`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // WHEN user taps "alive" filter
        viewModel.onIntent(CharacterListIntent.OnStatusFilterChange("alive"))
        advanceUntilIdle()
        assertEquals("alive", viewModel.uiState.value.filter.status)

        // WHEN user taps "alive" again (toggle off)
        viewModel.onIntent(CharacterListIntent.OnStatusFilterChange("alive"))
        advanceUntilIdle()
        assertEquals("", viewModel.uiState.value.filter.status)
    }

    @Test
    fun `switching status replaces previous selection`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(CharacterListIntent.OnStatusFilterChange("alive"))
        advanceUntilIdle()

        // WHEN user taps "dead" (different status)
        viewModel.onIntent(CharacterListIntent.OnStatusFilterChange("dead"))
        advanceUntilIdle()

        // THEN the old status is replaced, not appended
        assertEquals("dead", viewModel.uiState.value.filter.status)
    }

    // ── Network status tests ────────────────────────────────────────────

    @Test
    fun `network status is reflected in UI state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Initial state: online
        assertTrue(viewModel.uiState.value.isOnline)

        // WHEN network goes offline
        networkFlow.value = false
        advanceUntilIdle()

        // THEN UI state updates
        assertFalse(viewModel.uiState.value.isOnline)
    }

    // ── Effect tests ────────────────────────────────────────────────────

    @Test
    fun `character click emits navigation effect`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Turbine: collect effects and assert them
        viewModel.effect.test {
            // WHEN user taps a character
            viewModel.onIntent(CharacterListIntent.OnCharacterClick(42))

            // THEN a navigation effect is emitted with the character ID
            val effect = awaitItem()
            assertTrue(effect is CharacterListEffect.NavigateToCharacterDetail)
            assertEquals(42, (effect as CharacterListEffect.NavigateToCharacterDetail).characterId)

            // Cancel collection (Turbine requires this)
            cancelAndConsumeRemainingEvents()
        }
    }
}
