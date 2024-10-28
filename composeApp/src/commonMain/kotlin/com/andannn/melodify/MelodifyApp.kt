package com.andannn.melodify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andannn.melodify.feature.player.PlayerStateViewModel
import com.andannn.melodify.feature.player.PlayerUiState
import com.andannn.melodify.feature.player.ui.ShrinkPlayerHeight
import com.andannn.melodify.navigation.MelodifyNavHost
import com.andannn.melodify.feature.common.util.getUiRetainedScope
import com.andannn.melodify.feature.drawer.BottomDrawerContainer
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.player.PlayerAreaView
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

@Composable
fun MelodifyApp(
    modifier: Modifier = Modifier,
    retainedScope: Scope = getUiRetainedScope()!!,
    playerStateViewModel: PlayerStateViewModel = koinViewModel {
        parametersOf(retainedScope.get<DrawerController>())
    },
    drawerController: DrawerController = retainedScope.get<DrawerController>(),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val state by playerStateViewModel.playerUiStateFlow.collectAsState()

        SmpNavHostContainer(
            modifier = Modifier.fillMaxSize(),
        )

        if (state is PlayerUiState.Active) {
            PlayerAreaView(
                state = state as PlayerUiState.Active,
                onEvent = playerStateViewModel::onEvent,
            )
        }

        val bottomSheetModel by drawerController.bottomSheetModel.collectAsState(null)
        val scope = rememberCoroutineScope()
        BottomDrawerContainer(
            bottomSheet = bottomSheetModel,
            onEvent = { event ->
                scope.launch {
                    drawerController.onEvent(event)
                }
            }
        )
    }
}


@Composable
fun SmpNavHostContainer(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    Box(modifier = modifier) {
        MelodifyNavHost(
            modifier = Modifier.fillMaxWidth(),
            navHostController = navController,
            onBackPressed = navController::popBackStack,
        )
        Spacer(
            modifier = Modifier
                .navigationBarsPadding()
                .height(ShrinkPlayerHeight)
        )
    }
}
