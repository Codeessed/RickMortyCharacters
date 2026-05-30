package com.android.rickmortyandroid.feature.characters.data.mapper

import com.android.rickmortyandroid.feature.characters.data.remote.CharacterDto
import com.android.rickmortyandroid.feature.characters.data.remote.EpisodeDto
import com.android.rickmortyandroid.feature.characters.data.remote.LocationDto
import com.android.rickmortyandroid.feature.characters.domain.model.EpisodeModel
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests all mapping functions in CharacterMapper.kt.
 */
class CharacterMapperTest {

    // ── Reusable test fixtures ──────────────────────────────────────────

    /** A fake CharacterDto mimicking what the Rick & Morty API returns */
    private fun fakeCharacterDto(id: Int = 1) = CharacterDto(
        id = id,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",              // API returns "" for most characters
        gender = "Male",
        origin = LocationDto(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
        location = LocationDto(name = "Citadel of Ricks", url = "https://rickandmortyapi.com/api/location/3"),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episode = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2",
            "https://rickandmortyapi.com/api/episode/3"
        ),
        url = "https://rickandmortyapi.com/api/character/1",
        created = "2017-11-04T18:48:46.250Z"
    )

    private fun fakeEpisodeDto() = EpisodeDto(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        episode = "S01E01",
        characters = listOf("https://rickandmortyapi.com/api/character/1"),
        url = "https://rickandmortyapi.com/api/episode/1",
        created = "2017-11-10T12:56:33.798Z"
    )

    // ── CharacterDto → CharacterEntity ──────────────────────────────────

    @Test
    fun `toEntity maps DTO fields to entity correctly`() {
        // GIVEN a CharacterDto from the API
        val dto = fakeCharacterDto()

        // WHEN we convert it to a Room entity (for local caching)
        val entity = dto.toEntity(page = 2)

        // THEN all fields should map correctly, and 'page' should be set
        assertEquals(1, entity.id)
        assertEquals("Rick Sanchez", entity.name)
        assertEquals("Alive", entity.status)
        assertEquals("Human", entity.species)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", entity.imageUrl)
        assertEquals(2, entity.page)  // page is metadata we track for pagination
    }

    // ── CharacterEntity → CharacterModel (domain) ───────────────────────

    @Test
    fun `toDomain maps entity to domain model`() {
        // GIVEN a CharacterEntity from Room
        val entity = fakeCharacterDto().toEntity(page = 1)

        // WHEN we convert it to a domain model (what the UI layer sees)
        val model = entity.toDomain()

        // THEN the domain model has only what the UI needs
        assertEquals(1, model.id)
        assertEquals("Rick Sanchez", model.name)
        assertEquals("Alive", model.status)
        assertEquals("Human", model.species)
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", model.imageUrl)
    }

    // ── CharacterDto → CharacterDetailModel ─────────────────────────────

    @Test
    fun `toDetailDomain maps all character info including episodes`() {
        // GIVEN a CharacterDto and a list of mapped episodes
        val dto = fakeCharacterDto()
        val episodes = listOf(
            EpisodeModel(id = 1, name = "Pilot", airDate = "December 2, 2013", episodeCode = "S01E01")
        )

        // WHEN we map to the detail model
        val detail = dto.toDetailDomain(episodes)

        // THEN all fields should be present
        assertEquals("Rick Sanchez", detail.name)
        assertEquals("Alive", detail.status)
        assertEquals("Human", detail.species)
        assertEquals("Male", detail.gender)
        assertEquals("Earth (C-137)", detail.origin)
        assertEquals("Citadel of Ricks", detail.location)
        assertEquals(1, detail.episodes.size)
        assertEquals(3, detail.totalEpisodeCount)  // total from the episode URL list
    }

    @Test
    fun `toDetailDomain replaces blank type with Unknown`() {
        // The API returns "" for type on most characters.
        // Our mapper should display "Unknown" instead of blank.
        val dto = fakeCharacterDto()  // type = ""
        val detail = dto.toDetailDomain(emptyList())

        assertEquals("Unknown", detail.type)
    }

    // ── EpisodeDto → EpisodeModel ───────────────────────────────────────

    @Test
    fun `episode toDomain maps correctly`() {
        val dto = fakeEpisodeDto()
        val model = dto.toDomain()

        assertEquals(1, model.id)
        assertEquals("Pilot", model.name)
        assertEquals("December 2, 2013", model.airDate)
        assertEquals("S01E01", model.episodeCode)
    }

    // ── extractEpisodeIds ───────────────────────────────────────────────

    @Test
    fun `extractEpisodeIds parses IDs from URLs`() {
        // GIVEN episode URLs from the API response
        val urls = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/28",
            "https://rickandmortyapi.com/api/episode/51"
        )

        // WHEN we extract the IDs
        val ids = extractEpisodeIds(urls)

        // THEN we get the numeric IDs in order
        assertEquals(listOf(1, 28, 51), ids)
    }

    @Test
    fun `extractEpisodeIds handles empty list`() {
        assertEquals(emptyList<Int>(), extractEpisodeIds(emptyList()))
    }

    @Test
    fun `extractEpisodeIds skips malformed URLs`() {
        // Edge case: if a URL doesn't end with a number, skip it
        val urls = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/",  // no ID
            "https://rickandmortyapi.com/api/episode/abc" // not a number
        )

        assertEquals(listOf(1), extractEpisodeIds(urls))
    }
}
