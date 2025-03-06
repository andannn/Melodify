package com.andannn.melodify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.andannn.melodify.navigation.routes.HOME_ROUTE
import com.andannn.melodify.navigation.routes.customTabSetting
import com.andannn.melodify.navigation.routes.homeScreen
import com.andannn.melodify.navigation.routes.libraryContentListScreen
import com.andannn.melodify.navigation.routes.libraryScreen
import com.andannn.melodify.navigation.routes.navigateToCustomTabSetting
import com.andannn.melodify.navigation.routes.navigateToLibrary
import com.andannn.melodify.navigation.routes.navigateToLibraryContentList
import com.andannn.melodify.navigation.routes.navigateToSearch
import com.andannn.melodify.navigation.routes.searchScreen

@Composable
fun MelodifyNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = HOME_ROUTE,
        modifier = modifier,
    ) {
        homeScreen(
            onNavigateCustomTabSetting = navHostController::navigateToCustomTabSetting,
            onNavigateToLibrary = navHostController::navigateToLibrary,
            onNavigateSearchPage = navHostController::navigateToSearch
        )
        customTabSetting(
            onBackPressed = navHostController::popBackStack
        )
        searchScreen(
            onBackPressed = navHostController::popBackStack,
            onNavigateToLibraryContentList = navHostController::navigateToLibraryContentList
        )
        libraryScreen(
            onBackPressed = navHostController::popBackStack,
            onNavigateSearchPage = navHostController::navigateToSearch,
            onNavigateToLibraryContentList = navHostController::navigateToLibraryContentList
        )
        libraryContentListScreen(
            onNavigateToLibraryContentList = navHostController::navigateToLibraryContentList,
            onBackPressed = navHostController::popBackStack
        )
    }
}
