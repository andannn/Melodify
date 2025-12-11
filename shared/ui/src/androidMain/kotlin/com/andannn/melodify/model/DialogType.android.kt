/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

import com.andannn.melodify.ui.popup.*
import com.andannn.melodify.ui.popup.internal.content.AlertDialog

actual val DialogId<*>.dialogType: DialogType
    get() =
        when (this) {
            is SleepTimerOptionDialog,
            SleepCountingDialog,
            is OptionDialog,
            is AddMusicsToPlayListDialog,
            is ChangeSortRuleDialog,
            DefaultSortRuleSettingDialog,
            -> DialogType.ModalBottomSheet

            NewPlayListDialog,
            is AlertDialog,
            AddLibraryPathDialog,
            -> DialogType.AlertDialog

            else -> error("")
        }
