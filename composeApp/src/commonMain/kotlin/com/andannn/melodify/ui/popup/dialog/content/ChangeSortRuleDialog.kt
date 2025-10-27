/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.dialog.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.SortRule
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.widgets.TransparentBackgroundListItem
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun ChangeSortRuleDialog(
    modifier: Modifier = Modifier,
    dialog: DialogId.ChangeSortRuleDialog,
    onAction: (DialogAction) -> Unit = {},
) {
    val state = rememberChangeSortRulePresenter(dialog.tab).present()
    Column(modifier = modifier) {
        val title = if (dialog.tab != null) dialog.tab.toString() else "Change Default Sort Order"
        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        val options = PresetSortRule.entries

        options.forEach { rule ->
            TransparentBackgroundListItem(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                onClick = {
                    if (rule != state.selectedOption) {
                        state.eventSink(UiEvent.OnChangeSortRule(rule))
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
                        selected = state.selectedOption == rule,
                        onClick = null,
                    )
                },
            )
        }
    }
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
        val sortRule by userPreferences
            .getSortRule(customTab)
            .collectAsRetainedState(SortRule.Preset.Default)
        val selectedOption =
            remember(sortRule) {
                PresetSortRule.entries.first { it.sortRule == sortRule }
            }
        val scope = rememberCoroutineScope()
        return UiState(selectedOption) { event ->
            when (event) {
                is UiEvent.OnChangeSortRule -> {
                    scope.launch {
                        if (customTab == null) {
                            userPreferences.saveDefaultSortRule(event.sortRule.sortRule)
                        } else {
                            userPreferences.saveSortRuleForTab(customTab, event.sortRule.sortRule)
                        }
                    }
                }
            }
        }
    }
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

@Stable
private data class UiState(
    val selectedOption: PresetSortRule,
    val eventSink: (UiEvent) -> Unit = {},
) : CircuitUiState

private sealed interface UiEvent {
    data class OnChangeSortRule(
        val sortRule: PresetSortRule,
    ) : UiEvent
}
