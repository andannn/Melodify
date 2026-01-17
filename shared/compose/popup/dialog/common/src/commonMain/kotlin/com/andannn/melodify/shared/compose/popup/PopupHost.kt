/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

@Composable
fun PopupHost(
    popupHostState: PopupHostState,
    popupFactoryProvider: List<PopupFactoryProvider> = listOf(DialogFactoryProvider()),
    entryProvider: (PopupId<*>) -> PopupEntry<PopupId<*>>,
) {
    val data: DialogData? = popupHostState.currentDialog
    if (data != null) {
        val entry =
            remember(data.popupId) {
                entryProvider(data.popupId)
            }

        ActionDialogContent(
            entry = entry,
            popupFactoryProvider = popupFactoryProvider,
            onPerformAction = {
                data.performAction(it)
            },
            onRequestDismiss = {
                data.performAction(null)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionDialogContent(
    entry: PopupEntry<PopupId<*>>,
    popupFactoryProvider: List<PopupFactoryProvider>,
    onRequestDismiss: () -> Unit,
    onPerformAction: (Any?) -> Unit,
) {
    val dialogFactory =
        popupFactoryProvider.firstNotNullOfOrNull { provider ->
            provider.create(entry)
        } ?: return

    val popupFactoryScope by rememberUpdatedState(
        PopupFactoryScope(
            onRequestDismiss,
            onPerformAction,
        ),
    )

    with(dialogFactory) {
        popupFactoryScope.Content()
    }
}

class PopupHostState {
    private val mutex = Mutex()

    internal var currentDialog by mutableStateOf<DialogData?>(null)
        private set

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> showDialog(popupId: PopupId<T>): T? = this@PopupHostState.showDialog(popupId) as T?

    internal suspend fun showDialog(popupId: PopupId<*>): Any? =
        mutex.withLock {
            try {
                return suspendCancellableCoroutine { continuation ->
                    currentDialog = PopupDataImpl(popupId, continuation)
                }
            } finally {
                currentDialog = null
            }
        }
}

internal interface DialogData {
    val popupId: PopupId<*>

    /**
     * Perform the user action. [action] is null if the user dismiss the dialog.
     */
    fun performAction(action: Any?)
}

internal class PopupDataImpl constructor(
    override val popupId: PopupId<*>,
    private val continuation: CancellableContinuation<Any?>,
) : DialogData {
    override fun performAction(action: Any?) {
        if (continuation.isActive) continuation.resume(action)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PopupDataImpl

        if (popupId != other.popupId) return false
        if (continuation != other.continuation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = popupId.hashCode()
        result = 31 * result + continuation.hashCode()
        return result
    }
}
