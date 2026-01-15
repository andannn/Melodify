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
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.DisplaySetting
import com.andannn.melodify.domain.model.isAudio
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "ChangeSortRulePresenter"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun retainedChangeSortRulePresenter(
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
internal data class UiState(
    val displaySetting: DisplaySetting,
    val isShowVideoProgress: Boolean,
    val eventSink: (UiEvent) -> Unit = {},
)

internal sealed interface UiEvent {
    data class OnChangeSortRule(
        val displaySetting: DisplaySetting,
    ) : UiEvent

    data object OnCustomRadioButtonClick : UiEvent

    data object OnToggleIsShowVideoProgress : UiEvent
}
