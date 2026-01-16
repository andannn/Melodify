/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.common

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

interface DialogFactory {
    @Composable
    fun DialogFactoryScope.Content()
}

data class DialogFactoryScope(
    val onRequestDismiss: () -> Unit,
    val onPerformAction: (Any) -> Unit,
)

data class AlertDialogFactory(
    private val entry: DialogEntry<*>,
    private val dialogProperties: DialogProperties,
) : DialogFactory {
    @Composable
    override fun DialogFactoryScope.Content() {
        Dialog(
            onDismissRequest = onRequestDismiss,
            properties = dialogProperties,
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
}

data class ModalBottomSheetFactory(
    private val entry: DialogEntry<*>,
) : DialogFactory {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun DialogFactoryScope.Content() {
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
