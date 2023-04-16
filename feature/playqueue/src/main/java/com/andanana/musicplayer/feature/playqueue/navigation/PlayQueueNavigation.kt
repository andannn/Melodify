package com.andanana.musicplayer.feature.playqueue.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andanana.musicplayer.feature.playqueue.PlayQueueScreen

private const val TAG = "PlayListNavigation"

const val playingQueueRoute = "play_list_route"

fun NavController.navigateToPlayQueue() {
    this.navigate(playingQueueRoute)
}

fun NavGraphBuilder.playListScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = playingQueueRoute
    ) {
        PlayQueueScreen()
    }
}