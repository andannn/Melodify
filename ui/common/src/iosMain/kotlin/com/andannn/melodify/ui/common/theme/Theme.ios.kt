package com.andannn.melodify.ui.common.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    isDynamicColor: Boolean,
): ColorScheme {
    return if (darkTheme) DarkColorPalette else LightColorPalette
}
