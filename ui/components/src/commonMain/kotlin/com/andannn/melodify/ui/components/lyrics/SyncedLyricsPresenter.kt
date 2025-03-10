package com.andannn.melodify.ui.components.lyrics

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.andannn.melodify.core.data.Repository
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class SyncedLyricsLine(
    val startTimeMs: Long,
    val endTimeMs: Long = 0L,
    val lyrics: String,
)

private const val TAG = "SyncedLyricsState"

sealed interface LyricsState {
    data object AutoScrolling : LyricsState
    data class Seeking(val currentSeekIndex: Int) : LyricsState
    data class WaitingSeekingResult(val requestTimeMs: Long) : LyricsState
}

class SyncedLyricsPresenter(
    private val syncedLyrics: String,
    private val lazyListState: LazyListState,
    repository: Repository,
) : Presenter<SyncedLyricsState> {
    private val playControlRepository = repository.mediaControllerRepository
    private val playerStateMonitoryRepository = repository.playerStateMonitoryRepository
    private var waitingToCancelSeekJob: Job? = null

    @Composable
    override fun present(): SyncedLyricsState {
        val syncedLyricsLines by remember {
            mutableStateOf(parseSyncedLyrics(syncedLyrics))
        }
        var lyricsState by remember {
            mutableStateOf<LyricsState>(LyricsState.AutoScrolling)
        }
        var currentPlayingIndex by remember {
            mutableIntStateOf(0)
        }
        Napier.d(tag = TAG) { "JQN: lyricsState $lyricsState" }

        LaunchedEffect(Unit) {
            fun onPositionChanged(currentPositionMs: Long) {
                val state = lyricsState
                if (state is LyricsState.WaitingSeekingResult) {
                    if (state.requestTimeMs != currentPositionMs) {
                        return
                    }

                    lyricsState = LyricsState.AutoScrolling
                }

                currentPlayingIndex = syncedLyricsLines
                    .indexOfFirst {
                        currentPositionMs >= it.startTimeMs && currentPositionMs < it.endTimeMs
                    }
                    .coerceAtLeast(0)

                Napier.d(tag = TAG) { "onPositionChanged: $currentPositionMs, currentIndex $currentPlayingIndex" }
            }

            playerStateMonitoryRepository.observeProgressFactor().collect {
                onPositionChanged(playerStateMonitoryRepository.getCurrentPositionMs())
            }
        }

        LaunchedEffect(Unit) {
            fun onDragStop() {
                Napier.d(tag = TAG) { "onDragStop" }

                waitingToCancelSeekJob = launch {
                    delay(5 * 1000)
                    lyricsState = LyricsState.AutoScrolling
                }
            }

            fun onDragStart() {
                Napier.d(tag = TAG) { "onDragStart" }
                waitingToCancelSeekJob?.cancel()
                lyricsState = LyricsState.Seeking(
                    lazyListState.firstVisibleItemIndex,
                )
            }

            lazyListState.interactionSource.interactions.collect {
                when (it) {
                    is DragInteraction.Start -> {
                        onDragStart()
                    }

                    is DragInteraction.Stop -> {
                        onDragStop()
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            snapshotFlow {
                lazyListState.firstVisibleItemIndex
            }.collect { firstVisibleIndex ->
                Napier.d(tag = TAG) { "First visible item index: $firstVisibleIndex" }
                if (lyricsState is LyricsState.Seeking) {
                    lyricsState = LyricsState.Seeking(
                        currentSeekIndex = firstVisibleIndex,
                    )
                }
            }
        }

        LaunchedEffect(lazyListState, currentPlayingIndex, lyricsState) {
            if (lyricsState !is LyricsState.AutoScrolling) {
                return@LaunchedEffect
            }

            val info = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull {
                it.index == currentPlayingIndex
            }
            Napier.d(tag = TAG) { "SyncedLyricsView: scroll to $currentPlayingIndex with info ${info.toString()}" }
            if (info != null) {
                lazyListState.animateScrollToItem(
                    currentPlayingIndex,
                    scrollOffset = info.size.div(2f).roundToInt()
                )
            } else {
                lazyListState.animateScrollToItem(currentPlayingIndex)
            }
        }

        return SyncedLyricsState(
            syncedLyricsLines = syncedLyricsLines,
            lyricsState = lyricsState,
            currentPlayingIndex = currentPlayingIndex,
        ) {
            when (it) {
                is SyncedLyricsEvent.SeekToTime -> {
                    Napier.d(tag = TAG) { "onSeekTimeClick: ${it.time}" }

                    lyricsState = LyricsState.WaitingSeekingResult(it.time)
                    playControlRepository.seekToTime(it.time)
                }
            }
        }
    }
}

fun parseSyncedLyrics(plainLyrics: String): List<SyncedLyricsLine> {
    val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2})\] (.*)""")
    val matches = regex.findAll(plainLyrics)

    val listWithoutEndTime = matches
        .map { match ->
            val (minutesString, secondsString, millisecondsString, lyrics) = match.destructured
            val minutes = minutesString.toIntOrNull() ?: 0
            val seconds = secondsString.toIntOrNull() ?: 0
            val milliSeconds = millisecondsString.toIntOrNull() ?: 0
            val timeMs = minutes.times(60 * 1000)
                .plus(seconds * 1000)
                .plus(milliSeconds)
                .toLong()
            SyncedLyricsLine(startTimeMs = timeMs, lyrics = lyrics)
        }
        .toList()

    return listWithoutEndTime.mapIndexed { index, item ->
        val nextStartTime = listWithoutEndTime.getOrNull(index + 1)?.startTimeMs ?: Long.MAX_VALUE
        item.copy(endTimeMs = nextStartTime)
    }
}

data class SyncedLyricsState(
    val syncedLyricsLines: List<SyncedLyricsLine>,
    val lyricsState: LyricsState,
    val currentPlayingIndex: Int,
    val eventSink: (SyncedLyricsEvent) -> Unit,
) : CircuitUiState

sealed interface SyncedLyricsEvent {
    data class SeekToTime(val time: Long) : SyncedLyricsEvent
}