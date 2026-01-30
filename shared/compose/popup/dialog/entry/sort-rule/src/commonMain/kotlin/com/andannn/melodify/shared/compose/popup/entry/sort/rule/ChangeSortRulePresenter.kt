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
import com.andannn.melodify.domain.model.Tab
import com.andannn.melodify.domain.model.TabSortRule
import com.andannn.melodify.domain.model.contentSortType
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
    private val tabSortRuleFlow =
        repository
            .getCurrentSortRule(tab)
            .stateIn(
                retainedScope,
                initialValue = TabSortRule.Preset.Audio.DefaultPreset,
                started = WhileSubscribed(5000),
            )

    private val isShowVideoProgressFlow =
        repository
            .getIsShowVideoProgressFlow(tab)
            .stateIn(
                retainedScope,
                initialValue = false,
                started = WhileSubscribed(5000),
            )

    @Composable
    override fun present(): UiState {
        val tabSortRule by tabSortRuleFlow.collectAsStateWithLifecycle()
        val isShowVideoProgress by isShowVideoProgressFlow.collectAsStateWithLifecycle()

        return UiState(
            tabSortRule = tabSortRule,
            isShowVideoProgress = isShowVideoProgress,
        ) { event ->
            when (event) {
                is UiEvent.OnChangeSortRule -> {
                    Napier.d(tag = TAG) { "OnChangeSortRule. ${event.tabSortRule}" }
                    retainedScope.launch {
                        repository.saveSortRuleForTab(tab, event.tabSortRule)
                    }
                }

                UiEvent.OnCustomRadioButtonClick -> {
                    retainedScope.launch {
                        val currentTab = tab
                        val customSortRule = repository.getTabCustomSortRule(currentTab)

                        if (customSortRule != null && !customSortRule.isPreset) {
                            Napier.d(tag = TAG) { "Already has custom sort rule. $customSortRule" }
                        }

                        repository.saveSortRuleForTab(
                            currentTab,
                            TabSortRule.getDefaultCustom(tab.contentSortType()),
                        )
                    }
                }

                UiEvent.OnToggleIsShowVideoProgress -> {
                    retainedScope.launch {
                        repository.setIsShowVideoProgress(
                            tab = tab,
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
    val tabSortRule: TabSortRule,
    val isShowVideoProgress: Boolean,
    val eventSink: (UiEvent) -> Unit = {},
)

internal sealed interface UiEvent {
    data class OnChangeSortRule(
        val tabSortRule: TabSortRule,
    ) : UiEvent

    data object OnCustomRadioButtonClick : UiEvent

    data object OnToggleIsShowVideoProgress : UiEvent
}
