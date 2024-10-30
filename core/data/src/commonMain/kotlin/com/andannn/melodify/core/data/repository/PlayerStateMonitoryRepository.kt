package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlayerStateMonitoryRepository {
    val currentPositionMs: Long

    val playingIndexInQueue: Int

    val playListQueue: List<AudioItemModel>

    val playingMediaStateFlow: Flow<AudioItemModel?>

    val playListQueueStateFlow: Flow<List<AudioItemModel>>

    fun observeIsShuffle(): StateFlow<Boolean>

    val playMode: PlayMode

    fun observePlayMode(): Flow<PlayMode>

    fun observeIsPlaying(): Flow<Boolean>

    fun observeProgressFactor(): Flow<Float>
}
