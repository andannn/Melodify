package com.andannn.melodify.feature.message

import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.playlist_page_title
import org.jetbrains.compose.resources.StringResource

sealed class MessageDialog(
    val title: StringResource,
    val message: StringResource,
    val positive: StringResource,
    val negative: StringResource? = null,
) {
    data object ConfirmDeletePlaylist: MessageDialog(
        title = Res.string.playlist_page_title,
        message = Res.string.playlist_page_title,
        positive = Res.string.playlist_page_title,
        negative = Res.string.playlist_page_title
    )
}