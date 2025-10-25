/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.player.PlayerState
import com.andannn.melodify.core.player.PlayerWrapper
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlayerStateMonitoryRepositoryImpl(
    private val playerWrapper: PlayerWrapper,
) : PlayerStateMonitoryRepository {
    override fun getCurrentPositionMs(): Long = playerWrapper.currentPositionMs

    override fun observeCurrentPositionMs() =
        playerWrapper
            .observePlayerState()
            .map { playerWrapper.currentDurationMs }
            .distinctUntilChanged()

    override fun getPlayingIndexInQueue(): Int = playerWrapper.playingIndexInQueue

    override suspend fun getPlayListQueue(): List<AudioItemModel> =
        playerWrapper.playList.map {
            it.toAppItem() as? AudioItemModel ?: error("invalid")
        }

    override fun getPlayingMediaStateFlow() =
        playerWrapper.observePlayingMedia().map {
            it?.toAppItem() as? AudioItemModel
        }

    override fun getPlayListQueueStateFlow() =
        playerWrapper.observePlayListQueue().map { items ->
            items.mapNotNull {
                it.toAppItem() as? AudioItemModel
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

    override fun observeIsPlaying() =
        playerWrapper
            .observePlayerState()
            .map {
                it is PlayerState.Playing
            }.distinctUntilChanged()

    override fun observeProgressFactor() =
        playerWrapper
            .observePlayerState()
            .map {
                if (getCurrentPositionMs() == 0L) {
                    return@map 0f
                } else {
                    it.currentPositionMs
                        .toFloat()
                        .div(playerWrapper.currentDurationMs)
                        .coerceIn(0f, 1f)
                }
            }.distinctUntilChanged()
}
