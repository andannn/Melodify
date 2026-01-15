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

object NoActionSystemUiController : SystemUiController {
    override fun setSystemUiVisibility(visible: Boolean) {
    }

    override fun setImmersiveModeEnabled(enable: Boolean) {
    }

    override fun setSystemUiDarkTheme(isDark: Boolean) {
    }

    override fun setSystemUiStyleAuto() {
    }
}
