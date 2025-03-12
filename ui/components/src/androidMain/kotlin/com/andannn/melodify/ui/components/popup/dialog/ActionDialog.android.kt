package com.andannn.melodify.ui.components.popup.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable


actual val DialogId.dialogIdType: DialogType
    get() = when (this) {
        is DialogId.SleepTimerOptionDialog,
        DialogId.SleepCountingDialog,
        is DialogId.MediaOption,
        is DialogId.AddToPlayListDialog -> DialogType.ModalBottomSheet

        DialogId.NewPlayListDialog,
        is DialogId.AlertDialog,
        DialogId.AddLibraryPathDialog -> DialogType.AlertDialog
    }

@Composable
actual fun DropDownOptionMenu(
    onRequestDismiss: () -> Unit,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    error("Not supported on ios")
}