/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.play.control.internal

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.platform.formatTime
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.last_watched_jump
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun ResumePointIndicator(
    videoId: Long,
    modifier: Modifier = Modifier,
    presenter: Presenter<ResumePointState> = retainResumePointPresenter(videoId),
) {
    val state = presenter.present()
    val savedResumePointMs = state.savedResumePointMs
    val alphaAnim = remember { Animatable(1f) }
    val isIndicatorShown = state.isIndicatorShown
    val scope = rememberCoroutineScope()

    LaunchedEffect(videoId, savedResumePointMs, isIndicatorShown) {
        if (savedResumePointMs != null && !isIndicatorShown) {
            alphaAnim.snapTo(1f)
            delay(10000)
            alphaAnim.animateTo(0f)
            state.eventSink.invoke(ResumePointEvent.OnTimeoutResume)
        }
    }

    if (alphaAnim.value != 0f && savedResumePointMs != null && !isIndicatorShown) {
        ResumePointIndicatorContent(
            modifier =
                modifier.graphicsLayer {
                    alpha = alphaAnim.value
                },
            savedResumePointMs = savedResumePointMs,
            onAccept = {
                state.eventSink.invoke(ResumePointEvent.OnAcceptResume)
                scope.launch {
                    alphaAnim.animateTo(0f)
                }
            },
            onDeny = {
                state.eventSink.invoke(ResumePointEvent.OnDenyResume)
                scope.launch {
                    alphaAnim.animateTo(0f)
                }
            },
        )
    }
}

@Composable
internal fun ResumePointIndicatorContent(
    savedResumePointMs: Long,
    modifier: Modifier = Modifier,
    onAccept: () -> Unit = {},
    onDeny: () -> Unit = {},
) {
    val labelString = remember(savedResumePointMs) { formatDuration(savedResumePointMs) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.Black.copy(alpha = 0.8f),
        contentColor = Color.White,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .clickable { onAccept() }
                    .height(IntrinsicSize.Max)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = stringResource(Res.string.last_watched_jump, labelString),
                style = MaterialTheme.typography.bodyMedium,
            )

            VerticalDivider(
                color = Color.White.copy(alpha = 0.2f),
            )

            IconButton(
                onClick = onDeny,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.7f),
                )
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val d = millis.milliseconds
    val minutes = d.inWholeMinutes
    val seconds = d.inWholeSeconds % 60
    return formatTime(minutes, seconds.toInt())
}

@Preview
@Composable
private fun ResumePointIndicatorContentPreview() {
    MelodifyTheme {
        ResumePointIndicatorContent(
            savedResumePointMs = 1000,
        )
    }
}
