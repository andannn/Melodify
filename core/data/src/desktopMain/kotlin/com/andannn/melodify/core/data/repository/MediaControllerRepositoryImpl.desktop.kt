package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.player.VlcPlayer

internal class MediaControllerRepositoryImpl(
    private val vlcPlayer: VlcPlayer,
) : MediaControllerRepository {
    override fun getCurrentPlayingItemDuration(): Long? = vlcPlayer.currentDurationMs

    override fun playMediaList(
        mediaList: List<AudioItemModel>,
        index: Int,
    ) {
        vlcPlayer.playMediaList(
            mediaList.map { it.source },
            index,
        )
    }

    override fun seekToNext() = vlcPlayer.seekToNext()

    override fun seekToPrevious() = vlcPlayer.seekToPrevious()

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) = vlcPlayer.seekMediaItem(mediaItemIndex, positionMs)

    override fun seekToTime(time: Long) = vlcPlayer.seekToTime(time)

    override fun setPlayMode(mode: PlayMode) {
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
    }

    override fun play() = vlcPlayer.play()

    override fun pause() = vlcPlayer.pause()

    override fun addMediaItems(
        index: Int,
        mediaItems: List<AudioItemModel>,
    ) = vlcPlayer.addMediaItems(
        index = index,
        mrls = mediaItems.map { it.source },
    )

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) = vlcPlayer.moveMediaItem(from, to)

    override fun removeMediaItem(index: Int) = vlcPlayer.removeMediaItem(index)
}
