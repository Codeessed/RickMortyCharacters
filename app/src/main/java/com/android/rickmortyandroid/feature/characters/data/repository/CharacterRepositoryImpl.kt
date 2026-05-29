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
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import com.android.rickmortyandroid.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApi,
    private val database: AppDatabase
) : CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(): Flow<PagingData<CharacterModel>> {
        val pagingSourceFactory = { database.characterDao().pagingSource() }

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            remoteMediator = CharacterRemoteMediator(
                api = api,
                database = database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }
}
