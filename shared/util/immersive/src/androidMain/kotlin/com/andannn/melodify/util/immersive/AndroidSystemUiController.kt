/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.immersive

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class AndroidSystemUiController(
    private val activity: ComponentActivity,
) : SystemUiController {
    private val windowInsetsController =
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)

    override fun setSystemUiVisibility(visible: Boolean) {
        if (visible) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    override fun setImmersiveModeEnabled(enable: Boolean) {
        if (enable) {
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    override fun setSystemUiDarkTheme(isDark: Boolean) {
        if (isDark) {
            activity.enableEdgeToEdge(
                statusBarStyle =
                    SystemBarStyle.dark(
                        android.graphics.Color.TRANSPARENT,
                    ),
            )
        } else {
            activity.enableEdgeToEdge(
                statusBarStyle =
                    SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ),
            )
        }
    }

    override fun setSystemUiStyleAuto() {
        activity.enableEdgeToEdge()
    }
}
