package com.andannn.melodify.core.player

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface VlcPlayer {
    val currentPositionMs: Long

    val currentDurationMs: Long

    val playingIndexInQueue: Int

    fun playMediaList(mediaList: List<String>, index: Int)

    fun observePlayerState(): StateFlow<PlayerState>

    fun observePlayListQueue(): Flow<List<String>>

    fun observePlayingMediaMrl(): Flow<String?>

    fun observeProgressFactor(): Flow<Float>

    val playList: List<String>

    fun seekToNext()

    fun seekToPrevious()

    fun seekMediaItem(mediaItemIndex: Int, positionMs: Long = 0)

    fun seekToTime(time: Long)

    fun setShuffleModeEnabled(enable: Boolean)

    fun play()

    fun pause()

    fun addMediaItems(index: Int, mrls: List<String>)

    fun moveMediaItem(from: Int, to: Int)

    fun removeMediaItem(index: Int)

    fun release()
}

