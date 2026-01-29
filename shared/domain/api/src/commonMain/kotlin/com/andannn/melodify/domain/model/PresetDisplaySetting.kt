/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

enum class PresetDisplaySetting(
    val displaySetting: DisplaySetting,
) {
    AlbumAsc(
        displaySetting = DisplaySetting.Preset.Audio.AlbumASC,
    ),
    ArtistAsc(
        displaySetting = DisplaySetting.Preset.Audio.ArtistASC,
    ),
    TitleNameAsc(
        displaySetting = DisplaySetting.Preset.Audio.TitleASC,
    ),
    ArtistAlbumASC(
        displaySetting = DisplaySetting.Preset.Audio.ArtistAlbumASC,
    ),
    VideoBucketNameASC(
        displaySetting = DisplaySetting.Preset.Video.BucketNameASC,
    ),
    PlaylistCreateDateDESC(
        displaySetting = DisplaySetting.Preset.Playlist.CreateDateDESC,
    ),
    ;

    companion object {
        val VIDEO_OPTIONS =
            listOf(
                VideoBucketNameASC,
            )

        val PLAYLIST_OPTIONS =
            listOf(
                PlaylistCreateDateDESC,
            )

        val AUDIO_OPTIONS =
            listOf(
                AlbumAsc,
                ArtistAsc,
                TitleNameAsc,
                ArtistAlbumASC,
            )
    }
}
