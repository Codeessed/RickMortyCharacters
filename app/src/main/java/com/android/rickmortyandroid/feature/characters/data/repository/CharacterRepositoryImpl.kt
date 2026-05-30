package com.android.rickmortyandroid.feature.characters.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.android.rickmortyandroid.core.data.db.AppDatabase
import com.android.rickmortyandroid.feature.characters.data.mapper.toDomain
import com.android.rickmortyandroid.feature.characters.data.mediator.CharacterRemoteMediator
import com.android.rickmortyandroid.feature.characters.data.remote.CharacterApi
import com.android.rickmortyandroid.feature.characters.data.remote.CharacterSearchPagingSource
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterFilter
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import com.android.rickmortyandroid.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApi,
    private val database: AppDatabase
) : CharacterRepository {

    private companion object {
        val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            prefetchDistance = 10,
            enablePlaceholders = false
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(filter: CharacterFilter): Flow<PagingData<CharacterModel>> {
        // When filters are active, use a network-only PagingSource
        // (search results aren't cached — you need the network to search)
        if (filter.isActive) {
            return Pager(
                config = PAGING_CONFIG,
                pagingSourceFactory = {
                    CharacterSearchPagingSource(api = api, filter = filter)
                }
            ).flow
        }

        // No filters — use offline-first RemoteMediator backed by Room
        return Pager(
            config = PAGING_CONFIG,
            remoteMediator = CharacterRemoteMediator(
                api = api,
                database = database
            ),
            pagingSourceFactory = { database.characterDao().pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }
}
