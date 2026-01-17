/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.common

import androidx.compose.material.CursorDropdownMenu
import androidx.compose.runtime.Composable
import com.andannn.melodify.shared.compose.popup.PopupEntry
import com.andannn.melodify.shared.compose.popup.PopupFactory
import com.andannn.melodify.shared.compose.popup.PopupFactoryProvider
import com.andannn.melodify.shared.compose.popup.PopupFactoryScope

data class CursorDropdownMenuFactory(
    private val entry: PopupEntry<*>,
) : PopupFactory {
    @Composable
    override fun PopupFactoryScope.Content() {
        CursorDropdownMenu(
            expanded = true,
            onDismissRequest = onRequestDismiss,
        ) {
            entry.Content(onAction = onPerformAction)
        }
    }
}

class CursorDropdownMenuFactoryProvider : PopupFactoryProvider {
    override fun create(entry: PopupEntry<*>): PopupFactory? =
        entry.metadata[CURSOR_DROPDOWN_MENU_KEY]?.let {
            CursorDropdownMenuFactory(
                entry = entry,
            )
        }

    companion object {
        internal const val CURSOR_DROPDOWN_MENU_KEY = "cursor_dropdown_menu"

        fun cursorDropdownMenu(): Map<String, Any> = mapOf(CURSOR_DROPDOWN_MENU_KEY to Unit)
    }
}
