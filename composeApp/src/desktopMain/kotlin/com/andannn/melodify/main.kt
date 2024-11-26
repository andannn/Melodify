package com.andannn.melodify

import androidx.compose.ui.window.application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    // TODO: Check build Config.
//    if (BuildConfig.DEBUG) {
    Napier.base(DebugAntilog())
//    }

    startKoin {
        modules(modules)
    }

    MelodifyDeskTopApp()
}
