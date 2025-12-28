/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.domain.MediaControllerRepository
import com.andannn.melodify.domain.PlayerStateMonitoryRepository
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class FakeMediaControllerRepository :
    MediaControllerRepository,
    PlayerStateMonitoryRepository {
    private val currentListFlow = MutableStateFlow(emptyList<MediaItemModel>())
    private val currentIndexFlow = MutableStateFlow<Int>(0)
    private val playModeFlow = MutableStateFlow<PlayMode>(PlayMode.REPEAT_ALL)
    private val isShuffleFlow = MutableStateFlow<Boolean>(false)
    private val isPlayFlow = MutableStateFlow<Boolean>(false)

    override fun playMediaList(
        mediaList: List<MediaItemModel>,
        index: Int,
    ) {
        currentListFlow.value = mediaList
        currentIndexFlow.value = index
    }

    override fun seekToNext() {
        currentIndexFlow.value++
    }

    override fun seekToPrevious() {
        currentIndexFlow.value--
    }

    override fun seekMediaItem(
        mediaItemIndex: Int,
        positionMs: Long,
    ) {
        currentIndexFlow.value = mediaItemIndex
    }

    override fun seekToTime(time: Long) {
    }

    override fun setPlayMode(mode: PlayMode) {
        playModeFlow.value = mode
    }

    override fun setShuffleModeEnabled(enable: Boolean) {
        isShuffleFlow.value = enable
    }

    override fun play() {
        isPlayFlow.value = true
    }

    override fun pause() {
        isPlayFlow.value = false
    }

    override fun addMediaItems(
        index: Int,
        mediaItems: List<MediaItemModel>,
    ) {
        currentListFlow.value =
            currentListFlow.value.toMutableList().apply {
                addAll(index, mediaItems)
            }
    }

    override fun moveMediaItem(
        from: Int,
        to: Int,
    ) {
        currentListFlow.value =
            currentListFlow.value.toMutableList().apply {
                add(to, removeAt(from))
            }
    }

    override fun removeMediaItem(index: Int) {
        currentListFlow.value =
            currentListFlow.value.toMutableList().apply {
                removeAt(index)
            }
    }

    override fun seekForward() {
    }

    override fun seekBack() {
    }

    override fun setPlaybackSpeed(speed: Float) {
    }

    override fun getCurrentPositionMs(): Long = 0

    override fun observeCurrentDurationMs(): Flow<Long> = MutableStateFlow(0)

    override fun getPlayingIndexInQueue(): Int = currentIndexFlow.value

    override suspend fun getPlayListQueue(): List<MediaItemModel> = currentListFlow.value

    override fun getPlayingMediaStateFlow(): Flow<MediaItemModel?> =
        combine(
            currentListFlow,
            currentIndexFlow,
        ) { list, index ->
            if (list.isEmpty()) {
                null
            } else {
                list[index]
            }
        }

    override fun getPlayListQueueStateFlow(): Flow<List<MediaItemModel>> = currentListFlow

    override fun getCurrentPlayMode(): PlayMode = playModeFlow.value

    override fun observeIsShuffle(): StateFlow<Boolean> = isShuffleFlow

    override fun observePlayMode(): Flow<PlayMode> = playModeFlow

    override fun observeIsPlaying(): Flow<Boolean> = isPlayFlow

    override fun observeProgressFactor(): Flow<Float> = MutableStateFlow(0f)

    override fun observePlayBackEndEvent(): Flow<MediaItemModel> = flowOf()
}
