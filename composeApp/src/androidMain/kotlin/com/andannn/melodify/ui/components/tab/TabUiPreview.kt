/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tab

import androidx.compose.runtime.Composable
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.ui.common.previews.CustomPreviews
import com.andannn.melodify.ui.common.theme.MelodifyTheme

@Composable
@CustomPreviews
internal fun TabUiPreview() {
    MelodifyTheme {
        TabUi(
            state =
                TabUiState(
                    selectedIndex = 0,
                    customTabList =
                        listOf(
                            CustomTab.AllMusic,
                            CustomTab.AlbumDetail(albumId = "1", label = "Long Long Long Long Long Long Long Long Long Long"),
                            CustomTab.ArtistDetail(artistId = "1", label = "Artist1"),
                            CustomTab.GenreDetail(genreId = "1", label = "Genre1"),
                        ),
                ),
        )
    }
}
