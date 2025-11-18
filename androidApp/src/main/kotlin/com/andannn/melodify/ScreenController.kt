/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.aakira.napier.Napier

private const val TAG = "ScreenController"

val LocalScreenController: ProvidableCompositionLocal<ScreenController> =
    staticCompositionLocalOf {
        error("ScreenOrientationController not provided")
    }

@Suppress("ktlint:standard:function-naming")
fun ScreenOrientationController(activity: MainActivity): ScreenController = ScreenControllerImpl(activity)

interface ScreenController {
    val isCurrentPortrait: Boolean

    fun requestLandscape()

    fun isRequestLandscape(): Boolean

    fun cancelRequest()
}

private class ScreenControllerImpl(
    private val activity: Activity,
) : ScreenController {
    override val isCurrentPortrait: Boolean
        get() = activity.orientation == Configuration.ORIENTATION_PORTRAIT

    override fun requestLandscape() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun isRequestLandscape(): Boolean = activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    override fun cancelRequest() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}

private val Activity.orientation
    get() = resources?.configuration?.orientation ?: Configuration.ORIENTATION_UNDEFINED

/**
 * Set immersive mode when this composable is active.
 * Set immersive mode off when this composable is disposed.
 */
@Composable
fun ImmersiveModeEffect() {
    val activity = LocalActivity.current ?: return
    val windowInsetsController =
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)
    DisposableEffect(Unit) {
        Napier.d(tag = TAG) { "set systemBarsBehavior BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE" }
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            Napier.d(tag = TAG) { "set systemBarsBehavior BEHAVIOR_DEFAULT" }
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}
