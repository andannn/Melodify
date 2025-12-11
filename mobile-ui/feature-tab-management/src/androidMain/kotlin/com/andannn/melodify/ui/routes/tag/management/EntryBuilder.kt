/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.tag.management

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.components.tabmanagement.TabManagementUi

fun EntryProviderScope<NavKey>.tabManagementEntryBuilder(navigator: Navigator) {
    entry<Screen.TabManage> {
        TabManagementScreen(navigator)
    }
}
