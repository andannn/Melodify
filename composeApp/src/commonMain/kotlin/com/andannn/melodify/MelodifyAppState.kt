package com.andannn.melodify

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

@Composable
fun rememberAppState(
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    popupController: PopupController = LocalPopupController.current,
) = remember(
    scope,
    snackBarHostState,
) {
    MelodifyAppState(
        scope = scope,
        snackBarHostState = snackBarHostState,
        popupController = popupController,
    )
}

private const val TAG = "MelodifyAppState"

class MelodifyAppState(
    val scope: CoroutineScope,
    val snackBarHostState: SnackbarHostState,
    val popupController: PopupController,
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