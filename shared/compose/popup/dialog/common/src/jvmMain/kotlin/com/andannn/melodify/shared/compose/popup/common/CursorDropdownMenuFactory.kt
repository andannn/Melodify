/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.common

import androidx.compose.material.CursorDropdownMenu
import androidx.compose.runtime.Composable

data class CursorDropdownMenuFactory(
    private val entry: DialogEntry<*>,
) : DialogFactory {
    @Composable
    override fun DialogFactoryScope.Content() {
        CursorDropdownMenu(
            expanded = true,
            onDismissRequest = onRequestDismiss,
        ) {
            entry.Content(onAction = onPerformAction)
        }
    }
}
