/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain

import androidx.paging.PagingData
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.VideoItemModel
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {
    fun getAllPlayListFlow(): Flow<List<PlayListItemModel>>

    /**
     * Return flow of audios of playList
     */
    fun getItemsOfPlayListFlow(
        playListId: Long,
        sort: List<SortOption.AudioOption>,
        wheres: List<GroupKey> = emptyList(),
    ): Flow<List<MediaItemModel>>

    fun getItemsPagingFlowOfPlayList(
        playListId: Long,
        sort: List<SortOption.AudioOption>,
        wheres: List<GroupKey> = emptyList(),
    ): Flow<PagingData<MediaItemModel>>

    /**
     * Return playList by playListId
     */
    suspend fun getPlayListById(playListId: Long): PlayListItemModel?

    /**
     * Return flow of playList by playListId
     */
    fun getPlayListFlowById(playListId: Long): Flow<PlayListItemModel?>

    /**
     * Add musics to playList
     *
     * return index of musics that already exist
     */
    suspend fun addItemsToPlayList(
        playListId: Long,
        items: List<MediaItemModel>,
    ): List<Long>

    /**
     * Return indexes of [items] which is duplicated in playList
     */
    suspend fun getDuplicatedMediaInPlayList(
        playListId: Long,
        items: List<MediaItemModel>,
    ): List<String>

    /**
     * Return flow of whether [mediaStoreId] is in favorite playList
     */
    fun isMediaInFavoritePlayListFlow(
        mediaStoreId: String,
        isAudio: Boolean,
    ): Flow<Boolean>

    /**
     * Toggle favorite media
     */
    suspend fun toggleFavoriteMedia(item: MediaItemModel)

    /**
     * Remove musics from playList
     */
    suspend fun removeMusicFromPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    )

    /**
     * Create new playList
     */
    suspend fun createNewPlayList(name: String): Long

    /**
     * Delete playList by playListId
     */
    suspend fun deletePlayList(playListId: Long)
}
