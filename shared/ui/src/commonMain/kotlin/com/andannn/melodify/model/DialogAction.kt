/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel

sealed interface DialogAction {
    data object Dismissed : DialogAction

    interface AlertDialog : DialogAction {
        data object Accept : AlertDialog

        data object Decline : AlertDialog
    }

    interface InputDialog : DialogAction {
        data class Accept(
            val input: String,
        ) : InputDialog

        object Decline : InputDialog
    }

    interface MediaOptionDialog : DialogAction {
        data class ClickOptionItem(
            val optionItem: OptionItem,
        ) : MediaOptionDialog
    }

    interface AddToPlayListDialog : DialogAction {
        data class OnAddToPlayList(
            val playList: PlayListItemModel,
            val items: List<MediaItemModel>,
        ) : AddToPlayListDialog

        object OnCreateNewPlayList : AddToPlayListDialog
    }

    interface SleepTimerOptionDialog : DialogAction {
        data class OnOptionClick(
            val option: SleepTimerOption,
        ) : SleepTimerOptionDialog
    }

    interface SleepTimerCountingDialog : DialogAction {
        data object OnCancelTimer : SleepTimerCountingDialog
    }
}
