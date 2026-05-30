package com.android.rickmortyandroid.feature.characters.data.mapper

import com.android.rickmortyandroid.feature.characters.data.local.CharacterEntity
import com.android.rickmortyandroid.feature.characters.data.remote.CharacterDto
import com.android.rickmortyandroid.feature.characters.data.remote.EpisodeDto
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import com.android.rickmortyandroid.feature.characters.domain.model.EpisodeModel

fun CharacterDto.toEntity(page: Int): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        imageUrl = image,
        page = page
    )
}

fun CharacterEntity.toDomain(): CharacterModel {
    return CharacterModel(
        id = id,
        name = name,
        status = status,
        species = species,
        imageUrl = imageUrl
    )
}

fun CharacterDto.toDetailDomain(episodes: List<EpisodeModel>): CharacterDetailModel {
    return CharacterDetailModel(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type.ifBlank { "Unknown" },
        gender = gender,
        origin = origin.name,
        location = location.name,
        imageUrl = image,
        episodes = episodes,
        totalEpisodeCount = episode.size
    )
}

fun EpisodeDto.toDomain(): EpisodeModel {
    return EpisodeModel(
        id = id,
        name = name,
        airDate = airDate,
        episodeCode = episode
    )
}

/**
 * Extracts episode IDs from episode URLs.
 * e.g. "https://rickandmortyapi.com/api/episode/28" → 28
 */
fun extractEpisodeIds(episodeUrls: List<String>): List<Int> {
    return episodeUrls.mapNotNull { url ->
        url.substringAfterLast("/").toIntOrNull()
    }
}
