package com.andanana.musicplayer.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.andanana.musicplayer.core.model.MusicInfo
import com.andanana.musicplayer.feature.home.navigation.homeRoute
import com.andanana.musicplayer.feature.home.navigation.homeScreen
import com.andanana.musicplayer.feature.library.navigation.addPlayListDialog
import com.andanana.musicplayer.feature.library.navigation.libraryScreen
import com.andanana.musicplayer.feature.library.navigation.newPlayListDialog
import com.andanana.musicplayer.feature.playList.navigation.navigateToPlayList
import com.andanana.musicplayer.feature.playList.navigation.playListScreen

private const val TAG = "SmpNavHost"

@Composable
fun SmpNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onShowMusicItemOption: (Uri) -> Unit,
    onNewPlayListButtonClick: () -> Unit,
    onCreateButtonClick: (name: String) -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = homeRoute,
        modifier = modifier
    ) {
        homeScreen(
            onNavigateToPlayList = {
                navHostController.navigateToPlayList(it)
            },
            onShowMusicItemOption = onShowMusicItemOption
        )
        libraryScreen(
            onNavigateToPlayScreen = {
                navHostController.navigateToPlayList(it)
            }
        )
        playListScreen(
            onShowMusicItemOption = onShowMusicItemOption,
            onBackPressed = onBackPressed
        )
        addPlayListDialog(
            onNewPlayListButtonClick = onNewPlayListButtonClick,
            onNavigateBack = onBackPressed
        )
        newPlayListDialog(
            onNavigateBack = onBackPressed,
            onCreateButtonClick = onCreateButtonClick
        )
    }
}
