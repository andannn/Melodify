/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.popup.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.runtime.Composable
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.popup.dialog.DialogType

actual val DialogId.dialogIdType: DialogType
    get() =
        when (this) {
            is DialogId.SleepTimerOptionDialog,
            DialogId.NewPlayListDialog,
            DialogId.SleepCountingDialog,
            is DialogId.AlertDialog,
            DialogId.AddLibraryPathDialog,
            is DialogId.AddMusicsToPlayListDialog,
            is DialogId.ChangeSortRuleDialog,
            is DialogId.OptionDialog,
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
