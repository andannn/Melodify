package com.andannn.melodify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.ui.components.tabselector.CustomTabSelector
import com.andannn.melodify.ui.components.tab.rememberTabUiStateHolder
import com.andannn.melodify.ui.components.tabcontent.rememberTabContentStateHolder
import com.andannn.melodify.navigation.routes.TabWithContent
import com.andannn.melodify.ui.components.playcontrol.Player
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
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        Row(modifier = modifier.weight(1f)) {
            LeftSidePaneSector(
                modifier = Modifier.weight(1f)
            )

            VerticalDivider()

            TabWithContentSector(
                modifier = Modifier.weight(2f)
            )
        }

        HorizontalDivider()

        Player(
            modifier = Modifier
        )
    }
}

@Composable
private fun TabWithContentSector(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val tabUiStateHolder = rememberTabUiStateHolder(
        scope = scope
    )
    val tabContentStateHolder = rememberTabContentStateHolder(
        scope = scope,
        selectedTab = tabUiStateHolder.state.selectedTab
    )
    Surface(
        modifier = modifier
    ) {
        TabWithContent(
            modifier = Modifier,
            tabUiStateHolder = tabUiStateHolder,
            tabContentStateHolder = tabContentStateHolder
        )
    }
}

@Composable
private fun LeftSidePaneSector(
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        CustomTabSelector(
            modifier = Modifier
        )
    }
}