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
import com.andannn.melodify.ui.components.drawer.DrawerController
import com.andannn.melodify.ui.components.message.MessageController
import com.andannn.melodify.ui.components.message.dialog.Dialog
import com.andannn.melodify.ui.components.message.dialog.InteractionResult
import com.andannn.melodify.navigation.routes.navigateToDialog
import com.andannn.melodify.ui.common.util.getUiRetainedScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.core.scope.Scope

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    retainedScope: Scope? = getUiRetainedScope(),
    drawerController: DrawerController = retainedScope?.get<DrawerController>()
        ?: getKoin().get<DrawerController>(),
    messageController: MessageController = retainedScope?.get<MessageController>()
        ?: getKoin().get<MessageController>(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
) = remember(
    navController,
    drawerController,
    scope,
    messageController,
    snackBarHostState,
    drawerState,
) {
    MelodifyAppState(
        scope = scope,
        drawerState = drawerState,
        navController = navController,
        drawerController = drawerController,
        messageController = messageController,
        snackBarHostState = snackBarHostState
    )
}

private const val TAG = "MelodifyAppState"

class MelodifyAppState(
    val scope: CoroutineScope,
    val navController: NavHostController,
    val drawerController: DrawerController,
    val snackBarHostState: SnackbarHostState,
    val drawerState: DrawerState,
    private val messageController: MessageController,
) {
    init {
        scope.launch {
            for (dialog in messageController.sendDialogChannel) {
                navController.navigateToDialog(dialog)
            }
        }

        scope.launch {
            for (message in messageController.snackBarMessageChannel) {
                Napier.d(tag = TAG) { "show snackbar: $message" }
                var result: SnackbarResult? = null
                try {
                    result = snackBarHostState.showSnackbar(message)
                } catch (e: CancellationException) {
                    result = SnackbarResult.Dismissed
                    throw e
                } finally {
                    messageController.snackBarResultChannel.send(result!!)
                    Napier.d(tag = TAG) { "show snackbar dismiss $result" }
                }
            }
        }
    }

    fun onDialogResult(dialog: Dialog, interactionResult: InteractionResult) {
        messageController.onResult(dialog, interactionResult)
    }
}