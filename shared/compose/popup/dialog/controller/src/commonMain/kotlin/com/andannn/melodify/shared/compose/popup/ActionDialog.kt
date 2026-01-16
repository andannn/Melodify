/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.andannn.melodify.shared.compose.popup.common.DialogEntry
import com.andannn.melodify.shared.compose.popup.common.DialogFactoryProvider
import com.andannn.melodify.shared.compose.popup.common.DialogFactoryScope
import com.andannn.melodify.shared.compose.popup.common.DialogId
import com.andannn.melodify.shared.compose.popup.internal.DialogData

@Composable
fun ActionDialog(
    dialogHostState: DialogHostState,
    dialogFactoryProvider: List<DialogFactoryProvider> = emptyList(),
    entryProvider: (DialogId<*>) -> DialogEntry<DialogId<*>>,
) {
    val data: DialogData? = dialogHostState.currentDialog
    if (data != null) {
        val entry =
            remember(data.dialogId) {
                entryProvider(data.dialogId)
            }

        ActionDialogContent(
            entry = entry,
            dialogFactoryProvider = dialogFactoryProvider,
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
    dialogFactoryProvider: List<DialogFactoryProvider>,
    onRequestDismiss: () -> Unit,
    onPerformAction: (Any?) -> Unit,
) {
    val dialogFactory =
        dialogFactoryProvider.firstNotNullOfOrNull { provider ->
            provider.create(entry)
        } ?: return

    val dialogFactoryScope by rememberUpdatedState(
        DialogFactoryScope(
            onRequestDismiss,
            onPerformAction,
        ),
    )

    with(dialogFactory) {
        dialogFactoryScope.Content()
    }
}
