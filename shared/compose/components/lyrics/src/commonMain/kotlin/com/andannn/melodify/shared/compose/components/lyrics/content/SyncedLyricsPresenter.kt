/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.lyrics.content

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "SyncedLyricsPresenter"

@Composable
internal fun retainSyncedLyricsPresenter(
    syncedLyric: String,
    lazyListState: LazyListState,
    repository: Repository = LocalRepository.current,
): Presenter<SyncedLyricsState> =
    retainPresenter(syncedLyric, lazyListState, repository) {
        SyncedLyricsPresenter(
            syncedLyrics = syncedLyric,
            lazyListState = lazyListState,
            repository = repository,
        )
    }

private class SyncedLyricsPresenter(
    private val syncedLyrics: String,
    val lazyListState: LazyListState,
    private val repository: Repository,
) : RetainedPresenter<SyncedLyricsState>() {
    private var waitingToCancelSeekJob: Job? = null

    @Composable
    override fun present(): SyncedLyricsState {
        val syncedLyricsLines by remember {
            mutableStateOf(parseSyncedLyrics(syncedLyrics))
        }
        var lyricsState by remember {
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

            repository.observeProgressFactor().collect {
                onPositionChanged(repository.getCurrentPositionMs())
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

                    lyricsState =
                        LyricsState.WaitingSeekingResult(
                            it.time,
                        )
                    repository.seekToTime(it.time)
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
                SyncedLyricsLine(
                    startTimeMs = timeMs,
                    lyrics = lyrics,
                )
            }.toList()

    return listWithoutEndTime.mapIndexed { index, item ->
        val nextStartTime = listWithoutEndTime.getOrNull(index + 1)?.startTimeMs ?: Long.MAX_VALUE
        item.copy(endTimeMs = nextStartTime)
    }
}
