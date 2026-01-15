/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.andannn.melodify.shared.compose.popup.common.DialogEntry
import com.andannn.melodify.shared.compose.popup.common.DialogId
import com.andannn.melodify.shared.compose.popup.common.DialogType
import com.andannn.melodify.shared.compose.popup.internal.DialogData

@Composable
fun ActionDialog(popupController: PopupController = LocalPopupController.current) {
    val data: DialogData? = popupController.currentDialog
    val entryProvider =
        remember {
            popupController.entryProvider
        }
    if (data != null) {
        val entry =
            remember(data.dialogId) {
                entryProvider(data.dialogId)
            }
        ActionDialogContent(
            entry = entry,
            onPerformAction = {
                data.performAction(it)
            },
            onRequestDismiss = {
                data.performAction(null)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionDialogContent(
    entry: DialogEntry<DialogId<*>>,
    onRequestDismiss: () -> Unit,
    onPerformAction: (Any?) -> Unit,
) {
    when (entry.dialogType) {
        DialogType.AlertDialog -> {
            Dialog(
                onDismissRequest = onRequestDismiss,
                content = {
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        shape = AlertDialogDefaults.shape,
                        tonalElevation = AlertDialogDefaults.TonalElevation,
                    ) {
                        entry.Content(
                            onAction = {
                                onPerformAction(it)
                            },
                        )
                    }
                },
            )
        }

        DialogType.DropDownDialog -> {
            //            DropDownOptionMenu(
            //                onRequestDismiss = onRequestDismiss,
            //                content = {
            //                    DialogContent(data)
            //                },
            //            )
        }

        DialogType.ModalBottomSheet -> {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                onDismissRequest = onRequestDismiss,
                content = {
                    entry.Content(
                        onAction = {
                            onPerformAction(it)
                        },
                    )
                },
            )
        }
    }
}
