package com.android.rickmortyandroid.feature.characters.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.android.rickmortyandroid.core.data.db.AppDatabase
import com.android.rickmortyandroid.feature.characters.data.local.CharacterDetailDao
import com.android.rickmortyandroid.feature.characters.data.mapper.extractEpisodeIds
import com.android.rickmortyandroid.feature.characters.data.mapper.toDomain
import com.android.rickmortyandroid.feature.characters.data.mapper.toDetailDomain
import com.android.rickmortyandroid.feature.characters.data.mapper.toDetailEntity
import com.android.rickmortyandroid.feature.characters.data.mapper.toEpisodeEntity
import com.android.rickmortyandroid.feature.characters.data.mediator.CharacterRemoteMediator
import com.android.rickmortyandroid.feature.characters.data.remote.CharacterApi
import com.android.rickmortyandroid.feature.characters.data.remote.CharacterSearchPagingSource
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterDetailModel
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterFilter
import com.android.rickmortyandroid.feature.characters.domain.model.CharacterModel
import com.android.rickmortyandroid.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApi,
    private val database: AppDatabase,
    private val characterDetailDao: CharacterDetailDao
) : CharacterRepository {

    private companion object {
        val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            prefetchDistance = 10,
            enablePlaceholders = false
        )

        /** Cache is considered stale after 1 hour (in milliseconds). */
        const val CACHE_TTL_MS = 60 * 60 * 1_000L
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

    /**
     * Stale-while-revalidate strategy:
     * 1. Emit cached data immediately (if available).
     * 2. If cache is missing or older than [CACHE_TTL_MS], fetch fresh data from the network.
     * 3. Persist and re-emit the fresh data.
     * 4. If the network call fails and we already emitted cached data, swallow the error silently
     *    so the UI is not disrupted. If there is no cache at all, emit the failure.
     */
    override fun getCharacterDetail(characterId: Int): Flow<Result<CharacterDetailModel>> = flow {
        val cachedDetail = characterDetailDao.getCharacterDetail(characterId)
        val cachedEpisodes = if (cachedDetail != null) {
            characterDetailDao.getEpisodes(characterId)
        } else {
            emptyList()
        }

        val hasCachedData = cachedDetail != null
        if (hasCachedData) {
            val domainEpisodes = cachedEpisodes.map { it.toDomain() }
            emit(Result.success(cachedDetail!!.toDetailDomain(domainEpisodes)))
        }

        val isStale = cachedDetail == null ||
            (System.currentTimeMillis() - cachedDetail.cachedAt) > CACHE_TTL_MS

        if (isStale) {
            try {
                val fresh = fetchAndPersist(characterId)
                emit(Result.success(fresh))
            } catch (e: Exception) {
                if (!hasCachedData) {
                    // Nothing in cache — surface the error to the UI
                    emit(Result.failure(e))
                }
                // If we already served cached data, swallow the error silently
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private suspend fun fetchAndPersist(characterId: Int): CharacterDetailModel {
        val characterDto = api.getCharacterById(characterId)
        val now = System.currentTimeMillis()

        val episodeIds = extractEpisodeIds(characterDto.episode).take(3)
        val episodeDtos = when {
            episodeIds.isEmpty() -> emptyList()
            episodeIds.size == 1 -> listOf(api.getEpisode(episodeIds.first()))
            else -> api.getEpisodes(episodeIds.joinToString(","))
        }

        val detailEntity = characterDto.toDetailEntity(cachedAt = now)
        val episodeEntities = episodeDtos.map { it.toEpisodeEntity(characterId) }

        characterDetailDao.upsertDetailWithEpisodes(detailEntity, episodeEntities)

        val domainEpisodes = episodeDtos.map { it.toDomain() }
        return characterDto.toDetailDomain(domainEpisodes)
    }
}
