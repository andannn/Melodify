package com.andannn.melodify.shared.compose.popup

import androidx.compose.runtime.Composable
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.shared.compose.popup.internal.content.AddLibraryPathDialog
import com.andannn.melodify.shared.compose.popup.internal.content.AddToPlayListDialogContent
import com.andannn.melodify.shared.compose.popup.internal.content.ChangeSortRuleDialogContent
import com.andannn.melodify.shared.compose.popup.internal.content.DefaultSortRuleSettingDialog
import com.andannn.melodify.shared.compose.popup.internal.content.MediaOptionContent
import com.andannn.melodify.shared.compose.popup.internal.content.NewPlayListDialogContent
import com.andannn.melodify.shared.compose.popup.internal.content.SleepTimerCountingContent
import com.andannn.melodify.shared.compose.popup.internal.content.SleepTimerOptionDialogContent
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.new_playlist_dialog_input_hint
import melodify.shared.compose.resource.generated.resources.new_playlist_dialog_title

interface DialogId<ACTION : DialogAction> {
    /**
     * Dialog content
     */
    @Composable
    fun Content(onAction: (ACTION) -> Unit)
}

data object SleepTimerOptionDialog : DialogId<DialogAction.SleepTimerOptionDialog> {
    @Composable
    override fun Content(onAction: (DialogAction.SleepTimerOptionDialog) -> Unit) {
        SleepTimerOptionDialogContent(onAction)
    }
}

data class AddMusicsToPlayListDialog(
    val items: List<MediaItemModel>,
    val isAudio: Boolean,
) : DialogId<DialogAction.AddToPlayListDialog> {
    @Composable
    override fun Content(onAction: (DialogAction.AddToPlayListDialog) -> Unit) {
        AddToPlayListDialogContent(
            dialog = this,
            onAction = onAction,
        )
    }
}

/**
 * Change the sort rule of the tab.
 */
data class ChangeSortRuleDialog(
    val tab: CustomTab,
) : DialogId<DialogAction.None> {
    @Composable
    override fun Content(onAction: (DialogAction.None) -> Unit) {
        ChangeSortRuleDialogContent(
            dialog = this,
        )
    }
}

data object DefaultSortRuleSettingDialog : DialogId<DialogAction.None> {
    @Composable
    override fun Content(onAction: (DialogAction.None) -> Unit) {
        DefaultSortRuleSettingDialog()
    }
}

data object NewPlayListDialog : DialogId<DialogAction.InputDialog> {
    val title = Res.string.new_playlist_dialog_title
    val playListNameInputHint = Res.string.new_playlist_dialog_input_hint

    @Composable
    override fun Content(onAction: (DialogAction.InputDialog) -> Unit) {
        NewPlayListDialogContent(onAction = onAction)
    }
}

data object AddLibraryPathDialog : DialogId<DialogAction.InputDialog> {
    val title = Res.string.new_playlist_dialog_title

    @Composable
    override fun Content(onAction: (DialogAction.InputDialog) -> Unit) {
        AddLibraryPathDialog(onAction = onAction)
    }
}

data class OptionDialog(
    val options: List<OptionItem>,
) : DialogId<DialogAction.MediaOptionDialog> {
    @Composable
    override fun Content(onAction: (DialogAction.MediaOptionDialog) -> Unit) {
        MediaOptionContent(
            dialogId = this,
            onAction = onAction,
        )
    }
}

data object SleepCountingDialog : DialogId<DialogAction.SleepTimerCountingDialog> {
    @Composable
    override fun Content(onAction: (DialogAction.SleepTimerCountingDialog) -> Unit) {
        SleepTimerCountingContent(onAction = onAction)
    }
}
