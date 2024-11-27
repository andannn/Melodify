package com.andannn.melodify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.andannn.melodify.feature.drawer.BottomDrawerContainer
import com.andannn.melodify.feature.player.PlayerAreaView
import com.andannn.melodify.feature.player.PlayerStateViewModel
import com.andannn.melodify.feature.player.PlayerUiState
import com.andannn.melodify.navigation.MelodifyNavHost
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MelodifyMobileApp(
    modifier: Modifier = Modifier,
    appState: MelodifyAppState = rememberAppState(),
    playerStateViewModel: PlayerStateViewModel = koinViewModel {
        parametersOf(appState.drawerController)
    },
) {
    ModalDrawer(
        drawerState = appState.drawerState,
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
                onBackPressed = navController::popBackStack,
                onDialogResult = appState::onDialogResult,
            )

            val playerUiState by playerStateViewModel.playerUiStateFlow.collectAsState()
            if (playerUiState is PlayerUiState.Active) {
                PlayerAreaView(
                    state = playerUiState as PlayerUiState.Active,
                    onEvent = playerStateViewModel::onEvent,
                )
            }

            val drawerController = appState.drawerController
            val bottomSheetModel by drawerController.bottomSheetModel.collectAsState(null)
            BottomDrawerContainer(
                bottomSheet = bottomSheetModel,
                onEvent = { event ->
                    drawerController.onEvent(event)
                }
            )
        }
    }
}
