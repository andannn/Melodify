/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.library

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.Screen

fun EntryProviderScope<NavKey>.libraryEntryBuilder(navigator: Navigator) {
    entry<Screen.Library> {
        Library(navigator)
    }

    entry<Screen.LibraryDetail> { screen ->
        LibraryDetail(
            navigator = navigator,
            dataSource = screen.datasource,
        )
    }
}
