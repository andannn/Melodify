/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.dialog.content

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.PresetDisplaySetting
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.isAscending
import com.andannn.melodify.core.data.model.isAudio
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.util.getCategoryResource
import com.andannn.melodify.ui.util.headerText
import com.andannn.melodify.ui.util.icon
import com.andannn.melodify.ui.util.label
import com.andannn.melodify.ui.util.orderLabel
import com.andannn.melodify.ui.util.subTitle
import com.andannn.melodify.ui.widgets.DropDownMenuIconButton
import com.andannn.melodify.ui.widgets.PresetSortOptionSelector
import com.andannn.melodify.ui.widgets.TransparentBackgroundListItem
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.content_sort_by
import melodify.shared.ui.generated.resources.customize
import melodify.shared.ui.generated.resources.display_setting_for
import melodify.shared.ui.generated.resources.preset
import melodify.shared.ui.generated.resources.preset_audio
import melodify.shared.ui.generated.resources.preset_video
import melodify.shared.ui.generated.resources.primary_group_by
import melodify.shared.ui.generated.resources.reset_settings
import melodify.shared.ui.generated.resources.secondary_group_by
import melodify.shared.ui.generated.resources.show_track_number
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "ChangeSortRuleDialog"

@Composable
fun ChangeSortRuleDialog(
    modifier: Modifier = Modifier,
    dialog: DialogId.ChangeSortRuleDialog,
    onAction: (DialogAction) -> Unit = {},
) {
    val state = rememberChangeSortRulePresenter(dialog.tab).present()

    ChangeSortRuleDialogContent(
        modifier = modifier,
        tab = dialog.tab,
        displaySetting = state.displaySetting,
        onChangePresetSortRule = {
            state.eventSink(UiEvent.OnChangeSortRule(it.displaySetting))
        },
        onCustomRadioButtonClick = {
            state.eventSink(UiEvent.OnCustomRadioButtonClick)
        },
        onChangeCustomSortRule = {
            state.eventSink(UiEvent.OnChangeSortRule(it))
        },
    )
}

@Composable
private fun ChangeSortRuleDialogContent(
    tab: CustomTab,
    displaySetting: DisplaySetting,
    modifier: Modifier = Modifier,
    onChangePresetSortRule: (PresetDisplaySetting) -> Unit = {},
    onChangeCustomSortRule: (DisplaySetting) -> Unit = {},
    onCustomRadioButtonClick: () -> Unit = {},
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
                Text(
                    modifier =
                        Modifier.padding(horizontal = 12.dp),
                    text = stringResource(Res.string.customize),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(10.dp))

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

enum class SortOptionType {
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

fun SortOptionType.createSortOption(isAscending: Boolean) =
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
private fun rememberChangeSortRulePresenter(customTab: CustomTab) =
    remember(
        customTab,
    ) {
        ChangeSortRulePresenter(
            customTab = customTab,
        )
    }

@OptIn(ExperimentalMaterial3Api::class)
private class ChangeSortRulePresenter(
    private val customTab: CustomTab,
    repository: Repository = getKoin().get<Repository>(),
) : Presenter<UiState> {
    private val userPreferences = repository.userPreferenceRepository

    @Composable
    override fun present(): UiState {
        val displaySetting by userPreferences
            .getCurrentSortRule(customTab)
            .collectAsStateWithLifecycle(DisplaySetting.Preset.Audio.DefaultPreset)

        val scope = rememberCoroutineScope()
        return UiState(displaySetting) { event ->
            when (event) {
                is UiEvent.OnChangeSortRule -> {
                    scope.launch {
                        userPreferences.saveSortRuleForTab(customTab, event.displaySetting)
                    }
                }

                UiEvent.OnCustomRadioButtonClick -> {
                    scope.launch {
                        val currentTab = customTab
                        val customSortRule = userPreferences.getTabCustomSortRule(currentTab)

                        if (customSortRule != null && !customSortRule.isPreset) {
                            Napier.d(tag = TAG) { "Already has custom sort rule. $customSortRule" }
                        }

                        userPreferences.saveSortRuleForTab(
                            currentTab,
                            DisplaySetting.getDefaultCustom(customTab.isAudio()),
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
    val eventSink: (UiEvent) -> Unit = {},
)

private sealed interface UiEvent {
    data class OnChangeSortRule(
        val displaySetting: DisplaySetting,
    ) : UiEvent

    data object OnCustomRadioButtonClick : UiEvent
}
