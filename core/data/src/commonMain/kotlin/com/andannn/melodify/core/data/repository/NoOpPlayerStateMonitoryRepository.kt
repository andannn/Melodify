package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.core.data.model.PlayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow

open class NoOpPlayerStateMonitoryRepository : PlayerStateMonitoryRepository {
    override fun getCurrentPositionMs(): Long = 0L

    override fun observeCurrentPositionMs(): Flow<Long> = flowOf()

    override fun getPlayingIndexInQueue(): Int = 0

    override suspend fun getPlayListQueue(): List<AudioItemModel> = emptyList()

    override fun getPlayingMediaStateFlow(): Flow<AudioItemModel?> = flowOf()

    override fun getPlayListQueueStateFlow(): Flow<List<AudioItemModel>> = flowOf()

    override fun getCurrentPlayMode(): PlayMode = PlayMode.REPEAT_ALL

    override fun observeIsShuffle(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun observePlayMode(): Flow<PlayMode> = flowOf()

    override fun observeIsPlaying(): Flow<Boolean> = flowOf()

    override fun observeProgressFactor(): Flow<Float> = flowOf()
}
