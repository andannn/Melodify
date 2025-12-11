package com.andannn.melodify.shared.compose.common.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun MelodifyTheme(
    darkTheme: Boolean,
    content: @Composable (() -> Unit),
) {
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val darkColorScheme = darkColorScheme(primary = Color(0xFF66ffc7))

    val colorScheme =
        when {
            supportsDynamicColor && darkTheme -> {
                dynamicDarkColorScheme(LocalContext.current)
            }

            supportsDynamicColor && !darkTheme -> {
                dynamicLightColorScheme(LocalContext.current)
            }

            darkTheme -> {
                darkColorScheme
            }

            else -> {
                expressiveLightColorScheme()
            }
        }

    val shapes = Shapes(largeIncreased = RoundedCornerShape(36.0.dp))

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        shapes = shapes,
        content = content,
    )
}
