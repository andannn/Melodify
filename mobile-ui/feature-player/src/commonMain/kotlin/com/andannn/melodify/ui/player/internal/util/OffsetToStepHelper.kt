package com.andannn.melodify.ui.player.internal.util

import kotlin.math.absoluteValue
import kotlin.math.floor

internal class OffsetToStepHelper(
    val stepThreshold: Float = DEFAULT_STEP_THRESHOLD,
    val onStep: (isPositive: Boolean) -> Unit,
) {
    private var currentIsPositive: Boolean? = null
    private var accumulateOffset: Float = 0f

    fun onOffset(offset: Float) {
        val old = currentIsPositive
        currentIsPositive = offset > 0
        if (old != currentIsPositive) {
            // reset
            accumulateOffset = 0f
        }

        accumulateOffset += offset
        val callTimes = floor(accumulateOffset.absoluteValue / stepThreshold).toInt()
        repeat(callTimes) {
            onStep(currentIsPositive!!)
        }

        accumulateOffset -= callTimes * stepThreshold * (if (currentIsPositive!!) 1 else -1)
    }

    fun reset() {
        currentIsPositive = null
        accumulateOffset = 0f
    }

    companion object {
        private const val DEFAULT_STEP_THRESHOLD = 20f
    }
}
