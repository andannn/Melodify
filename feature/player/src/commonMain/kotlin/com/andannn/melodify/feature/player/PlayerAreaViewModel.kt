package com.andannn.melodify.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.feature.common.GlobalUiController
import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.core.data.LyricRepository
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.model.next
import com.andannn.melodify.feature.common.drawer.SheetModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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

    data class OnProgressChange(val progress: Float) : PlayerUiEvent

    data class OnSwapPlayQueue(val from: Int, val to: Int) : PlayerUiEvent

    data class OnDeleteMediaItem(val index: Int) : PlayerUiEvent

    data class OnItemClickInQueue(val item: AudioItemModel) : PlayerUiEvent

    data class OnSeekLyrics(val timeMs: Long) : PlayerUiEvent
}

private const val TAG = "PlayerStateViewModel"

class PlayerStateViewModel(
    private val mediaContentRepository: MediaContentRepository,
    private val mediaControllerRepository: MediaControllerRepository,
    private val lyricRepository: LyricRepository,
    private val playerStateMonitoryRepository: PlayerStateMonitoryRepository,
    private val globalUiController: GlobalUiController
) : ViewModel() {
    private val interactingMusicItem = playerStateMonitoryRepository.playingMediaStateFlow
    private val playStateFlow = combine(
        playerStateMonitoryRepository.observeIsPlaying(),
        playerStateMonitoryRepository.observeProgressFactor(),
        playerStateMonitoryRepository.observePlayMode(),
        playerStateMonitoryRepository.observeIsShuffle()
    ) { isPlaying, progress, playMode, isShuffle ->
        PlayState(isPlaying, progress, playMode, isShuffle)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlayState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val lyricFlow: Flow<LyricState> = interactingMusicItem
        .filterNotNull()
        .flatMapLatest {
            lyricRepository.getLyricByMediaIdFlow(it.id)
                .map<LyricModel?, LyricState> { lyricOrNull -> LyricState.Loaded(lyricOrNull) }
                .onStart { emit(LyricState.Loading) }
        }

    private val playListQueueFlow = playerStateMonitoryRepository.playListQueueStateFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isCurrentMediaFavoriteFlow = interactingMusicItem
        .distinctUntilChanged()
        .flatMapLatest {
            if (it == null) {
                return@flatMapLatest flowOf(false)
            }
            mediaContentRepository.isMediaInFavoritePlayListFlow(it.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val playerUiStateFlow =
        combine(
            interactingMusicItem,
            playStateFlow,
            playListQueueFlow,
            lyricFlow,
            isCurrentMediaFavoriteFlow,
        ) { interactingMusicItem, state, playListQueue, lyric, isFavorite ->
            if (interactingMusicItem == null) {
                PlayerUiState.Inactive
            } else {
                PlayerUiState.Active(
                    lyric = lyric,
                    mediaItem = interactingMusicItem,
                    duration = mediaControllerRepository.duration ?: 0L,
                    playMode = state.playMode,
                    isShuffle = state.isShuffle,
                    isFavorite = isFavorite,
                    playListQueue = playListQueue,
                    isPlaying = state.isPlaying,
                    progress = state.playProgress,
                )
            }
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlayerUiState.Inactive)

    init {
        viewModelScope.launch {
            interactingMusicItem
                .filterNotNull()
                .distinctUntilChanged()
                .onEach { audio ->
                    lyricRepository.tryGetLyricOrIgnore(
                        mediaId = audio.id,
                        trackName = audio.name,
                        artistName = audio.artist,
                        albumName = audio.album,
                    )
                }
                .collect {}
        }
    }

    fun onEvent(event: PlayerUiEvent) {
        Napier.d(tag = TAG) { "onEvent: $event" }
        when (event) {
            PlayerUiEvent.OnFavoriteButtonClick -> {
                val currentId =
                    (playerUiStateFlow.value as? PlayerUiState.Active)?.mediaItem?.id
                Napier.d(tag = TAG) { "currentId: $currentId" }
                if (currentId == null) return

                viewModelScope.launch {
                    onToggleFavoriteState(currentId)
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
                    globalUiController.updateBottomSheet(
                        SheetModel.PlayerOptionSheet(event.mediaItem)
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

            is PlayerUiEvent.OnSwapPlayQueue -> {
                mediaControllerRepository.moveMediaItem(event.from, event.to)
            }

            is PlayerUiEvent.OnItemClickInQueue -> {
                val state = playerUiStateFlow.value as PlayerUiState.Active
                mediaControllerRepository.seekMediaItem(
                    mediaItemIndex = state.playListQueue.indexOf(event.item)
                )
            }

            is PlayerUiEvent.OnDeleteMediaItem -> {
                mediaControllerRepository.removeMediaItem(event.index)
            }

            is PlayerUiEvent.OnSeekLyrics -> {
                seekToTime(event.timeMs)
            }
        }
    }

    private suspend fun onToggleFavoriteState(mediaId: String) {
        if (isCurrentMediaFavoriteFlow.value) {
            Napier.d(tag = TAG) { "Add to favorite start: $mediaId" }
            val failedIndex =
                mediaContentRepository.removeMusicFromFavoritePlayList(listOf(mediaId))
            Napier.d(tag = TAG) { "Add to favorite done: failedIndex: $failedIndex" }

        } else {
            Napier.d(tag = TAG) { "Add to favorite start: $mediaId" }
            val failedIndex =
                mediaContentRepository.addMusicToFavoritePlayList(listOf(mediaId))
            Napier.d(tag = TAG) { "Add to favorite done: failedIndex: $failedIndex" }
        }
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
    val isShuffle: Boolean = false
)

sealed class LyricState {
    data object Loading : LyricState()

    data class Loaded(val lyric: LyricModel?) : LyricState()
}

sealed class PlayerUiState {
    data object Inactive : PlayerUiState()

    data class Active(
        val lyric: LyricState = LyricState.Loading,
        val isShuffle: Boolean = false,
        val duration: Long = 0L,
        val isFavorite: Boolean = false,
        val playMode: PlayMode = PlayMode.REPEAT_ALL,
        val mediaItem: AudioItemModel,
        val playListQueue: List<AudioItemModel>,
        val progress: Float,
        val isPlaying: Boolean,
    ) : PlayerUiState()
}
