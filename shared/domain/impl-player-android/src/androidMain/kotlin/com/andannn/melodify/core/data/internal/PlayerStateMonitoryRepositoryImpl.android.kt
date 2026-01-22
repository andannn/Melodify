/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.player.ExoPlayerWrapper
import com.andannn.melodify.domain.PlayerStateMonitoryRepository
import com.andannn.melodify.domain.impl.toPlayerState
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.domain.model.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlayerStateMonitoryRepositoryImpl(
    private val playerWrapper: ExoPlayerWrapper,
) : PlayerStateMonitoryRepository {
    override fun getCurrentPositionMs(): Long = playerWrapper.currentPositionMs

    override fun observeCurrentDurationMs() =
        playerWrapper
            .observePlayerState()
            .map { playerWrapper.currentDurationMs }
            .distinctUntilChanged()

    override fun getPlayingIndexInQueue(): Int = playerWrapper.playingIndexInQueue

    override suspend fun getPlayListQueue(): List<MediaItemModel> =
        playerWrapper.playList.map {
            it.toAppItem()
        }

    override fun getPlayingMediaStateFlow() =
        playerWrapper.observePlayingMedia().map {
            it?.toAppItem()
        }

    override fun getPlayListQueueStateFlow(): Flow<List<MediaItemModel>> =
        playerWrapper.observePlayListQueue().map { items ->
            items.map {
                it.toAppItem()
            }
        }

    override fun getCurrentPlayMode(): PlayMode =
        playerWrapper.observePlayMode().value.let {
            fromRepeatMode(it)
        }

    override fun observeIsShuffle() = playerWrapper.observeIsShuffle()

    override fun observePlayMode() =
        playerWrapper
            .observePlayMode()
            .map {
                fromRepeatMode(it)
            }.distinctUntilChanged()

    override fun observePlayerState(): Flow<PlayerState> =
        playerWrapper
            .observePlayerState()
            .map {
                it.toPlayerState()
            }.distinctUntilChanged()

    override fun observeProgressFactor() =
        playerWrapper
            .observePlayerState()
            .map {
                val currentDurationMs = playerWrapper.currentDurationMs
                if (currentDurationMs == 0L) {
                    0f
                } else {
                    it.currentPositionMs
                        .toFloat()
                        .div(currentDurationMs)
                        .coerceIn(0f, 1f)
                }
            }.distinctUntilChanged()

    override fun observePlayBackEndEvent(): Flow<MediaItemModel> =
        playerWrapper
            .observePlayBackEndEvent()
            .map {
                it.toAppItem()
            }
}
