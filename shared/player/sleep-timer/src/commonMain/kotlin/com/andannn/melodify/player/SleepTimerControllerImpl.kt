/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.player

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "SleepTimerController"

class SleepTimerControllerImpl :
    SleepTimerController,
    CoroutineScope {
    private val _counterState = MutableStateFlow<SleepTimeCounterState>(SleepTimeCounterState.Idle)

    private var countingJob: Job? = null

    override val counterState: SleepTimeCounterState
        get() = _counterState.value

    override fun getCounterStateFlow() = _counterState

    override fun startTimer(duration: Duration) {
        countingJob =
            launch {
                var currentRemainTime = duration

                while (true) {
                    _counterState.value = SleepTimeCounterState.Counting(currentRemainTime)

                    Napier.d(tag = TAG) { "_counterState ${_counterState.value}" }

                    delay(1000)
                    currentRemainTime = currentRemainTime.minus(1.seconds)

                    if (currentRemainTime.isNegative()) {
                        _counterState.value = SleepTimeCounterState.Finish
                        Napier.d(tag = TAG) { "_counterState ${_counterState.value}" }
                        break
                    }
                }
            }
    }

    override fun cancelTimer() {
        countingJob?.cancel()
        _counterState.value = SleepTimeCounterState.Idle
        Napier.d(tag = TAG) { "_counterState ${_counterState.value}" }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}
