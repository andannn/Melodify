/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.internal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andannn.melodify.shared.compose.popup.DialogAction
import com.andannn.melodify.shared.compose.popup.DialogId
import com.andannn.melodify.shared.compose.popup.PopupController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

private const val TAG = "PopupController"

internal interface DialogData {
    val dialogId: DialogId<*>

    /**
     * Perform the user action. [action] is null if the user dismiss the dialog.
     */
    fun performAction(action: DialogAction?)
}

internal class DialogDataImpl constructor(
    override val dialogId: DialogId<*>,
    private val continuation: CancellableContinuation<DialogAction?>,
) : DialogData {
    override fun performAction(action: DialogAction?) {
        if (continuation.isActive) continuation.resume(action)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DialogDataImpl

        if (dialogId != other.dialogId) return false
        if (continuation != other.continuation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dialogId.hashCode()
        result = 31 * result + continuation.hashCode()
        return result
    }
}

internal class PopupControllerImpl : PopupController {
    private val mutex = Mutex()

    var currentDialog by mutableStateOf<DialogData?>(null)
        private set

    override suspend fun showDialog(dialogId: DialogId<*>): DialogAction? =
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
