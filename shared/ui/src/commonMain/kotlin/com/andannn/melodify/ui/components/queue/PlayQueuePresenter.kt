/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.queue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberPlayQueuePresenter(repository: Repository = LocalRepository.current) =
    retainPresenter(repository) {
        PlayQueuePresenter(repository)
    }

class PlayQueuePresenter(
    private val repository: Repository,
) : ScopedPresenter<PlayQueueState>() {
    private val playListQueueFlow =
        repository.playerStateMonitoryRepository
            .getPlayListQueueStateFlow()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList(),
            )
    private val interactingMusicItemFlow =
        repository.playerStateMonitoryRepository
            .getPlayingMediaStateFlow()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = AudioItemModel.DEFAULT,
            )

    @Composable
    override fun present(): PlayQueueState {
        val playListQueue by playListQueueFlow.collectAsStateWithLifecycle()
        val interactingMusicItem by interactingMusicItemFlow.collectAsStateWithLifecycle()
        return PlayQueueState(
            interactingMusicItem,
            playListQueue,
        ) { eventSink ->
            when (eventSink) {
                is PlayQueueEvent.OnItemClick -> onItemClick(playListQueue, eventSink.item)
                is PlayQueueEvent.OnSwapFinished ->
                    onSwapFinished(
                        from = eventSink.from,
                        to = eventSink.to,
                    )

                is PlayQueueEvent.OnDeleteFinished -> onDeleteFinished(deleted = eventSink.id)
            }
        }
    }

    private fun onItemClick(
        playListQueue: List<MediaItemModel>,
        item: MediaItemModel,
    ) {
        repository.mediaControllerRepository.seekMediaItem(
            mediaItemIndex = playListQueue.indexOf(item),
        )
    }

    private fun onSwapFinished(
        from: Int,
        to: Int,
    ) {
        repository.mediaControllerRepository.moveMediaItem(from, to)
    }

    private fun onDeleteFinished(deleted: Int) {
        repository.mediaControllerRepository.removeMediaItem(deleted)
    }
}

@Stable
data class PlayQueueState(
    val interactingMusicItem: MediaItemModel?,
    val playListQueue: List<MediaItemModel>,
    val eventSink: (PlayQueueEvent) -> Unit = {},
)

sealed interface PlayQueueEvent {
    data class OnItemClick(
        val item: MediaItemModel,
    ) : PlayQueueEvent

    data class OnDeleteFinished(
        val id: Int,
    ) : PlayQueueEvent

    data class OnSwapFinished(
        val from: Int,
        val to: Int,
    ) : PlayQueueEvent
}
