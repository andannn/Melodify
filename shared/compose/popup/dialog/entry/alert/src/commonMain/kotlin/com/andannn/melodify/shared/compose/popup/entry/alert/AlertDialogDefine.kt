/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.alert

import com.andannn.melodify.shared.compose.popup.PopupId
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.change_play_list_alert_dialog_content
import melodify.shared.compose.resource.generated.resources.confirm_delete_playlist_item
import melodify.shared.compose.resource.generated.resources.decline
import melodify.shared.compose.resource.generated.resources.duplicated_alert_dialog_title
import melodify.shared.compose.resource.generated.resources.having_registered_track_in_playlist
import melodify.shared.compose.resource.generated.resources.invalid_path_alert_dialog_content
import melodify.shared.compose.resource.generated.resources.ok
import melodify.shared.compose.resource.generated.resources.skip_registered_songs
import org.jetbrains.compose.resources.StringResource

data object ConfirmDeletePlaylist : AlertPopup(
    message = Res.string.confirm_delete_playlist_item,
    positive = Res.string.ok,
    negative = Res.string.decline,
)

data object InvalidPathAlert : AlertPopup(
    message = Res.string.invalid_path_alert_dialog_content,
    positive = Res.string.ok,
)

data object DuplicatedAlert : AlertPopup(
    title = Res.string.duplicated_alert_dialog_title,
    message = Res.string.having_registered_track_in_playlist,
    positive = Res.string.skip_registered_songs,
)

data object ChangePlayListAlert : AlertPopup(
    message = Res.string.change_play_list_alert_dialog_content,
    positive = Res.string.ok,
    negative = Res.string.decline,
)

abstract class AlertPopup(
    val title: StringResource? = null,
    val message: StringResource? = null,
    val positive: StringResource,
    val negative: StringResource? = null,
) : PopupId<AlertDialogAction>

sealed interface AlertDialogAction {
    data object Accept : AlertDialogAction

    data object Decline : AlertDialogAction
}
