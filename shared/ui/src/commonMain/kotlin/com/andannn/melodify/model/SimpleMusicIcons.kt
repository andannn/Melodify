/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.ui.graphics.vector.ImageVector

object SimpleMusicIcons {
    val AddFavorite = SmpIcon.ImageVectorIcon(Icons.Rounded.Favorite)
    val AddPlayList = SmpIcon.ImageVectorIcon(Icons.AutoMirrored.Rounded.PlaylistAdd)
    val PlayNext = SmpIcon.ImageVectorIcon(Icons.Rounded.PlayCircle)
    val Information = SmpIcon.ImageVectorIcon(Icons.Rounded.Info)
    val Delete = SmpIcon.ImageVectorIcon(Icons.Rounded.Delete)
    val DeleteSweep = SmpIcon.ImageVectorIcon(Icons.Rounded.DeleteSweep)
    val Timer = SmpIcon.ImageVectorIcon(Icons.Rounded.Timer)
    val PlayListRemove = SmpIcon.ImageVectorIcon(Icons.Rounded.PlaylistRemove)
    val AddToHomeTab = SmpIcon.ImageVectorIcon(Icons.Filled.Home)
    val Music = SmpIcon.ImageVectorIcon(Icons.Filled.MusicNote)
    val Album = SmpIcon.ImageVectorIcon(Icons.Filled.Album)
    val Artist = SmpIcon.ImageVectorIcon(Icons.Filled.Person)
    val Genre = SmpIcon.ImageVectorIcon(Icons.Rounded.LibraryMusic)
    val PlayList = SmpIcon.ImageVectorIcon(Icons.Rounded.PlayCircle)
    val QueueMusic = SmpIcon.ImageVectorIcon(Icons.AutoMirrored.Outlined.QueueMusic)
    val DisplaySettings = SmpIcon.ImageVectorIcon(Icons.Rounded.DisplaySettings)
}

sealed class SmpIcon {
    data class ImageVectorIcon(
        val imageVector: ImageVector,
    ) : SmpIcon()
}
