/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.player

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

sealed interface SleepTimeCounterState {
    data object Idle : SleepTimeCounterState

    data class Counting(
        val remain: Duration,
    ) : SleepTimeCounterState

    data object Finish : SleepTimeCounterState
}

interface SleepTimeCounterProvider {
    val counterState: SleepTimeCounterState

    fun getCounterStateFlow(): Flow<SleepTimeCounterState>
}

interface SleepTimerController : SleepTimeCounterProvider {
    fun startTimer(duration: Duration)

    fun cancelTimer()
}
