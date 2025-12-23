/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain

import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayMode

interface MediaControllerRepository {
    fun playMediaList(
        mediaList: List<MediaItemModel>,
        index: Int = 0,
    )

    fun seekToNext()

    fun seekToPrevious()

    fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long = 0,
    )

    fun seekToTime(time: Long)

    fun setPlayMode(mode: PlayMode)

    fun setShuffleModeEnabled(enable: Boolean)

    fun play()

    fun pause()

    fun addMediaItems(
        index: Int,
        mediaItems: List<MediaItemModel>,
    )

    fun moveMediaItem(
        from: Int,
        to: Int,
    )

    fun removeMediaItem(index: Int)

    fun seekForward()

    fun seekBack()

    fun setPlaybackSpeed(speed: Float)
}
