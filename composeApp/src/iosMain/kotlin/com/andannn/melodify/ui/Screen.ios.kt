/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui
/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import com.andannn.melodify.model.LibraryDataSource
import com.slack.circuit.runtime.screen.Screen

actual object HomeScreen : Screen

actual object LibraryScreen : Screen

actual object SearchScreen : Screen

actual object TabManageScreen : Screen

actual data class LibraryDetailScreen actual constructor(
    actual val datasource: LibraryDataSource,
) : Screen
