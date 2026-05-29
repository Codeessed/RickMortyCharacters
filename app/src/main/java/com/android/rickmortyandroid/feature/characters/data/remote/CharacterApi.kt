package com.android.rickmortyandroid.feature.characters.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharacterResponseDto
}
