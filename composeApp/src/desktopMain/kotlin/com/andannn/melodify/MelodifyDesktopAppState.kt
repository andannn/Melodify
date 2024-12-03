package com.andannn.melodify

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andannn.melodify.ui.common.util.getUiRetainedScope
import com.andannn.melodify.ui.components.message.MessageController
import com.andannn.melodify.ui.components.message.dialog.Dialog
import com.andannn.melodify.ui.components.message.dialog.InteractionResult
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.getKoin

private const val TAG = "MelodifyDesktopAppState"

@Composable
fun rememberMelodifyDesktopAppState(
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    messageController: MessageController = getUiRetainedScope()?.get()
        ?: getKoin().get<MessageController>(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = remember(
    scope,
    navController,
    messageController,
    snackBarHostState,
) {
    MelodifyDesktopAppState(
        scope = scope,
        navController = navController,
        messageController = messageController,
        snackBarHostState = snackBarHostState,
    )
}

class MelodifyDesktopAppState(
    scope: CoroutineScope,
    val navController: NavHostController,
    val messageController: MessageController,
    val snackBarHostState: SnackbarHostState,
) {
    fun onDialogResult(dialog: Dialog, interactionResult: InteractionResult) {
        messageController.onResult(dialog, interactionResult)
    }
}