package com.andannn.melodify.util.brightness

import androidx.compose.runtime.mutableStateOf

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
        brightnessState.value = BrightnessState.Manual(brightness)
    }

    override fun resetToSystemBrightness() {
        brightnessController.resetToSystemBrightness()
        brightnessState.value = BrightnessState.Auto
    }
}
