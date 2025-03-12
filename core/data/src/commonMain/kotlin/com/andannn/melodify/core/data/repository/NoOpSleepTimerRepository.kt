package com.andannn.melodify.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration

open class NoOpSleepTimerRepository: SleepTimerRepository {
    override fun isCounting(): Boolean = false

    override fun observeIsCounting(): Flow<Boolean> = flowOf(false)

    override fun observeRemainTime(): Flow<Duration> = flowOf()

    override fun startSleepTimer(duration: Duration) {}

    override fun cancelSleepTimer() {}
}