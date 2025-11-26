/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

import androidx.paging.PagingData
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.database.dao.PlayListDao
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {
    /**
     * Return flow of all playLists
     */
    fun getAllPlayListFlow(): Flow<List<PlayListItemModel>>

    /**
     * Return flow of audios of playList
     */
    fun getAudiosOfPlayListFlow(
        playListId: Long,
        sort: List<SortOption.AudioOption>,
        wheres: List<GroupKey> = emptyList(),
    ): Flow<List<AudioItemModel>>

    fun getAudioPagingFlowOfPlayList(
        playListId: Long,
        sort: List<SortOption.AudioOption>,
        wheres: List<GroupKey> = emptyList(),
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return audios of playList
     */
    suspend fun getAudiosOfPlayList(playListId: Long): List<AudioItemModel>

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
     * Return indexes of [musics] which is duplicated in playList
     */
    suspend fun getDuplicatedMediaInPlayList(
        playListId: Long,
        musics: List<AudioItemModel>,
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
    suspend fun toggleFavoriteMedia(audio: MediaItemModel)

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
