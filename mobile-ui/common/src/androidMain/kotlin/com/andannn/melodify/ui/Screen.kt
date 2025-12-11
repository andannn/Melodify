/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui

import androidx.navigation3.runtime.NavKey
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Library : Screen

    @Serializable
    data class LibraryDetail(
        val datasource: LibraryDataSource,
    ) : Screen

    @Serializable
    object Search : Screen

    @Serializable
    object TabManage : Screen
}
