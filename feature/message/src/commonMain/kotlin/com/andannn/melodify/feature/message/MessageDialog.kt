package com.andannn.melodify.feature.message

import androidx.compose.ui.window.DialogProperties
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.playlist_page_title
import org.jetbrains.compose.resources.StringResource

sealed class MessageDialog(
    val id: String,
    val title: StringResource,
    val message: StringResource,
    val positive: StringResource,
    val negative: StringResource? = null,
    val dialogProperties: DialogProperties = DialogProperties()
) {
    data object ConfirmDeletePlaylist : MessageDialog(
        id = ConfirmDeletePlaylist::class.simpleName!!,
        title = Res.string.playlist_page_title,
        message = Res.string.playlist_page_title,
        positive = Res.string.playlist_page_title,
        negative = Res.string.playlist_page_title
    )

    companion object {
        fun fromId(id: String) = when (id) {
            ConfirmDeletePlaylist::class.simpleName!! -> ConfirmDeletePlaylist
            else -> error("invalid id $id")
        }
    }
}