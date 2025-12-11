/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.dialog

import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

interface DialogData {
    val dialogId: DialogId<*>

    /**
     * Perform the user action. [action] is null if the user dismiss the dialog.
     */
    fun performAction(action: DialogAction?)
}

class DialogDataImpl(
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
