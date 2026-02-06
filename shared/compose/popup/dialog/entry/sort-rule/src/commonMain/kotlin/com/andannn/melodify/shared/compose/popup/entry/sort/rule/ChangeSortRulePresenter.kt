/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sort.rule

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.AudioTrackStyle
import com.andannn.melodify.domain.model.CustomDisplaySetting
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.domain.model.Tab
import com.andannn.melodify.domain.model.TabSortRule
import com.andannn.melodify.domain.model.contentSortType
import com.andannn.melodify.domain.model.defaultPresetSetting
import com.andannn.melodify.domain.model.toDisplaySetting
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun retainedChangeSortRulePresenter(
    tab: Tab,
    repository: Repository = LocalRepository.current,
) = retainPresenter(
    tab,
    repository,
) {
    ChangeSortRulePresenter(
        repository = repository,
        tab = tab,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private class ChangeSortRulePresenter(
    private val tab: Tab,
    private val repository: Repository,
) : RetainedPresenter<UiState>() {
    private val tabCustomDisplaySettingFlow =
        repository
            .getTabCustomDisplaySettingFlow(tab)
            .stateInRetainedModel(
                initialValue = null,
            )

    private val tabPresetDisplaySettingFlow =
        repository
            .getTabPresetDisplaySettingFlow(tab)
            .stateInRetainedModel(
                initialValue = null,
            )

    @Composable
    override fun present(): UiState {
        val tabCustomDisplaySetting by tabCustomDisplaySettingFlow.collectAsStateWithLifecycle()
        val tabPresetDisplaySetting by tabPresetDisplaySettingFlow.collectAsStateWithLifecycle()

        return UiState(
            tabCustomDisplaySetting = tabCustomDisplaySetting,
            tabPresetDisplaySetting = tabPresetDisplaySetting,
        ) { event ->
            when (event) {
                UiEvent.OnSelectCustomDisplaySetting -> {
                    retainedScope.launch {
                        repository.selectTabCustomDisplaySetting(
                            tab.tabId,
                            tab.contentSortType().defaultPresetSetting().toDisplaySetting(),
                        )
                    }
                }

                UiEvent.OnClickResetSetting -> {
                    retainedScope.launch {
                        repository.selectTabCustomDisplaySetting(
                            tab.tabId,
                            tab.contentSortType().defaultPresetSetting().toDisplaySetting(),
                        )
                    }
                }

                is UiEvent.OnSelectPresetDisplaySetting -> {
                    retainedScope.launch {
                        repository.selectTabPresetDisplaySetting(
                            tab.tabId,
                            event.preset,
                        )
                    }
                }

                is UiEvent.OnChangeAudioTrackStyle -> {
                    retainedScope.launch {
                        repository.selectTabCustomDisplaySetting(
                            tab.tabId,
                            displaySetting =
                                tabCustomDisplaySetting?.copy(
                                    audioTrackStyle = event.style,
                                ) ?: error("no custom display setting"),
                        )
                    }
                }

                is UiEvent.OnChangeCustomSortRule -> {
                    retainedScope.launch {
                        repository.selectTabCustomDisplaySetting(
                            tab.tabId,
                            displaySetting =
                                tabCustomDisplaySetting?.copy(
                                    tabSortRule = event.tabSortRule,
                                ) ?: error("no custom display setting"),
                        )
                    }
                }

                is UiEvent.OnToggleIsShowVideoProgress -> {
                    retainedScope.launch {
                        repository.selectTabCustomDisplaySetting(
                            tab.tabId,
                            displaySetting =
                                tabCustomDisplaySetting?.let {
                                    it.copy(
                                        isShowVideoProgress = !it.isShowVideoProgress,
                                    )
                                } ?: error("no custom display setting"),
                        )
                    }
                }
            }
        }
    }
}

@Stable
internal data class UiState(
    val tabCustomDisplaySetting: CustomDisplaySetting?,
    val tabPresetDisplaySetting: PresetDisplaySetting?,
    val eventSink: (UiEvent) -> Unit = {},
)

internal sealed interface UiEvent {
    data class OnChangeCustomSortRule(
        val tabSortRule: TabSortRule,
    ) : UiEvent

    data object OnToggleIsShowVideoProgress : UiEvent

    data object OnClickResetSetting : UiEvent

    data class OnChangeAudioTrackStyle(
        val style: AudioTrackStyle,
    ) : UiEvent

    data object OnSelectCustomDisplaySetting : UiEvent

    data class OnSelectPresetDisplaySetting(
        val preset: PresetDisplaySetting,
    ) : UiEvent
}
