/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.common

import com.andannn.melodify.ui.components.librarycontentlist.LibraryDataSource
import com.slack.circuit.runtime.screen.Screen

expect object HomeScreen : Screen

expect object LibraryScreen : Screen

expect object SearchScreen : Screen

expect class LibraryContentListScreen : Screen {
    val datasource: LibraryDataSource
}

expect fun newLibraryContentListScreen(datasource: LibraryDataSource): LibraryContentListScreen
