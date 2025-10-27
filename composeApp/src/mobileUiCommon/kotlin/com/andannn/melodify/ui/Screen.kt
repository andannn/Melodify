/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui

import com.andannn.melodify.model.LibraryDataSource
import com.slack.circuit.runtime.screen.Screen

expect object HomeScreen : Screen

expect object LibraryScreen : Screen

expect object SearchScreen : Screen

expect class LibraryDetailScreen : Screen {
    constructor(datasource: LibraryDataSource)

    val datasource: LibraryDataSource
}

expect object TabManageScreen : Screen
