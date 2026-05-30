package com.android.rickmortyandroid.feature.characters.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests [CharacterFilter.isActive] logic.
 */
class CharacterFilterTest {

    @Test
    fun `default filter is not active`() {
        // GIVEN a filter with all defaults (empty strings)
        val filter = CharacterFilter()

        // THEN isActive should be false — we use RemoteMediator in this case
        assertFalse(filter.isActive)
    }

    @Test
    fun `filter with name is active`() {
        val filter = CharacterFilter(name = "Rick")
        assertTrue(filter.isActive)
    }

    @Test
    fun `filter with status is active`() {
        val filter = CharacterFilter(status = "alive")
        assertTrue(filter.isActive)
    }

    @Test
    fun `filter with species is active`() {
        val filter = CharacterFilter(species = "human")
        assertTrue(filter.isActive)
    }

    @Test
    fun `filter with only blank spaces is not active`() {
        // Edge case: user types spaces into search bar
        val filter = CharacterFilter(name = "   ")
        assertFalse(filter.isActive)
    }

    @Test
    fun `filter with multiple fields is active`() {
        val filter = CharacterFilter(name = "Morty", status = "alive", species = "human")
        assertTrue(filter.isActive)
    }
}
