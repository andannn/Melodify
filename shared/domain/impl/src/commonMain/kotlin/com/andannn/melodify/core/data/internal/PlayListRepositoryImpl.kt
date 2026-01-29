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
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.model.PlayListWithMediaCount
import com.andannn.melodify.domain.PlayListRepository
import com.andannn.melodify.domain.impl.mapToAppItem
import com.andannn.melodify.domain.impl.toAppItem
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.VideoItemModel
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
        sort: List<SortOption.AudioOption>,
        wheres: List<GroupKey>,
    ): Flow<List<MediaItemModel>> {
        TODO("Not yet implemented")
    }

    override fun getItemsPagingFlowOfPlayList(
        playListId: Long,
        sort: List<SortOption.AudioOption>,
        wheres: List<GroupKey>,
    ): Flow<PagingData<MediaItemModel>> {
        TODO("Not yet implemented")
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
        val playListEntity = playListDao.getPlayListEntity(playListId) ?: return emptyList()

        val videos = items.filterIsInstance<VideoItemModel>()
        val musics = items.filterIsInstance<AudioItemModel>()

        val insertedIndexList =
            if (playListEntity.isAudioPlayList == true) {
                if (videos.isNotEmpty()) error("insert videos to audio playlist")
                playListDao.insertPlayListWithMediaCrossRef(
                    crossRefs =
                        musics.map {
                            PlayListWithMediaCrossRef(
                                playListId = playListId,
                                mediaStoreId = it.id,
                                artist = it.artist,
                                title = it.name,
                                addedDate = Clock.System.now().toEpochMilliseconds(),
                            )
                        },
                )
            } else {
                if (musics.isNotEmpty()) error("insert musics to video playlist")
                playListDao.insertPlayListWithMediaCrossRef(
                    crossRefs =
                        videos.map {
                            PlayListWithMediaCrossRef(
                                playListId = playListId,
                                mediaStoreId = it.id,
                                artist = "",
                                title = it.name,
                                addedDate = Clock.System.now().toEpochMilliseconds(),
                            )
                        },
                )
            }

        return insertedIndexList
            .mapIndexed { index, insertedIndex ->
                if (insertedIndex == -1L) index.toLong() else null
            }.filterNotNull()
            .toList()
    }

    override suspend fun getDuplicatedMediaInPlayList(
        playListId: Long,
        items: List<MediaItemModel>,
    ): List<String> = playListDao.getDuplicateMediaInPlayList(playListId, items.map { it.id })

    override fun isMediaInFavoritePlayListFlow(
        mediaStoreId: String,
        isAudio: Boolean,
    ) = playListDao.getFavoritePlayListFlow(isAudio).flatMapLatest { favoriteOrNull ->
        if (favoriteOrNull == null) {
            flow { emit(false) }
        } else {
            playListDao.getIsMediaInPlayListFlow(
                favoriteOrNull.id.toString(),
                mediaStoreId,
            )
        }
    }

    override suspend fun toggleFavoriteMedia(item: MediaItemModel) {
        val isAudio = item is AudioItemModel
        val favoritePlayListOrNull = playListDao.getFavoritePlayListFlow(isAudio = isAudio).first()

        if (favoritePlayListOrNull == null) {
            val newId = createFavoritePlayList(isAudio = isAudio)
            addItemsToPlayList(newId, listOf(item))
        } else {
            val isFavorite =
                playListDao
                    .getIsMediaInPlayListFlow(
                        favoritePlayListOrNull.id.toString(),
                        item.id,
                    ).first()
            if (isFavorite) {
                removeMusicFromPlayList(favoritePlayListOrNull.id, listOf(item.id))
            } else {
                addItemsToPlayList(favoritePlayListOrNull.id, listOf(item))
            }
        }
    }

    override suspend fun removeMusicFromPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    ) = playListDao.deleteMediaFromPlayList(playListId, mediaIdList)

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
    private suspend fun createFavoritePlayList(isAudio: Boolean): Long =
        playListDao
            .insertPlayListEntities(
                listOf(
                    PlayListEntity(
                        name = if (isAudio) "Favorite music" else "Favorite video",
                        createdDate = Clock.System.now().toEpochMilliseconds(),
                        artworkUri = null,
                        isFavoritePlayList = true,
                        isAudioPlayList = isAudio,
                    ),
                ),
            ).first()
}
