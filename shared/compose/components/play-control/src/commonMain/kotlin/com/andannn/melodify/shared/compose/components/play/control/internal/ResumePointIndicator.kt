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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.platform.formatTime
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
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
    modifier: Modifier = Modifier,
    savedResumePointMs: Long,
    onAccept: () -> Unit,
    onDeny: () -> Unit,
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

@Composable
internal fun retainResumePointPresenter(
    videoId: Long,
    repository: Repository = LocalRepository.current,
) = retainPresenter(
    videoId,
    repository,
) {
    ResumePointPresenter(
        videoId = videoId,
        repository = repository,
    )
}

internal const val TAG = "ResumePointIndicator"

internal class ResumePointPresenter(
    private val videoId: Long,
    private val repository: Repository,
) : RetainedPresenter<ResumePointState>() {
    private val resumePointFlow = MutableStateFlow<Long?>(null)
    private val isIndicatorShownFlow = MutableStateFlow<Boolean>(false)

    private val userActionCompleter = CompletableDeferred<UserAction>()

    init {
        Napier.d(tag = TAG) { "init $videoId" }
        retainedScope.launch {
            run jumpOrNoneOp@{
                val savedState =
                    repository
                        .getResumePointMsFlow(videoId)
                        .first()

                if (savedState == null) {
                    Napier.d(tag = TAG) { "no saved progress $videoId" }
                    return@jumpOrNoneOp
                }

                val (savePointMs, isFinished) = savedState
                if (isFinished) {
                    Napier.d(tag = TAG) { "this video is marked finished $videoId" }
                    return@jumpOrNoneOp
                }

                if (savePointMs < MIN_RESUME_POINT_MS) {
                    Napier.d(tag = TAG) { "saved point is less than $MIN_RESUME_POINT_MS milliseconds $videoId" }
                    return@jumpOrNoneOp
                }

                resumePointFlow.value = savePointMs

                when (userActionCompleter.await()) {
                    UserAction.ACCEPT -> {
                        // Jump to saved Position
                        Napier.d(tag = TAG) { "seekToTime: $savePointMs" }
                        repository.seekToTime(savePointMs)
                    }

                    else -> {
                        // Noop
                    }
                }
            }

            // start to record playing progress.
            launchMarkProgressTask()
        }
    }

    private fun CoroutineScope.launchMarkProgressTask() {
        launch {
            while (isActive) {
                delay(1000)
                Napier.d(tag = TAG) { "markProgress. id: $videoId, progress: ${repository.getCurrentPositionMs()}" }
                repository.savePlayProgress(videoId, repository.getCurrentPositionMs())
                delay(4000)
            }
        }
    }

    override fun onClear() {
        Napier.d(tag = TAG) { "clear $videoId" }
    }

    @Composable
    override fun present(): ResumePointState {
        val resumePointMs by resumePointFlow.collectAsStateWithLifecycle()
        val isIndicatorShown by isIndicatorShownFlow.collectAsStateWithLifecycle()
        return ResumePointState(
            savedResumePointMs = resumePointMs,
            isIndicatorShown = isIndicatorShown,
        ) { event ->
            if (!userActionCompleter.isCompleted) {
                when (event) {
                    ResumePointEvent.OnAcceptResume -> userActionCompleter.complete(UserAction.ACCEPT)
                    ResumePointEvent.OnDenyResume -> userActionCompleter.complete(UserAction.DENY)
                    ResumePointEvent.OnTimeoutResume -> userActionCompleter.complete(UserAction.TIMEOUT)
                }
                isIndicatorShownFlow.value = true
            }
        }
    }

    companion object {
        private const val MIN_RESUME_POINT_MS = 5000L
    }
}

internal sealed interface ResumePointEvent {
    data object OnAcceptResume : ResumePointEvent

    data object OnDenyResume : ResumePointEvent

    data object OnTimeoutResume : ResumePointEvent
}

@Stable
internal data class ResumePointState(
    val savedResumePointMs: Long?,
    val isIndicatorShown: Boolean = false,
    val eventSink: (ResumePointEvent) -> Unit,
)

private enum class UserAction {
    ACCEPT,
    DENY,
    TIMEOUT,
}

private fun formatDuration(millis: Long): String {
    val d = millis.milliseconds
    val minutes = d.inWholeMinutes
    val seconds = d.inWholeSeconds % 60
    return formatTime(minutes, seconds.toInt())
}
