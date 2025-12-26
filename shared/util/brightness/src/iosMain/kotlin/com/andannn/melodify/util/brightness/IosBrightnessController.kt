/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.brightness

import platform.UIKit.UIScreen

class IosBrightnessController : BrightnessController {
    private var originalSystemBrightness: Float? = null

    init {
        originalSystemBrightness = UIScreen.mainScreen.brightness.toFloat()
    }

    override fun getWindowBrightness(): Float = UIScreen.mainScreen.brightness.toFloat()

    override fun setWindowBrightness(brightness: Float) {
        UIScreen.mainScreen.brightness = brightness.coerceIn(0f, 1f).toDouble()
    }

    override fun resetToSystemBrightness() {
        UIScreen.mainScreen.brightness = originalSystemBrightness?.toDouble() ?: return
    }
}
