/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.player.PlayerState
import com.andannn.melodify.core.player.VlcPlayer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

internal class PlayerStateMonitoryRepositoryImpl(
    private val vlcPlayer: VlcPlayer,
    private val libraryDao: MediaLibraryDao,
) : PlayerStateMonitoryRepository {
    override fun getCurrentPositionMs(): Long = vlcPlayer.currentPositionMs

    override fun observeCurrentPositionMs() =
        vlcPlayer
            .observeProgressFactor()
            .map { vlcPlayer.currentPositionMs }
            .distinctUntilChanged()

    override fun getPlayingIndexInQueue(): Int = vlcPlayer.playingIndexInQueue

    override suspend fun getPlayListQueue(): List<AudioItemModel> = getPlayListQueueStateFlow().first()

    override fun getPlayingMediaStateFlow() =
        vlcPlayer
            .observePlayingMediaMrl()
            .map { mrl ->
                if (mrl == null) {
                    return@map null
                }
                libraryDao
                    .getMediaByMediaIds(listOf(mrl.hashCode().toString()))
                    .firstOrNull()
                    ?.toAppItem()
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPlayListQueueStateFlow() =
        vlcPlayer
            .observePlayListQueue()
            .mapLatest { mrls ->
                val keys = mrls.map { it.hashCode().toString() }
                val idToEntityMap =
                    libraryDao.getMediaByMediaIds(keys).associateBy { it.id.toString() }
                keys.mapNotNull { idToEntityMap[it]?.toAppItem() }
            }

    override fun getCurrentPlayMode(): PlayMode = PlayMode.REPEAT_ALL

    override fun observeIsShuffle(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun observePlayMode() = flowOf(PlayMode.REPEAT_ALL)

    override fun observeIsPlaying(): Flow<Boolean> =
        vlcPlayer
            .observePlayerState()
            .map {
                it is PlayerState.Playing
            }.distinctUntilChanged()

    override fun observeProgressFactor(): Flow<Float> = vlcPlayer.observeProgressFactor()
}
