package com.andannn.melodify.navigation.routes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andannn.melodify.ui.components.library.LibraryView

const val LIBRARY_ROUTE = "LIBRARY_ROUTE"

fun NavController.navigateToLibrary() {
    this.navigate(LIBRARY_ROUTE)
}

fun NavGraphBuilder.libraryScreen(
    onBackPressed: () -> Unit,
    onNavigateSearchPage: () -> Unit,
) {
    composable(
        route = LIBRARY_ROUTE,
    ) {
        LibraryView(
            onBackKeyPressed = onBackPressed,
            navigateToSearch = onNavigateSearchPage
        )
    }
}
