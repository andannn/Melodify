/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListEntryType
import com.andannn.melodify.core.database.entity.PlayListItemEntryEntity
import com.andannn.melodify.core.database.helper.paging.PagingProviderFactory
import com.andannn.melodify.core.database.model.PlayListWithMediaCount
import com.andannn.melodify.domain.PlayListRepository
import com.andannn.melodify.domain.impl.mapToAudioItemModel
import com.andannn.melodify.domain.impl.mapToMediaItemModel
import com.andannn.melodify.domain.impl.toAppItem
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.VideoItemModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class PlayListRepositoryImpl(
    private val playListDao: PlayListDao,
) : PlayListRepository {
    override fun getAllPlayListFlow(): Flow<List<PlayListItemModel>> =
        playListDao
            .getAllPlayListFlow()
            .map(::mapPlayListToAudioList)

    override fun getItemsOfPlayListFlow(
        playListId: Long,
        sort: List<SortOption.PlayListOption>,
        wheres: List<GroupKey>,
    ): Flow<List<MediaItemModel>> =
        PagingProviderFactory
            .playListPagingProvider(playListId)
            .getDataFlow(
                null,
                sort.toPlayListSortMethod(),
            ).map { it.mapToMediaItemModel() }

    override fun getItemsPagingFlowOfPlayList(
        playListId: Long,
        sort: List<SortOption.PlayListOption>,
        wheres: List<GroupKey>,
    ): Flow<PagingData<MediaItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                PagingProviderFactory
                    .playListPagingProvider(
                        playListId = playListId,
                    ).getPagingSource(
                        null,
                        sort.toPlayListSortMethod(),
                    )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override suspend fun getPlayListById(playListId: Long) = playListDao.getPlayListFlowById(playListId).first()?.toAppItem()

    override fun getPlayListFlowById(playListId: Long) =
        playListDao
            .getPlayListFlowById(playListId)
            .map {
                it?.toAppItem()
            }

    @OptIn(ExperimentalTime::class)
    override suspend fun addItemsToPlayList(
        playListId: Long,
        items: List<MediaItemModel>,
    ): List<Long> {
        val insertedIndexList =
            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    items.map { item ->
                        PlayListItemEntryEntity(
                            playListId = playListId,
                            audioId = item.id.takeIf { item is AudioItemModel },
                            videoId = item.id.takeIf { item is VideoItemModel },
                            addedDate = Clock.System.now().toEpochMilliseconds(),
                        )
                    },
            )

        return insertedIndexList
            .mapIndexed { index, insertedIndex ->
                if (insertedIndex == -1L) index.toLong() else null
            }.filterNotNull()
            .toList()
    }

    override suspend fun getDuplicatedMediaInPlayList(
        playListId: Long,
        items: List<MediaItemModel>,
    ): List<Long> =
        items
            .filter {
                playListDao
                    .getIsMediaInPlayListFlow(
                        playList = playListId,
                        entryType = if (it is AudioItemModel) PlayListEntryType.AUDIO else PlayListEntryType.VIDEO,
                        mediaId = it.id,
                    ).first()
            }.map { it.id }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun isMediaInFavoritePlayListFlow(item: MediaItemModel) =
        playListDao.getFavoritePlayListFlow().flatMapLatest { favoriteOrNull ->
            if (favoriteOrNull == null) {
                flow { emit(false) }
            } else {
                playListDao.getIsMediaInPlayListFlow(
                    favoriteOrNull.id,
                    if (item is AudioItemModel) PlayListEntryType.AUDIO else PlayListEntryType.VIDEO,
                    item.id,
                )
            }
        }

    override suspend fun toggleFavoriteMedia(item: MediaItemModel) {
        val favoritePlayListOrNull = playListDao.getFavoritePlayListFlow().first()

        if (favoritePlayListOrNull == null) {
            val newId = createFavoritePlayList()
            addItemsToPlayList(newId, listOf(item))
        } else {
            val isFavorite =
                playListDao
                    .getIsMediaInPlayListFlow(
                        favoritePlayListOrNull.id,
                        if (item is AudioItemModel) PlayListEntryType.AUDIO else PlayListEntryType.VIDEO,
                        item.id,
                    ).first()
            if (isFavorite) {
                removeMusicFromPlayList(favoritePlayListOrNull.id, listOf(item))
            } else {
                addItemsToPlayList(favoritePlayListOrNull.id, listOf(item))
            }
        }
    }

    override suspend fun removeMusicFromPlayList(
        playListId: Long,
        mediaList: List<MediaItemModel>,
    ) = playListDao.deleteMediasFromPlayList(
        playListId,
        mediaList.map {
            it.id to if (it is AudioItemModel) PlayListEntryType.AUDIO else PlayListEntryType.VIDEO
        },
    )

    @OptIn(ExperimentalTime::class)
    override suspend fun createNewPlayList(name: String): Long {
        val ids =
            playListDao.insertPlayListEntities(
                listOf(
                    PlayListEntity(
                        name = name,
                        createdDate = Clock.System.now().toEpochMilliseconds(),
                        artworkUri = null,
                    ),
                ),
            )
        return ids.first()
    }

    override suspend fun deletePlayList(playListId: Long) {
        playListDao.deletePlayListById(playListId)
    }

    private fun mapPlayListToAudioList(list: List<PlayListWithMediaCount>) = list.map { it.toAppItem() }

    @OptIn(ExperimentalTime::class)
    private suspend fun createFavoritePlayList(): Long =
        playListDao
            .insertPlayListEntities(
                listOf(
                    PlayListEntity(
                        name = "Favorite",
                        createdDate = Clock.System.now().toEpochMilliseconds(),
                        artworkUri = null,
                        isFavoritePlayList = true,
                    ),
                ),
            ).first()
}
