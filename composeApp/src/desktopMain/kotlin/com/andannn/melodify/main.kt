package com.andannn.melodify

import androidx.compose.ui.window.application
import com.andannn.melodify.app.MelodifyDeskTopApp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    Napier.base(DebugAntilog())

    startKoin {
        modules(modules)
    }

    MelodifyDeskTopApp()
}
