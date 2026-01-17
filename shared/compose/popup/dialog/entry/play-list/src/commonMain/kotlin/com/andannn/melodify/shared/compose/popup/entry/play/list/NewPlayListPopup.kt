/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.play.list

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
import com.andannn.melodify.shared.compose.popup.DialogFactoryProvider
import com.andannn.melodify.shared.compose.popup.PopupEntryProviderScope
import com.andannn.melodify.shared.compose.popup.PopupId
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.decline
import melodify.shared.compose.resource.generated.resources.new_playlist_dialog_input_hint
import melodify.shared.compose.resource.generated.resources.new_playlist_dialog_title
import melodify.shared.compose.resource.generated.resources.ok
import org.jetbrains.compose.resources.stringResource

sealed interface InputDialogResult {
    data class Accept(
        val input: String,
    ) : InputDialogResult

    object Decline : InputDialogResult
}

data object NewPlayListPopup : PopupId<InputDialogResult> {
    val title = Res.string.new_playlist_dialog_title
    val playListNameInputHint = Res.string.new_playlist_dialog_input_hint
}

fun PopupEntryProviderScope<PopupId<*>>.newPlayListDialogEntry() {
    entry(
        dialogId = NewPlayListPopup,
        metadata = DialogFactoryProvider.metadata(),
    ) { _, onAction ->
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = AlertDialogDefaults.shape,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            NewPlayListDialogContent(
                onAction,
            )
        }
    }
}

@Composable
internal fun NewPlayListDialogContent(
    onAction: (InputDialogResult) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    SimpleInputDialogContent(
        modifier = modifier,
        title = stringResource(NewPlayListPopup.title),
        inputHint = stringResource(NewPlayListPopup.playListNameInputHint),
        onAction = onAction,
    )
}

@Composable
private fun SimpleInputDialogContent(
    modifier: Modifier = Modifier,
    title: String = "",
    inputHint: String = "",
    onAction: (InputDialogResult) -> Unit = {},
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
                        onAction(InputDialogResult.Decline)
                    },
                ) {
                    Text(stringResource(Res.string.decline))
                }

                TextButton(
                    enabled = acceptButtonEnabled,
                    onClick = {
                        onAction(InputDialogResult.Accept(input = inputName))
                    },
                ) {
                    Text(stringResource(Res.string.ok))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Preview
@Composable
private fun NewPlayListDialogContentPreview() {
    MelodifyTheme {
        Surface {
            NewPlayListDialogContent()
        }
    }
}
