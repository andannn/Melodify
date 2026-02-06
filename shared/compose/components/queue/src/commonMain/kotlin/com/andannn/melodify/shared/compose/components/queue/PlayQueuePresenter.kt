/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.queue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberPlayQueuePresenter(repository: Repository = LocalRepository.current) =
    retainPresenter(repository) {
        PlayQueuePresenter(repository)
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

private class PlayQueuePresenter(
    private val repository: Repository,
) : RetainedPresenter<PlayQueueState>() {
    private val playListQueueFlow =
        repository
            .getPlayListQueueStateFlow()
            .stateInRetainedModel(
                initialValue = emptyList(),
            )
    private val interactingMusicItemFlow =
        repository
            .getPlayingMediaStateFlow()
            .stateInRetainedModel(
                initialValue = null,
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
                is PlayQueueEvent.OnItemClick -> {
                    onItemClick(playListQueue, eventSink.item)
                }

                is PlayQueueEvent.OnSwapFinished -> {
                    onSwapFinished(
                        from = eventSink.from,
                        to = eventSink.to,
                    )
                }

                is PlayQueueEvent.OnDeleteFinished -> {
                    onDeleteFinished(deleted = eventSink.id)
                }
            }
        }
    }

    private fun onItemClick(
        playListQueue: List<MediaItemModel>,
        item: MediaItemModel,
    ) {
        repository.seekMediaItem(
            mediaItemIndex = playListQueue.indexOf(item),
        )
    }

    private fun onSwapFinished(
        from: Int,
        to: Int,
    ) {
        repository.moveMediaItem(from, to)
    }

    private fun onDeleteFinished(deleted: Int) {
        repository.removeMediaItem(deleted)
    }
}
