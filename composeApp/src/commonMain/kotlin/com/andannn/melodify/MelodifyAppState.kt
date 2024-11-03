package com.andannn.melodify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andannn.melodify.feature.common.util.getUiRetainedScope
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.message.InteractionResult
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.message.MessageDialog
import com.andannn.melodify.feature.message.navigateToAlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.scope.Scope

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    retainedScope: Scope = getUiRetainedScope()!!,
    drawerController: DrawerController = retainedScope.get<DrawerController>(),
    messageController: MessageController = retainedScope.get<MessageController>()
) = remember(
    navController,
    drawerController,
    scope,
    messageController
) {
    MelodifyAppState(
        scope = scope,
        navController = navController,
        drawerController = drawerController,
        messageController = messageController,
    )
}

class MelodifyAppState(
    val scope: CoroutineScope,
    val navController: NavHostController,
    val drawerController: DrawerController,
    private val messageController: MessageController
) {
    fun onDialogResult(messageDialog: MessageDialog, interactionResult: InteractionResult) {
        messageController.onResult(messageDialog, interactionResult)
    }

    init {
        scope.launch {
            for (dialog in messageController.sendDialogChannel) {
                navController.navigateToAlertDialog(dialog)
            }
        }
    }
}