package com.andannn.melodify.ui.common.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.LineStyle
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.ui.graphics.vector.ImageVector

object SimpleMusicIcons {
    val Home = Icons.Filled.Home
    val UnselectedHome = Icons.Outlined.Home
    val Settings = Icons.Filled.Settings
    val UnselectedSettings = Icons.Outlined.Settings
    val Library = Icons.Filled.LibraryMusic
    val UnselectedLibrary = Icons.Outlined.LibraryMusic
    val Share = SmpIcon.ImageVectorIcon(Icons.Rounded.Share)
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
}

sealed class SmpIcon {
    data class ImageVectorIcon(val imageVector: ImageVector) : SmpIcon()
}
