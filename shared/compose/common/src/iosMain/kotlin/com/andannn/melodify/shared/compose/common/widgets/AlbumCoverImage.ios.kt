package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import org.jetbrains.compose.resources.painterResource

@Composable
internal actual fun MediaCoverImage(
    model: String?,
    modifier: Modifier,
) {
    if (model == null) return

    if (isIOSCustomMPLibraryUri(model)) {
        IOSMediaArtworkView(
            modifier = Modifier.fillMaxSize(),
            coverUri = model,
        )
    } else {
        AsyncImageImpl(model, modifier)
    }
}
