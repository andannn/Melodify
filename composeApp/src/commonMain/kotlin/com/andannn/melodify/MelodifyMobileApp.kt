package com.andannn.melodify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.feature.customtab.CustomTabSelector
import com.andannn.melodify.feature.drawer.BottomDrawerContainer
import com.andannn.melodify.feature.player.PlayerAreaView
import com.andannn.melodify.feature.player.PlayerStateViewModel
import com.andannn.melodify.feature.player.PlayerUiState
import com.andannn.melodify.navigation.MelodifyNavHost
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.library_title
import org.jetbrains.compose.resources.stringResource
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

@Composable
private fun ModalDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = modifier,
                drawerState = drawerState
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.library_title),
                    style = MaterialTheme.typography.titleLarge,
                )

                CustomTabSelector()
            }
        },
        content = content,
    )
}
