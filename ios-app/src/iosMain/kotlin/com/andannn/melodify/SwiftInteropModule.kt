/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import com.andannn.melodify.artwork.ios.MediaArtworkViewControllerFactory
import com.andannn.melodify.core.player.AVPlayerWrapper
import com.andannn.melodify.core.syncer.MPMediaScanner
import com.andannn.melodify.core.syncer.MusicLibraryPermissionHandler
import com.andannn.melodify.util.orientation.ScreenOrientationController
import org.koin.dsl.bind
import org.koin.dsl.module

fun swiftInteropModule(
    mPMediaScanner: MPMediaScanner,
    musicLibraryPermissionHandler: MusicLibraryPermissionHandler,
    avPlayerWrapper: AVPlayerWrapper,
    screenOrientationController: ScreenOrientationController,
    mediaArtworkViewControllerFactory: MediaArtworkViewControllerFactory,
) = module {
    single { mPMediaScanner }.bind(MPMediaScanner::class)
    single { musicLibraryPermissionHandler }.bind(MusicLibraryPermissionHandler::class)
    single { avPlayerWrapper }.bind(AVPlayerWrapper::class)
    single { screenOrientationController }.bind(ScreenOrientationController::class)
    single { mediaArtworkViewControllerFactory }.bind(MediaArtworkViewControllerFactory::class)
}
