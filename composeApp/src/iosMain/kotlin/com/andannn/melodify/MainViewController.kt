package com.andannn.melodify

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import platform.MediaPlayer.MPMediaLibrary

fun MainViewController() = ComposeUIViewController(
    configure = {
// TODO: Check debug build
        Napier.base(DebugAntilog())

        startKoin {
            modules(
                modules
            )
        }
    }
) {
    var permissionGranted by remember {
        mutableStateOf(haveMediaPermission())
    }
    if (!permissionGranted) {
        LaunchedEffect(Unit) {
            MPMediaLibrary.requestAuthorization { status ->
                permissionGranted = haveMediaPermission()
            }
        }
    }

    Napier.d("Permission granted: $permissionGranted")
    if (permissionGranted) {
        MelodifyMobileApp()
    }
}

private fun haveMediaPermission(): Boolean {
    return MPMediaLibrary.authorizationStatus() == MPMediaLibraryAuthorizationStatus.authorized.ordinal.toLong()
}

private enum class MPMediaLibraryAuthorizationStatus {
    notDetermined,
    restricted,
    denied,
    authorized
}