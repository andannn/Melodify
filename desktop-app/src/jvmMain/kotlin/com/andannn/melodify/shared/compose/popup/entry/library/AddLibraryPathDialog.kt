/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.popup.common.DialogEntryProviderScope
import com.andannn.melodify.shared.compose.popup.common.DialogId
import com.andannn.melodify.shared.compose.popup.common.DialogType
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.decline
import melodify.shared.compose.resource.generated.resources.new_playlist_dialog_title
import melodify.shared.compose.resource.generated.resources.ok
import org.jetbrains.compose.resources.stringResource

data object AddLibraryPathDialog : DialogId<InputDialog> {
    val title = Res.string.new_playlist_dialog_title
}

sealed interface InputDialog {
    data class Accept(
        val input: String,
    ) : InputDialog

    object Decline : InputDialog
}

fun DialogEntryProviderScope<DialogId<*>>.addLibraryPathDialogEntry() {
    entry(
        dialogId = AddLibraryPathDialog,
        dialogType = DialogType.AlertDialog,
    ) { dialogId, onAction ->
        AddLibraryPathDialog()
    }
}

private val positive = Res.string.ok

private val negative = Res.string.decline

@Composable
internal fun AddLibraryPathDialog(
    modifier: Modifier = Modifier,
    onAction: (InputDialog) -> Unit = {},
) {
    SimpleInputDialogContent(
        modifier = modifier,
        title = stringResource(AddLibraryPathDialog.title),
        onAction = onAction,
    )
}

@Composable
private fun SimpleInputDialogContent(
    modifier: Modifier = Modifier,
    title: String = "",
    inputHint: String = "",
    onAction: (InputDialog) -> Unit = {},
) {
    Surface(
        modifier = modifier.wrapContentSize(),
        shape = AlertDialogDefaults.shape,
        tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
        var inputName by rememberSaveable {
            mutableStateOf("")
        }

        val acceptButtonEnabled by remember {
            derivedStateOf {
                inputName.isNotEmpty()
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputName,
                onValueChange = {
                    inputName = it
                },
                label = { Text(inputHint) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        onAction(InputDialog.Decline)
                    },
                ) {
                    Text(stringResource(negative))
                }

                TextButton(
                    enabled = acceptButtonEnabled,
                    onClick = {
                        onAction(InputDialog.Accept(input = inputName))
                    },
                ) {
                    Text(stringResource(positive))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Preview
@Composable
private fun AddLibraryPathDialogPreview() {
    MelodifyTheme {
        Surface {
            AddLibraryPathDialog()
        }
    }
}
