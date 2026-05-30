package com.android.rickmortyandroid.feature.characters.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.rickmortyandroid.feature.characters.data.mapper.toDomain
import com.android.rickmortyandroid.feature.characters.data.mapper.toEntity
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterFilter
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import retrofit2.HttpException
import java.io.IOException

class CharacterSearchPagingSource(
    private val api: CharacterApi,
    private val filter: CharacterFilter
) : PagingSource<Int, CharacterModel>() {

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val page = params.key ?: 1

        return try {
            val response = api.getCharacters(
                page = page,
                name = filter.name.ifBlank { null },
                status = filter.status.ifBlank { null },
                species = filter.species.ifBlank { null }
            )

            val characters = response.results.map { dto ->
                dto.toEntity(page).toDomain()
            }

            LoadResult.Page(
                data = characters,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.info.next == null) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            // Rick & Morty API returns 404 when no results match the filter
            if (e.code() == 404) {
                LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            } else {
                LoadResult.Error(e)
            }
        }
    }
}
