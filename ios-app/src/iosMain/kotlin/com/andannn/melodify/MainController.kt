/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.andannn.melodify.core.syncer.MediaLibrarySyncRepository
import com.andannn.melodify.core.syncer.MusicLibraryPermissionHandler
import com.andannn.melodify.domain.UserPreferenceRepository
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.usecase.startSyncMediaLibraryIfNeeded
import com.andannn.melodify.ui.AppTitleHolder
import com.andannn.melodify.ui.LocalAppTitleHolder
import com.andannn.melodify.ui.LocalScreenOrientationController
import com.andannn.melodify.ui.LocalSystemUiController
import com.andannn.melodify.ui.app.MelodifyMobileApp
import com.andannn.melodify.ui.player.LocalPlayerStateHolder
import com.andannn.melodify.ui.player.PlayerStateHolder
import com.andannn.melodify.util.brightness.IosBrightnessController
import com.andannn.melodify.util.brightness.LocalBrightnessController
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
        val syncer: MediaLibrarySyncRepository = getKoin().get()
        val userPreferenceRepository: UserPreferenceRepository = getKoin().get()
        var haveMediaLibraryPermission by remember { mutableStateOf(permissionHandler.mediaPermissionGranted()) }

        LaunchedEffect(Unit) {
            context(syncer, userPreferenceRepository) {
                startSyncMediaLibraryIfNeeded()
            }
        }

        if (!haveMediaLibraryPermission) {
            LaunchedEffect(Unit) {
                permissionHandler.requestMusicLibraryAuthorization { granted ->
                    haveMediaLibraryPermission = granted
                }
            }
        }

        CompositionLocalProvider(
            LocalScreenOrientationController provides getKoin().get(),
            LocalBrightnessController provides IosBrightnessController(),
            LocalSystemUiController provides getKoin().get(),
            LocalPlayerStateHolder provides retain { PlayerStateHolder() },
            LocalAppTitleHolder provides AppTitleHolder("Melodify"),
        ) {
            if (haveMediaLibraryPermission) {
                MelodifyTheme {
                    MelodifyMobileApp()
                }
            }
        }
    }
