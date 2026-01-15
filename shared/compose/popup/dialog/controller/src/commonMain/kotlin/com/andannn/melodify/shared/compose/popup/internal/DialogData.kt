/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.internal

import com.andannn.melodify.shared.compose.popup.common.DialogId
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

private const val TAG = "PopupController"

internal interface DialogData {
    val dialogId: DialogId<*>

    /**
     * Perform the user action. [action] is null if the user dismiss the dialog.
     */
    fun performAction(action: Any?)
}

internal class DialogDataImpl constructor(
    override val dialogId: DialogId<*>,
    private val continuation: CancellableContinuation<Any?>,
) : DialogData {
    override fun performAction(action: Any?) {
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
