/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.components.tab.management.TabManagementUi
import com.andannn.melodify.shared.compose.popup.ModalBottomSheetFactoryProvider
import com.andannn.melodify.ui.routes.home.tab.TabSelector
import com.andannn.melodify.ui.routes.home.tab.retainTabSelectorPresenter
import io.github.andannn.popup.PopupEntryProviderScope
import io.github.andannn.popup.PopupId

data object TabManagementDialogID : PopupId<Any>

fun PopupEntryProviderScope<PopupId<*>>.addTabManagementDialogEntry() {
    entry(
        TabManagementDialogID,
        metadata = ModalBottomSheetFactoryProvider.bottomSheet(),
    ) { _, _ ->
        TabManagementDialogContent()
    }
}

@Composable
private fun TabManagementDialogContent(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column {
            TabManagementUi(modifier = Modifier.weight(2f))

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()

            TabSelector(modifier = Modifier.weight(3f), retainTabSelectorPresenter().present())
        }
    }
}
