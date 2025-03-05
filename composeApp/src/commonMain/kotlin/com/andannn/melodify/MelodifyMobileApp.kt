package com.andannn.melodify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.navigation.MelodifyNavHost
import com.andannn.melodify.ui.components.playcontrol.ui.PlayerAreaView
import com.andannn.melodify.ui.components.popup.dialog.ActionDialogContainer

@Composable
fun MelodifyMobileApp(
    modifier: Modifier = Modifier,
    appState: MelodifyAppState = rememberAppState(),
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        snackbarHost = {
            SnackbarHost(appState.snackBarHostState)
        },
    ) {
        val navController = appState.navController
        MelodifyNavHost(
            navHostController = navController,
            modifier = Modifier.fillMaxWidth(),
        )

        PlayerAreaView()

        ActionDialogContainer()
    }
}
