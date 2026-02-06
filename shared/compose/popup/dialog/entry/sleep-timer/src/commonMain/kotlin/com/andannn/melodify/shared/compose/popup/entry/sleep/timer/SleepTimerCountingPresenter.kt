/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sleep.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
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
            .stateInRetainedModel(
                initialValue = 0.seconds,
            )

    @Composable
    override fun present(): CounterState {
        val remainTime by remainedTimeFlow.collectAsStateWithLifecycle()
        return CounterState(remainTime)
    }
}
