package com.andannn.melodify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andannn.melodify.feature.common.util.getUiRetainedScope
import com.andannn.melodify.feature.drawer.DrawerController
import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope


@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    retainedScope: Scope = getUiRetainedScope()!!,
    drawerController: DrawerController = retainedScope.get<DrawerController>(),
) = remember(
    key1 = navController,
    key2 = drawerController,
    key3 = scope
) {
    MelodifyAppState(
        scope = scope,
        navController = navController,
        drawerController = drawerController
    )
}

class MelodifyAppState(
    val scope: CoroutineScope,
    val navController: NavHostController,
    val drawerController: DrawerController
) {
}