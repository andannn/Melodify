/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.window

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "MelodifyDesktopAppState"

@Composable
fun rememberCommonWindowState(
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    popupController: PopupController = LocalPopupController.current,
) = remember(
    scope,
    snackBarHostState,
) {
    CommonWindowState(
        scope = scope,
        snackBarHostState = snackBarHostState,
        popupController = popupController,
    )
}

class CommonWindowState(
    scope: CoroutineScope,
    val snackBarHostState: SnackbarHostState,
    private val popupController: PopupController,
) {
    init {
        scope.launch {
            for (message in popupController.snackBarMessageChannel) {
                Napier.d(tag = TAG) { "show snackbar: $message" }
                var result: SnackbarResult? = null
                try {
                    result = snackBarHostState.showSnackbar(message)
                } catch (e: CancellationException) {
                    result = SnackbarResult.Dismissed
                    throw e
                } finally {
                    popupController.snackBarResultChannel.send(result!!)
                    Napier.d(tag = TAG) { "show snackbar dismiss $result" }
                }
            }
        }
    }
}
