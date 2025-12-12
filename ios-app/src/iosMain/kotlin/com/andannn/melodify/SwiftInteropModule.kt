package com.andannn.melodify

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
) = module {
    single { mPMediaScanner }.bind(MPMediaScanner::class)
    single { musicLibraryPermissionHandler }.bind(MusicLibraryPermissionHandler::class)
    single { avPlayerWrapper }.bind(AVPlayerWrapper::class)
    single { screenOrientationController }.bind(ScreenOrientationController::class)
}
