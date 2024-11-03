package com.andannn.melodify.feature.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import org.jetbrains.compose.resources.stringResource

internal const val ALERT_DIALOG_ROUTE_PREFIX = "alert_dialog_route_"

fun NavController.navigateToAlertDialog(dialog: MessageDialog) {
    this.navigate("$ALERT_DIALOG_ROUTE_PREFIX${dialog.id}")
}

fun NavGraphBuilder.alertDialog(
    dialog: MessageDialog,
    onRequestDismiss: () -> Unit,
    onResult: (MessageDialog, InteractionResult) -> Unit
) {
    dialog(
        route = "$ALERT_DIALOG_ROUTE_PREFIX${dialog.id}",
        dialogProperties = dialog.dialogProperties
    ) {
        AlertMessageDialog(
            dialog = dialog,
            onRequestDismiss = onRequestDismiss,
            onResult = {
                onResult(dialog, it)
            }
        )
    }
}

@Composable
fun AlertMessageDialog(
    dialog: MessageDialog,
    onRequestDismiss: () -> Unit,
    onResult: (InteractionResult) -> Unit
) {
    var interactionResult: InteractionResult by remember {
        mutableStateOf(InteractionResult.DISMISS)
    }

    DisposableEffect(Unit) {
        onDispose {
            onResult(interactionResult)
        }
    }

    Surface(
        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
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
                            onRequestDismiss()
                            interactionResult = InteractionResult.DECLINE
                        },
                    ) {
                        Text(stringResource(dialog.negative))
                    }
                }

                TextButton(
                    onClick = {
                        onRequestDismiss()
                        interactionResult = InteractionResult.ACCEPT
                    },
                ) {
                    Text(stringResource(dialog.positive))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}