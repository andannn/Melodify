package com.andannn.melodify.util.brightness

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.Flow

val LocalBrightnessController: ProvidableCompositionLocal<BrightnessController> =
    staticCompositionLocalOf {
        TODO("Provide BrightnessController")
    }

interface BrightnessController {
    /**
     * Get current window brightness.
     *
     * @return current window brightness(Range: 0.0 - 1.0).
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
}
