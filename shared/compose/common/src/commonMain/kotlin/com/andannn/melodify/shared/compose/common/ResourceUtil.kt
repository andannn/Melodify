/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOneOn
import androidx.compose.runtime.Composable
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.domain.model.Tab
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.audio_page_title
import melodify.shared.compose.resource.generated.resources.number_hours
import melodify.shared.compose.resource.generated.resources.number_minutes
import melodify.shared.compose.resource.generated.resources.number_seconds
import melodify.shared.compose.resource.generated.resources.sort_by_album
import melodify.shared.compose.resource.generated.resources.sort_by_artist
import melodify.shared.compose.resource.generated.resources.sort_by_artist_then_album
import melodify.shared.compose.resource.generated.resources.sort_by_playlist_create_data
import melodify.shared.compose.resource.generated.resources.sort_by_title
import melodify.shared.compose.resource.generated.resources.sort_by_video_bucket
import melodify.shared.compose.resource.generated.resources.sort_sub_album_asc
import melodify.shared.compose.resource.generated.resources.sort_sub_artist_album_asc
import melodify.shared.compose.resource.generated.resources.sort_sub_artist_asc
import melodify.shared.compose.resource.generated.resources.sort_sub_bucket_name_asc
import melodify.shared.compose.resource.generated.resources.sort_sub_playlist_create_date_desc
import melodify.shared.compose.resource.generated.resources.sort_sub_title_name_asc
import melodify.shared.compose.resource.generated.resources.video_page_title
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration

@Composable
fun durationString(duration: Duration): String {
    duration.toComponents { hours, minutes, seconds, nanoseconds ->
        val hasHours = hours != 0L
        val hasMinutes = minutes != 0
        val hasSeconds = seconds != 0 || nanoseconds != 0

        return buildString {
            if (hasHours) {
                append(stringResource(Res.string.number_hours, hours))
            }
            if (hasMinutes) {
                if (hasHours) {
                    append(" ")
                }
                append(stringResource(Res.string.number_minutes, minutes))
            }
            if (hasSeconds) {
                if (hasHours || hasMinutes) {
                    append(" ")
                }
                append(stringResource(Res.string.number_seconds, seconds))
            }
        }
    }
}

@Composable
fun getCategoryResource(category: Tab): String =
    when (category) {
        is Tab.AllMusic -> stringResource(Res.string.audio_page_title)
        is Tab.AllVideo -> stringResource(Res.string.video_page_title)
        is Tab.AlbumDetail -> category.label
        is Tab.ArtistDetail -> category.label
        is Tab.GenreDetail -> category.label
        is Tab.PlayListDetail -> category.label
        is Tab.BucketDetail -> category.label
    }

@Composable
fun PresetDisplaySetting.headerText(): String =
    when (this) {
        PresetDisplaySetting.AlbumAsc -> {
            stringResource(Res.string.sort_by_album)
        }

        PresetDisplaySetting.ArtistAsc -> {
            stringResource(Res.string.sort_by_artist)
        }

        PresetDisplaySetting.TitleNameAsc -> {
            stringResource(Res.string.sort_by_title)
        }

        PresetDisplaySetting.ArtistAlbumASC -> {
            stringResource(Res.string.sort_by_artist_then_album)
        }

        PresetDisplaySetting.VideoBucketNameASC -> {
            stringResource(Res.string.sort_by_video_bucket)
        }

        PresetDisplaySetting.PlaylistCreateDateDESC -> {
            stringResource(Res.string.sort_by_playlist_create_data)
        }
    }

@Composable
fun PresetDisplaySetting.subTitle(): String =
    when (this) {
        PresetDisplaySetting.AlbumAsc -> {
            stringResource(Res.string.sort_sub_album_asc)
        }

        PresetDisplaySetting.ArtistAsc -> {
            stringResource(Res.string.sort_sub_artist_asc)
        }

        PresetDisplaySetting.TitleNameAsc -> {
            stringResource(Res.string.sort_sub_title_name_asc)
        }

        PresetDisplaySetting.ArtistAlbumASC -> {
            stringResource(Res.string.sort_sub_artist_album_asc)
        }

        PresetDisplaySetting.VideoBucketNameASC -> {
            stringResource(Res.string.sort_sub_bucket_name_asc)
        }

        PresetDisplaySetting.PlaylistCreateDateDESC -> {
            stringResource(Res.string.sort_sub_playlist_create_date_desc)
        }
    }

fun PlayMode.getIcon() =
    when (this) {
        PlayMode.REPEAT_ONE -> Icons.Rounded.RepeatOneOn
        PlayMode.REPEAT_OFF -> Icons.Rounded.Repeat
        PlayMode.REPEAT_ALL -> Icons.Rounded.RepeatOn
    }
