/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.play.control.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.shared.compose.common.LocalRepository
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
