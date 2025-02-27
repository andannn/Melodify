package com.andannn.melodify.navigation.routes


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andannn.melodify.ui.components.search.SearchView

const val SEARCH_ROUTE = "search_route"

fun NavController.navigateToSearch() {
    this.navigate(SEARCH_ROUTE)
}

fun NavGraphBuilder.searchScreen(onBackPressed: () -> Unit) {
    composable(
        route = SEARCH_ROUTE,
    ) {
        SearchView(
            onBackKeyPressed = onBackPressed
        )
    }
}
