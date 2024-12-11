package com.andannn.melodify.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ApplicationScope
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import com.andannn.melodify.window.main.MainWindow
import com.andannn.melodify.window.preferences.PreferenceWindow
import org.koin.java.KoinJavaComponent.getKoin

@Composable
internal fun ApplicationScope.MelodifyDeskTopApp(
    appState: MelodifyDeskTopAppState = rememberMelodifyDeskTopAppState()
) {
    LaunchedEffect(Unit) {
        getKoin().get<MediaLibrarySyncer>().syncAllMediaLibrary()
    }

    CompositionLocalProvider(
        LocalPopupController provides remember { PopupControllerImpl() },
    ) {
        MainWindow(
            appState = appState,
            onCloseRequest = ::exitApplication
        )
    }

    if (appState.showPreferenceWindow) {
        CompositionLocalProvider(
            LocalPopupController provides remember { PopupControllerImpl() },
        ) {
            PreferenceWindow(
                appState = appState,
                onCloseRequest = appState::closePreferenceWindow
            )
        }
    }
}

