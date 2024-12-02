package com.andannn.melodify.ui.player.ui.shrinkable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.player.ui.ShrinkPlayerHeight

@Preview
@Composable
private fun MiniPlayerLayoutPreview() {
    MelodifyTheme {
        Surface {
            MiniPlayerLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ShrinkPlayerHeight),
                title = "BBBBB",
                artist = "AAAAA",
                isPlaying = true,
                isFavorite = false,
                enabled = true,
            )
        }
    }
}
