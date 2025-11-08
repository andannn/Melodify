/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import androidx.paging.Pager
import androidx.paging.map
import com.andannn.melodify.core.data.PlayListRepository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.toSortMethod
import com.andannn.melodify.core.data.model.toWheresMethod
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCount
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

internal class PlayListRepositoryImpl(
    private val playListDao: PlayListDao,
) : PlayListRepository {
    override fun getAllPlayListFlow(): Flow<List<PlayListItemModel>> =
        playListDao
            .getAllPlayListFlow()
            .map(::mapPlayListToAudioList)

    override suspend fun getPlayListById(playListId: Long) =
        playListDao.getPlayList(playListId)?.let {
            PlayListItemModel(
                id = it.playList.id.toString(),
                name = it.playList.name,
                artWorkUri = it.playList.artworkUri ?: "",
                trackCount = it.medias.size,
            )
        }

    override fun getPlayListFlowById(playListId: Long) =
        playListDao
            .getPlayListFlow(playListId)
            .map {
                if (it == null) return@map null
                PlayListItemModel(
                    id = it.playList.id.toString(),
                    name = it.playList.name,
                    artWorkUri = it.playList.artworkUri ?: "",
                    trackCount = it.medias.size,
                )
            }

    override suspend fun addMusicToPlayList(
        playListId: Long,
        musics: List<AudioItemModel>,
    ): List<Long> {
        val insertedIndexList =
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

        return insertedIndexList
            .mapIndexed { index, insertedIndex ->
                if (insertedIndex == -1L) index.toLong() else null
            }.filterNotNull()
            .toList()
    }

    override suspend fun getDuplicatedMediaInPlayList(
        playListId: Long,
        musics: List<AudioItemModel>,
    ): List<String> = playListDao.getDuplicateMediaInPlayList(playListId, musics.map { it.id })

    override fun isMediaInFavoritePlayListFlow(mediaStoreId: String) =
        playListDao.getIsMediaInPlayListFlow(
            PlayListDao.FAVORITE_PLAY_LIST_ID.toString(),
            mediaStoreId,
        )

    override suspend fun toggleFavoriteMedia(audio: AudioItemModel) {
        val isFavorite =
            playListDao
                .getIsMediaInPlayListFlow(
                    PlayListDao.FAVORITE_PLAY_LIST_ID.toString(),
                    audio.id,
                ).first()
        if (isFavorite) {
            removeMusicFromFavoritePlayList(listOf(audio.id))
        } else {
            addMusicToFavoritePlayList(listOf(audio))
        }
    }

    override suspend fun removeMusicFromPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    ) = playListDao.deleteMediaFromPlayList(playListId, mediaIdList)

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

    override fun getAudiosOfPlayListFlow(
        playListId: Long,
        sort: List<SortOption>,
        wheres: List<GroupKey>,
    ) = playListDao
        .getMediasInPlayListFlow(playListId, wheres.toWheresMethod(), sort.toSortMethod())
        .map { it.map { it.mapToAppItem() } }

    override fun getAudioPagingFlowOfPlayList(
        playListId: Long,
        sort: List<SortOption>,
        wheres: List<GroupKey>,
    ) = Pager(
        config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
        pagingSourceFactory = {
            playListDao.getMediaPagingSourceInPlayList(
                playListId = playListId,
                wheres = wheres.toWheresMethod(),
                mediaSorts = sort.toSortMethod(),
            )
        },
    ).flow.map { pagingData ->
        pagingData.map { it.mapToAppItem() }
    }

    override suspend fun getAudiosOfPlayList(playListId: Long) =
        playListDao
            .getMediasInPlayList(playListId)
            .map { it.mapToAppItem() }

    private fun mapPlayListToAudioList(list: List<PlayListWithMediaCount>) = list.map { it.toAppItem() }
}
