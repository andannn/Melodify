/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.internal.content

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.DisplaySetting
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.isAscending
import com.andannn.melodify.domain.model.isAudio
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.getCategoryResource
import com.andannn.melodify.shared.compose.common.headerText
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.subTitle
import com.andannn.melodify.shared.compose.common.widgets.DropDownMenuIconButton
import com.andannn.melodify.shared.compose.common.widgets.TransparentBackgroundListItem
import com.andannn.melodify.shared.compose.popup.ChangeSortRuleDialog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
import melodify.shared.compose.resource.generated.resources.preset_video
import melodify.shared.compose.resource.generated.resources.primary_group_by
import melodify.shared.compose.resource.generated.resources.reset_settings
import melodify.shared.compose.resource.generated.resources.secondary_group_by
import melodify.shared.compose.resource.generated.resources.show_play_progress
import melodify.shared.compose.resource.generated.resources.show_track_number
import melodify.shared.compose.resource.generated.resources.sort_by_genre
import melodify.shared.compose.resource.generated.resources.sort_by_media_title
import melodify.shared.compose.resource.generated.resources.sort_by_none
import melodify.shared.compose.resource.generated.resources.sort_by_release_year
import melodify.shared.compose.resource.generated.resources.sort_by_track_number
import melodify.shared.compose.resource.generated.resources.sort_by_video_bucket
import org.jetbrains.compose.resources.stringResource

private const val TAG = "ChangeSortRuleDialog"

@Composable
internal fun ChangeSortRuleDialogContent(
    modifier: Modifier = Modifier,
    dialog: ChangeSortRuleDialog,
) {
    val state = retainedChangeSortRulePresenter(dialog.tab).present()

    ChangeSortRuleDialogContent(
        modifier = modifier,
        tab = dialog.tab,
        displaySetting = state.displaySetting,
        isShowVideoProgress = state.isShowVideoProgress,
        onChangePresetSortRule = {
            state.eventSink(UiEvent.OnChangeSortRule(it.displaySetting))
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
    displaySetting: DisplaySetting,
    modifier: Modifier = Modifier,
    onChangePresetSortRule: (PresetDisplaySetting) -> Unit = {},
    onChangeCustomSortRule: (DisplaySetting) -> Unit = {},
    onCustomRadioButtonClick: () -> Unit = {},
    onToggleIsShowVideoProgress: () -> Unit = {},
) {
    val isAudio = tab.isAudio()
    val selectedPresetOption =
        remember(displaySetting) {
            PresetDisplaySetting.entries.firstOrNull { displaySetting.isPreset && it.displaySetting == displaySetting }
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
                if (!isAudio) {
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
                    isAudio = isAudio,
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
                        remember(displaySetting) {
                            if (displaySetting.isPreset) {
                                DisplaySetting.getDefaultCustom(isAudio)
                            } else {
                                displaySetting
                            }
                        }

                    Column(
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp)
                                .weight(1f)
                                .graphicsLayer {
                                    alpha = if (displaySetting.isPreset) 0.5f else 1f
                                },
                    ) {
                        val enabled = !displaySetting.isPreset

                        CustomSortOptionGroup(
                            modifier = Modifier,
                            isAudio = isAudio,
                            enabled = enabled,
                            displaySetting = resolvedSortOption,
                            onPrimarySortRuleChange = { option ->
                                if (!displaySetting.containsOption(option)) {
                                    onChangeCustomSortRule(
                                        displaySetting.copy(
                                            primaryGroupSort = option,
                                        ),
                                    )
                                }
                            },
                            onSecondarySortRuleChange = { option ->
                                if (!displaySetting.containsOption(option)) {
                                    onChangeCustomSortRule(
                                        displaySetting.copy(
                                            secondaryGroupSort = option,
                                        ),
                                    )
                                }
                            },
                            onContentSortRuleChange = { option ->
                                if (!displaySetting.containsOption(option)) {
                                    onChangeCustomSortRule(
                                        displaySetting.copy(
                                            contentSort = option,
                                        ),
                                    )
                                }
                            },
                        )

                        Spacer(Modifier.height(8.dp))

                        if (isAudio) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(stringResource(Res.string.show_track_number))
                                Spacer(Modifier.width(6.dp))
                                Switch(
                                    enabled = enabled,
                                    checked = resolvedSortOption.showTrackNum,
                                    onCheckedChange = {
                                        onChangeCustomSortRule(
                                            resolvedSortOption.copy(
                                                showTrackNum = it,
                                            ),
                                        )
                                    },
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        TextButton(
                            enabled = enabled,
                            onClick = {
                                onChangeCustomSortRule(
                                    DisplaySetting.getDefaultCustom(isAudio),
                                )
                            },
                        ) {
                            Text(stringResource(Res.string.reset_settings))
                        }
                    }

                    RadioButton(
                        modifier = Modifier.padding(end = 8.dp),
                        selected = !displaySetting.isPreset,
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
    isAudio: Boolean,
    displaySetting: DisplaySetting,
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
            options = SortOptionType.primaryGroupOptions(isAudio),
            currentOption = displaySetting.primaryGroupSort,
            label = stringResource(Res.string.primary_group_by),
            onChangeSortRule = onPrimarySortRuleChange,
        )
        Spacer(Modifier.height(8.dp))
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.secondaryGroupOptions(isAudio),
            currentOption = displaySetting.secondaryGroupSort,
            label = stringResource(Res.string.secondary_group_by),
            onChangeSortRule = onSecondarySortRuleChange,
        )
        Spacer(Modifier.height(8.dp))
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.contentOptions(isAudio),
            currentOption = displaySetting.contentSort,
            label = stringResource(Res.string.content_sort_by),
            onChangeSortRule = onContentSortRuleChange,
        )
    }
}

@Composable
fun PresetSortOptionSelector(
    modifier: Modifier = Modifier,
    selectedPresetOption: PresetDisplaySetting?,
    isAudio: Boolean,
    onChangePresetSortRule: (PresetDisplaySetting) -> Unit = {},
) {
    val options =
        if (isAudio) PresetDisplaySetting.AUDIO_OPTIONS else PresetDisplaySetting.VIDEO_OPTIONS

    Column(modifier) {
        Text(
            modifier =
                Modifier.padding(horizontal = 12.dp),
            text = stringResource(if (isAudio) Res.string.preset_audio else Res.string.preset_video),
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

private fun DisplaySetting.containsOption(sortOption: SortOption) =
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
    ;

    companion object {
        fun primaryGroupOptions(isAudio: Boolean = true) =
            if (isAudio) {
                listOf(
                    Album,
                    Artist,
                    Title,
                    Genre,
                    ReleaseYear,
                )
            } else {
                listOf(
                    VideoBucket,
                    VideoTitle,
                )
            }

        fun secondaryGroupOptions(isAudio: Boolean = true) =
            if (isAudio) {
                listOf(
                    Album,
                    Artist,
                    Title,
                    Genre,
                    ReleaseYear,
                    None,
                )
            } else {
                listOf(
                    VideoTitle,
                    None,
                )
            }

        fun contentOptions(isAudio: Boolean = true) =
            if (isAudio) {
                listOf(
                    TrackNum,
                    Title,
                    None,
                )
            } else {
                listOf(
                    VideoTitle,
                    None,
                )
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
        SortOptionType.None -> SortOption.NONE
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun retainedChangeSortRulePresenter(
    customTab: CustomTab,
    repository: Repository = LocalRepository.current,
) = retainPresenter(
    customTab,
    repository,
) {
    ChangeSortRulePresenter(
        repository = repository,
        customTab = customTab,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private class ChangeSortRulePresenter(
    private val customTab: CustomTab,
    private val repository: Repository,
) : RetainedPresenter<UiState>() {
    private val displaySettingFlow =
        repository
            .getCurrentSortRule(customTab)
            .stateIn(
                retainedScope,
                initialValue = DisplaySetting.Preset.Audio.DefaultPreset,
                started = WhileSubscribed(5000),
            )

    private val isShowVideoProgressFlow =
        repository
            .getIsShowVideoProgressFlow(customTab.tabId)
            .stateIn(
                retainedScope,
                initialValue = false,
                started = WhileSubscribed(5000),
            )

    @Composable
    override fun present(): UiState {
        val displaySetting by displaySettingFlow.collectAsStateWithLifecycle()
        val isShowVideoProgress by isShowVideoProgressFlow.collectAsStateWithLifecycle()

        return UiState(
            displaySetting = displaySetting,
            isShowVideoProgress = isShowVideoProgress,
        ) { event ->
            when (event) {
                is UiEvent.OnChangeSortRule -> {
                    retainedScope.launch {
                        repository.saveSortRuleForTab(customTab, event.displaySetting)
                    }
                }

                UiEvent.OnCustomRadioButtonClick -> {
                    retainedScope.launch {
                        val currentTab = customTab
                        val customSortRule = repository.getTabCustomSortRule(currentTab)

                        if (customSortRule != null && !customSortRule.isPreset) {
                            Napier.d(tag = TAG) { "Already has custom sort rule. $customSortRule" }
                        }

                        repository.saveSortRuleForTab(
                            currentTab,
                            DisplaySetting.getDefaultCustom(customTab.isAudio()),
                        )
                    }
                }

                UiEvent.OnToggleIsShowVideoProgress -> {
                    retainedScope.launch {
                        repository.setIsShowVideoProgress(
                            tabId = customTab.tabId,
                            isShow = !isShowVideoProgress,
                        )
                    }
                }
            }
        }
    }
}

@Stable
private data class UiState(
    val displaySetting: DisplaySetting,
    val isShowVideoProgress: Boolean,
    val eventSink: (UiEvent) -> Unit = {},
)

private sealed interface UiEvent {
    data class OnChangeSortRule(
        val displaySetting: DisplaySetting,
    ) : UiEvent

    data object OnCustomRadioButtonClick : UiEvent

    data object OnToggleIsShowVideoProgress : UiEvent
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

        SortOptionType.ReleaseYear -> {
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
