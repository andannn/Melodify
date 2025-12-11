/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.andannn.melodify.shared.compose.popup.internal.DialogData
import com.andannn.melodify.shared.compose.popup.internal.PopupControllerImpl

@Composable
fun ActionDialog(popupController: PopupController = LocalPopupController.current) {
    val data: DialogData? = popupController.currentDialog()
    if (data != null) {
        ActionDialogContent(
            data = data,
            onRequestDismiss = {
                data.performAction(null)
            },
        )
    }
}

private fun PopupController.currentDialog() = (this as? PopupControllerImpl)?.currentDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionDialogContent(
    data: DialogData,
    onRequestDismiss: () -> Unit,
) {
//    when (data.dialogId.dialogType) {
//        DialogType.AlertDialog -> {
    Dialog(
        onDismissRequest = onRequestDismiss,
        content = {
            Surface(
                modifier = Modifier.wrapContentSize(),
                shape = AlertDialogDefaults.shape,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                data.dialogId.Content(
                    onAction = {
                        data.performAction(it)
                    },
                )
            }
        },
    )
//        }
//
//        DialogType.DropDownDialog -> {
// //            DropDownOptionMenu(
// //                onRequestDismiss = onRequestDismiss,
// //                content = {
// //                    DialogContent(data)
// //                },
// //            )
//        }
//
//        DialogType.ModalBottomSheet -> {
//            ModalBottomSheet(
//                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
//                onDismissRequest = onRequestDismiss,
//                content = {
//                    data.dialogId.Content(
//                        onAction = {
//                            data.performAction(it)
//                        },
//                    )
//                },
//            )
//        }
//    }
}
