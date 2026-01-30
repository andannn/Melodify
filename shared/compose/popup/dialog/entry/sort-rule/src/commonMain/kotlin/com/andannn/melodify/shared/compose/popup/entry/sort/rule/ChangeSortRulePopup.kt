/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sort.rule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.ContentSortType
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.TabSortRule
import com.andannn.melodify.domain.model.contentSortType
import com.andannn.melodify.domain.model.isAscending
import com.andannn.melodify.shared.compose.common.getCategoryResource
import com.andannn.melodify.shared.compose.common.headerText
import com.andannn.melodify.shared.compose.common.subTitle
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.common.widgets.DropDownMenuIconButton
import com.andannn.melodify.shared.compose.common.widgets.TransparentBackgroundListItem
import com.andannn.melodify.shared.compose.popup.ModalBottomSheetFactoryProvider
import io.github.andannn.popup.PopupEntryProviderScope
import io.github.andannn.popup.PopupId
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.album_page_title
import melodify.shared.compose.resource.generated.resources.artist_page_title
import melodify.shared.compose.resource.generated.resources.content_sort_by
import melodify.shared.compose.resource.generated.resources.customize
import melodify.shared.compose.resource.generated.resources.display_setting_for
import melodify.shared.compose.resource.generated.resources.order_1_to_9
import melodify.shared.compose.resource.generated.resources.order_9_to_1
import melodify.shared.compose.resource.generated.resources.order_a_to_z
import melodify.shared.compose.resource.generated.resources.order_new_to_old
import melodify.shared.compose.resource.generated.resources.order_old_to_new
import melodify.shared.compose.resource.generated.resources.order_z_to_a
import melodify.shared.compose.resource.generated.resources.preset_audio
import melodify.shared.compose.resource.generated.resources.preset_playlist
import melodify.shared.compose.resource.generated.resources.preset_video
import melodify.shared.compose.resource.generated.resources.primary_group_by
import melodify.shared.compose.resource.generated.resources.reset_settings
import melodify.shared.compose.resource.generated.resources.secondary_group_by
import melodify.shared.compose.resource.generated.resources.show_play_progress
import melodify.shared.compose.resource.generated.resources.sort_by_genre
import melodify.shared.compose.resource.generated.resources.sort_by_media_title
import melodify.shared.compose.resource.generated.resources.sort_by_none
import melodify.shared.compose.resource.generated.resources.sort_by_playlist_create_data
import melodify.shared.compose.resource.generated.resources.sort_by_release_year
import melodify.shared.compose.resource.generated.resources.sort_by_track_number
import melodify.shared.compose.resource.generated.resources.sort_by_video_bucket
import org.jetbrains.compose.resources.stringResource

/**
 * Change the sort rule of the tab.
 */
data class ChangeSortRulePopup(
    val tab: CustomTab,
) : PopupId<Unit>

fun PopupEntryProviderScope<PopupId<*>>.addChangeSortRuleDialogEntry() {
    entry(
        metadata = ModalBottomSheetFactoryProvider.bottomSheet(),
    ) { dialogId, _ ->
        ChangeSortRuleDialogContent(
            dialogId,
        )
    }
}

@Composable
internal fun ChangeSortRuleDialogContent(
    dialog: ChangeSortRulePopup,
    modifier: Modifier = Modifier,
) {
    val state = retainedChangeSortRulePresenter(dialog.tab).present()

    ChangeSortRuleDialogContent(
        modifier = modifier,
        tab = dialog.tab,
        tabSortRule = state.tabSortRule,
        isShowVideoProgress = state.isShowVideoProgress,
        onChangePresetSortRule = {
            state.eventSink(UiEvent.OnChangeSortRule(it.tabSortRule))
        },
        onCustomRadioButtonClick = {
            state.eventSink(UiEvent.OnCustomRadioButtonClick)
        },
        onChangeCustomSortRule = {
            state.eventSink(UiEvent.OnChangeSortRule(it))
        },
        onToggleIsShowVideoProgress = {
            state.eventSink(UiEvent.OnToggleIsShowVideoProgress)
        },
    )
}

@Composable
private fun ChangeSortRuleDialogContent(
    tab: CustomTab,
    isShowVideoProgress: Boolean,
    tabSortRule: TabSortRule,
    modifier: Modifier = Modifier,
    onChangePresetSortRule: (PresetDisplaySetting) -> Unit = {},
    onChangeCustomSortRule: (TabSortRule) -> Unit = {},
    onCustomRadioButtonClick: () -> Unit = {},
    onToggleIsShowVideoProgress: () -> Unit = {},
) {
    val type: ContentSortType = tab.contentSortType()
    val selectedPresetOption =
        remember(tabSortRule) {
            PresetDisplaySetting.entries.firstOrNull { tabSortRule.isPreset && it.tabSortRule == tabSortRule }
        }

    Column(modifier = modifier.fillMaxHeight()) {
        val title =
            stringResource(Res.string.display_setting_for, getCategoryResource(tab))
        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            item {
                if (type == ContentSortType.Video) {
                    Row(
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(Res.string.show_play_progress))
                        Spacer(Modifier.width(6.dp))
                        Switch(
                            checked = isShowVideoProgress,
                            onCheckedChange = {
                                onToggleIsShowVideoProgress()
                            },
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                PresetSortOptionSelector(
                    modifier = Modifier.fillMaxWidth(),
                    selectedPresetOption = selectedPresetOption,
                    type = type,
                    onChangePresetSortRule = onChangePresetSortRule,
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Custom sort rule
            item {
                Text(
                    modifier =
                        Modifier.padding(horizontal = 12.dp),
                    text = stringResource(Res.string.customize),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                ) {
                    val resolvedSortOption =
                        remember(tabSortRule) {
                            if (tabSortRule.isPreset) {
                                TabSortRule.getDefaultCustom(type)
                            } else {
                                tabSortRule
                            }
                        }

                    Column(
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp)
                                .weight(1f)
                                .graphicsLayer {
                                    alpha = if (tabSortRule.isPreset) 0.5f else 1f
                                },
                    ) {
                        val enabled = !tabSortRule.isPreset

                        CustomSortOptionGroup(
                            modifier = Modifier,
                            type = type,
                            enabled = enabled,
                            tabSortRule = resolvedSortOption,
                            onPrimarySortRuleChange = { option ->
                                if (!tabSortRule.containsOption(option)) {
                                    onChangeCustomSortRule(
                                        tabSortRule.copy(
                                            primaryGroupSort = option,
                                        ),
                                    )
                                }
                            },
                            onSecondarySortRuleChange = { option ->
                                if (!tabSortRule.containsOption(option)) {
                                    onChangeCustomSortRule(
                                        tabSortRule.copy(
                                            secondaryGroupSort = option,
                                        ),
                                    )
                                }
                            },
                            onContentSortRuleChange = { option ->
                                if (!tabSortRule.containsOption(option)) {
                                    onChangeCustomSortRule(
                                        tabSortRule.copy(
                                            contentSort = option,
                                        ),
                                    )
                                }
                            },
                        )

                        Spacer(Modifier.height(8.dp))
// TODO: set
//                        if (type == ContentSortType.Audio) {
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                            ) {
//                                Text(stringResource(Res.string.show_track_number))
//                                Spacer(Modifier.width(6.dp))
//                                Switch(
//                                    enabled = enabled,
//                                    checked = resolvedSortOption.showTrackNum,
//                                    onCheckedChange = {
//                                        onChangeCustomSortRule(
//                                            resolvedSortOption.copy(
//                                                showTrackNum = it,
//                                            ),
//                                        )
//                                    },
//                                )
//                            }
//                            Spacer(Modifier.height(8.dp))
//                        }

                        TextButton(
                            enabled = enabled,
                            onClick = {
                                onChangeCustomSortRule(
                                    TabSortRule.getDefaultCustom(type),
                                )
                            },
                        ) {
                            Text(stringResource(Res.string.reset_settings))
                        }
                    }

                    RadioButton(
                        modifier = Modifier.padding(end = 8.dp),
                        selected = !tabSortRule.isPreset,
                        onClick = onCustomRadioButtonClick,
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CustomSortOptionGroup(
    type: ContentSortType,
    tabSortRule: TabSortRule,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onPrimarySortRuleChange: (SortOption) -> Unit = {},
    onSecondarySortRuleChange: (SortOption) -> Unit = {},
    onContentSortRuleChange: (SortOption) -> Unit = {},
) {
    Column(modifier) {
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.primaryGroupOptions(type),
            currentOption = tabSortRule.primaryGroupSort,
            label = stringResource(Res.string.primary_group_by),
            onChangeSortRule = onPrimarySortRuleChange,
        )
        Spacer(Modifier.height(8.dp))
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.secondaryGroupOptions(type),
            currentOption = tabSortRule.secondaryGroupSort,
            label = stringResource(Res.string.secondary_group_by),
            onChangeSortRule = onSecondarySortRuleChange,
        )
        Spacer(Modifier.height(8.dp))
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.contentOptions(type),
            currentOption = tabSortRule.contentSort,
            label = stringResource(Res.string.content_sort_by),
            onChangeSortRule = onContentSortRuleChange,
        )
    }
}

@Composable
internal fun PresetSortOptionSelector(
    modifier: Modifier = Modifier,
    selectedPresetOption: PresetDisplaySetting?,
    type: ContentSortType,
    onChangePresetSortRule: (PresetDisplaySetting) -> Unit = {},
) {
    val options =
        when (type) {
            ContentSortType.Audio -> PresetDisplaySetting.AUDIO_OPTIONS
            ContentSortType.Video -> PresetDisplaySetting.VIDEO_OPTIONS
            ContentSortType.PlayList -> PresetDisplaySetting.PLAYLIST_OPTIONS
        }

    val title =
        when (type) {
            ContentSortType.Audio -> Res.string.preset_audio
            ContentSortType.Video -> Res.string.preset_video
            ContentSortType.PlayList -> Res.string.preset_playlist
        }

    Column(modifier) {
        Text(
            modifier =
                Modifier.padding(horizontal = 12.dp),
            text = stringResource(title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        options.forEach { rule ->
            TransparentBackgroundListItem(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onClick = {
                    if (rule != selectedPresetOption) {
                        onChangePresetSortRule(rule)
                    }
                },
                headlineContent = {
                    Text(
                        text = rule.headerText(),
                    )
                },
                supportingContent = {
                    Text(
                        text = rule.subTitle(),
                    )
                },
                trailingContent = {
                    RadioButton(
                        selected = selectedPresetOption == rule,
                        onClick = null,
                    )
                },
            )
        }
    }
}

@Composable
private fun SortOptionSelector(
    currentOption: SortOption,
    enabled: Boolean,
    label: String,
    modifier: Modifier = Modifier,
    options: List<SortOptionType> = emptyList(),
    onChangeSortRule: (SortOption) -> Unit = {},
) {
    val currentOptionType =
        remember(currentOption) {
            currentOption.toSortOptionType()
        }
    val isAscending =
        remember(currentOption) {
            currentOption.isAscending()
        }
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DropDownMenuIconButton(
                modifier = Modifier.weight(1f),
                enabled = enabled,
                options = options.map { it.label() },
                imageVector = currentOptionType.icon(),
                selectedIndex = options.indexOf(currentOptionType).takeIf { it != -1 },
                onSelectIndex = {
                    if (currentOptionType != options[it]) {
                        onChangeSortRule(
                            // Default sort is ascending.
                            options[it].createSortOption(true),
                        )
                    }
                },
            )

            Box(
                Modifier.weight(1.5f),
            ) {
                if (currentOption !is SortOption.NONE) {
                    FlowRow(modifier = Modifier.fillMaxWidth()) {
                        SelectableTextButton(
                            text = currentOptionType.orderLabel(ascending = true),
                            enabled = enabled,
                            selected = isAscending,
                            onClick = {
                                val newOption = currentOptionType.createSortOption(true)
                                if (currentOption != newOption) {
                                    onChangeSortRule(newOption)
                                }
                            },
                        )
                        SelectableTextButton(
                            text = currentOptionType.orderLabel(ascending = false),
                            enabled = enabled,
                            selected = !isAscending,
                            onClick = {
                                val newOption = currentOptionType.createSortOption(false)
                                if (currentOption != newOption) {
                                    onChangeSortRule(newOption)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectableTextButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    selected: Boolean,
    text: String,
    onClick: () -> Unit = {},
) {
    Box(modifier = modifier) {
        val colors =
            if (selected) {
                ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                ButtonDefaults.textButtonColors()
            }

        TextButton(
            enabled = enabled,
            colors = colors,
            onClick = onClick,
        ) {
            Text(text, maxLines = 1)
        }
    }
}

private fun TabSortRule.containsOption(sortOption: SortOption) =
    primaryGroupSort == sortOption || secondaryGroupSort == sortOption || contentSort == sortOption

private fun SortOption.toSortOptionType() =
    when (this) {
        is SortOption.AudioOption.Album -> SortOptionType.Album
        is SortOption.AudioOption.Artist -> SortOptionType.Artist
        is SortOption.AudioOption.Title -> SortOptionType.Title
        is SortOption.AudioOption.TrackNum -> SortOptionType.TrackNum
        is SortOption.AudioOption.ReleaseYear -> SortOptionType.ReleaseYear
        is SortOption.AudioOption.Genre -> SortOptionType.Genre
        is SortOption.VideoOption.Title -> SortOptionType.VideoTitle
        is SortOption.VideoOption.Bucket -> SortOptionType.VideoBucket
        is SortOption.PlayListOption.CreateData -> SortOptionType.PlayListCreateDate
        is SortOption.NONE -> SortOptionType.None
    }

private enum class SortOptionType {
    Artist,
    Album,
    Title,
    TrackNum,
    ReleaseYear,
    None,
    Genre,
    VideoBucket,
    VideoTitle,
    PlayListCreateDate,
    ;

    companion object {
        fun primaryGroupOptions(type: ContentSortType) =
            when (type) {
                ContentSortType.Audio -> {
                    listOf(
                        Album,
                        Artist,
                        Title,
                        Genre,
                        ReleaseYear,
                    )
                }

                ContentSortType.Video -> {
                    listOf(
                        VideoBucket,
                        VideoTitle,
                    )
                }

                ContentSortType.PlayList -> {
                    listOf(
                        None,
                    )
                }
            }

        fun secondaryGroupOptions(type: ContentSortType) =
            when (type) {
                ContentSortType.Audio -> {
                    listOf(
                        Album,
                        Artist,
                        Title,
                        Genre,
                        ReleaseYear,
                        None,
                    )
                }

                ContentSortType.Video -> {
                    listOf(
                        VideoTitle,
                        None,
                    )
                }

                ContentSortType.PlayList -> {
                    listOf(
                        None,
                    )
                }
            }

        fun contentOptions(type: ContentSortType) =
            when (type) {
                ContentSortType.Audio -> {
                    listOf(
                        TrackNum,
                        Title,
                        None,
                    )
                }

                ContentSortType.Video -> {
                    listOf(
                        VideoTitle,
                        None,
                    )
                }

                ContentSortType.PlayList -> {
                    listOf(
                        PlayListCreateDate,
                        None,
                    )
                }
            }
    }
}

private fun SortOptionType.createSortOption(isAscending: Boolean) =
    when (this) {
        SortOptionType.Artist -> SortOption.AudioOption.Artist(isAscending)
        SortOptionType.Album -> SortOption.AudioOption.Album(isAscending)
        SortOptionType.Title -> SortOption.AudioOption.Title(isAscending)
        SortOptionType.TrackNum -> SortOption.AudioOption.TrackNum(isAscending)
        SortOptionType.Genre -> SortOption.AudioOption.Genre(isAscending)
        SortOptionType.ReleaseYear -> SortOption.AudioOption.ReleaseYear(isAscending)
        SortOptionType.VideoBucket -> SortOption.VideoOption.Bucket(isAscending)
        SortOptionType.VideoTitle -> SortOption.VideoOption.Title(isAscending)
        SortOptionType.PlayListCreateDate -> SortOption.PlayListOption.CreateData(isAscending)
        SortOptionType.None -> SortOption.NONE
    }

private fun SortOptionType.label() =
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

        SortOptionType.PlayListCreateDate -> Res.string.sort_by_playlist_create_data
    }

private fun SortOptionType.icon() =
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

        SortOptionType.PlayListCreateDate -> Icons.Outlined.Remove
    }

@Composable
private fun SortOptionType.orderLabel(ascending: Boolean): String =
    when (this) {
        SortOptionType.Artist,
        SortOptionType.Title,
        SortOptionType.Genre,
        SortOptionType.Album,
        SortOptionType.VideoBucket,
        SortOptionType.VideoTitle,
        -> {
            if (ascending) {
                stringResource(Res.string.order_a_to_z)
            } else {
                stringResource(Res.string.order_z_to_a)
            }
        }

        SortOptionType.TrackNum -> {
            if (ascending) {
                stringResource(Res.string.order_1_to_9)
            } else {
                stringResource(Res.string.order_9_to_1)
            }
        }

        SortOptionType.ReleaseYear,
        SortOptionType.PlayListCreateDate,
        -> {
            if (ascending) {
                stringResource(Res.string.order_old_to_new)
            } else {
                stringResource(Res.string.order_new_to_old)
            }
        }

        SortOptionType.None -> {
            error("Never. This should not happen.")
        }
    }

@Preview
@Composable
private fun ChangeSortRuleDialogContentAudioPreview() {
    MelodifyTheme {
        Surface {
            ChangeSortRuleDialogContent(
                tab = CustomTab.AllMusic(tabId = 1),
                isShowVideoProgress = true,
                tabSortRule = TabSortRule.Preset.Audio.AlbumASC,
            )
        }
    }
}

@Preview
@Composable
private fun ChangeSortRuleDialogContentAudioCustomPreview() {
    MelodifyTheme {
        Surface {
            ChangeSortRuleDialogContent(
                tab = CustomTab.AllMusic(tabId = 1),
                isShowVideoProgress = true,
                tabSortRule = TabSortRule.Preset.Audio.DefaultCustom,
            )
        }
    }
}

@Preview
@Composable
private fun ChangeSortRuleDialogContentVideoPreview() {
    MelodifyTheme {
        Surface {
            ChangeSortRuleDialogContent(
                tab = CustomTab.AllVideo(tabId = 1),
                isShowVideoProgress = true,
                tabSortRule = TabSortRule.Preset.Video.BucketNameASC,
            )
        }
    }
}
