package com.andannn.melodify.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.model.next
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.ui.common.components.drawer.DrawerController
import com.andannn.melodify.ui.common.components.drawer.DrawerEvent
import com.andannn.melodify.ui.common.components.drawer.model.SheetModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PlayerUiEvent {
    data object OnFavoriteButtonClick : PlayerUiEvent

    data class OnOptionIconClick(
        val mediaItem: AudioItemModel,
    ) : PlayerUiEvent

    data object OnPlayButtonClick : PlayerUiEvent

    data object OnNextButtonClick : PlayerUiEvent

    data object OnPlayModeButtonClick : PlayerUiEvent

    data object OnPreviousButtonClick : PlayerUiEvent

    data object OnShuffleButtonClick : PlayerUiEvent

    data class OnProgressChange(
        val progress: Float,
    ) : PlayerUiEvent

    data object OnTimerIconClick : PlayerUiEvent
}

private const val TAG = "PlayerStateViewModel"

class PlayerStateViewModel(
    private val playListRepository: PlayListRepository,
    private val mediaControllerRepository: MediaControllerRepository,
    private val playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    private val drawerController: DrawerController,
) : ViewModel() {
    private val interactingMusicItem = playerStateMonitoryRepository.playingMediaStateFlow
    private val playStateFlow =
        combine(
            playerStateMonitoryRepository.observeIsPlaying(),
            playerStateMonitoryRepository.observeProgressFactor(),
            playerStateMonitoryRepository.observePlayMode(),
            playerStateMonitoryRepository.observeIsShuffle(),
        ) { isPlaying, progress, playMode, isShuffle ->
            PlayState(isPlaying, progress, playMode, isShuffle)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlayState())

    private val isCountingFlow = mediaControllerRepository.observeIsCounting()
    private val playListQueueFlow = playerStateMonitoryRepository.playListQueueStateFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isCurrentMediaFavoriteFlow =
        interactingMusicItem
            .distinctUntilChanged()
            .flatMapLatest {
                if (it == null) {
                    return@flatMapLatest flowOf(false)
                }
                playListRepository.isMediaInFavoritePlayListFlow(it.id)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val playerUiStateFlow =
        combine(
            interactingMusicItem,
            playStateFlow,
            playListQueueFlow,
            isCurrentMediaFavoriteFlow,
            isCountingFlow,
        ) { interactingMusicItem, state, playListQueue, isFavorite, isCounting ->
            if (interactingMusicItem == null) {
                PlayerUiState.Inactive
            } else {
                PlayerUiState.Active(
                    mediaItem = interactingMusicItem,
                    duration = mediaControllerRepository.currentDuration ?: 0L,
                    playMode = state.playMode,
                    isShuffle = state.isShuffle,
                    isFavorite = isFavorite,
                    playListQueue = playListQueue,
                    isPlaying = state.isPlaying,
                    progress = state.playProgress,
                    isCounting = isCounting,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlayerUiState.Inactive)

    fun onEvent(event: PlayerUiEvent) {
        Napier.d(tag = TAG) { "onEvent: $event" }
        when (event) {
            PlayerUiEvent.OnFavoriteButtonClick -> {
                val current =
                    (playerUiStateFlow.value as? PlayerUiState.Active)?.mediaItem
                Napier.d(tag = TAG) { "currentId: $current" }
                if (current == null) return

                viewModelScope.launch {
                    onToggleFavoriteState(current)
                }
            }

            PlayerUiEvent.OnPlayModeButtonClick -> {
                val nextPlayMode = playerStateMonitoryRepository.playMode.next()
                mediaControllerRepository.setPlayMode(nextPlayMode)
            }

            PlayerUiEvent.OnPlayButtonClick -> togglePlayState()
            PlayerUiEvent.OnPreviousButtonClick -> previous()
            PlayerUiEvent.OnNextButtonClick -> next()
            PlayerUiEvent.OnShuffleButtonClick -> {
                mediaControllerRepository.setShuffleModeEnabled(!playStateFlow.value.isShuffle)
            }

            is PlayerUiEvent.OnOptionIconClick -> {
                viewModelScope.launch {
                    drawerController.onEvent(
                        DrawerEvent.OnShowBottomDrawer(SheetModel.PlayerOptionSheet(event.mediaItem)),
                    )
                }
            }

            is PlayerUiEvent.OnProgressChange -> {
                val time =
                    with((playerUiStateFlow.value as PlayerUiState.Active)) {
                        duration.times(event.progress).toLong()
                    }
                seekToTime(time)
            }

            PlayerUiEvent.OnTimerIconClick -> {
                drawerController.onEvent(DrawerEvent.OnShowTimerSheet)
            }
        }
    }

    private suspend fun onToggleFavoriteState(audio: AudioItemModel) {
        playListRepository.toggleFavoriteMedia(audio)
    }

    private fun togglePlayState() {
        val state = playerUiStateFlow.value
        if (state is PlayerUiState.Active) {
            playerUiStateFlow.value.let {
                if (state.isPlaying) {
                    mediaControllerRepository.pause()
                } else {
                    mediaControllerRepository.play()
                }
            }
        }
    }

    fun next() {
        mediaControllerRepository.seekToNext()
    }

    private fun previous() {
        mediaControllerRepository.seekToPrevious()
    }

    private fun seekToTime(time: Long) {
        mediaControllerRepository.seekToTime(time)
    }
}

private data class PlayState(
    val isPlaying: Boolean = false,
    val playProgress: Float = 0f,
    val playMode: PlayMode = PlayMode.REPEAT_ALL,
    val isShuffle: Boolean = false,
)

sealed class PlayerUiState {
    data object Inactive : PlayerUiState()

    data class Active(
        val isShuffle: Boolean = false,
        val duration: Long = 0L,
        val isFavorite: Boolean = false,
        val playMode: PlayMode = PlayMode.REPEAT_ALL,
        val mediaItem: AudioItemModel,
        val playListQueue: List<AudioItemModel>,
        val progress: Float,
        val isPlaying: Boolean,
        val isCounting: Boolean,
    ) : PlayerUiState()
}
