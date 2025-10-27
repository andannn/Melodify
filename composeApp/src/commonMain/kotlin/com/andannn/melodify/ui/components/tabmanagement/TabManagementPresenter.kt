/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabmanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private const val TAG = "TabManagementPresenter"

@Composable
fun rememberTabManagementPresenter(repository: Repository = LocalRepository.current) =
    remember(
        repository,
    ) {
        TabManagementPresenter(
            repository,
        )
    }

class TabManagementPresenter(
    private val repository: Repository,
) : Presenter<TabManagementState> {
    private val userPreferenceRepository = repository.userPreferenceRepository

    @Composable
    override fun present(): TabManagementState {
        val scope = rememberCoroutineScope()
        val currentTabList by userPreferenceRepository.currentCustomTabsFlow.collectAsRetainedState(
            emptyList(),
        )

        return TabManagementState(
            currentTabList.toImmutableList(),
        ) { event ->
            when (event) {
                is TabManagementEvent.OnSwapFinished -> {
                    scope.launch {
                        val (from, to) = event
                        val newTabList =
                            currentTabList.toMutableList().apply {
                                val item = removeAt(from)
                                add(to, item)
                            }

                        userPreferenceRepository.updateCurrentCustomTabs(
                            newTabList,
                        )
                    }
                }

                is TabManagementEvent.OnDeleteFinished -> {
                    scope.launch {
                        val newTabList =
                            currentTabList.toMutableList().apply {
                                removeAt(event.index)
                            }
                        userPreferenceRepository.updateCurrentCustomTabs(
                            newTabList,
                        )
                    }
                }
            }
        }
    }
}

data class TabManagementState(
    val tabList: ImmutableList<CustomTab> = persistentListOf(),
    val eventSink: (TabManagementEvent) -> Unit = {},
) : CircuitUiState

sealed interface TabManagementEvent {
    data class OnSwapFinished(
        val from: Int,
        val to: Int,
    ) : TabManagementEvent

    data class OnDeleteFinished(
        val index: Int,
    ) : TabManagementEvent
}
