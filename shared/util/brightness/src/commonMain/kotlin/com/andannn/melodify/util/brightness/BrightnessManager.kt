/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.brightness

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberBrightnessManager(brightnessController: BrightnessController = LocalBrightnessController.current) =
    remember(brightnessController) {
        BrightnessManager(brightnessController)
    }

sealed interface BrightnessState {
    data object Auto : BrightnessState

    data class Manual(
        val brightness: Float,
    ) : BrightnessState
}

class BrightnessManager(
    private val brightnessController: BrightnessController,
) : BrightnessController by brightnessController {
    val brightnessState = mutableStateOf<BrightnessState>(BrightnessState.Auto)

    override fun setWindowBrightness(brightness: Float) {
        brightnessController.setWindowBrightness(brightness)
        brightnessState.value = BrightnessState.Manual(getWindowBrightness())
    }

    override fun resetToSystemBrightness() {
        brightnessController.resetToSystemBrightness()
        brightnessState.value = BrightnessState.Auto
    }
}
