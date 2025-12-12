package com.andannn.melodify

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.core.syncer.MusicLibraryPermissionHandler
import com.andannn.melodify.ui.LocalScreenOrientationController
import com.andannn.melodify.ui.app.MelodifyMobileApp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.collect
import org.koin.mp.KoinPlatform.getKoin

@Suppress("ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController(
        configure = {
// TODO: Check debug build
            Napier.base(DebugAntilog())
        },
    ) {
        val permissionHandler: MusicLibraryPermissionHandler = getKoin().get()
        val syncer: MediaLibrarySyncer = getKoin().get()
        var haveMediaLibraryPermission by remember { mutableStateOf(permissionHandler.mediaPermissionGranted()) }
// TEST
        LaunchedEffect(Unit) {
            syncer.syncAllMediaLibrary().collect()
        }
// TEST

        if (!haveMediaLibraryPermission) {
            LaunchedEffect(Unit) {
                permissionHandler.requestMusicLibraryAuthorization { granted ->
                    haveMediaLibraryPermission = granted
                }
            }
        }

        CompositionLocalProvider(
            LocalScreenOrientationController provides getKoin().get(),
        ) {
            if (haveMediaLibraryPermission) {
                MelodifyMobileApp()
            }
        }
    }
