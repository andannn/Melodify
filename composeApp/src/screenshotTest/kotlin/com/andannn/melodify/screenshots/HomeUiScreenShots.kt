package com.andannn.melodify.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.navigation.routes.HomeState
import com.andannn.melodify.navigation.routes.HomeUiScreen
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tabcontent.TabContentState

@PreviewLightDark
@Composable
fun HomeScreenShot(modifier: Modifier = Modifier) {
    MelodifyTheme {
        HomeUiScreen(
            homeState = HomeState(
                tabUiState = TabUiState(
                    selectedIndex = 0,
                    customTabList = listOf(
                        CustomTab.AlbumDetail(albumId = "a", label = "TAG 1"),
                        CustomTab.AlbumDetail(albumId = "b", label = "TAG 2"),
                        CustomTab.AlbumDetail(albumId = "c", label = "TAG 3"),
                    )
                ),
                tabContentState = TabContentState()
            )
        )
    }
}