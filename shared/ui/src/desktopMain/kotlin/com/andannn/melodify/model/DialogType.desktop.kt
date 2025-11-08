/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

actual val DialogId.dialogIdType: DialogType
    get() =
        when (this) {
            is DialogId.SleepTimerOptionDialog,
            DialogId.NewPlayListDialog,
            DialogId.SleepCountingDialog,
            is DialogId.AlertDialog,
            DialogId.AddLibraryPathDialog,
            is DialogId.AddMusicsToPlayListDialog,
            is DialogId.ChangeSortRuleDialog,
            is DialogId.OptionDialog,
            -> DialogType.AlertDialog
        }
