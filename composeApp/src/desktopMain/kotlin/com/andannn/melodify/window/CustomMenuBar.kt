package com.andannn.melodify.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.andannn.melodify.app.MelodifyDeskTopAppState

@Composable
internal fun FrameWindowScope.CustomMenuBar(
    appState: MelodifyDeskTopAppState
) {
    MenuBar {
        Menu("Preferences") {
            Item("Configure",
                onClick = {
                    appState.showPreferenceWindow = true
                }
            )
        }
    }
}