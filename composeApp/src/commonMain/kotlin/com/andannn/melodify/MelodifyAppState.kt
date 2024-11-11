package com.andannn.melodify

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andannn.melodify.feature.common.util.getUiRetainedScope
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.message.dialog.Dialog
import com.andannn.melodify.feature.message.dialog.InteractionResult
import com.andannn.melodify.feature.message.dialog.navigateToDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.scope.Scope

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    retainedScope: Scope = getUiRetainedScope()!!,
    drawerController: DrawerController = retainedScope.get<DrawerController>(),
    messageController: MessageController = retainedScope.get<MessageController>(),
    snackBarHostState :SnackbarHostState =  remember { SnackbarHostState() }
) = remember(
    navController,
    drawerController,
    scope,
    messageController,
    snackBarHostState
) {
    MelodifyAppState(
        scope = scope,
        navController = navController,
        drawerController = drawerController,
        messageController = messageController,
        snackBarHostState = snackBarHostState
    )
}

class MelodifyAppState(
    val scope: CoroutineScope,
    val navController: NavHostController,
    val drawerController: DrawerController,
    private val messageController: MessageController,
    val snackBarHostState: SnackbarHostState
) {
    init {
        scope.launch {
            for (dialog in messageController.sendDialogChannel) {
                navController.navigateToDialog(dialog)
            }
        }

        scope.launch {
            for (message in messageController.snackBarMessageChannel) {
                val result = snackBarHostState.showSnackbar(message)
                messageController.snackBarResultChannel.send(result)
            }
        }
    }

    fun onDialogResult(dialog: Dialog, interactionResult: InteractionResult) {
        messageController.onResult(dialog, interactionResult)
    }
}