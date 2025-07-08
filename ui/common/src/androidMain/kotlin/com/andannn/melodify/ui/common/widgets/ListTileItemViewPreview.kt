package com.andannn.melodify.ui.common.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun MusicCardPreview1() {
    MaterialTheme {
        ListTileItemView(
            albumArtUri = "",
            title = "Title",
            subTitle = "artist",
            showTrackNum = true,
        )
    }
}

@Preview
@Composable
private fun MusicCardPreviewActive() {
    MaterialTheme {
        ListTileItemView(
            albumArtUri = "",
            title = "Title",
            trackNum = 9,
            subTitle = "artist",
            isActive = true,
            showTrackNum = true,
        )
    }
}

@Preview
@Composable
private fun MusicCardSwapNoSubTitle() {
    MaterialTheme {
        ListTileItemView(
            albumArtUri = "",
            swapIconModifier = Modifier,
            title = "Title",
            subTitle = "",
            showTrackNum = true,
        )
    }
}
