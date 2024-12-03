package com.andannn.melodify

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.navigation.compose.NavHost
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.navigation.routes.melodifyDialog
import com.andannn.melodify.routes.MAIN_ROUTE
import com.andannn.melodify.routes.mainRoute
import com.andannn.melodify.ui.components.menu.ActionMenuContainer
import com.andannn.melodify.ui.components.message.dialog.Dialog
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun ApplicationScope.MelodifyDeskTopApp(
    appState: MelodifyDesktopAppState
) {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Melodify",
    ) {
        LaunchedEffect(Unit) {
            getKoin().get<MediaLibrarySyncer>().syncMediaLibrary()
        }

        MenuBar {
            Menu("Preferences") {
                Item("Media library", onClick = {})
            }
        }

        NavHost(
            navController = appState.navController,
            startDestination = MAIN_ROUTE,
            modifier = Modifier.fillMaxSize(),
        ) {
            mainRoute()

            Dialog.getAllDialogs().forEach {
                melodifyDialog(
                    navHostController = appState.navController,
                    dialog = it,
                    onRequestDismiss = appState.navController::popBackStack,
                    onResult = appState::onDialogResult
                )
            }
        }

        ActionMenuContainer()
    }
}
