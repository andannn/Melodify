package com.andannn.melodify.feature.common.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    isDynamicColor: Boolean
): ColorScheme {
    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    return when {
        dynamicColor && darkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }

        dynamicColor && !darkTheme -> {
            dynamicLightColorScheme(LocalContext.current)
        }

        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
}