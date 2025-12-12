/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.player.AvPlayerQueuePlayer

internal class MediaControllerRepositoryImpl(
    private val queuePlayer: AvPlayerQueuePlayer,
) : MediaControllerRepository {
    override fun playMediaList(
        mediaList: List<MediaItemModel>,
        index: Int,
    ) {
        mediaList as List<AudioItemModel>
        queuePlayer.playMediaList(
            mediaList.map { it.source },
            index,
        )
    }

    override fun seekToNext() = queuePlayer.seekToNext()

    override fun seekToPrevious() = queuePlayer.seekToPrevious()

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) = queuePlayer.seekMediaItem(mediaItemIndex, positionMs)

    override fun seekToTime(time: Long) = queuePlayer.seekToTime(time)

    override fun setPlayMode(mode: PlayMode) {
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
    }

    override fun play() = queuePlayer.play()

    override fun pause() = queuePlayer.pause()

    override fun addMediaItems(
        index: Int,
        mediaItems: List<MediaItemModel>,
    ) {
        mediaItems as List<AudioItemModel>
        queuePlayer.addMediaItems(
            index = index,
            mrls = mediaItems.map { it.source },
        )
    }

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) = queuePlayer.moveMediaItem(from, to)

    override fun removeMediaItem(index: Int) = queuePlayer.removeMediaItem(index)
}
