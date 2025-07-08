package com.andannn.melodify.core.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CoroutineTimer(
    private val delayMs: Long = 1000 / 30L,
    val action: () -> Unit,
) : CoroutineScope {
    private var jobTracker: Job? = null

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    fun startTicker() {
        if (jobTracker != null) {
            return
        }

        jobTracker =
            launch {
                while (true) {
                    action()
                    delay(delayMs)
                }
            }
    }

    fun stopTicker() {
        jobTracker?.cancel()
        jobTracker = null
    }
}
