/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.play.control

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.domain.model.next
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.popup.DialogAction
import com.andannn.melodify.shared.compose.popup.LocalPopupController
import com.andannn.melodify.shared.compose.popup.OptionDialog
import com.andannn.melodify.shared.compose.popup.OptionItem
import com.andannn.melodify.shared.compose.popup.PopupController
import com.andannn.melodify.shared.compose.popup.SleepCountingDialog
import com.andannn.melodify.shared.compose.popup.showDialogAndWaitAction
import com.andannn.melodify.shared.compose.usecase.addToNextPlay
import com.andannn.melodify.shared.compose.usecase.addToQueue
import com.andannn.melodify.shared.compose.usecase.openSleepTimer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun rememberPlayerPresenter(
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
): Presenter<PlayerUiState> =
    retainPresenter(
        repository,
        popupController,
    ) {
        PlayerPresenter(repository, popupController)
    }

@Stable
sealed class PlayerUiState(
    open val eventSink: (PlayerUiEvent) -> Unit,
) {
    data class Inactive(
        override val eventSink: (PlayerUiEvent) -> Unit,
    ) : PlayerUiState(eventSink)

    data class Active(
        val isShuffle: Boolean = false,
        val duration: Long = 0L,
        val isFavorite: Boolean = false,
        val playMode: PlayMode = PlayMode.REPEAT_ALL,
        val mediaItem: MediaItemModel,
        val progress: Float,
        val isPlaying: Boolean,
        val isCounting: Boolean,
        override val eventSink: (PlayerUiEvent) -> Unit,
    ) : PlayerUiState(eventSink)
}

sealed interface PlayerUiEvent {
    data object OnFavoriteButtonClick : PlayerUiEvent

    data class OnOptionIconClick(
        val mediaItem: MediaItemModel,
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

private const val TAG = "PlayerPresenter"

private class PlayerPresenter(
    private val repository: Repository,
    private val popupController: PopupController,
) : RetainedPresenter<PlayerUiState>() {
    private val interactingMusicItemFlow =
        repository
            .getPlayingMediaStateFlow()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null,
            )

    private val isPlayingFlow =
        repository
            .observeIsPlaying()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false,
            )

    private val progressFactorFlow =
        repository
            .observeProgressFactor()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = 0f,
            )

    private val playModeFlow =
        repository
            .observePlayMode()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = PlayMode.REPEAT_ALL,
            )

    private val isShuffleFlow =
        repository
            .observeIsShuffle()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false,
            )

    private val durationFlow =
        repository
            .observeCurrentDurationMs()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = 0L,
            )

    private val isSleepTimerCountingFlow =
        repository
            .observeIsCounting()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false,
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isFavoriteFlow =
        interactingMusicItemFlow
            .flatMapLatest { interactingMusicItem ->
                Napier.d(tag = TAG) { "interactingMusicItem $interactingMusicItem" }
                getIsFavoriteFlow(interactingMusicItem)
            }.stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false,
            )

    init {
        Napier.d(tag = TAG) { "init ${this.hashCode()}" }
    }

    @Composable
    override fun present(): PlayerUiState {
        val interactingMusicItem by interactingMusicItemFlow.collectAsStateWithLifecycle()
        val isPlaying by isPlayingFlow.collectAsStateWithLifecycle()
        val progressFactor by progressFactorFlow.collectAsStateWithLifecycle()
        val playMode by playModeFlow.collectAsStateWithLifecycle()
        val isShuffle by isShuffleFlow.collectAsStateWithLifecycle()
        val duration by durationFlow.collectAsStateWithLifecycle()
        val isSleepTimerCounting by isSleepTimerCountingFlow.collectAsStateWithLifecycle()
        val isFavorite by isFavoriteFlow.collectAsStateWithLifecycle()

        val eventSink: (PlayerUiEvent) -> Unit by rememberUpdatedState {
            when (it) {
                PlayerUiEvent.OnFavoriteButtonClick -> {
                    onFavoriteButtonClick(
                        interactingMusicItem,
                    )
                }

                PlayerUiEvent.OnNextButtonClick -> {
                    next()
                }

                PlayerUiEvent.OnPreviousButtonClick -> {
                    previous()
                }

                PlayerUiEvent.OnPlayButtonClick -> {
                    togglePlayState(isPlaying)
                }

                PlayerUiEvent.OnShuffleButtonClick -> {
                    repository.setShuffleModeEnabled(!isShuffle)
                }

                PlayerUiEvent.OnTimerIconClick -> {
                    retainedScope.launch {
                        val result =
                            popupController.showDialogAndWaitAction(
                                SleepCountingDialog,
                            )
                        if (result is DialogAction.SleepTimerCountingDialog.OnCancelTimer) {
                            repository.cancelSleepTimer()
                        }
                    }
                }

                is PlayerUiEvent.OnOptionIconClick -> {
                    onOptionIconClick(it.mediaItem)
                }

                PlayerUiEvent.OnPlayModeButtonClick -> {
                    val nextPlayMode = repository.getCurrentPlayMode().next()
                    repository.setPlayMode(nextPlayMode)
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
                eventSink = eventSink,
            )
        }
    }

    private fun onOptionIconClick(mediaItem: MediaItemModel) {
        retainedScope.launch {
            val result =
                popupController.showDialogAndWaitAction(
                    OptionDialog(
                        options =
                            listOf(
                                OptionItem.PLAY_NEXT,
                                OptionItem.ADD_TO_QUEUE,
                                OptionItem.SLEEP_TIMER,
                            ),
                    ),
                )
            if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
                context(repository, popupController) {
                    when (result.optionItem) {
                        OptionItem.PLAY_NEXT -> {
                            addToNextPlay(listOf(mediaItem))
                        }

                        OptionItem.ADD_TO_QUEUE -> {
                            addToQueue(listOf(mediaItem))
                        }

                        OptionItem.SLEEP_TIMER -> {
                            openSleepTimer()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun getIsFavoriteFlow(interactingMusicItem: MediaItemModel?): Flow<Boolean> =
        if (interactingMusicItem == null) {
            flowOf(false)
        } else {
            repository.isMediaInFavoritePlayListFlow(
                interactingMusicItem.id,
                interactingMusicItem is AudioItemModel,
            )
        }

    private fun onFavoriteButtonClick(current: MediaItemModel?) {
        if (current == null) return

        retainedScope.launch {
            repository.toggleFavoriteMedia(current)
        }
    }

    private fun togglePlayState(isPlaying: Boolean) {
        if (isPlaying) {
            repository.pause()
        } else {
            repository.play()
        }
    }

    private fun next() {
        repository.seekToNext()
    }

    private fun previous() {
        repository.seekToPrevious()
    }

    private fun seekToTime(time: Long) {
        repository.seekToTime(time)
    }
}
