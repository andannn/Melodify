package com.andannn.melodify

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andannn.melodify.ui.common.util.getUiRetainedScope
import com.andannn.melodify.ui.components.popup.PopupController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    popupController: PopupController = getUiRetainedScope()?.get() ?: getKoin().get(),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
) = remember(
    navController,
    scope,
    snackBarHostState,
    drawerState
) {
    MelodifyAppState(
        scope = scope,
        navController = navController,
        snackBarHostState = snackBarHostState,
        popupController = popupController,
        drawerState = drawerState,
    )
}

private const val TAG = "MelodifyAppState"

class MelodifyAppState(
    val scope: CoroutineScope,
    val navController: NavHostController,
    val snackBarHostState: SnackbarHostState,
    val popupController: PopupController,
    val drawerState: DrawerState
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