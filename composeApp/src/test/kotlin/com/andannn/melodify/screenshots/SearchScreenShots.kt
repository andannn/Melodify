/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.screenshots

import androidx.compose.runtime.Composable
import com.andannn.melodify.screenshots.util.ScreenShotsTest
import com.andannn.melodify.screenshots.util.album1
import com.andannn.melodify.screenshots.util.audioList1
import com.andannn.melodify.screenshots.util.snapshotWithOption
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.components.search.Search
import com.andannn.melodify.ui.components.search.SearchState
import com.andannn.melodify.ui.components.search.SearchUiState
import kotlin.test.Test

@Composable
fun SearchScreenShots(isDark: Boolean) {
    MelodifyTheme(isDark) {
        Search(
            state =
                SearchUiState(
                    isExpand = false,
                    inputText = "keyword",
                    searchState =
                        SearchState.Result(
                            albums = listOf(album1),
                            audios = audioList1,
                        ),
                ),
        )
    }
}

class SearchScreenShots : ScreenShotsTest() {
    @Test
    fun takeScreenShot() {
        paparazzi.snapshotWithOption("SearchScreenShots") { isDark ->
            SearchScreenShots(isDark)
        }
    }
}
