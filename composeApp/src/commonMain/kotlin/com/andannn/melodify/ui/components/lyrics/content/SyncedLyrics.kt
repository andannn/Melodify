/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.lyrics.content

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.platform.formatTime
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SyncedLyrics(
    modifier: Modifier = Modifier,
    syncedLyric: String,
    lazyListState: LazyListState = rememberLazyListState(),
    presenter: Presenter<SyncedLyricsState> =
        rememberSyncedLyricsPresenter(
            syncedLyric,
            lazyListState,
        ),
) {
    SyncedLyricsContent(
        modifier = modifier,
        lazyListState = lazyListState,
        state = presenter.present(),
    )
}

data class SyncedLyricsState(
    val syncedLyricsLines: List<SyncedLyricsLine>,
    val lyricsState: LyricsState,
    val currentPlayingIndex: Int,
    val eventSink: (SyncedLyricsEvent) -> Unit,
) : CircuitUiState

sealed interface SyncedLyricsEvent {
    data class SeekToTime(
        val time: Long,
    ) : SyncedLyricsEvent
}

data class SyncedLyricsLine(
    val startTimeMs: Long,
    val endTimeMs: Long = 0L,
    private val lyrics: String,
) {
    val lyricsString: String
        get() = lyrics.takeIf { it.isNotBlank() } ?: "[ Music ]"
}

sealed interface LyricsState {
    data object AutoScrolling : LyricsState

    data class Seeking(
        val currentSeekIndex: Int,
    ) : LyricsState

    data class WaitingSeekingResult(
        val requestTimeMs: Long,
    ) : LyricsState
}

@Composable
private fun SyncedLyricsContent(
    state: SyncedLyricsState,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val activeIndex =
        remember(state) {
            when (val lyricsState = state.lyricsState) {
                LyricsState.AutoScrolling -> -1
                is LyricsState.Seeking -> lyricsState.currentSeekIndex
                is LyricsState.WaitingSeekingResult -> -1
            }
        }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val maxHeight = maxHeight
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = maxHeight / 2),
        ) {
            itemsIndexed(
                items = state.syncedLyricsLines,
                key = { _, item -> item.startTimeMs },
            ) { index, item ->
                LyricLine(
                    lyricsLine = item,
                    isPlaying = state.currentPlayingIndex == index,
                    isActive = activeIndex == index,
                    onSeekTimeClick = {
                        state.eventSink.invoke(SyncedLyricsEvent.SeekToTime(item.startTimeMs))
                    },
                )
            }
        }
    }
}

@Composable
private fun LyricLine(
    isPlaying: Boolean,
    isActive: Boolean,
    lyricsLine: SyncedLyricsLine,
    modifier: Modifier = Modifier,
    onSeekTimeClick: () -> Unit = {},
) {
    val transition = updateTransition(targetState = isPlaying, label = "playingState")

    val alpha by transition.animateFloat(label = "alpha") { playing ->
        if (playing) 1f else 0.4f
    }

    val activeTransition = updateTransition(targetState = isActive, label = "playingState")
    val backgroundColor by activeTransition.animateColor(label = "color") { active ->
        if (active) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        }
    }

    val fontColor by activeTransition.animateColor(label = "fontColor") { active ->
        if (active) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    }

    val seekButtonAlpha by activeTransition.animateFloat(label = "seekButtonAlpha") { active ->
        if (active) 1f else 0f
    }

    Box(
        modifier =
            modifier
                .background(backgroundColor)
                .fillMaxWidth(),
    ) {
        Text(
            modifier =
                Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .alpha(alpha),
            text = lyricsLine.lyricsString,
            style = MaterialTheme.typography.headlineSmall,
            color = fontColor,
        )

        if (seekButtonAlpha != 0f) {
            Surface(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .alpha(seekButtonAlpha)
                        .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = onSeekTimeClick,
            ) {
                Row(
                    modifier =
                        Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        modifier = Modifier,
                        text =
                            lyricsLine.startTimeMs.milliseconds.toComponents { minutes, seconds, _ ->
                                formatTime(minutes, seconds)
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun rememberSyncedLyricsPresenter(
    syncedLyric: String,
    lazyListState: LazyListState,
    repository: Repository = LocalRepository.current,
): Presenter<SyncedLyricsState> =
    remember(syncedLyric, lazyListState, repository) {
        SyncedLyricsPresenter(
            syncedLyrics = syncedLyric,
            lazyListState = lazyListState,
            repository = repository,
        )
    }

private class SyncedLyricsPresenter(
    private val syncedLyrics: String,
    val lazyListState: LazyListState,
    repository: Repository,
) : Presenter<SyncedLyricsState> {
    private val playControlRepository = repository.mediaControllerRepository
    private val playerStateMonitoryRepository = repository.playerStateMonitoryRepository
    private var waitingToCancelSeekJob: Job? = null

    @Composable
    override fun present(): SyncedLyricsState {
        val syncedLyricsLines by rememberRetained {
            mutableStateOf(parseSyncedLyrics(syncedLyrics))
        }
        var lyricsState by rememberRetained {
            mutableStateOf<LyricsState>(LyricsState.AutoScrolling)
        }
        var currentPlayingIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        LaunchedEffect(Unit) {
            fun onPositionChanged(currentPositionMs: Long) {
                val state = lyricsState
                if (state is LyricsState.WaitingSeekingResult) {
                    if (state.requestTimeMs != currentPositionMs) {
                        return
                    }

                    lyricsState = LyricsState.AutoScrolling
                }

                currentPlayingIndex =
                    syncedLyricsLines
                        .indexOfFirst {
                            currentPositionMs >= it.startTimeMs && currentPositionMs < it.endTimeMs
                        }.coerceAtLeast(0)

                Napier.d(tag = TAG) { "onPositionChanged: $currentPositionMs, currentIndex $currentPlayingIndex" }
            }

            playerStateMonitoryRepository.observeProgressFactor().collect {
                onPositionChanged(playerStateMonitoryRepository.getCurrentPositionMs())
            }
        }

        LaunchedEffect(Unit) {
            fun onDragStop() {
                Napier.d(tag = TAG) { "onDragStop" }

                waitingToCancelSeekJob =
                    launch {
                        delay(5 * 1000)
                        lyricsState = LyricsState.AutoScrolling
                    }
            }

            fun onDragStart() {
                Napier.d(tag = TAG) { "onDragStart" }
                waitingToCancelSeekJob?.cancel()
                lyricsState =
                    LyricsState.Seeking(
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
                    lyricsState =
                        LyricsState.Seeking(
                            currentSeekIndex = firstVisibleIndex,
                        )
                }
            }
        }

        LaunchedEffect(lazyListState, currentPlayingIndex, lyricsState) {
            if (lyricsState !is LyricsState.AutoScrolling) {
                return@LaunchedEffect
            }

            val info =
                lazyListState.layoutInfo.visibleItemsInfo.firstOrNull {
                    it.index == currentPlayingIndex
                }
            Napier.d(tag = TAG) { "SyncedLyricsView: scroll to $currentPlayingIndex with info $info" }
            if (info != null) {
                lazyListState.animateScrollToItem(
                    currentPlayingIndex,
                    scrollOffset = info.size.div(2f).roundToInt(),
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

private fun parseSyncedLyrics(plainLyrics: String): List<SyncedLyricsLine> {
    val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2})\] (.*)""")
    val matches = regex.findAll(plainLyrics)

    val listWithoutEndTime =
        matches
            .map { match ->
                val (minutesString, secondsString, millisecondsString, lyrics) = match.destructured
                val minutes = minutesString.toIntOrNull() ?: 0
                val seconds = secondsString.toIntOrNull() ?: 0
                val milliSeconds = millisecondsString.toIntOrNull() ?: 0
                val timeMs =
                    minutes
                        .times(60 * 1000)
                        .plus(seconds * 1000)
                        .plus(milliSeconds)
                        .toLong()
                SyncedLyricsLine(startTimeMs = timeMs, lyrics = lyrics)
            }.toList()

    return listWithoutEndTime.mapIndexed { index, item ->
        val nextStartTime = listWithoutEndTime.getOrNull(index + 1)?.startTimeMs ?: Long.MAX_VALUE
        item.copy(endTimeMs = nextStartTime)
    }
}

private const val TAG = "SyncedLyricsState"
