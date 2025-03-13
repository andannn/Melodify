package com.andannn.melodify.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.navigation.routes.HomeState
import com.andannn.melodify.navigation.routes.HomeUiScreen
import com.andannn.melodify.screenshots.util.ScreenShotsTest
import com.andannn.melodify.screenshots.util.album1
import com.andannn.melodify.screenshots.util.album2
import com.andannn.melodify.screenshots.util.audioList1
import com.andannn.melodify.screenshots.util.audioList2
import com.andannn.melodify.screenshots.util.snapshotWithOption
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tabcontent.TabContentState
import org.junit.Rule
import org.junit.Test

@Composable
fun HomeScreenShot(isDark: Boolean) {
    MelodifyTheme(
        darkTheme = isDark
    ) {
        HomeUiScreen(
            homeState = HomeState(
                tabUiState = TabUiState(
                    selectedIndex = 0,
                    customTabList = listOf(
                        CustomTab.AllMusic,
                        CustomTab.AlbumDetail(albumId = "a", label = "TAG 1"),
                        CustomTab.AlbumDetail(albumId = "b", label = "TAG 2"),
                        CustomTab.AlbumDetail(albumId = "c", label = "TAG 3"),
                    )
                ),
                tabContentState = TabContentState(
                    contentMap = mapOf(
                        album1 to audioList1,
                        album2 to audioList2
                    )
                )
            )
        )
    }
}

class HomeUiScreenShotsTest : ScreenShotsTest() {

    @Test
    fun takeScreenShot() {
        paparazzi.snapshotWithOption("HomeScreenShot") { isDark ->
            HomeScreenShot(isDark)
        }
    }
}

