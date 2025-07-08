package com.andannn.melodify

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.application
import com.andannn.melodify.app.MelodifyDeskTopApp
import com.andannn.melodify.core.syncer.SyncLibraryService
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.GlobalContext.startKoin
import org.koin.mp.KoinPlatform.getKoin

fun main() =
    application {
        Napier.base(DebugAntilog())

        startKoin {
            modules(modules)
        }

        // start watching library changes
        LaunchedEffect(Unit) {
            getKoin().get<SyncLibraryService>().startWatchingLibrary()
        }

        MelodifyDeskTopApp()
    }
