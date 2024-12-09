package com.andannn.melodify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ApplicationScope
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.ui.components.popup.dialog.ActionDialogContainer
import com.andannn.melodify.window.MainWindow
import com.andannn.melodify.window.PreferenceWindow
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun ApplicationScope.MelodifyDeskTopApp(
    appState: MelodifyDesktopAppState
) {
    LaunchedEffect(Unit) {
        getKoin().get<MediaLibrarySyncer>().syncMediaLibrary()
    }

    MainWindow(
        appState = appState,
        onCloseRequest = ::exitApplication
    )

    if (appState.showPreferenceWindow) {
        PreferenceWindow(
            appState = appState,
            onCloseRequest = appState::closePreferenceWindow
        )
    }

    ActionDialogContainer()
}

