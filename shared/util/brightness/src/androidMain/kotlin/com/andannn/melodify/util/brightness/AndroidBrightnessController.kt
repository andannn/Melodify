/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.brightness

import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import com.andannn.melodify.util.brightness.BrightnessController.Companion.SYSTEM_BRIGHTNESS

class AndroidBrightnessController(
    private val activity: Activity,
) : BrightnessController {
    private val window: Window
        get() = activity.window

    override fun getWindowBrightness(): Float {
        val currentBrightness = window.attributes.screenBrightness
        return if (currentBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            // System brightness
            SYSTEM_BRIGHTNESS
        } else {
            currentBrightness
        }
    }

    override fun setWindowBrightness(brightness: Float) {
        val layoutParams = window.attributes

        var newBrightness = brightness
        if (brightness > 1.0f) newBrightness = 1.0f
        if (brightness < 0.0f && brightness != WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            newBrightness = 0.0f
        }

        layoutParams.screenBrightness = newBrightness
        window.attributes = layoutParams
    }

    override fun resetToSystemBrightness() {
        val params = window.attributes

        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

        window.attributes = params
    }

    private fun getSystemBrightnessFloat(context: Context): Float {
        val systemBrightnessInt = getSystemBrightness(context)

        return (systemBrightnessInt / 255.0f).coerceIn(0f, 1f)
    }
}

private fun getSystemBrightness(context: Context): Int =
    try {
        Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
        )
    } catch (e: Settings.SettingNotFoundException) {
        e.printStackTrace()
        127
    }
