package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

interface PlayerStateMonitoryRepository {
    fun getCurrentPositionMs(): Long

    fun getPlayingIndexInQueue(): Int

    suspend fun getPlayListQueue(): List<AudioItemModel>

    fun getPlayingMediaStateFlow(): Flow<AudioItemModel?>

    fun getPlayListQueueStateFlow(): Flow<List<AudioItemModel>>

    fun getCurrentPlayMode(): PlayMode

    fun observeIsShuffle(): StateFlow<Boolean>

    fun observePlayMode(): Flow<PlayMode>

    fun observeIsPlaying(): Flow<Boolean>

    fun observeProgressFactor(): Flow<Float>
}
