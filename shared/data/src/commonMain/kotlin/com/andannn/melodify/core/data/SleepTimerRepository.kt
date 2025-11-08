/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface SleepTimerRepository {
    fun isCounting(): Boolean

    fun observeIsCounting(): Flow<Boolean>

    fun observeRemainTime(): Flow<Duration>

    fun startSleepTimer(duration: Duration)

    fun cancelSleepTimer()
}
