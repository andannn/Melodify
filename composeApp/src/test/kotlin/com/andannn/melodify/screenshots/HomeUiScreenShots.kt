/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.screenshots

import androidx.compose.runtime.Composable
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.navigation.routes.HomeState
import com.andannn.melodify.navigation.routes.HomeUiScreen
import com.andannn.melodify.screenshots.util.ScreenShotsTest
import com.andannn.melodify.screenshots.util.audioList1
import com.andannn.melodify.screenshots.util.audioList2
import com.andannn.melodify.screenshots.util.snapshotWithOption
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tabcontent.GroupType
import com.andannn.melodify.ui.components.tabcontent.HeaderItem
import com.andannn.melodify.ui.components.tabcontent.TabContentState
import org.junit.Test

@Composable
fun HomeScreenShot(isDark: Boolean) {
    MelodifyTheme(
        darkTheme = isDark,
    ) {
        HomeUiScreen(
            homeState =
                HomeState(
                    tabUiState =
                        TabUiState(
                            selectedIndex = 0,
                            customTabList =
                                listOf(
                                    CustomTab.AllMusic,
                                    CustomTab.AlbumDetail(albumId = "a", label = "TAG 1"),
                                    CustomTab.AlbumDetail(albumId = "b", label = "TAG 2"),
                                    CustomTab.AlbumDetail(albumId = "c", label = "TAG 3"),
                                ),
                        ),
                    tabContentState =
                        TabContentState(
                            contentMap =
                                mapOf(
                                    HeaderItem(GroupType.ALBUM, "7466598606566714508") to audioList1,
                                    HeaderItem(GroupType.ALBUM, "570547186712440806") to audioList2,
                                ),
                        ),
                ),
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
