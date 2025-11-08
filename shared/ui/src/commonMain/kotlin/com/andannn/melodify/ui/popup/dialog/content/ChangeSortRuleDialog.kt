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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Audiotrack
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
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.isAscending
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.util.getCategoryResource
import com.andannn.melodify.ui.widgets.DropDownMenuIconButton
import com.andannn.melodify.ui.widgets.TransparentBackgroundListItem
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.album_page_title
import melodify.shared.ui.generated.resources.artist_page_title
import melodify.shared.ui.generated.resources.change_default_sort_order
import melodify.shared.ui.generated.resources.content_sort_by
import melodify.shared.ui.generated.resources.customize
import melodify.shared.ui.generated.resources.display_setting_for
import melodify.shared.ui.generated.resources.order_1_to_9
import melodify.shared.ui.generated.resources.order_9_to_1
import melodify.shared.ui.generated.resources.order_a_to_z
import melodify.shared.ui.generated.resources.order_new_to_old
import melodify.shared.ui.generated.resources.order_old_to_new
import melodify.shared.ui.generated.resources.order_z_to_a
import melodify.shared.ui.generated.resources.preset
import melodify.shared.ui.generated.resources.primary_group_by
import melodify.shared.ui.generated.resources.reset_settings
import melodify.shared.ui.generated.resources.secondary_group_by
import melodify.shared.ui.generated.resources.show_track_number
import melodify.shared.ui.generated.resources.sort_by_album
import melodify.shared.ui.generated.resources.sort_by_artist
import melodify.shared.ui.generated.resources.sort_by_artist_then_album
import melodify.shared.ui.generated.resources.sort_by_genre
import melodify.shared.ui.generated.resources.sort_by_media_title
import melodify.shared.ui.generated.resources.sort_by_none
import melodify.shared.ui.generated.resources.sort_by_release_year
import melodify.shared.ui.generated.resources.sort_by_title
import melodify.shared.ui.generated.resources.sort_by_track_number
import melodify.shared.ui.generated.resources.sort_sub_album_asc
import melodify.shared.ui.generated.resources.sort_sub_artist_album_asc
import melodify.shared.ui.generated.resources.sort_sub_artist_asc
import melodify.shared.ui.generated.resources.sort_sub_title_name_asc
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
    tab: CustomTab?,
    displaySetting: DisplaySetting,
    modifier: Modifier = Modifier,
    onChangePresetSortRule: (PresetSortRule) -> Unit = {},
    onChangeCustomSortRule: (DisplaySetting) -> Unit = {},
    onCustomRadioButtonClick: () -> Unit = {},
) {
    val isDefaultSettings = tab == null
    val selectedPresetOption =
        remember(displaySetting) {
            PresetSortRule.entries.firstOrNull { displaySetting.isPreset && it.displaySetting == displaySetting }
        }
    val options = PresetSortRule.entries

    Column(modifier = modifier.fillMaxHeight()) {
        val title =
            if (isDefaultSettings) {
                stringResource(Res.string.change_default_sort_order)
            } else {
                stringResource(Res.string.display_setting_for, getCategoryResource(tab))
            }
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
                Text(
                    modifier =
                        Modifier.padding(horizontal = 12.dp),
                    text = stringResource(Res.string.preset),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            item {
                Column {
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

            item {
                if (!isDefaultSettings) {
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
                                    DisplaySetting.DefaultCustom
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
                                    onChangeCustomSortRule(DisplaySetting.DefaultCustom)
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
}

@Composable
private fun CustomSortOptionGroup(
    modifier: Modifier = Modifier,
    displaySetting: DisplaySetting,
    enabled: Boolean,
    onPrimarySortRuleChange: (SortOption) -> Unit = {},
    onSecondarySortRuleChange: (SortOption) -> Unit = {},
    onContentSortRuleChange: (SortOption) -> Unit = {},
) {
    Column(modifier) {
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.PrimaryGroupOptions,
            currentOption = displaySetting.primaryGroupSort,
            label = stringResource(Res.string.primary_group_by),
            onChangeSortRule = onPrimarySortRuleChange,
        )
        Spacer(Modifier.height(8.dp))
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.SecondaryGroupOptions,
            currentOption = displaySetting.secondaryGroupSort,
            label = stringResource(Res.string.secondary_group_by),
            onChangeSortRule = onSecondarySortRuleChange,
        )
        Spacer(Modifier.height(8.dp))
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.ContentOptions,
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
    options: List<SortOptionType> = emptyList(),
    modifier: Modifier = Modifier,
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
                selectedIndex = options.indexOf(currentOptionType),
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
        is SortOption.Album -> SortOptionType.Album
        is SortOption.Artist -> SortOptionType.Artist
        is SortOption.Title -> SortOptionType.Title
        is SortOption.TrackNum -> SortOptionType.TrackNum
        is SortOption.ReleaseYear -> SortOptionType.ReleaseYear
        is SortOption.Genre -> SortOptionType.Genre
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
    ;

    companion object {
        val PrimaryGroupOptions =
            listOf(
                Album,
                Artist,
                Title,
                Genre,
                ReleaseYear,
            )
        val SecondaryGroupOptions =
            listOf(
                Album,
                Artist,
                Title,
                Genre,
                ReleaseYear,
                None,
            )
        val ContentOptions =
            listOf(
                TrackNum,
                Title,
            )
    }
}

@Composable
private fun SortOptionType.orderLabel(ascending: Boolean): String =
    when (this) {
        SortOptionType.Artist,
        SortOptionType.Title,
        SortOptionType.Genre,
        SortOptionType.Album,
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

private fun SortOptionType.label() =
    when (this) {
        SortOptionType.Album -> Res.string.album_page_title
        SortOptionType.Artist -> Res.string.artist_page_title
        SortOptionType.None -> Res.string.sort_by_none
        SortOptionType.Title -> Res.string.sort_by_media_title
        SortOptionType.TrackNum -> Res.string.sort_by_track_number
        SortOptionType.Genre -> Res.string.sort_by_genre
        SortOptionType.ReleaseYear -> Res.string.sort_by_release_year
    }

private fun SortOptionType.icon() =
    when (this) {
        SortOptionType.Album -> Icons.Outlined.Album
        SortOptionType.Artist -> Icons.Outlined.Person
        SortOptionType.Title -> Icons.Outlined.SortByAlpha
        SortOptionType.TrackNum -> Icons.Outlined.Audiotrack
        SortOptionType.None -> Icons.Outlined.Remove
        SortOptionType.Genre -> Icons.Outlined.Tag
        SortOptionType.ReleaseYear -> Icons.Outlined.Timeline
    }

private fun SortOptionType.createSortOption(isAscending: Boolean) =
    when (this) {
        SortOptionType.Artist -> SortOption.Artist(isAscending)
        SortOptionType.Album -> SortOption.Album(isAscending)
        SortOptionType.Title -> SortOption.Title(isAscending)
        SortOptionType.TrackNum -> SortOption.TrackNum(isAscending)
        SortOptionType.Genre -> SortOption.Genre(isAscending)
        SortOptionType.ReleaseYear -> SortOption.ReleaseYear(isAscending)
        SortOptionType.None -> SortOption.NONE
    }

enum class PresetSortRule(
    val displaySetting: DisplaySetting,
) {
    AlbumAsc(
        displaySetting = DisplaySetting.Preset.AlbumASC,
    ),
    ArtistAsc(
        displaySetting = DisplaySetting.Preset.ArtistASC,
    ),
    TitleNameAsc(
        displaySetting = DisplaySetting.Preset.TitleASC,
    ),
    ArtistAlbumASC(
        displaySetting = DisplaySetting.Preset.ArtistAlbumASC,
    ),
}

@Composable
private fun PresetSortRule.headerText(): String =
    when (this) {
        PresetSortRule.AlbumAsc ->
            stringResource(Res.string.sort_by_album)
        PresetSortRule.ArtistAsc ->
            stringResource(Res.string.sort_by_artist)
        PresetSortRule.TitleNameAsc ->
            stringResource(Res.string.sort_by_title)
        PresetSortRule.ArtistAlbumASC ->
            stringResource(Res.string.sort_by_artist_then_album)
    }

@Composable
private fun PresetSortRule.subTitle(): String =
    when (this) {
        PresetSortRule.AlbumAsc ->
            stringResource(Res.string.sort_sub_album_asc)
        PresetSortRule.ArtistAsc ->
            stringResource(Res.string.sort_sub_artist_asc)
        PresetSortRule.TitleNameAsc ->
            stringResource(Res.string.sort_sub_title_name_asc)
        PresetSortRule.ArtistAlbumASC ->
            stringResource(Res.string.sort_sub_artist_album_asc)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberChangeSortRulePresenter(customTab: CustomTab?) =
    remember(
        customTab,
    ) {
        ChangeSortRulePresenter(
            customTab = customTab,
        )
    }

@OptIn(ExperimentalMaterial3Api::class)
private class ChangeSortRulePresenter(
    private val customTab: CustomTab?,
    repository: Repository = getKoin().get<Repository>(),
) : Presenter<UiState> {
    private val userPreferences = repository.userPreferenceRepository

    @Composable
    override fun present(): UiState {
        val isDefaultSettings = customTab == null
        val displaySetting by userPreferences
            .getCurrentSortRule(customTab)
            .collectAsStateWithLifecycle(DisplaySetting.Preset.DefaultPreset)
        if (isDefaultSettings && !displaySetting.isPreset) {
            error("Never. Default setting only supports preset sort rule.")
        }

        val scope = rememberCoroutineScope()
        return UiState(displaySetting) { event ->
            when (event) {
                is UiEvent.OnChangeSortRule -> {
                    scope.launch {
                        if (customTab == null) {
                            userPreferences.saveDefaultSortRule(event.displaySetting)
                        } else {
                            userPreferences.saveSortRuleForTab(customTab, event.displaySetting)
                        }
                    }
                }

                UiEvent.OnCustomRadioButtonClick -> {
                    scope.launch {
                        val currentTab =
                            customTab
                                ?: error("Never. Default setting only supports preset sort rule.")
                        val customSortRule = userPreferences.getTabCustomSortRule(currentTab)

                        if (customSortRule != null && !customSortRule.isPreset) {
                            Napier.d(tag = TAG) { "Already has custom sort rule. $customSortRule" }
                        }

                        userPreferences.saveSortRuleForTab(customTab, DisplaySetting.DefaultCustom)
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
