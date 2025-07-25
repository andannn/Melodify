/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.lyrics

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.platform.formatTime
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SyncedLyrics(
    modifier: Modifier = Modifier,
    syncedLyric: String,
    presenter: SyncedLyricsPresenter = rememberSyncedLyricsPresenter(syncedLyric),
) {
    SyncedLyricsContent(
        modifier = modifier,
        lazyListState = presenter.lazyListState,
        state = presenter.present(),
    )
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
            Color.Transparent
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
            text = lyricsLine.lyrics,
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
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
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
