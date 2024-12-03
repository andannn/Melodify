package com.andannn.melodify.ui.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val DarkColorPalette = darkColorScheme()

val LightColorPalette = lightColorScheme()

@Composable
expect fun getColorScheme(darkTheme: Boolean, isDynamicColor: Boolean): ColorScheme

@Composable
fun MelodifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = getColorScheme(darkTheme, isDynamicColor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
