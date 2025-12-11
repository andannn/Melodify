/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

import com.andannn.melodify.ui.popup.*

internal actual val DialogId<*>.dialogType: DialogType
    get() =
        when (this) {
            is SleepTimerOptionDialog,
            NewPlayListDialog,
            SleepCountingDialog,
            is AlertDialog,
            AddLibraryPathDialog,
            is AddMusicsToPlayListDialog,
            is ChangeSortRuleDialog,
            is OptionDialog,
            DefaultSortRuleSettingDialog,
            -> DialogType.AlertDialog
        }
