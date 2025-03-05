package com.andannn.melodify.navigation.routes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andannn.melodify.ui.components.librarycontentlist.LibraryContentListView
import com.andannn.melodify.ui.components.librarycontentlist.LibraryDataSource

internal const val LIBRARY_CONTENT_LIST_ROUTE = "library_content_list_route"

private const val DATA_SOURCE_ARGUMENT = "data_source"

internal fun NavController.navigateToLibraryContentList(dataSource: LibraryDataSource) {
    this.navigate("$LIBRARY_CONTENT_LIST_ROUTE/${dataSource.toStringCode()}")
}

internal fun NavGraphBuilder.libraryContentListScreen(
    onNavigateToLibraryContentList: (LibraryDataSource) -> Unit = {},
    onBackPressed: () -> Unit
) {
    composable(
        route = "$LIBRARY_CONTENT_LIST_ROUTE/{$DATA_SOURCE_ARGUMENT}",
        arguments = listOf(
            navArgument(DATA_SOURCE_ARGUMENT) {
                type = NavType.StringType
            }
        )
    ) { entry ->
        LibraryContentListView(
            onNavigateToLibraryContentList = onNavigateToLibraryContentList,
            onBackPressed = onBackPressed,
            dataSource = entry.arguments?.getString(DATA_SOURCE_ARGUMENT)
                ?.let { LibraryDataSource.parseFromString(it) }
                ?: error("No data source provided")
        )
    }
}
