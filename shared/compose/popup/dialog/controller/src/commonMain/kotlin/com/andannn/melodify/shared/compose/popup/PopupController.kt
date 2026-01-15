/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andannn.melodify.shared.compose.popup.common.DialogEntry
import com.andannn.melodify.shared.compose.popup.common.DialogId
import com.andannn.melodify.shared.compose.popup.internal.DialogData
import com.andannn.melodify.shared.compose.popup.internal.DialogDataImpl
import io.github.aakira.napier.Napier
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

val LocalPopupController: ProvidableCompositionLocal<PopupController> =
    compositionLocalOf { error("No popup controller") }

private const val TAG = "PopupController"

class PopupController constructor(
    val entryProvider: (DialogId<*>) -> DialogEntry<DialogId<*>>,
) {
    private val mutex = Mutex()

    internal var currentDialog by mutableStateOf<DialogData?>(null)
        private set

    suspend fun showDialog(dialogId: DialogId<*>): Any? =
        mutex.withLock {
            Napier.d(tag = TAG) { "show dialog. dialogId = $dialogId" }
            try {
                return suspendCancellableCoroutine { continuation ->
                    currentDialog = DialogDataImpl(dialogId, continuation)
                }
            } finally {
                Napier.d(tag = TAG) { "currentDialog closed = $dialogId" }
                currentDialog = null
            }
        }
}

suspend inline fun <reified T> PopupController.showDialogAndWaitAction(dialogId: DialogId<T>): T? = showDialog(dialogId) as T?
