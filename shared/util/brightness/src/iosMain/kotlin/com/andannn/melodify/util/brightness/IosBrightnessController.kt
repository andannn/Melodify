/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.brightness

class IosBrightnessController : BrightnessController {
    override fun getWindowBrightness(): Float = 0f

    override fun setWindowBrightness(brightness: Float) {
    }

    override fun resetToSystemBrightness() {
    }
}
