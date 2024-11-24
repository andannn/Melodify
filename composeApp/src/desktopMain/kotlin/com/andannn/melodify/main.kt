package com.andannn.melodify

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() = application {
    // TODO: Check build Config.
//    if (BuildConfig.DEBUG) {
    Napier.base(DebugAntilog())
//    }

    startKoin {
        modules(modules)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Melodify",
    ) {

        LaunchedEffect(Unit) {
            getKoin().get<MediaLibrarySyncer>().syncMediaLibrary()
        }
        MelodifyApp()
    }
}
