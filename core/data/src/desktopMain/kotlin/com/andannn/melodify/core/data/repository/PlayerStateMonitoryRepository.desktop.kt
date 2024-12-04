package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.util.toAppItem
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.player.VlcPlayer
import com.andannn.melodify.core.player.PlayerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

internal class PlayerStateMonitoryRepositoryImpl(
    private val vlcPlayer: VlcPlayer,
    private val libraryDao: MediaLibraryDao
) : PlayerStateMonitoryRepository {
    override val currentPositionMs: Long get() = vlcPlayer.currentPositionMs

    override val playingIndexInQueue: Int get() = vlcPlayer.playingIndexInQueue

    override val playListQueue: List<AudioItemModel> = emptyList()

    override val playingMediaStateFlow: Flow<AudioItemModel?> = vlcPlayer.observePlayingMediaMrl()
        .map { mrl ->
            if (mrl == null) {
                return@map null
            }
            libraryDao.getMediaByMediaIds(listOf(mrl.hashCode().toString())).firstOrNull()
                ?.toAppItem()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val playListQueueStateFlow: Flow<List<AudioItemModel>> =
        vlcPlayer.observePlayListQueue()
            .mapLatest { mrls ->
                libraryDao.getMediaByMediaIds(mrls.map { it.hashCode().toString() })
                    .map { it.toAppItem() }
            }

    override fun observeIsShuffle(): StateFlow<Boolean> {
        return MutableStateFlow(false)
    }

    override val playMode: PlayMode
        get() = PlayMode.REPEAT_ALL

    override fun observePlayMode() = flowOf(PlayMode.REPEAT_ALL)

    override fun observeIsPlaying(): Flow<Boolean> = vlcPlayer.observePlayerState()
        .map {
            it is PlayerState.Playing
        }
        .distinctUntilChanged()

    override fun observeProgressFactor(): Flow<Float> = vlcPlayer.observeProgressFactor()
}