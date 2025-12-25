/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.brightness

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalBrightnessController: ProvidableCompositionLocal<BrightnessController> =
    staticCompositionLocalOf {
        TODO("Provide BrightnessController")
    }

interface BrightnessController {
    /**
     * Get current window brightness.
     *
     * @return current window brightness(Range: 0.0 - 1.0). or [BrightnessController.SYSTEM_BRIGHTNESS] if not adjust window brightness.
     */
    fun getWindowBrightness(): Float

    /**
     * Set window brightness.
     *
     * @param brightness window brightness(Range: 0.0 - 1.0).
     */
    fun setWindowBrightness(brightness: Float)

    /**
     * Reset window brightness to follow system brightness.
     */
    fun resetToSystemBrightness()

    companion object {
        const val SYSTEM_BRIGHTNESS = -1f
    }
}

/**
 * Adjust window brightness.
 *
 * @param offset brightness offset.
 */
fun BrightnessController.adjustBrightness(offset: Float) {
    val old = getWindowBrightness()
    if (old == BrightnessController.SYSTEM_BRIGHTNESS) {
        if (offset > 0) {
            setWindowBrightness(offset)
        }
        return
    }

    val new = old + offset
    if (new <= 0f) {
        resetToSystemBrightness()
    } else {
        setWindowBrightness(old + offset)
    }
}
