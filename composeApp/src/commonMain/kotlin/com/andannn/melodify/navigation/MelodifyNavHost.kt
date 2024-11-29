package com.andannn.melodify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.andannn.melodify.feature.customtab.navigation.customTabSetting
import com.andannn.melodify.feature.customtab.navigation.navigateToCustomTabSetting
import com.andannn.melodify.feature.home.navigation.HOME_ROUTE
import com.andannn.melodify.feature.home.navigation.homeScreen
import com.andannn.melodify.feature.message.dialog.Dialog
import com.andannn.melodify.feature.message.dialog.InteractionResult
import com.andannn.melodify.feature.message.dialog.melodifyDialog

@Composable
fun MelodifyNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onDialogResult: (Dialog, InteractionResult) -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = HOME_ROUTE,
        modifier = modifier,
    ) {
        homeScreen(
            onNavigateCustomTabSetting = navHostController::navigateToCustomTabSetting
        )
        customTabSetting(
            onBackPressed = onBackPressed
        )

        Dialog.getAllDialogs().forEach {
            melodifyDialog(
                navHostController = navHostController,
                dialog = it,
                onRequestDismiss = onBackPressed,
                onResult = onDialogResult
            )
        }
    }
}
