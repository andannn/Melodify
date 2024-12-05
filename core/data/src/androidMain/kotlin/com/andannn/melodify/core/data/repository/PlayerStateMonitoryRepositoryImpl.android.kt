package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.player.PlayerState
import com.andannn.melodify.core.player.PlayerWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlayerStateMonitoryRepositoryImpl(
    private val playerWrapper: PlayerWrapper,
) : PlayerStateMonitoryRepository {
    override fun getCurrentPositionMs(): Long {
        return playerWrapper.currentPositionMs
    }

    override fun getPlayingIndexInQueue(): Int {
        return playerWrapper.playingIndexInQueue
    }

    override suspend fun getPlayListQueue(): List<AudioItemModel> {
        return  playerWrapper.playList.map {
            it.toAppItem() as? AudioItemModel ?: error("invalid")
        }
    }

    override fun getPlayingMediaStateFlow()= playerWrapper.observePlayingMedia().map {
        it?.toAppItem() as? AudioItemModel
    }

    override fun getPlayListQueueStateFlow()= playerWrapper.observePlayListQueue().map { items ->
        items.mapNotNull {
            it.toAppItem() as? AudioItemModel
        }
    }

    override fun getCurrentPlayMode(): PlayMode {
        return playerWrapper.observePlayMode().value.let {
            fromRepeatMode(it)
        }
    }

    override fun observeIsShuffle() = playerWrapper.observeIsShuffle()

    override fun observePlayMode() = playerWrapper.observePlayMode()
        .map {
            fromRepeatMode(it)
        }
        .distinctUntilChanged()


    override fun observeIsPlaying() = playerWrapper.observePlayerState()
        .map {
            it is PlayerState.Playing
        }
        .distinctUntilChanged()

    override fun observeProgressFactor() = playerWrapper.observePlayerState()
        .map {
            if (getCurrentPositionMs() == 0L) {
                return@map 0f
            } else {
                it.currentPositionMs.toFloat().div(playerWrapper.currentDurationMs).coerceIn(0f, 1f)
            }
        }
        .distinctUntilChanged()
}