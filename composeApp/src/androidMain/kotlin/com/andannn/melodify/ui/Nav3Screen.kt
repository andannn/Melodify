package com.andannn.melodify.ui

import androidx.navigation3.runtime.NavKey
import com.andannn.melodify.model.LibraryDataSource
import kotlinx.serialization.Serializable

@Serializable
sealed interface Nav3Screen : NavKey {
    @Serializable
    data object HomeScreen : Nav3Screen

    @Serializable
    data object LibraryScreen : Nav3Screen

    @Serializable
    data class LibraryDetailScreen(
        val datasource: LibraryDataSource,
    ) : Nav3Screen

    @Serializable
    object SearchScreen : Nav3Screen

    @Serializable
    object TabManageScreen : Nav3Screen
}
