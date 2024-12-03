package com.andannn.melodify.ui.components.message.dialog

import androidx.compose.ui.window.DialogProperties
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.confirm_delete_playlist_item
import melodify.ui.common.generated.resources.decline
import melodify.ui.common.generated.resources.duplicated_alert_dialog_title
import melodify.ui.common.generated.resources.having_registered_track_in_playlist
import melodify.ui.common.generated.resources.new_playlist_dialog_input_hint
import melodify.ui.common.generated.resources.new_playlist_dialog_title
import melodify.ui.common.generated.resources.ok
import melodify.ui.common.generated.resources.skip_registered_songs
import org.jetbrains.compose.resources.StringResource

sealed interface InteractionResult {
    data object DISMISS : InteractionResult

    interface AlertDialog : InteractionResult {
        data object ACCEPT : AlertDialog

        data object DECLINE : AlertDialog
    }

    interface NewPlaylistDialog : InteractionResult {
        data class ACCEPT(val playlistName: String) : NewPlaylistDialog

        data object DECLINE : NewPlaylistDialog
    }

    interface AddToPlayListRequest : InteractionResult {
        data class ClickPlayList(val playListId: String) : AddToPlayListRequest

        data object CreateNewPlayList : AddToPlayListRequest
    }
}

sealed class Dialog(
    open val id: String,
    open val dialogProperties: DialogProperties
) {
    abstract class AlertDialog(
        override val id: String,
        override val dialogProperties: DialogProperties = DialogProperties(),
        val title: StringResource? = null,
        val message: StringResource? = null,
        val positive: StringResource,
        val negative: StringResource? = null,
    ) : Dialog(id, dialogProperties)

    data object ConfirmDeletePlaylist : AlertDialog(
        id = ConfirmDeletePlaylist::class.simpleName!!,
        message = Res.string.confirm_delete_playlist_item,
        positive = Res.string.ok,
        negative = Res.string.decline
    )

    data object DuplicatedAlert : AlertDialog(
        id = DuplicatedAlert::class.simpleName!!,
        title = Res.string.duplicated_alert_dialog_title,
        message = Res.string.having_registered_track_in_playlist,
        positive = Res.string.skip_registered_songs,
    )

    data object NewPlayListDialog : Dialog(
        id = NewPlayListDialog::class.simpleName!!,
        dialogProperties = DialogProperties(),
    ) {
        val title = Res.string.new_playlist_dialog_title
        val playListNameInputHint = Res.string.new_playlist_dialog_input_hint
        val positive = Res.string.ok
        val negative = Res.string.decline
    }

    companion object {
        fun getAllDialogs() = listOf(
            ConfirmDeletePlaylist,
            DuplicatedAlert,
            NewPlayListDialog
        )
    }
}
