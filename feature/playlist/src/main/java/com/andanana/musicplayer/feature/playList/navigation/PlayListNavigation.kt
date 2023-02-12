package com.andanana.musicplayer.feature.playList.navigation

import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andanana.musicplayer.feature.playList.PlayListScreen
import com.andanana.musicplayer.feature.playList.navigation.RequestType.Companion.toRequestType

private const val TAG = "PlayListNavigation"

const val playListRoute = "play_list_route"
const val requestUriTypeArg = "request_play_list_uri"
const val requestUriLastSegmentArg = "request_play_list_lastSegment"

enum class RequestType(val externalContentUri: String) {
    ALBUM_REQUEST(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.toString()),
    ARTIST_REQUEST(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI.toString());

    companion object {
        fun Uri.toRequestType(): RequestType? {
            return RequestType.values().find {
                this.toString().contains(it.externalContentUri)
            }
        }

        fun RequestType.toUri(lastPathSegment: String): Uri =
            Uri.withAppendedPath(Uri.parse(externalContentUri), lastPathSegment)
    }
}

fun NavController.navigateToPlayList(uri: Uri) {
    uri.toRequestType()?.let { type ->
        this.navigate("$playListRoute/${uri.lastPathSegment}/$type")
    }
}

fun NavGraphBuilder.playListScreen(
    navHostController: NavHostController,
    onBackPressed: () -> Unit
) {
    composable(
        route = "$playListRoute/{$requestUriLastSegmentArg}/{$requestUriTypeArg}",
        arguments = listOf(
            navArgument(name = requestUriLastSegmentArg) {
                type = NavType.StringType
            },
            navArgument(name = requestUriTypeArg) {
                type = NavType.EnumType(RequestType::class.java)
            }
        )
    ) {
        val parentEntry = remember(it) {
            navHostController.getBackStackEntry("home_route")
        }
        PlayListScreen(
            playerStateViewModel = hiltViewModel(parentEntry)
        )
    }
}