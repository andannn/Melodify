package com.andannn.melodify.ui.components.queue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberPlayQueueStateHolder(
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: Repository = getKoin().get(),
) = remember(
    scope,
    repository,
) {
    PlayQueueStateHolder(
        scope = scope,
        repository = repository,
    )
}

class PlayQueueStateHolder(
    scope: CoroutineScope,
    private val repository: Repository,
) {
    var playListQueue: List<AudioItemModel> by mutableStateOf(value = emptyList())
        private set

    var interactingMusicItem: AudioItemModel by mutableStateOf(AudioItemModel.DEFAULT)
        private set

    init {
        scope.launch {
            repository.playerStateMonitoryRepository.playListQueueStateFlow.collect {
                playListQueue = it
            }
        }

        scope.launch {
            repository.playerStateMonitoryRepository.playingMediaStateFlow
                .filterNotNull()
                .collect {
                    interactingMusicItem = it
                }
        }
    }

    fun onItemClick(item: AudioItemModel) {
        repository.mediaControllerRepository.seekMediaItem(
            mediaItemIndex = playListQueue.indexOf(item),
        )
    }

    fun onSwapFinished(
        from: Int,
        to: Int,
    ) {
        repository.mediaControllerRepository.moveMediaItem(from, to)
    }

    fun onDeleteFinished(deleted: Int) {
        repository.mediaControllerRepository.removeMediaItem(deleted)
    }
}
