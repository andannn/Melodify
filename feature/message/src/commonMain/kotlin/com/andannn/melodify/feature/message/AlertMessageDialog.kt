package com.andannn.melodify.feature.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument

internal const val ALERT_DIALOG_ROUTE = "alert_dialog_route"
internal const val DIALOG_ID = "dialog_id"

fun NavController.navigateToAlertDialog(dialog: MessageDialog) {
    this.navigate("$ALERT_DIALOG_ROUTE/${dialog.id}")
}

fun NavGraphBuilder.alertDialog(onDismiss: (InteractionResult) -> Unit) {
    dialog(
        route = "$ALERT_DIALOG_ROUTE/{$DIALOG_ID}",
        arguments =
        listOf(
            navArgument(name = DIALOG_ID) {
                type = NavType.StringType
            },
        ),
    ) { backStackEntry ->
        val dialogId = backStackEntry.arguments?.getString(DIALOG_ID)
        val dialog = dialogId?.let { MessageDialog.fromId(it) } ?: error("invalid id $dialogId")
        AlertMessageDialog(
            dialog = dialog,
            onDismiss = onDismiss
        )
    }
}


@Composable
fun AlertMessageDialog(
    dialog: MessageDialog,
    onDismiss: (InteractionResult) -> Unit
) {
    Surface(
        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Failed to connect MediaBrowser.")
            Spacer(
                modifier =
                Modifier.height(24.dp)
            )
            TextButton(
                onClick = {
                    onDismiss(InteractionResult.ACCEPT)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Finish")
            }
        }
    }
}