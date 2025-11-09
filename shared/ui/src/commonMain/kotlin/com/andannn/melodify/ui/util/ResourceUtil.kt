/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOneOn
import androidx.compose.runtime.Composable
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.core.data.model.PresetDisplaySetting
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.ui.popup.dialog.content.SortOptionType
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.album_page_title
import melodify.shared.ui.generated.resources.artist_page_title
import melodify.shared.ui.generated.resources.audio_page_title
import melodify.shared.ui.generated.resources.number_hours
import melodify.shared.ui.generated.resources.number_minutes
import melodify.shared.ui.generated.resources.number_seconds
import melodify.shared.ui.generated.resources.order_1_to_9
import melodify.shared.ui.generated.resources.order_9_to_1
import melodify.shared.ui.generated.resources.order_a_to_z
import melodify.shared.ui.generated.resources.order_new_to_old
import melodify.shared.ui.generated.resources.order_old_to_new
import melodify.shared.ui.generated.resources.order_z_to_a
import melodify.shared.ui.generated.resources.sort_by_album
import melodify.shared.ui.generated.resources.sort_by_artist
import melodify.shared.ui.generated.resources.sort_by_artist_then_album
import melodify.shared.ui.generated.resources.sort_by_genre
import melodify.shared.ui.generated.resources.sort_by_media_title
import melodify.shared.ui.generated.resources.sort_by_none
import melodify.shared.ui.generated.resources.sort_by_release_year
import melodify.shared.ui.generated.resources.sort_by_title
import melodify.shared.ui.generated.resources.sort_by_track_number
import melodify.shared.ui.generated.resources.sort_by_video_bucket
import melodify.shared.ui.generated.resources.sort_sub_album_asc
import melodify.shared.ui.generated.resources.sort_sub_artist_album_asc
import melodify.shared.ui.generated.resources.sort_sub_artist_asc
import melodify.shared.ui.generated.resources.sort_sub_bucket_name_asc
import melodify.shared.ui.generated.resources.sort_sub_title_name_asc
import melodify.shared.ui.generated.resources.video_page_title
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
fun getCategoryResource(category: CustomTab): String =
    when (category) {
        is CustomTab.AllMusic -> stringResource(Res.string.audio_page_title)
        is CustomTab.AllVideo -> stringResource(Res.string.video_page_title)
        is CustomTab.AlbumDetail -> category.label
        is CustomTab.ArtistDetail -> category.label
        is CustomTab.GenreDetail -> category.label
        is CustomTab.PlayListDetail -> category.label
    }

@Composable
fun PresetDisplaySetting.headerText(): String =
    when (this) {
        PresetDisplaySetting.AlbumAsc ->
            stringResource(Res.string.sort_by_album)

        PresetDisplaySetting.ArtistAsc ->
            stringResource(Res.string.sort_by_artist)

        PresetDisplaySetting.TitleNameAsc ->
            stringResource(Res.string.sort_by_title)

        PresetDisplaySetting.ArtistAlbumASC ->
            stringResource(Res.string.sort_by_artist_then_album)

        PresetDisplaySetting.VideoBucketNameASC ->
            stringResource(Res.string.sort_by_video_bucket)
    }

@Composable
fun PresetDisplaySetting.subTitle(): String =
    when (this) {
        PresetDisplaySetting.AlbumAsc ->
            stringResource(Res.string.sort_sub_album_asc)

        PresetDisplaySetting.ArtistAsc ->
            stringResource(Res.string.sort_sub_artist_asc)

        PresetDisplaySetting.TitleNameAsc ->
            stringResource(Res.string.sort_sub_title_name_asc)

        PresetDisplaySetting.ArtistAlbumASC ->
            stringResource(Res.string.sort_sub_artist_album_asc)

        PresetDisplaySetting.VideoBucketNameASC ->
            stringResource(Res.string.sort_sub_bucket_name_asc)
    }

@Composable
fun SortOptionType.orderLabel(ascending: Boolean): String =
    when (this) {
        SortOptionType.Artist,
        SortOptionType.Title,
        SortOptionType.Genre,
        SortOptionType.Album,
        SortOptionType.VideoBucket,
        SortOptionType.VideoTitle,
        ->
            if (ascending) {
                stringResource(Res.string.order_a_to_z)
            } else {
                stringResource(Res.string.order_z_to_a)
            }

        SortOptionType.TrackNum ->
            if (ascending) {
                stringResource(Res.string.order_1_to_9)
            } else {
                stringResource(Res.string.order_9_to_1)
            }

        SortOptionType.ReleaseYear ->
            if (ascending) {
                stringResource(Res.string.order_old_to_new)
            } else {
                stringResource(Res.string.order_new_to_old)
            }

        SortOptionType.None -> error("Never. This should not happen.")
    }

fun SortOptionType.label() =
    when (this) {
        SortOptionType.Album -> Res.string.album_page_title
        SortOptionType.Artist -> Res.string.artist_page_title
        SortOptionType.None -> Res.string.sort_by_none
        SortOptionType.VideoTitle,
        SortOptionType.Title,
        -> Res.string.sort_by_media_title

        SortOptionType.TrackNum -> Res.string.sort_by_track_number
        SortOptionType.Genre -> Res.string.sort_by_genre
        SortOptionType.ReleaseYear -> Res.string.sort_by_release_year
        SortOptionType.VideoBucket -> Res.string.sort_by_video_bucket
    }

fun SortOptionType.icon() =
    when (this) {
        SortOptionType.Album -> Icons.Outlined.Album
        SortOptionType.Artist -> Icons.Outlined.Person
        SortOptionType.VideoTitle,
        SortOptionType.Title,
        -> Icons.Outlined.SortByAlpha

        SortOptionType.TrackNum -> Icons.Outlined.Audiotrack
        SortOptionType.None -> Icons.Outlined.Remove
        SortOptionType.Genre -> Icons.Outlined.Tag
        SortOptionType.ReleaseYear -> Icons.Outlined.Timeline
        SortOptionType.VideoBucket -> Icons.Outlined.Folder
    }

fun PlayMode.getIcon() =
    when (this) {
        PlayMode.REPEAT_ONE -> Icons.Rounded.RepeatOneOn
        PlayMode.REPEAT_OFF -> Icons.Rounded.Repeat
        PlayMode.REPEAT_ALL -> Icons.Rounded.RepeatOn
    }
