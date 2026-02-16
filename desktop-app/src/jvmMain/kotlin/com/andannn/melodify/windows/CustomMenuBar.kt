/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.andannn.melodify.shared.compose.common.model.ShortcutItem
import com.andannn.melodify.shared.compose.common.model.toDataSource
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.search_your_library
import org.jetbrains.compose.resources.stringResource

sealed interface MenuEvent {
    data object OnOpenMediaLibrarySettings : MenuEvent

    data object OnOpenSearch : MenuEvent

    data class OnOpenMediaLibrary(
        val shortcutItem: ShortcutItem,
    ) : MenuEvent
}

fun WindowNavigator.handleMenuEvent(menuEvent: MenuEvent) {
    when (menuEvent) {
        MenuEvent.OnOpenMediaLibrarySettings -> {
            openWindow(
                WindowType.SettingPreference,
            )
        }

        is MenuEvent.OnOpenMediaLibrary -> {
            openWindow(WindowType.MediaLibrary(menuEvent.shortcutItem.toDataSource()))
        }

        MenuEvent.OnOpenSearch -> {
            openWindow(WindowType.Search)
        }
    }
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
        Menu("Library") {
            ShortcutItem.entries.forEach { item ->
                val text = stringResource(item.textRes)
                Item(
                    text,
                    onClick = {
                        handler.invoke(MenuEvent.OnOpenMediaLibrary(item))
                    },
                )
            }

            Item(
                stringResource(Res.string.search_your_library),
                onClick = {
                    handler.invoke(MenuEvent.OnOpenSearch)
                },
            )
        }
    }
}
