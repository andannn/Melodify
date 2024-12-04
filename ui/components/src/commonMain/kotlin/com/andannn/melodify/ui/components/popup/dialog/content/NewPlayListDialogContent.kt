package com.andannn.melodify.ui.components.popup.dialog.content

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
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.components.popup.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewPlayListDialogContent(
    modifier: Modifier = Modifier,
    onAction: (DialogAction) -> Unit = {}
) {
    Surface(
        modifier = modifier.wrapContentSize(),
        shape = AlertDialogDefaults.shape,
        tonalElevation = AlertDialogDefaults.TonalElevation
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
                text = stringResource(DialogId.NewPlayListDialog.title),
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputName,
                onValueChange = {
                    inputName = it
                },
                label = { Text(stringResource(DialogId.NewPlayListDialog.playListNameInputHint)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        onAction(DialogAction.NewPlaylistDialog.Decline)
                    },
                ) {
                    Text(stringResource(DialogId.NewPlayListDialog.negative))
                }

                TextButton(
                    enabled = acceptButtonEnabled,
                    onClick = {
                        onAction(DialogAction.NewPlaylistDialog.Accept(playlistName = inputName))
                    },
                ) {
                    Text(stringResource(DialogId.NewPlayListDialog.positive))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
