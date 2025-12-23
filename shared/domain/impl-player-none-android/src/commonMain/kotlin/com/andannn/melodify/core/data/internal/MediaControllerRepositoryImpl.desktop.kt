/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.player.AvPlayerQueuePlayer
import com.andannn.melodify.domain.MediaControllerRepository
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayMode

internal class MediaControllerRepositoryImpl(
    private val player: AvPlayerQueuePlayer,
) : MediaControllerRepository {
    override fun playMediaList(
        mediaList: List<MediaItemModel>,
        index: Int,
    ) {
        mediaList as List<AudioItemModel>
        player.playMediaList(
            mediaList.map { it.source },
            index,
        )
    }

    override fun seekToNext() = player.seekToNext()

    override fun seekToPrevious() = player.seekToPrevious()

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) = player.seekMediaItem(mediaItemIndex, positionMs)

    override fun seekToTime(time: Long) = player.seekToTime(time)

    override fun setPlayMode(mode: PlayMode) {
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
    }

    override fun play() = player.play()

    override fun pause() = player.pause()

    override fun addMediaItems(
        index: Int,
        mediaItems: List<MediaItemModel>,
    ) {
        mediaItems as List<AudioItemModel>
        player.addMediaItems(
            index = index,
            mrls = mediaItems.map { it.source },
        )
    }

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) = player.moveMediaItem(from, to)

    override fun removeMediaItem(index: Int) = player.removeMediaItem(index)

    override fun seekForward() {
        player.seekToTime(player.currentPositionMs.plus(DEFAULT_SEEK_INCREMENT_MS))
    }

    override fun seekBack() {
        player.seekToTime(player.currentPositionMs.minus(DEFAULT_SEEK_BACK_INCREMENT_MS))
    }

    override fun setPlaybackSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }
}

private const val DEFAULT_SEEK_INCREMENT_MS = 10_000L
private const val DEFAULT_SEEK_BACK_INCREMENT_MS = 10_000L
