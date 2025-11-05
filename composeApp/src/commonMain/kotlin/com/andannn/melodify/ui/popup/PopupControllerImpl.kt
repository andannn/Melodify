/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andannn.melodify.PopupController
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.SnackBarMessage
import com.andannn.melodify.ui.popup.dialog.DialogData
import com.andannn.melodify.ui.popup.dialog.DialogDataImpl
import io.github.aakira.napier.Napier
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "PopupController"

class PopupControllerImpl : PopupController {
    private val mutex = Mutex()

    private var _currentDialog by mutableStateOf<DialogData?>(null)

    override val currentDialog: DialogData?
        get() = _currentDialog

    override var snackBarController: SnackbarHostState? = null

    /**
     * Show snackbar and wait for user interaction.
     */
    override suspend fun showSnackBar(message: SnackBarMessage): SnackbarResult =
        snackBarController?.showSnackbar(message.toSnackbarVisuals())
            ?: error("Snackbar HostState is not setup. ")

    /**
     * Show dialog and wait for user interaction.
     *
     * Dialog show at most one snackbar at a time.
     */
    override suspend fun showDialog(dialogId: DialogId): DialogAction =
        mutex.withLock {
            Napier.d(tag = TAG) { "show dialog. dialogId = $dialogId" }
            try {
                return suspendCancellableCoroutine { continuation ->
                    _currentDialog = DialogDataImpl(dialogId, continuation)
                }
            } finally {
                Napier.d(tag = TAG) { "currentDialog closed = $dialogId" }
                _currentDialog = null
            }
        }
}
