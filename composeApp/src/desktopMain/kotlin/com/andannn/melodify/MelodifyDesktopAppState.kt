package com.andannn.melodify

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andannn.melodify.ui.common.util.getUiRetainedScope
import com.andannn.melodify.ui.components.popup.PopupController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "MelodifyDesktopAppState"

@Composable
fun rememberMelodifyDesktopAppState(
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    popupController: PopupController = getUiRetainedScope()?.get() ?: getKoin().get(),
) = remember(
    scope,
    snackBarHostState,
) {
    MelodifyDesktopAppState(
        scope = scope,
        snackBarHostState = snackBarHostState,
        popupController = popupController
    )
}

class MelodifyDesktopAppState(
    scope: CoroutineScope,
    val snackBarHostState: SnackbarHostState,
    private val popupController: PopupController
) {
    var showPreferenceWindow by mutableStateOf(false)

    fun closePreferenceWindow() {
        showPreferenceWindow = false
    }

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