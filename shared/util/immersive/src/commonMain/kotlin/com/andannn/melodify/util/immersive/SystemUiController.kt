/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.immersive

interface SystemUiController {
    fun setSystemUiVisibility(visible: Boolean)

    fun setImmersiveModeEnabled(enable: Boolean)

    fun setSystemUiDarkTheme(isDark: Boolean)

    fun setSystemUiStyleAuto()
}
