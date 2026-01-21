/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import com.andannn.melodify.artwork.ios.MediaArtworkViewControllerFactory
import com.andannn.melodify.core.syncer.isCustomMpLibraryUri
import com.andannn.melodify.core.syncer.persistIdOfUri
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun IOSMediaArtworkView(
    modifier: Modifier,
    coverUri: String,
) {
    val factory =
        remember {
            getKoin().get<MediaArtworkViewControllerFactory>()
        }
    val persistentID = persistIdOfUri(coverUri)
    UIKitViewController(
        modifier = modifier,
        factory = {
            factory.createMediaArtworkViewController(persistentID)
        },
    )
}

internal fun isIOSCustomMPLibraryUri(uri: String): Boolean = uri.isCustomMpLibraryUri()
