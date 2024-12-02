package com.andannn.melodify.ui.components.message.dialog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.components.message.dialog.Dialog
import com.andannn.melodify.ui.components.message.dialog.InteractionResult
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlertMessageDialog(
    dialog: Dialog.AlertDialog,
    onResult: (InteractionResult.AlertDialog) -> Unit
) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = AlertDialogDefaults.shape,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            dialog.title?.let {
                Text(
                    text = stringResource(dialog.title),
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            dialog.message?.let {
                Text(
                    text = stringResource(dialog.message),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Row {
                Spacer(modifier = Modifier.weight(1f))
                dialog.negative?.let {
                    TextButton(
                        onClick = {
                            onResult(InteractionResult.AlertDialog.DECLINE)
                        },
                    ) {
                        Text(stringResource(dialog.negative))
                    }
                }

                TextButton(
                    onClick = {
                        onResult(InteractionResult.AlertDialog.ACCEPT)
                    },
                ) {
                    Text(stringResource(dialog.positive))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}