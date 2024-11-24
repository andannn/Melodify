package com.andannn.melodify.feature.common.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Desktop theme.
 * Not support dynamic color theme.
 */
@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    isDynamicColor: Boolean
): ColorScheme {
    return if (darkTheme) DarkColorPalette else LightColorPalette
}