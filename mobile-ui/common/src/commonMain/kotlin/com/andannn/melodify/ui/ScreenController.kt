/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.keepScreenOn
import com.andannn.melodify.util.orientation.ScreenOrientationController

val LocalScreenOrientationController: ProvidableCompositionLocal<ScreenOrientationController> =
    staticCompositionLocalOf {
        error("ScreenOrientationController not provided")
    }

/**
 * Set keep screen on when this composable is active.
 * Set keep screen off when this composable is disposed.
 */
@Composable
fun KeepScreenOnEffect() {
    Spacer(Modifier.keepScreenOn())
}
