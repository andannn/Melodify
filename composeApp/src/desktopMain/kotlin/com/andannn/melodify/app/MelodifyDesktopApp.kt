package com.andannn.melodify.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ApplicationScope
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import com.andannn.melodify.window.main.MainWindow
import com.andannn.melodify.window.preferences.PreferenceWindow

@Composable
internal fun ApplicationScope.MelodifyDeskTopApp(
    appState: MelodifyDeskTopAppState = rememberMelodifyDeskTopAppState()
) {
    MelodifyTheme(darkTheme = false) {
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
}

