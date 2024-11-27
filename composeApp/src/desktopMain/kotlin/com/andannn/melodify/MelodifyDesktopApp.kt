package com.andannn.melodify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.home.HomeRoute
import com.andannn.melodify.feature.home.HomeUiContent
import com.andannn.melodify.feature.home.HomeUiEvent
import com.andannn.melodify.feature.home.HomeViewModel
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.player.PlayerSector
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun MelodifyDeskTopApp() {
    Window(
        onCloseRequest = {},
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

        MainWindowContent()
    }
}

@Composable
fun MainWindowContent(
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxSize()) {
        CustomShrinkableTabSelector(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface)
        )

        VerticalDivider()

        Column(
            modifier = Modifier.weight(2f),
        ) {
            HomeSector(
                modifier = Modifier.weight(1f)
            )

            HorizontalDivider()

            PlayerSector(
                modifier = Modifier
            )
        }
    }
}

@Composable
fun HomeSector(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val state by homeViewModel.state.collectAsState()

    Surface(
        modifier = modifier
    ) {
        HomeUiContent(
            modifier = Modifier,
            uiState = state,
            onEvent = homeViewModel::onEvent,
            onMediaItemClick = {
                if (it is AudioItemModel) {
                    homeViewModel.onEvent(HomeUiEvent.OnMusicItemClick(it))
                }
            }
        )
    }
}