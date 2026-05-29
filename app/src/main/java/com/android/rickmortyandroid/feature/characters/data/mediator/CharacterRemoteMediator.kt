package com.android.rickmortyandroid.feature.characters.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.android.rickmortyandroid.core.data.db.AppDatabase
import com.android.rickmortyandroid.feature.characters.data.local.CharacterEntity
import com.android.rickmortyandroid.feature.characters.data.local.CharacterRemoteKeys
import com.android.rickmortyandroid.feature.characters.data.mapper.toEntity
import com.android.rickmortyandroid.feature.characters.data.remote.CharacterApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val api: CharacterApi,
    private val database: AppDatabase
) : RemoteMediator<Int, CharacterEntity>() {

    override suspend fun initialize(): InitializeAction {
        return if (database.characterDao().getCount() > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val response = api.getCharacters(page = page)
            val characters = response.results
            val endOfPaginationReached = characters.isEmpty() || response.info.next == null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.characterRemoteKeysDao().clearAll()
                    database.characterDao().clearAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = characters.map {
                    CharacterRemoteKeys(id = it.id, prevPage = prevKey, nextPage = nextKey)
                }

                database.characterRemoteKeysDao().insertAll(keys)
                database.characterDao().insertAll(characters.map { it.toEntity(page) })
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CharacterEntity>): CharacterRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { character ->
                database.characterRemoteKeysDao().remoteKeyByCharacterId(character.id)
            }
    }
}
