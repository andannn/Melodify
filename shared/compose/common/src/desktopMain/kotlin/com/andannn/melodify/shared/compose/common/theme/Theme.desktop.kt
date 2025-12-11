package com.andannn.melodify.shared.compose.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun MelodifyTheme(
    darkTheme: Boolean,
    content: @Composable (() -> Unit),
) {
    val colorScheme =
        if (darkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
