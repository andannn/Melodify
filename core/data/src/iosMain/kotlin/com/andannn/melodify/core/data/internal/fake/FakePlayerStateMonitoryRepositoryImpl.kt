/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal.fake

import com.andannn.melodify.core.data.internal.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

internal class FakePlayerStateMonitoryRepositoryImpl : PlayerStateMonitoryRepository {
    override fun getCurrentPositionMs(): Long = 0L

    override fun observeCurrentPositionMs(): Flow<Long> = flowOf(0L)

    override fun getPlayingIndexInQueue(): Int = 0

    override suspend fun getPlayListQueue(): List<AudioItemModel> = emptyList()

    override fun getPlayingMediaStateFlow() =
        flow {
            emit(null)
        }

    override fun getPlayListQueueStateFlow(): Flow<List<AudioItemModel>> =
        flow {
            emit(emptyList())
        }

    override fun getCurrentPlayMode(): PlayMode = PlayMode.REPEAT_ALL

    override fun observeIsShuffle(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun observePlayMode(): Flow<PlayMode> =
        flow {
            emit(PlayMode.REPEAT_ALL)
        }

    override fun observeIsPlaying(): Flow<Boolean> =
        flow {
            emit(false)
        }

    override fun observeProgressFactor(): Flow<Float> =
        flow {
            emit(0f)
        }
}
