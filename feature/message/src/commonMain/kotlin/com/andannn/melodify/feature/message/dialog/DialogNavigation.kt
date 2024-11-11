package com.andannn.melodify.feature.message.dialog

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import com.andannn.melodify.feature.message.dialog.ui.AlertMessageDialog
import com.andannn.melodify.feature.message.dialog.ui.NewPlayListDialog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal const val DIALOG_ROUTE_PREFIX = "alert_dialog_route_"

fun NavController.navigateToDialog(dialog: Dialog) {
    this.navigate("$DIALOG_ROUTE_PREFIX${dialog.id}")
}

fun NavGraphBuilder.melodifyDialog(
    navHostController: NavHostController,
    dialog: Dialog,
    onRequestDismiss: () -> Unit,
    onResult: (Dialog, InteractionResult) -> Unit
) {
    dialog(
        route = "$DIALOG_ROUTE_PREFIX${dialog.id}",
        dialogProperties = dialog.dialogProperties
    ) { entry ->
        var interaction by remember {
            mutableStateOf<InteractionResult>(InteractionResult.DISMISS)
        }

        LaunchedEffect(Unit) {
            navHostController.currentBackStack
                .map { it.contains(entry) }
                .distinctUntilChanged()
                .collect { inBackStack ->
                    if (!inBackStack) {
                        onResult(dialog, interaction)
                    }
                }
        }

        when (dialog) {
            is Dialog.AlertDialog -> AlertMessageDialog(
                dialog = dialog,
                onResult = {
                    interaction = it
                    onRequestDismiss()
                }
            )

            Dialog.NewPlayListDialog -> NewPlayListDialog(
                onResult = {
                    interaction = it
                    onRequestDismiss()
                }
            )
        }
    }
}
