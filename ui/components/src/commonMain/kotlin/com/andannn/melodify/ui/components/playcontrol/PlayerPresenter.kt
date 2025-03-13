package com.andannn.melodify.ui.components.playcontrol

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.model.next
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.repository.SleepTimerRepository
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.onMediaOptionClick
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun rememberPlayerPresenter(
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
): PlayerPresenter = remember(
    repository,
    popupController
) {
    PlayerPresenter(repository, popupController)
}

class PlayerPresenter(
    private val repository: Repository,
    private val popupController: PopupController,
) : Presenter<PlayerUiState> {
    private val playListRepository: PlayListRepository = repository.playListRepository
    private val mediaControllerRepository: MediaControllerRepository =
        repository.mediaControllerRepository
    private val playerStateMonitoryRepository: PlayerStateMonitoryRepository =
        repository.playerStateMonitoryRepository
    private val sleepTimerRepository: SleepTimerRepository =
        repository.sleepTimerRepository

    @Composable
    override fun present(): PlayerUiState {
        val scope = rememberCoroutineScope()

        val interactingMusicItem by
            playerStateMonitoryRepository.getPlayingMediaStateFlow().collectAsState(null)
        val isPlaying by
            playerStateMonitoryRepository.observeIsPlaying().collectAsState(false)
        val progressFactor by
            playerStateMonitoryRepository.observeProgressFactor().collectAsState(0f)
        val playMode by
            playerStateMonitoryRepository.observePlayMode().collectAsState(PlayMode.REPEAT_ALL)
        val isShuffle by
            playerStateMonitoryRepository.observeIsShuffle().collectAsState(false)
        val duration by
            playerStateMonitoryRepository.observeCurrentPositionMs().collectAsState(0L)
        val isSleepTimerCounting by sleepTimerRepository.observeIsCounting().collectAsState(false)
        var isFavorite by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(interactingMusicItem) {
            getIsFavoriteFlow(interactingMusicItem).collect {
                isFavorite = it
            }
        }

        val eventSink: (PlayerUiEvent) -> Unit by rememberUpdatedState {
            when (it) {
                PlayerUiEvent.OnFavoriteButtonClick -> onFavoriteButtonClick(
                    scope,
                    interactingMusicItem
                )

                PlayerUiEvent.OnNextButtonClick -> next()
                PlayerUiEvent.OnPreviousButtonClick -> previous()
                PlayerUiEvent.OnPlayButtonClick -> togglePlayState(isPlaying)
                PlayerUiEvent.OnShuffleButtonClick -> {
                    mediaControllerRepository.setShuffleModeEnabled(!isShuffle)
                }

                PlayerUiEvent.OnTimerIconClick -> scope.launch {
                    popupController.showDialog(
                        DialogId.SleepCountingDialog
                    )
                }

                is PlayerUiEvent.OnOptionIconClick -> onOptionIconClick(scope, it.mediaItem)
                PlayerUiEvent.OnPlayModeButtonClick -> {
                    val nextPlayMode = playerStateMonitoryRepository.getCurrentPlayMode().next()
                    mediaControllerRepository.setPlayMode(nextPlayMode)
                }

                is PlayerUiEvent.OnProgressChange -> {
                    seekToTime(duration.times(it.progress).toLong())
                }
            }
        }

        return if (interactingMusicItem == null) {
            PlayerUiState.Inactive(eventSink)
        } else {
            PlayerUiState.Active(
                mediaItem = interactingMusicItem!!,
                duration = duration,
                playMode = playMode,
                isShuffle = isShuffle,
                isFavorite = isFavorite,
                isPlaying = isPlaying,
                progress = progressFactor,
                isCounting = isSleepTimerCounting,
                eventSink = eventSink
            )
        }
    }

    private fun onOptionIconClick(scope: CoroutineScope, mediaItem: AudioItemModel) {
        scope.launch {
            val result = popupController.showDialog(
                DialogId.PlayerOption(mediaItem)
            )
            if (result is DialogAction.MediaOptionDialog.ClickItem) {
                repository.onMediaOptionClick(
                    optionItem = result.optionItem,
                    dialog = result.dialog,
                    popupController = popupController
                )
            }
        }
    }

    private fun getIsFavoriteFlow(interactingMusicItem: AudioItemModel?): Flow<Boolean> {
        return if (interactingMusicItem == null) {
            flowOf(false)
        } else {
            playListRepository.isMediaInFavoritePlayListFlow(interactingMusicItem.id)
        }
    }

    private fun onFavoriteButtonClick(scope: CoroutineScope, current: AudioItemModel?) {
        if (current == null) return

        scope.launch {
            playListRepository.toggleFavoriteMedia(current)
        }
    }

    private fun togglePlayState(isPlaying: Boolean) {
        if (isPlaying) {
            mediaControllerRepository.pause()
        } else {
            mediaControllerRepository.play()
        }
    }

    private fun next() {
        mediaControllerRepository.seekToNext()
    }

    private fun previous() {
        mediaControllerRepository.seekToPrevious()
    }

    private fun seekToTime(time: Long) {
        mediaControllerRepository.seekToTime(time)
    }
}

sealed class PlayerUiState(
    open val eventSink: (PlayerUiEvent) -> Unit
) : CircuitUiState {
    data class Inactive(
        override val eventSink: (PlayerUiEvent) -> Unit
    ) : PlayerUiState(eventSink)

    data class Active(
        val isShuffle: Boolean = false,
        val duration: Long = 0L,
        val isFavorite: Boolean = false,
        val playMode: PlayMode = PlayMode.REPEAT_ALL,
        val mediaItem: AudioItemModel,
        val progress: Float,
        val isPlaying: Boolean,
        val isCounting: Boolean,
        override val eventSink: (PlayerUiEvent) -> Unit
    ) : PlayerUiState(eventSink)
}

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
