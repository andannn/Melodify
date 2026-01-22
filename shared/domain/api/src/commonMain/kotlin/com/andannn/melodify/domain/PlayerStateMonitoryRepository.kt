/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain

import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.domain.model.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlayerStateMonitoryRepository {
    fun getCurrentPositionMs(): Long

    fun observeCurrentDurationMs(): Flow<Long>

    fun getPlayingIndexInQueue(): Int

    suspend fun getPlayListQueue(): List<MediaItemModel>

    fun getPlayingMediaStateFlow(): Flow<MediaItemModel?>

    fun getPlayListQueueStateFlow(): Flow<List<MediaItemModel>>

    fun getCurrentPlayMode(): PlayMode

    fun observeIsShuffle(): StateFlow<Boolean>

    fun observePlayMode(): Flow<PlayMode>

    fun observePlayerState(): Flow<PlayerState>

    fun observeProgressFactor(): Flow<Float>

    fun observePlayBackEndEvent(): Flow<MediaItemModel>
}
