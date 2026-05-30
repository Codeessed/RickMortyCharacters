package com.android.rickmortyandroid.feature.characters.domain.usecase

import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel
import com.android.rickmortyandroid.feature.characters.domain.repository.CharacterRepository
import io.mockk.coEvery      // coEvery = mock a suspend function
import io.mockk.coVerify     // coVerify = verify a suspend function was called
import io.mockk.mockk        // mockk = create a mock object
import kotlinx.coroutines.test.runTest  // runTest = run suspend code in a test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests [GetCharacterDetailUseCase].
 */
class GetCharacterDetailUseCaseTest {

    // Create a mock of CharacterRepository — no real DB or API calls
    private val repository = mockk<CharacterRepository>()

    // The class under test, injected with the mock
    private val useCase = GetCharacterDetailUseCase(repository)

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // GIVEN the repository returns a successful result
        val expectedDetail = CharacterDetailModel(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "Unknown",
            gender = "Male",
            origin = "Earth (C-137)",
            location = "Citadel of Ricks",
            imageUrl = "https://example.com/rick.jpg",
            episodes = emptyList(),
            totalEpisodeCount = 51
        )
        // coEvery: "when someone calls getCharacterDetail(1), return this"
        coEvery { repository.getCharacterDetail(1) } returns Result.success(expectedDetail)

        // WHEN we invoke the use case
        val result = useCase(characterId = 1)

        // THEN it should forward the success result
        assertTrue(result.isSuccess)
        assertEquals("Rick Sanchez", result.getOrNull()?.name)

        // AND it should have called the repository exactly once with ID 1
        coVerify(exactly = 1) { repository.getCharacterDetail(1) }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // GIVEN the repository returns a failure
        val exception = RuntimeException("Network error")
        coEvery { repository.getCharacterDetail(99) } returns Result.failure(exception)

        // WHEN we invoke the use case
        val result = useCase(characterId = 99)

        // THEN it should forward the failure
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
