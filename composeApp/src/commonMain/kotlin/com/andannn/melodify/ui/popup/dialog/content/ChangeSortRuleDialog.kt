/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.dialog.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.SortRule
import com.andannn.melodify.core.data.model.isAscending
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.util.getCategoryResource
import com.andannn.melodify.ui.widgets.DropDownMenuIconButton
import com.andannn.melodify.ui.widgets.TransparentBackgroundListItem
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import melodify.composeapp.generated.resources.Res
import melodify.composeapp.generated.resources.album_page_title
import melodify.composeapp.generated.resources.artist_page_title
import melodify.composeapp.generated.resources.sort_by_media_title
import melodify.composeapp.generated.resources.sort_by_none
import melodify.composeapp.generated.resources.sort_by_track_number
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
        sortRule = state.sortRule,
        onChangePresetSortRule = {
            state.eventSink(UiEvent.OnChangeSortRule(it.sortRule))
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
    sortRule: SortRule,
    modifier: Modifier = Modifier,
    onChangePresetSortRule: (PresetSortRule) -> Unit = {},
    onChangeCustomSortRule: (SortRule) -> Unit = {},
    onCustomRadioButtonClick: () -> Unit = {},
) {
    val isDefaultSettings = tab == null
    val selectedPresetOption =
        remember(sortRule) {
            PresetSortRule.entries.firstOrNull { sortRule.isPreset && it.sortRule == sortRule }
        }

    Column(modifier = modifier) {
        val title =
            if (isDefaultSettings) {
                "Change Default Sort Order"
            } else {
                "Display Setting for " + getCategoryResource(tab)
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
        val options = PresetSortRule.entries

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier =
                Modifier.padding(horizontal = 12.dp),
            text = "Preset",
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

        if (!isDefaultSettings) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier =
                    Modifier.padding(horizontal = 12.dp),
                text = "Customize",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val resolvedSortOption =
                    remember(sortRule) {
                        if (sortRule.isPreset) {
                            SortRule.DefaultCustom
                        } else {
                            sortRule
                        }
                    }

                Column(
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                            .graphicsLayer {
                                alpha = if (sortRule.isPreset) 0.5f else 1f
                            },
                ) {
                    val enabled = !sortRule.isPreset

                    CustomSortOptionGroup(
                        modifier = Modifier,
                        enabled = enabled,
                        sortRule = resolvedSortOption,
                        onPrimarySortRuleChange = { option ->
                            if (!sortRule.containsOption(option)) {
                                onChangeCustomSortRule(
                                    sortRule.copy(
                                        primaryGroupSort = option,
                                    ),
                                )
                            }
                        },
                        onSecondarySortRuleChange = { option ->
                            if (!sortRule.containsOption(option)) {
                                onChangeCustomSortRule(
                                    sortRule.copy(
                                        secondaryGroupSort = option,
                                    ),
                                )
                            }
                        },
                        onContentSortRuleChange = { option ->
                            if (!sortRule.containsOption(option)) {
                                onChangeCustomSortRule(
                                    sortRule.copy(
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
                        Text("Show Track Number")
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
                            onChangeCustomSortRule(SortRule.DefaultCustom)
                        },
                    ) {
                        Text("Reset")
                    }
                }

                RadioButton(
                    modifier = Modifier.padding(end = 8.dp),
                    selected = !sortRule.isPreset,
                    onClick = onCustomRadioButtonClick,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CustomSortOptionGroup(
    modifier: Modifier = Modifier,
    sortRule: SortRule,
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
            currentOption = sortRule.primaryGroupSort,
            label = "Primary Group by..",
            onChangeSortRule = onPrimarySortRuleChange,
        )
        Spacer(Modifier.height(8.dp))
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.SecondaryGroupOptions,
            currentOption = sortRule.secondaryGroupSort,
            label = "Secondary Group by..",
            onChangeSortRule = onSecondarySortRuleChange,
        )
        Spacer(Modifier.height(8.dp))
        SortOptionSelector(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            options = SortOptionType.ContentOptions,
            currentOption = sortRule.contentSort,
            label = "Content Sort by..",
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
                Modifier.weight(1.5f).height(48.dp),
            ) {
                if (currentOption !is SortOption.NONE) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SelectableTextButton(
                            modifier = Modifier.weight(1f),
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
                            modifier = Modifier.weight(1f),
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
            Text(text)
        }
    }
}

private fun SortRule.containsOption(sortOption: SortOption) =
    primaryGroupSort == sortOption || secondaryGroupSort == sortOption || contentSort == sortOption

private fun SortOption.toSortOptionType() =
    when (this) {
        is SortOption.Album -> SortOptionType.Album
        is SortOption.Artist -> SortOptionType.Artist
        is SortOption.Title -> SortOptionType.Title
        is SortOption.TrackNum -> SortOptionType.TrackNum
        is SortOption.NONE -> SortOptionType.None
    }

private enum class SortOptionType {
    Artist,
    Album,
    Title,
    TrackNum,
    None,
    ;

    companion object {
        val PrimaryGroupOptions =
            listOf(
                Album,
                Artist,
                Title,
            )
        val SecondaryGroupOptions =
            listOf(
                Album,
                Artist,
                Title,
                None,
            )
        val ContentOptions =
            listOf(
                TrackNum,
                Title,
            )
    }
}

private fun SortOptionType.orderLabel(ascending: Boolean) =
    when (this) {
        SortOptionType.Artist,
        SortOptionType.Title,
        SortOptionType.Album,
        -> if (ascending) "A → Z" else "Z → A"

        SortOptionType.TrackNum -> if (ascending) "1 → 9" else "9 → 1"
        SortOptionType.None -> error("Never. This should not happen.")
    }

private fun SortOptionType.label() =
    when (this) {
        SortOptionType.Album -> Res.string.album_page_title
        SortOptionType.Artist -> Res.string.artist_page_title
        SortOptionType.None -> Res.string.sort_by_none
        SortOptionType.Title -> Res.string.sort_by_media_title
        SortOptionType.TrackNum -> Res.string.sort_by_track_number
    }

private fun SortOptionType.icon() =
    when (this) {
        SortOptionType.Album -> Icons.Outlined.Album
        SortOptionType.Artist -> Icons.Outlined.Person
        SortOptionType.Title -> Icons.Outlined.SortByAlpha
        SortOptionType.TrackNum -> Icons.Outlined.Audiotrack
        SortOptionType.None -> Icons.Outlined.Remove
    }

private fun SortOptionType.createSortOption(isAscending: Boolean) =
    when (this) {
        SortOptionType.Artist -> SortOption.Artist(isAscending)
        SortOptionType.Album -> SortOption.Album(isAscending)
        SortOptionType.Title -> SortOption.Title(isAscending)
        SortOptionType.TrackNum -> SortOption.TrackNum(isAscending)
        SortOptionType.None -> SortOption.NONE
    }

private enum class PresetSortRule(
    val sortRule: SortRule,
) {
    AlbumAsc(
        sortRule = SortRule.Preset.AlbumASC,
    ),
    ArtistAsc(
        sortRule = SortRule.Preset.ArtistASC,
    ),
    TitleNameAsc(
        sortRule = SortRule.Preset.TitleASC,
    ),
    ArtistAlbumASC(
        sortRule = SortRule.Preset.ArtistAlbumASC,
    ),
}

private fun PresetSortRule.headerText() =
    when (this) {
        PresetSortRule.AlbumAsc -> "Sort by Album"
        PresetSortRule.ArtistAsc -> "Sort by Artist"
        PresetSortRule.TitleNameAsc -> "Sort by Title"
        PresetSortRule.ArtistAlbumASC -> "Sort by Artist, then Album"
    }

private fun PresetSortRule.subTitle() =
    when (this) {
        PresetSortRule.AlbumAsc -> "Group items by album and sort tracks in ascending order."
        PresetSortRule.ArtistAsc -> "Group items by artist in ascending order."
        PresetSortRule.TitleNameAsc -> "Group items alphabetically by title."
        PresetSortRule.ArtistAlbumASC -> "Group items by artist and then by album, sorting tracks in ascending order."
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
        val sortRule by userPreferences
            .getCurrentSortRule(customTab)
            .collectAsRetainedState(SortRule.Preset.DefaultPreset)
        if (isDefaultSettings && !sortRule.isPreset) {
            error("Never. Default setting only supports preset sort rule.")
        }

        val scope = rememberCoroutineScope()
        return UiState(sortRule) { event ->
            when (event) {
                is UiEvent.OnChangeSortRule -> {
                    scope.launch {
                        if (customTab == null) {
                            userPreferences.saveDefaultSortRule(event.sortRule)
                        } else {
                            userPreferences.saveSortRuleForTab(customTab, event.sortRule)
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

                        userPreferences.saveSortRuleForTab(customTab, SortRule.DefaultCustom)
                    }
                }
            }
        }
    }
}

@Stable
private data class UiState(
    val sortRule: SortRule,
    val eventSink: (UiEvent) -> Unit = {},
) : CircuitUiState

private sealed interface UiEvent {
    data class OnChangeSortRule(
        val sortRule: SortRule,
    ) : UiEvent

    data object OnCustomRadioButtonClick : UiEvent
}
