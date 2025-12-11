/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaItemModel
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.change_play_list_alert_dialog_content
import melodify.shared.ui.generated.resources.confirm_delete_playlist_item
import melodify.shared.ui.generated.resources.decline
import melodify.shared.ui.generated.resources.duplicated_alert_dialog_title
import melodify.shared.ui.generated.resources.having_registered_track_in_playlist
import melodify.shared.ui.generated.resources.invalid_path_alert_dialog_content
import melodify.shared.ui.generated.resources.new_playlist_dialog_input_hint
import melodify.shared.ui.generated.resources.new_playlist_dialog_title
import melodify.shared.ui.generated.resources.ok
import melodify.shared.ui.generated.resources.skip_registered_songs
import org.jetbrains.compose.resources.StringResource

sealed interface DialogId<ACTION : DialogAction> {
    abstract class AlertDialog(
        val title: StringResource? = null,
        val message: StringResource? = null,
        val positive: StringResource,
        val negative: StringResource? = null,
    ) : DialogId<DialogAction.AlertDialog>

    data object ConfirmDeletePlaylist : AlertDialog(
        message = Res.string.confirm_delete_playlist_item,
        positive = Res.string.ok,
        negative = Res.string.decline,
    )

    data object InvalidPathAlert : AlertDialog(
        message = Res.string.invalid_path_alert_dialog_content,
        positive = Res.string.ok,
    )

    data object DuplicatedAlert : AlertDialog(
        title = Res.string.duplicated_alert_dialog_title,
        message = Res.string.having_registered_track_in_playlist,
        positive = Res.string.skip_registered_songs,
    )

    data object ChangePlayListAlert : AlertDialog(
        message = Res.string.change_play_list_alert_dialog_content,
        positive = Res.string.ok,
        negative = Res.string.decline,
    )

    data object NewPlayListDialog : DialogId<DialogAction.InputDialog> {
        val title = Res.string.new_playlist_dialog_title
        val playListNameInputHint = Res.string.new_playlist_dialog_input_hint
        val positive = Res.string.ok
        val negative = Res.string.decline
    }

    data object AddLibraryPathDialog : DialogId<DialogAction.InputDialog> {
        val title = Res.string.new_playlist_dialog_title
        val positive = Res.string.ok
        val negative = Res.string.decline
    }

    /**
     * Change the sort rule of the tab.
     */
    data class ChangeSortRuleDialog(
        val tab: CustomTab,
    ) : DialogId<DialogAction.None>

    data object DefaultSortRuleSettingDialog : DialogId<DialogAction.None>

    data class AddMusicsToPlayListDialog constructor(
        val items: List<MediaItemModel>,
        val isAudio: Boolean,
    ) : DialogId<DialogAction.AddToPlayListDialog>

    data object SleepTimerOptionDialog : DialogId<DialogAction.SleepTimerOptionDialog>

    data object SleepCountingDialog : DialogId<DialogAction.SleepTimerCountingDialog>

    data class OptionDialog(
        val options: List<OptionItem>,
    ) : DialogId<DialogAction.MediaOptionDialog>
}
