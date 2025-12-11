/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel

sealed interface DialogAction {
    data object None : DialogAction

    sealed interface AlertDialog : DialogAction {
        data object Accept : AlertDialog

        data object Decline : AlertDialog
    }

    sealed interface InputDialog : DialogAction {
        data class Accept(
            val input: String,
        ) : InputDialog

        object Decline : InputDialog
    }

    sealed interface MediaOptionDialog : DialogAction {
        data class ClickOptionItem(
            val optionItem: OptionItem,
        ) : MediaOptionDialog
    }

    sealed interface AddToPlayListDialog : DialogAction {
        data class OnAddToPlayList(
            val playList: PlayListItemModel,
            val items: List<MediaItemModel>,
        ) : AddToPlayListDialog

        object OnCreateNewPlayList : AddToPlayListDialog

        object OnDismiss : AddToPlayListDialog
    }

    sealed interface SleepTimerOptionDialog : DialogAction {
        data class OnOptionClick(
            val option: SleepTimerOption,
        ) : SleepTimerOptionDialog
    }

    sealed interface SleepTimerCountingDialog : DialogAction {
        data object OnCancelTimer : SleepTimerCountingDialog
    }
}
