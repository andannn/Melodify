/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

sealed interface MenuEvent {
    data object OnOpenMediaLibrarySettings : MenuEvent
}

@Composable
internal fun FrameWindowScope.CustomMenuBar(handler: (MenuEvent) -> Unit) {
    MenuBar {
        Menu("Settings") {
            Item(
                "Media library",
                onClick = {
                    handler.invoke(MenuEvent.OnOpenMediaLibrarySettings)
                },
            )
        }
    }
}
