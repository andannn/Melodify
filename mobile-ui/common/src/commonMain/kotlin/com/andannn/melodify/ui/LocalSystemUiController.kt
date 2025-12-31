package com.andannn.melodify.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import com.andannn.melodify.util.immersive.SystemUiController

val LocalSystemUiController =
    staticCompositionLocalOf<SystemUiController> { error("no SystemUiController") }

/**
 * Set immersive mode when this composable is active.
 * Set immersive mode off when this composable is disposed.
 */
@Composable
fun ImmersiveModeEffect(systemUiController: SystemUiController = LocalSystemUiController.current) {
    DisposableEffect(Unit) {
        systemUiController.setImmersiveModeEnabled(true)
        systemUiController.setSystemUiVisibility(false)

        onDispose {
            systemUiController.setImmersiveModeEnabled(false)
            systemUiController.setSystemUiVisibility(true)
        }
    }
}
