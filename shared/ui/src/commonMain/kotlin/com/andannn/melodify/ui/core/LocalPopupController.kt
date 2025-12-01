/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.SnackBarMessage
import com.andannn.melodify.ui.popup.PopupControllerImpl
import com.andannn.melodify.ui.popup.dialog.DialogData

val LocalPopupController: ProvidableCompositionLocal<PopupController> =
    compositionLocalOf { NoOpPopupController() }

fun PopupController(): PopupController = PopupControllerImpl()

interface PopupController {
    val currentDialog: DialogData?

    var snackBarController: SnackbarHostState?

    suspend fun showSnackBar(message: SnackBarMessage): SnackbarResult

    suspend fun showDialog(dialogId: DialogId): DialogAction
}

class NoOpPopupController : PopupController {
    override val currentDialog: DialogData?
        get() = null

    override var snackBarController: SnackbarHostState? = null

    override suspend fun showSnackBar(message: SnackBarMessage): SnackbarResult = SnackbarResult.Dismissed

    override suspend fun showDialog(dialogId: DialogId): DialogAction = DialogAction.Dismissed
}

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
