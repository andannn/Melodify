/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import com.andannn.melodify.shared.compose.components.tab.management.TabManagementUi
import com.andannn.melodify.shared.compose.popup.ModalBottomSheetFactoryProvider
import io.github.andannn.popup.PopupEntryProviderScope
import io.github.andannn.popup.PopupId

data object TabManagementDialogID : PopupId<Any>

fun PopupEntryProviderScope<PopupId<*>>.addTabManagementDialogEntry() {
    entry(
        TabManagementDialogID,
        metadata = ModalBottomSheetFactoryProvider.bottomSheet(),
    ) { _, _ ->
        TabManagementUi()
    }
}
