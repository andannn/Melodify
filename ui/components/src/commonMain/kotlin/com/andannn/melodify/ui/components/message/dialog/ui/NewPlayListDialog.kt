package com.andannn.melodify.ui.components.message.dialog.ui

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
import com.andannn.melodify.ui.components.message.dialog.Dialog
import com.andannn.melodify.ui.components.message.dialog.InteractionResult
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewPlayListDialog(
    modifier: Modifier = Modifier,
    onResult: (InteractionResult.NewPlaylistDialog) -> Unit = {}
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
                text = stringResource(Dialog.NewPlayListDialog.title),
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputName,
                onValueChange = {
                    inputName = it
                },
                label = { Text(stringResource(Dialog.NewPlayListDialog.playListNameInputHint)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        onResult(InteractionResult.NewPlaylistDialog.DECLINE)
                    },
                ) {
                    Text(stringResource(Dialog.NewPlayListDialog.negative))
                }

                TextButton(
                    enabled = acceptButtonEnabled,
                    onClick = {
                        onResult(InteractionResult.NewPlaylistDialog.ACCEPT(inputName))
                    },
                ) {
                    Text(stringResource(Dialog.NewPlayListDialog.positive))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
