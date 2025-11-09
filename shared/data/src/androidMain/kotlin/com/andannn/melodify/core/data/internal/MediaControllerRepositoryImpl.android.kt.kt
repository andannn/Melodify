/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.Player
import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.player.MediaBrowserManager

private const val TAG = "MediaControllerRepository"

class MediaControllerRepositoryImpl constructor(
    private val mediaBrowserManager: MediaBrowserManager,
) : MediaControllerRepository {
    private val mediaBrowser
        get() = mediaBrowserManager.mediaBrowser

    fun getPlayer(): Player = mediaBrowser

    override fun getCurrentPlayingItemDuration(): Long = mediaBrowser.duration

    override fun playMediaList(
        mediaList: List<MediaItemModel>,
        index: Int,
    ) {
        Log.d(TAG, "playMediaList: ")
        with(mediaBrowser) {
            setMediaItems(
                mediaList.map { it.toMediaItem(generateUniqueId = true) },
                index,
                C.TIME_UNSET,
            )
            prepare()
            play()
        }
    }

    override fun seekToNext() {
        mediaBrowser.seekToNext()
    }

    override fun seekToPrevious() {
        mediaBrowser.seekToPrevious()
    }

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) {
        mediaBrowser.seekTo(mediaItemIndex, positionMs)
    }

    override fun seekToTime(time: Long) {
        mediaBrowser.seekTo(time)
    }

    override fun setPlayMode(mode: PlayMode) {
        mediaBrowser.repeatMode = mode.toExoPlayerMode()
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
        mediaBrowser.shuffleModeEnabled = enable
    }

    override fun play() {
        mediaBrowser.play()
    }

    override fun pause() {
        mediaBrowser.pause()
    }

    override fun addMediaItems(
        index: Int,
        mediaItems: List<MediaItemModel>,
    ) {
        Log.d(TAG, "addMediaItems: index $index, mediaItems $mediaItems")
        mediaBrowser.addMediaItems(
            // index =
            index,
            // mediaItems =
            mediaItems.map { it.toMediaItem(generateUniqueId = true) },
        )
    }

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) {
        mediaBrowser.moveMediaItem(from, to)
    }

    override fun removeMediaItem(index: Int) {
        mediaBrowser.removeMediaItem(index)
    }
}
