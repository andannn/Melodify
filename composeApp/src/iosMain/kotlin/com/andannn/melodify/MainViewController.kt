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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.andannn.melodify.ui.components.playcontrol.LocalPlayerUiController
import com.andannn.melodify.ui.components.playcontrol.PlayerUiController
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import platform.MediaPlayer.MPMediaLibrary

@Suppress("ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController(
        configure = {
// TODO: Check debug build
            Napier.base(DebugAntilog())

            startKoin {
                modules(
                    modules,
                )
            }
        },
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
            val coroutineScope = rememberCoroutineScope()
            CompositionLocalProvider(
                LocalPopupController provides remember { PopupControllerImpl() },
                LocalPlayerUiController provides remember { PlayerUiController(coroutineScope) },
            ) {
                MelodifyMobileApp()
            }
        }
    }

private fun haveMediaPermission(): Boolean =
    MPMediaLibrary.authorizationStatus() == MPMediaLibraryAuthorizationStatus.AUTHORIZED.ordinal.toLong()

private enum class MPMediaLibraryAuthorizationStatus {
    NOT_DETERMINED,
    RESTRICTED,
    DENIED,
    AUTHORIZED,
}
