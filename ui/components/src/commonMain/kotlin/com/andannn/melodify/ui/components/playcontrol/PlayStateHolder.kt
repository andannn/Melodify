package com.andannn.melodify.ui.components.playcontrol

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.model.next
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.ui.common.util.getUiRetainedScope
import com.andannn.melodify.ui.components.drawer.DrawerController
import com.andannn.melodify.ui.components.drawer.DrawerEvent
import com.andannn.melodify.ui.components.drawer.model.SheetModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberPlayStateHolder(
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: Repository = getKoin().get(),
    drawerController: DrawerController = getUiRetainedScope()?.get() ?: getKoin().get()
) = remember(
    scope,
    repository,
    drawerController
) {
    PlayStateHolder(
        scope = scope,
        playListRepository = repository.playListRepository,
        mediaControllerRepository = repository.mediaControllerRepository,
        playerStateMonitoryRepository = repository.playerStateMonitoryRepository,
        drawerController = drawerController
    )
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

private const val TAG = "PlayerStateViewModel"

class PlayStateHolder(
    private val scope: CoroutineScope,
    private val playListRepository: PlayListRepository,
    private val mediaControllerRepository: MediaControllerRepository,
    private val playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    private val drawerController: DrawerController,
) {
    private val interactingMusicItem = playerStateMonitoryRepository.playingMediaStateFlow
    private val playStateFlow =
        combine(
            playerStateMonitoryRepository.observeIsPlaying(),
            playerStateMonitoryRepository.observeProgressFactor(),
            playerStateMonitoryRepository.observePlayMode(),
            playerStateMonitoryRepository.observeIsShuffle(),
        ) { isPlaying, progress, playMode, isShuffle ->
            PlayState(isPlaying, progress, playMode, isShuffle)
        }.stateIn(scope, SharingStarted.WhileSubscribed(), PlayState())

    private val isCountingFlow = mediaControllerRepository.observeIsCounting()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isCurrentMediaFavoriteFlow =
        interactingMusicItem
            .distinctUntilChanged()
            .flatMapLatest {
                if (it == null) {
                    return@flatMapLatest flowOf(false)
                }
                playListRepository.isMediaInFavoritePlayListFlow(it.id)
            }.stateIn(scope, SharingStarted.WhileSubscribed(), false)

    var state by mutableStateOf<PlayerUiState>(PlayerUiState.Inactive)
        private set

    private val playerUiStateFlow =
        combine(
            interactingMusicItem,
            playStateFlow,
            isCurrentMediaFavoriteFlow,
            isCountingFlow,
        ) { interactingMusicItem, state, isFavorite, isCounting ->
            if (interactingMusicItem == null) {
                PlayerUiState.Inactive
            } else {
                PlayerUiState.Active(
                    mediaItem = interactingMusicItem,
                    duration = mediaControllerRepository.currentDuration ?: 0L,
                    playMode = state.playMode,
                    isShuffle = state.isShuffle,
                    isFavorite = isFavorite,
                    isPlaying = state.isPlaying,
                    progress = state.playProgress,
                    isCounting = isCounting,
                )
            }
        }

    init {
        scope.launch {
            playerUiStateFlow.collect {
                state = it
            }
        }
    }

    fun onEvent(event: PlayerUiEvent) {
        Napier.d(tag = TAG) { "onEvent: $event" }
        when (event) {
            PlayerUiEvent.OnFavoriteButtonClick -> {
                val current =
                    (state as? PlayerUiState.Active)?.mediaItem
                Napier.d(tag = TAG) { "currentId: $current" }
                if (current == null) return

                scope.launch {
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
                scope.launch {
                    drawerController.onEvent(
                        DrawerEvent.OnShowBottomDrawer(SheetModel.PlayerOptionSheet(event.mediaItem)),
                    )
                }
            }

            is PlayerUiEvent.OnProgressChange -> {
                val time =
                    with((state as PlayerUiState.Active)) {
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
        val state = state
        if (state is PlayerUiState.Active) {
            state.let {
                if (state.isPlaying) {
                    mediaControllerRepository.pause()
                } else {
                    mediaControllerRepository.play()
                }
            }
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
        val progress: Float,
        val isPlaying: Boolean,
        val isCounting: Boolean,
    ) : PlayerUiState()
}
