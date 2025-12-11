/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.internal.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.popup.DialogAction
import com.andannn.melodify.shared.compose.popup.DialogId
import com.andannn.melodify.shared.compose.popup.DialogType
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.change_play_list_alert_dialog_content
import melodify.shared.compose.resource.generated.resources.confirm_delete_playlist_item
import melodify.shared.compose.resource.generated.resources.decline
import melodify.shared.compose.resource.generated.resources.duplicated_alert_dialog_title
import melodify.shared.compose.resource.generated.resources.having_registered_track_in_playlist
import melodify.shared.compose.resource.generated.resources.invalid_path_alert_dialog_content
import melodify.shared.compose.resource.generated.resources.ok
import melodify.shared.compose.resource.generated.resources.skip_registered_songs
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data object ConfirmDeletePlaylist : AlertDialog(
    message = Res.string.confirm_delete_playlist_item,
    positive = Res.string.ok,
    negative = Res.string.decline,
)

data object InvalidPathAlert : AlertDialog(
    message = Res.string.invalid_path_alert_dialog_content,
    positive = Res.string.ok,
)

data object DuplicatedAlert : AlertDialog(
    title = Res.string.duplicated_alert_dialog_title,
    message = Res.string.having_registered_track_in_playlist,
    positive = Res.string.skip_registered_songs,
)

data object ChangePlayListAlert : AlertDialog(
    message = Res.string.change_play_list_alert_dialog_content,
    positive = Res.string.ok,
    negative = Res.string.decline,
)

abstract class AlertDialog(
    val title: StringResource? = null,
    val message: StringResource? = null,
    val positive: StringResource,
    val negative: StringResource? = null,
) : DialogId<DialogAction.AlertDialog> {
    override val dialogType: DialogType = DialogType.AlertDialog

    @Composable
    override fun Content(onAction: (DialogAction.AlertDialog) -> Unit) {
        AlertMessageDialogContent(
            dialogId = this,
            onAction = onAction,
        )
    }
}

@Composable
private fun AlertMessageDialogContent(
    dialogId: AlertDialog,
    onAction: (DialogAction.AlertDialog) -> Unit,
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
                        onAction(DialogAction.AlertDialog.Decline)
                    },
                ) {
                    Text(stringResource(dialogId.negative))
                }
            }

            TextButton(
                onClick = {
                    onAction(DialogAction.AlertDialog.Accept)
                },
            ) {
                Text(stringResource(dialogId.positive))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
