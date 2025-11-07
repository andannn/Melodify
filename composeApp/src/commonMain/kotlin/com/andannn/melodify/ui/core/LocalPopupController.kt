/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.NavEntryDecorator
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.SnackBarMessage
import com.andannn.melodify.ui.popup.PopupControllerImpl
import com.andannn.melodify.ui.popup.dialog.DialogData

val LocalPopupController: ProvidableCompositionLocal<PopupController> =
    compositionLocalOf { error("no popup controller") }

interface PopupController {
    val currentDialog: DialogData?

    var snackBarController: SnackbarHostState?

    suspend fun showSnackBar(message: SnackBarMessage): SnackbarResult

    suspend fun showDialog(dialogId: DialogId): DialogAction
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

@Composable
fun <T : Any> rememberPopupControllerNavEntryDecorator(): NavEntryDecorator<T> = remember { PopupControllerNavEntryDecorator() }

private class PopupControllerNavEntryDecorator<T : Any> :
    NavEntryDecorator<T>(
        onPop = {
        },
        decorate = { entry ->
            val holder = retain { PopupControllerImpl() }
            CompositionLocalProvider(
                LocalPopupController provides holder,
            ) {
                entry.Content()
            }
        },
    )
