package com.android.rickmortyandroid.feature.characters.domain.model

data class CharacterFilter(
    val name: String = "",
    val status: String = "",
    val species: String = ""
) {
    val isActive: Boolean
        get() = name.isNotBlank() || status.isNotBlank() || species.isNotBlank()
}
