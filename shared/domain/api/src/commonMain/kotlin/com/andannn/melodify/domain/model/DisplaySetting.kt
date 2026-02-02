/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

data class CustomDisplaySetting(
    val tabSortRule: TabSortRule,
    val isShowVideoProgress: Boolean,
    val audioTrackStyle: AudioTrackStyle,
)

fun PresetDisplaySetting.toDisplaySetting() =
    CustomDisplaySetting(
        tabSortRule = tabSortRule,
        isShowVideoProgress = isShowVideoProgress,
        audioTrackStyle = audioTrackStyle,
    )

enum class PresetDisplaySetting(
    val value: Int,
    val tabSortRule: TabSortRule,
    val isShowVideoProgress: Boolean = false,
    val audioTrackStyle: AudioTrackStyle = AudioTrackStyle.ALBUM_COVER,
) {
    AlbumAsc(
        0,
        tabSortRule = TabSortRule.Preset.Audio.AlbumASC,
        audioTrackStyle = AudioTrackStyle.TRACK_NUMBER,
    ),
    ArtistAsc(
        1,
        tabSortRule = TabSortRule.Preset.Audio.ArtistASC,
        audioTrackStyle = AudioTrackStyle.ALBUM_COVER,
    ),
    TitleNameAsc(
        2,
        tabSortRule = TabSortRule.Preset.Audio.TitleASC,
        audioTrackStyle = AudioTrackStyle.ALBUM_COVER,
    ),
    ArtistAlbumASC(
        3,
        tabSortRule = TabSortRule.Preset.Audio.ArtistAlbumASC,
        audioTrackStyle = AudioTrackStyle.TRACK_NUMBER,
    ),
    VideoBucketNameASC(
        4,
        tabSortRule = TabSortRule.Preset.Video.BucketNameASC,
        isShowVideoProgress = false,
    ),
    PlaylistCreateDateDESC(
        5,
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

        fun fromValue(value: Int): PresetDisplaySetting? = entries.find { it.value == value }
    }
}
