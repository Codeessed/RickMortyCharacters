package com.android.rickmortyandroid.feature.characters.data.mapper

import com.android.rickmortyandroid.feature.characters.data.local.CharacterEntity
import com.android.rickmortyandroid.feature.characters.data.remote.CharacterDto
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel

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
