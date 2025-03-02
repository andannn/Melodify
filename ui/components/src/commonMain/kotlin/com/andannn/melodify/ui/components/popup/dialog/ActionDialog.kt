package com.andannn.melodify.ui.components.popup.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.content.AddLibraryPathDialog
import com.andannn.melodify.ui.components.popup.dialog.content.AddToPlayListDialogContent
import com.andannn.melodify.ui.components.popup.dialog.content.AlertMessageDialogContent
import com.andannn.melodify.ui.components.popup.dialog.content.MediaOptionContent
import com.andannn.melodify.ui.components.popup.dialog.content.NewPlayListDialogContent
import com.andannn.melodify.ui.components.popup.dialog.content.SleepTimerCountingContent
import com.andannn.melodify.ui.components.popup.dialog.content.SleepTimerOptionDialogContent

enum class DialogType {
    AlertDialog,
    ModalBottomSheet,
    DropDownDialog,
}

@Composable
fun ActionDialogContainer(
    popupController: PopupController = LocalPopupController.current,
    data: DialogData? =  popupController.currentDialog
) {
// TODO: onRequestDismiss lambda is not updated when data changed. use state by *rememberUpdatedState*
//    if (data != null) {
//        ActionDialogContent(
//            data = data,
//            onRequestDismiss = {
//                data.performAction(DialogAction.Dismissed)
//            }
//        )
//    }

    val dataState = rememberUpdatedState(data)
    if (dataState.value != null) {
        ActionDialogContent(
            data = dataState.value!!,
            onRequestDismiss = {
                dataState.value?.performAction(DialogAction.Dismissed)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionDialogContent(
    data: DialogData,
    onRequestDismiss: () -> Unit
) {
    when (data.dialogId.dialogIdType) {
        DialogType.AlertDialog -> Dialog(
            onDismissRequest = onRequestDismiss,
            content = {
                Surface(
                    modifier = Modifier.wrapContentSize(),
                    shape = AlertDialogDefaults.shape,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {
                    DialogContent(data)
                }
            }
        )

        DialogType.DropDownDialog -> DropDownOptionMenu(
            onRequestDismiss = onRequestDismiss,
            content = {
                DialogContent(data)
            }
        )

        DialogType.ModalBottomSheet -> ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = onRequestDismiss,
            content = {
                DialogContent(data)
            }
        )
    }
}

@Composable
fun DialogContent(
    data: DialogData
) {
    when (val dialog = data.dialogId) {
        is DialogId.AlertDialog -> AlertMessageDialogContent(
            dialogId = dialog,
            onAction = {
                data.performAction(it)
            }
        )

        DialogId.AddLibraryPathDialog -> AddLibraryPathDialog(
            onAction = {
                data.performAction(it)
            }
        )

        DialogId.NewPlayListDialog -> NewPlayListDialogContent(
            onAction = {
                data.performAction(it)
            }
        )

        is DialogId.MediaOption -> MediaOptionContent(
            dialogId = dialog,
            onAction = {
                data.performAction(it)
            }
        )

        is DialogId.AddToPlayListDialog -> AddToPlayListDialogContent(
            source = dialog.source,
            onAction = {
                data.performAction(it)
            }
        )

        is DialogId.SleepTimerOptionDialog -> SleepTimerOptionDialogContent(
            onAction = {
                data.performAction(it)
            }
        )

        DialogId.SleepCountingDialog -> SleepTimerCountingContent(
            onAction = {
                data.performAction(it)
            }
        )
    }
}

internal expect val DialogId.dialogIdType: DialogType

@Composable
internal expect fun DropDownOptionMenu(
    onRequestDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
)
