/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

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

fun buildSavedStateConfiguration() =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Screen.Home::class, Screen.Home.serializer())
                    subclass(Screen.Library::class, Screen.Library.serializer())
                    subclass(Screen.LibraryDetail::class, Screen.LibraryDetail.serializer())
                    subclass(Screen.Search::class, Screen.Search.serializer())
                    subclass(Screen.TabManage::class, Screen.TabManage.serializer())
                }
            }
    }
