package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

internal class PlayerStateMonitoryRepositoryImpl(
) : PlayerStateMonitoryRepository {
    override val currentPositionMs: Long
        get() = 0L

    override val playingIndexInQueue: Int
        get() = 0

    override val playListQueue: List<AudioItemModel>
        get() = emptyList()

    override val playingMediaStateFlow: Flow<AudioItemModel?>
        get() = flow {
            emit(null)
        }

    override val playListQueueStateFlow: Flow<List<AudioItemModel>>
        get() = flow {
            emit(emptyList())
        }

    override fun observeIsShuffle(): StateFlow<Boolean> = MutableStateFlow(false)

    override val playMode: PlayMode
        get() = PlayMode.REPEAT_ALL

    override fun observePlayMode(): Flow<PlayMode> = flow {
        emit(PlayMode.REPEAT_ALL)
    }

    override fun observeIsPlaying(): Flow<Boolean> = flow {
        emit(false)
    }

    override fun observeProgressFactor(): Flow<Float> = flow {
        emit(0f)
    }
}