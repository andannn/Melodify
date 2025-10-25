/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GroupSort
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.database.dao.PlayListDao
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {
    companion object {
        const val FAVORITE_PLAY_LIST_ID = PlayListDao.FAVORITE_PLAY_LIST_ID
    }

    /**
     * Return flow of all playLists
     */
    fun getAllPlayListFlow(): Flow<List<PlayListItemModel>>

    /**
     * Return flow of audios of playList
     */
    fun getAudiosOfPlayListFlow(playListId: Long, sort: GroupSort): Flow<List<AudioItemModel>>

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
    suspend fun getPlayListFlowById(playListId: Long): Flow<PlayListItemModel?>

    /**
     * Add musics to favorite playList
     */
    suspend fun addMusicToFavoritePlayList(musics: List<AudioItemModel>) = addMusicToPlayList(PlayListDao.FAVORITE_PLAY_LIST_ID, musics)

    /**
     * Add musics to playList
     *
     * return index of musics that already exist
     */
    suspend fun addMusicToPlayList(
        playListId: Long,
        musics: List<AudioItemModel>,
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
    fun isMediaInFavoritePlayListFlow(mediaStoreId: String): Flow<Boolean>

    /**
     * Toggle favorite media
     */
    suspend fun toggleFavoriteMedia(audio: AudioItemModel)

    /**
     * Remove musics from favorite playList
     */
    suspend fun removeMusicFromFavoritePlayList(mediaIdList: List<String>) =
        removeMusicFromPlayList(PlayListDao.FAVORITE_PLAY_LIST_ID, mediaIdList)

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
