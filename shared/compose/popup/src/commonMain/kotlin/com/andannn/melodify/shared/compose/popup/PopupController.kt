/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.andannn.melodify.shared.compose.popup.internal.PopupControllerImpl

val LocalPopupController: ProvidableCompositionLocal<PopupController> =
    compositionLocalOf { NoOpPopupController() }

fun PopupController(): PopupController = PopupControllerImpl()

interface PopupController {
    var snackBarController: SnackbarHostState?

    suspend fun showSnackBar(message: SnackBarMessage): SnackbarResult

    suspend fun showDialog(dialogId: DialogId<*>): DialogAction?
}

suspend inline fun <reified T : DialogAction> PopupController.showDialogAndWaitAction(dialogId: DialogId<T>): T? =
    showDialog(dialogId) as T?

@Composable
fun rememberAndSetupSnackBarHostState(holder: PopupController = LocalPopupController.current): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }

    DisposableEffect(snackbarHostState) {
        holder.snackBarController = snackbarHostState

        onDispose {
            holder.snackBarController = null
        }
    }

    return snackbarHostState
}

private class NoOpPopupController : PopupController {
    override var snackBarController: SnackbarHostState? = null

    override suspend fun showSnackBar(message: SnackBarMessage): SnackbarResult = SnackbarResult.Dismissed

    override suspend fun showDialog(dialogId: DialogId<*>): DialogAction? = null
}
