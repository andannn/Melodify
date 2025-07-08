package com.andannn.melodify.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface SleepTimerRepository {
    fun isCounting(): Boolean

    fun observeIsCounting(): Flow<Boolean>

    fun observeRemainTime(): Flow<Duration>

    fun startSleepTimer(duration: Duration)

    fun cancelSleepTimer()
}
