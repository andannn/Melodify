package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode

interface MediaControllerRepository {
    fun getCurrentPlayingItemDuration() : Long?

    fun playMediaList(mediaList: List<AudioItemModel>, index: Int = 0)

    fun seekToNext()

    fun seekToPrevious()

    fun seekMediaItem(mediaItemIndex: Int, positionMs: Long = 0)

    fun seekToTime(time: Long)

    fun setPlayMode(mode: PlayMode)

    fun setShuffleModeEnabled(enable: Boolean)

    fun play()

    fun pause()

    fun addMediaItems(index: Int, mediaItems: List<AudioItemModel>)

    fun moveMediaItem(from: Int, to: Int)

    fun removeMediaItem(index: Int)
}