package com.andannn.melodify

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.andannn.melodify.feature.customtab.rememberCustomTabSettingViewStateHolder

@Composable
fun ModalDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    content: @Composable () -> Unit
) {

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            val stateHolder = rememberCustomTabSettingViewStateHolder()
            stateHolder.state.collectAsState()
            ModalDrawerSheet(
                drawerState = drawerState
            ) {
                NavigationDrawerItem(
                    label = { Text("Home") },
                    onClick = { /*TODO*/ },
                    selected = true,
                )
            }
        },
        content = content,
    )
}
