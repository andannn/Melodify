package com.andannn.melodify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun MelodifyDeskTopApp() {
    Window(
        onCloseRequest = {},
        title = "Melodify",
    ) {
        LaunchedEffect(Unit) {
            getKoin().get<MediaLibrarySyncer>().syncMediaLibrary()
        }

        MenuBar {
            Menu("Preferences") {
                Item("Media library", onClick = {})
            }
        }

    }
}