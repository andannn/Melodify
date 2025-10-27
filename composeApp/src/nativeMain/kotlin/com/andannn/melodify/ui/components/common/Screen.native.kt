/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.common

import com.andannn.melodify.ui.components.librarydetail.LibraryDataSource
import com.slack.circuit.runtime.screen.Screen

actual object HomeScreen : Screen

actual object LibraryScreen : Screen

actual object SearchScreen : Screen

actual data class LibraryContentListScreen(
    actual val datasource: LibraryDataSource,
) : Screen

actual fun newLibraryContentListScreen(datasource: LibraryDataSource): LibraryContentListScreen = LibraryContentListScreen(datasource)

actual object TabManageScreen : Screen
