/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class NoOpPlayListRepository : PlayListRepository {
    override fun getAllPlayListFlow(): Flow<List<PlayListItemModel>> = flowOf()

    override fun getAudiosOfPlayListFlow(playListId: Long): Flow<List<AudioItemModel>> = flowOf()

    override suspend fun getAudiosOfPlayList(playListId: Long): List<AudioItemModel> = emptyList()

    override suspend fun getPlayListById(playListId: Long): PlayListItemModel? = null

    override suspend fun getPlayListFlowById(playListId: Long): Flow<PlayListItemModel?> = flowOf()

    override suspend fun addMusicToPlayList(
        playListId: Long,
        musics: List<AudioItemModel>,
    ): List<Long> = emptyList()

    override suspend fun getDuplicatedMediaInPlayList(
        playListId: Long,
        musics: List<AudioItemModel>,
    ): List<String> = emptyList()

    override fun isMediaInFavoritePlayListFlow(mediaStoreId: String): Flow<Boolean> = flowOf()

    override suspend fun toggleFavoriteMedia(audio: AudioItemModel) {}

    override suspend fun removeMusicFromPlayList(
        playListId: Long,
        mediaIdList: List<String>,
    ) {}

    override suspend fun createNewPlayList(name: String): Long = 0L

    override suspend fun deletePlayList(playListId: Long) {}
}
