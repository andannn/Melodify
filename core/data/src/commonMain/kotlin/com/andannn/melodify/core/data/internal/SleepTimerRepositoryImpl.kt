/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.player.SleepTimeCounterState
import com.andannn.melodify.core.player.SleepTimerController
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class SleepTimerRepositoryImpl(
    private val sleepTimerController: SleepTimerController,
) : SleepTimerRepository {
    override fun isCounting(): Boolean = sleepTimerController.counterState is SleepTimeCounterState.Counting

    override fun observeIsCounting() =
        sleepTimerController
            .getCounterStateFlow()
            .map { it is SleepTimeCounterState.Counting }
            .distinctUntilChanged()

    override fun observeRemainTime() =
        sleepTimerController
            .getCounterStateFlow()
            .takeWhile {
                it !is SleepTimeCounterState.Idle
            }.map {
                when (it) {
                    is SleepTimeCounterState.Counting -> it.remain
                    SleepTimeCounterState.Finish -> 0.seconds
                    SleepTimeCounterState.Idle -> error("")
                }
            }

    override fun startSleepTimer(duration: Duration) {
        sleepTimerController.startTimer(duration)
    }

    override fun cancelSleepTimer() {
        sleepTimerController.cancelTimer()
    }
}
