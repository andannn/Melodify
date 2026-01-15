/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.internal.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun retainCounterPresenter(repository: Repository = LocalRepository.current) =
    retainPresenter(
        repository,
    ) {
        SleepTimerCountingPresenter(repository)
    }

internal data class CounterState(
    val remainTime: Duration,
)

private class SleepTimerCountingPresenter(
    repository: Repository,
) : RetainedPresenter<CounterState>() {
    private val remainedTimeFlow =
        repository
            .observeRemainTime()
            .stateIn(
                retainedScope,
                initialValue = 0.seconds,
                started = WhileSubscribed(5000),
            )

    @Composable
    override fun present(): CounterState {
        val remainTime by remainedTimeFlow.collectAsStateWithLifecycle()
        return CounterState(remainTime)
    }
}
