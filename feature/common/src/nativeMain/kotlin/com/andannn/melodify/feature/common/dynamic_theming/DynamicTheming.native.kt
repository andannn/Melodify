package com.andannn.melodify.feature.common.dynamic_theming

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun rememberDominantColorState(
    defaultColor: Color,
    defaultOnColor: Color,
    cacheSize: Int,
    isColorValid: (Color) -> Boolean
): DominantColorState {
    TODO("Not yet implemented")
}

actual class DominantColorState {
    actual val color: Color = Color.Red

    actual suspend fun updateColorsFromImageUrl(url: String) {

    }

    actual fun setDynamicThemeEnable(enable: Boolean) {

    }
}