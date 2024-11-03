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
    override val currentPositionMs: Long
        get() = playerWrapper.currentPositionMs

    override val playingIndexInQueue: Int
        get() = playerWrapper.playingIndexInQueue

    override val playListQueue: List<AudioItemModel>
        get() = playerWrapper.playList.map {
            it.toAppItem() as? AudioItemModel ?: error("invalid")
        }

    override val playingMediaStateFlow: Flow<AudioItemModel?>
        get() = playerWrapper.observePlayingMedia().map {
            it?.toAppItem() as? AudioItemModel
        }

    override val playListQueueStateFlow: Flow<List<AudioItemModel>>
        get() = playerWrapper.observePlayListQueue().map { items ->
            items.mapNotNull {
                it.toAppItem() as? AudioItemModel
            }
        }

    override fun observeIsShuffle() = playerWrapper.observeIsShuffle()

    override val playMode: PlayMode
        get() = playerWrapper.observePlayMode().value.let {
            fromRepeatMode(it)
        }

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
            if (currentPositionMs == 0L) {
                return@map 0f
            } else {
                it.currentPositionMs.toFloat().div(playerWrapper.currentDurationMs).coerceIn(0f, 1f)
            }
        }
        .distinctUntilChanged()
}