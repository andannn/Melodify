/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.andannn.melodify.shared.compose.popup.internal.PopupControllerImpl

val LocalPopupController: ProvidableCompositionLocal<PopupController> =
    compositionLocalOf { NoOpPopupController() }

fun PopupController(): PopupController = PopupControllerImpl()

interface PopupController {
    suspend fun showDialog(dialogId: DialogId<*>): DialogAction?
}

suspend inline fun <reified T : DialogAction> PopupController.showDialogAndWaitAction(dialogId: DialogId<T>): T? =
    showDialog(dialogId) as T?

private class NoOpPopupController : PopupController {
    override suspend fun showDialog(dialogId: DialogId<*>): DialogAction? = null
}
