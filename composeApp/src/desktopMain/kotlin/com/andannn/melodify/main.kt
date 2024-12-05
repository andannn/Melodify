package com.andannn.melodify

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.GlobalContext.startKoin
import java.awt.Dimension
import java.awt.GraphicsEnvironment

fun main() = application {
    // TODO: Check build Config.
//    if (BuildConfig.DEBUG) {
    Napier.base(DebugAntilog())
//    }

    startKoin {
        modules(modules)
    }

    val appState = rememberMelodifyDesktopAppState()
    MelodifyDeskTopApp(
        appState = appState
    )
}
