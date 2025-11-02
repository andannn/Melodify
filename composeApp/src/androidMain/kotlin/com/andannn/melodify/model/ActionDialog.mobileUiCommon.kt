/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

actual val DialogId.dialogIdType: DialogType
    get() =
        when (this) {
            is DialogId.SleepTimerOptionDialog,
            DialogId.SleepCountingDialog,
            is DialogId.OptionDialog,
            is DialogId.AddMusicsToPlayListDialog,
            is DialogId.ChangeSortRuleDialog,
            -> DialogType.ModalBottomSheet

            DialogId.NewPlayListDialog,
            is DialogId.AlertDialog,
            DialogId.AddLibraryPathDialog,
            -> DialogType.AlertDialog
        }
