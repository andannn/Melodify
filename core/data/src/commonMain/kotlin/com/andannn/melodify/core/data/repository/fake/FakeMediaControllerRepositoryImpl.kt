/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.repository.fake

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.repository.MediaControllerRepository

private const val TAG = "MediaControllerRepository"

internal class FakeMediaControllerRepositoryImpl() : MediaControllerRepository {
    override fun getCurrentPlayingItemDuration(): Long? = 0L

    override fun playMediaList(
        mediaList: List<AudioItemModel>,
        index: Int,
    ) {
    }

    override fun seekToNext() {
    }

    override fun seekToPrevious() {
    }

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) {
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

    override fun addMediaItems(
        index: Int,
        mediaItems: List<AudioItemModel>,
    ) {
    }

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) {
    }

    override fun removeMediaItem(index: Int) {
    }
}
