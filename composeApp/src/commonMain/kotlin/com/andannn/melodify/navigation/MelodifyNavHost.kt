package com.andannn.melodify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.andannn.melodify.navigation.routes.HOME_ROUTE
import com.andannn.melodify.navigation.routes.SEARCH_ROUTE
import com.andannn.melodify.navigation.routes.customTabSetting
import com.andannn.melodify.navigation.routes.homeScreen
import com.andannn.melodify.navigation.routes.navigateToCustomTabSetting
import com.andannn.melodify.navigation.routes.navigateToSearch
import com.andannn.melodify.navigation.routes.searchScreen

@Composable
fun MelodifyNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = HOME_ROUTE,
        modifier = modifier,
    ) {
        homeScreen(
            onNavigateCustomTabSetting = navHostController::navigateToCustomTabSetting,
            onOpenDrawer = onOpenDrawer,
            onNavigateSearchPage = navHostController::navigateToSearch
        )
        customTabSetting(
            onBackPressed = navHostController::popBackStack
        )
        searchScreen(
            onBackPressed = navHostController::popBackStack
        )
    }
}
