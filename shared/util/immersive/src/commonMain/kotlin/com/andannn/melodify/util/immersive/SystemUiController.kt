package com.andannn.melodify.util.immersive

interface SystemUiController {
    fun setSystemUiVisibility(visible: Boolean)

    fun setImmersiveModeEnabled(enable: Boolean)

    fun setSystemUiDarkTheme(isDark: Boolean)

    fun setSystemUiStyleAuto()
}
