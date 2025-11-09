/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlayerStateMonitoryRepository {
    fun getCurrentPositionMs(): Long

    fun observeCurrentPositionMs(): Flow<Long>

    fun getPlayingIndexInQueue(): Int

    suspend fun getPlayListQueue(): List<MediaItemModel>

    fun getPlayingMediaStateFlow(): Flow<MediaItemModel?>

    fun getPlayListQueueStateFlow(): Flow<List<MediaItemModel>>

    fun getCurrentPlayMode(): PlayMode

    fun observeIsShuffle(): StateFlow<Boolean>

    fun observePlayMode(): Flow<PlayMode>

    fun observeIsPlaying(): Flow<Boolean>

    fun observeProgressFactor(): Flow<Float>
}
