package com.andannn.melodify.feature.message.dialog

import androidx.compose.ui.window.DialogProperties
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.confirm_delete_playlist_item
import melodify.feature.common.generated.resources.decline
import melodify.feature.common.generated.resources.duplicated_alert_dialog_title
import melodify.feature.common.generated.resources.having_registered_track_in_playlist
import melodify.feature.common.generated.resources.ok
import melodify.feature.common.generated.resources.skip_registered_songs
import org.jetbrains.compose.resources.StringResource

sealed class MessageDialog(
    val id: String,
    val title: StringResource? = null,
    val message: StringResource? = null,
    val positive: StringResource,
    val negative: StringResource? = null,
    val dialogProperties: DialogProperties = DialogProperties()
) {
    data object ConfirmDeletePlaylist : MessageDialog(
        id = ConfirmDeletePlaylist::class.simpleName!!,
        message = Res.string.confirm_delete_playlist_item,
        positive = Res.string.ok,
        negative = Res.string.decline
    )

    data object DuplicatedAlert : MessageDialog(
        id = DuplicatedAlert::class.simpleName!!,
        title = Res.string.duplicated_alert_dialog_title,
        message = Res.string.having_registered_track_in_playlist,
        positive = Res.string.skip_registered_songs,
    )
}