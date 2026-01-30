/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

enum class AudioTrackStyle {
    ALBUM_COVER,
    TRACK_NUMBER,
}

enum class PresetDisplaySetting(
    val tabSortRule: TabSortRule,
    val isShowVideoProgress: Boolean = false,
    val audioTrackStyle: AudioTrackStyle = AudioTrackStyle.ALBUM_COVER,
) {
    AlbumAsc(
        tabSortRule = TabSortRule.Preset.Audio.AlbumASC,
        audioTrackStyle = AudioTrackStyle.TRACK_NUMBER,
    ),
    ArtistAsc(
        tabSortRule = TabSortRule.Preset.Audio.ArtistASC,
        audioTrackStyle = AudioTrackStyle.ALBUM_COVER,
    ),
    TitleNameAsc(
        tabSortRule = TabSortRule.Preset.Audio.TitleASC,
        audioTrackStyle = AudioTrackStyle.ALBUM_COVER,
    ),
    ArtistAlbumASC(
        tabSortRule = TabSortRule.Preset.Audio.ArtistAlbumASC,
        audioTrackStyle = AudioTrackStyle.TRACK_NUMBER,
    ),
    VideoBucketNameASC(
        tabSortRule = TabSortRule.Preset.Video.BucketNameASC,
        isShowVideoProgress = true,
    ),
    PlaylistCreateDateDESC(
        tabSortRule = TabSortRule.Preset.Playlist.CreateDateDESC,
        isShowVideoProgress = false,
        audioTrackStyle = AudioTrackStyle.ALBUM_COVER,
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
