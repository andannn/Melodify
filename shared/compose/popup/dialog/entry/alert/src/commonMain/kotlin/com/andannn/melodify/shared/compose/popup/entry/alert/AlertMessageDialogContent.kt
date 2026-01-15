/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.popup.common.DialogEntryProviderScope
import com.andannn.melodify.shared.compose.popup.common.DialogId
import com.andannn.melodify.shared.compose.popup.common.DialogType
import org.jetbrains.compose.resources.stringResource

fun DialogEntryProviderScope<DialogId<*>>.alertDialogEntry() {
    listOf(
        ConfirmDeletePlaylist,
        InvalidPathAlert,
        DuplicatedAlert,
        ChangePlayListAlert,
    ).forEach { id ->
        entry(
            dialogId = id,
            dialogType = DialogType.AlertDialog,
        ) { dialogId, onAction ->
            AlertMessageDialogContent(dialogId, onAction)
        }
    }
}

@Composable
private fun AlertMessageDialogContent(
    dialogId: AlertDialog,
    onAction: (AlertDialogAction) -> Unit = {},
) {
    Column(modifier = Modifier.padding(16.dp)) {
        dialogId.title?.let {
            Text(
                text = stringResource(dialogId.title),
                style = MaterialTheme.typography.titleSmall,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        dialogId.message?.let {
            Text(
                text = stringResource(dialogId.message),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Row {
            Spacer(modifier = Modifier.weight(1f))
            dialogId.negative?.let {
                TextButton(
                    onClick = {
                        onAction(AlertDialogAction.Decline)
                    },
                ) {
                    Text(stringResource(dialogId.negative))
                }
            }

            TextButton(
                onClick = {
                    onAction(AlertDialogAction.Accept)
                },
            ) {
                Text(stringResource(dialogId.positive))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
@Preview
private fun AlertMessageDialogContent() {
    MelodifyTheme {
        Surface {
            AlertMessageDialogContent(
                dialogId = ConfirmDeletePlaylist,
            )
        }
    }
}

@Composable
@Preview
private fun AlertMessageDialogContent1() {
    MelodifyTheme {
        Surface {
            AlertMessageDialogContent(
                dialogId = InvalidPathAlert,
            )
        }
    }
}

@Composable
@Preview
private fun AlertMessageDialogContent2() {
    MelodifyTheme {
        Surface {
            AlertMessageDialogContent(
                dialogId = DuplicatedAlert,
            )
        }
    }
}

@Composable
@Preview
private fun AlertMessageDialogContent3() {
    MelodifyTheme {
        Surface {
            AlertMessageDialogContent(
                dialogId = ConfirmDeletePlaylist,
            )
        }
    }
}
