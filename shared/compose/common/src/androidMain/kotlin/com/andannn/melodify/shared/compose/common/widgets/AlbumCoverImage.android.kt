package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun MediaCoverImage(
    model: String?,
    modifier: Modifier,
) = AsyncImageImpl(model, modifier)
