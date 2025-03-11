package com.andannn.melodify.ui.components.queue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter

class PlayQueuePresenter(
    private val repository: Repository,
) : Presenter<PlayQueueState> {

    @Composable
    override fun present(): PlayQueueState {
        val playListQueue by
        repository.playerStateMonitoryRepository.getPlayListQueueStateFlow().collectAsState(
            emptyList()
        )
        val interactingMusicItem by
        repository.playerStateMonitoryRepository.getPlayingMediaStateFlow().collectAsState(
            AudioItemModel.DEFAULT
        )
        return PlayQueueState(
            interactingMusicItem!!,
            playListQueue,
        ) { eventSink ->
            when (eventSink) {
                is PlayQueueEvent.OnItemClick -> onItemClick(playListQueue, eventSink.item)
                is PlayQueueEvent.OnSwapFinished -> onSwapFinished(
                    from = eventSink.from,
                    to = eventSink.to
                )

                is PlayQueueEvent.OnDeleteFinished -> onDeleteFinished(deleted = eventSink.id)
            }
        }
    }

    private fun onItemClick(playListQueue: List<AudioItemModel>, item: AudioItemModel) {
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

data class PlayQueueState(
    val interactingMusicItem: AudioItemModel,
    val playListQueue: List<AudioItemModel>,
    val eventSink: (PlayQueueEvent) -> Unit = {},
) : CircuitUiState

sealed interface PlayQueueEvent {
    data class OnItemClick(val item: AudioItemModel) : PlayQueueEvent
    data class OnDeleteFinished(val id: Int) : PlayQueueEvent
    data class OnSwapFinished(
        val from: Int,
        val to: Int,
    ) : PlayQueueEvent
}