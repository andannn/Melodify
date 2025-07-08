package com.andannn.melodify.core.data.repository.fake

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

internal class FakePlayerStateMonitoryRepositoryImpl() : PlayerStateMonitoryRepository {
    override fun getCurrentPositionMs(): Long {
        return 0L
    }

    override fun observeCurrentPositionMs(): Flow<Long> {
        return flowOf(0L)
    }

    override fun getPlayingIndexInQueue(): Int {
        return 0
    }

    override suspend fun getPlayListQueue(): List<AudioItemModel> {
        return emptyList()
    }

    override fun getPlayingMediaStateFlow() =
        flow {
            emit(null)
        }

    override fun getPlayListQueueStateFlow(): Flow<List<AudioItemModel>> =
        flow {
            emit(emptyList())
        }

    override fun getCurrentPlayMode(): PlayMode {
        return PlayMode.REPEAT_ALL
    }

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
