/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.popup.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.runtime.Composable

actual val DialogId.dialogIdType: DialogType
    get() =
        when (this) {
            is DialogId.MediaOption -> DialogType.DropDownDialog

            is DialogId.SleepTimerOptionDialog,
            DialogId.NewPlayListDialog,
            DialogId.SleepCountingDialog,
            is DialogId.AddToPlayListDialog,
            is DialogId.AlertDialog,
            DialogId.AddLibraryPathDialog,
            -> DialogType.AlertDialog
        }

@Composable
internal actual fun DropDownOptionMenu(
    onRequestDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    CursorDropdownMenu(
        expanded = true,
        onDismissRequest = onRequestDismiss,
        content = content,
    )
}
