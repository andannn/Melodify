package com.andannn.melodify

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.navigation.compose.NavHost
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.routes.MAIN_ROUTE
import com.andannn.melodify.routes.mainRoute
import com.andannn.melodify.ui.components.popup.dialog.ActionDialogContainer
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

        Scaffold(
            snackbarHost = {
                SnackbarHost(appState.snackBarHostState)
            }
        ) {
            NavHost(
                navController = appState.navController,
                startDestination = MAIN_ROUTE,
                modifier = Modifier.fillMaxSize(),
            ) {
                mainRoute()
            }
        }

        ActionDialogContainer()
    }
}
