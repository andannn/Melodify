package com.andannn.melodify.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andannn.melodify.core.data.model.MediaListSource
import com.andannn.melodify.feature.home.HomeRoute

const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.homeScreen(
    onNavigateCustomTabSetting: () -> Unit
) {
    composable(route = HOME_ROUTE) {
        HomeRoute(
            onNavigateCustomTabSetting = onNavigateCustomTabSetting,
        )
    }
}
