package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.player.AVPlayerWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration

class MediaControllerRepositoryImpl(
    private val avPlayerWrapper: AVPlayerWrapper
): MediaControllerRepository {
    override fun getCurrentPlayingItemDuration(): Long? {
        return 0L
    }

    override fun playMediaList(mediaList: List<AudioItemModel>, index: Int) {
        avPlayerWrapper.playMediaList(mediaList.map { it.source }, index)
    }

    override fun seekToNext() {
    }

    override fun seekToPrevious() {
    }

    override fun seekMediaItem(mediaItemIndex: Int, positionMs: Long) {
    }

    override fun seekToTime(time: Long) {
    }

    override fun setPlayMode(mode: PlayMode) {
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
    }

    override fun play() {
    }

    override fun pause() {
    }

    override fun addMediaItems(index: Int, mediaItems: List<AudioItemModel>) {
    }

    override fun moveMediaItem(from: Int, to: Int) {
    }

    override fun removeMediaItem(index: Int) {
    }
}