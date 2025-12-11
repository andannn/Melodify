/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabmanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "TabManagementPresenter"

@Composable
fun retainTabManagementPresenter(repository: Repository = LocalRepository.current) =
    retainPresenter(
        repository,
    ) {
        TabManagementPresenter(
            repository,
        )
    }

class TabManagementPresenter(
    private val repository: Repository,
) : RetainedPresenter<TabManagementState>() {
    private val currentTabListFlow =
        repository.currentCustomTabsFlow
            .stateIn(
                retainedScope,
                started = WhileSubscribed(),
                initialValue = emptyList(),
            )

    @Composable
    override fun present(): TabManagementState {
        val currentTabList by currentTabListFlow.collectAsStateWithLifecycle()
        return TabManagementState(
            currentTabList.toImmutableList(),
        ) { event ->
            when (event) {
                is TabManagementEvent.OnSwapFinished -> {
                    retainedScope.launch {
                        val (from, to) = event
                        repository.swapTabOrder(
                            from = currentTabList[from],
                            to = currentTabList[to],
                        )
                    }
                }

                is TabManagementEvent.OnDeleteFinished -> {
                    retainedScope.launch {
                        val toDelete =
                            currentTabList[event.index]
                        repository.deleteCustomTab(toDelete)
                    }
                }
            }
        }
    }
}

@Stable
data class TabManagementState(
    val tabList: ImmutableList<CustomTab> = persistentListOf(),
    val eventSink: (TabManagementEvent) -> Unit = {},
)

sealed interface TabManagementEvent {
    data class OnSwapFinished(
        val from: Int,
        val to: Int,
    ) : TabManagementEvent

    data class OnDeleteFinished(
        val index: Int,
    ) : TabManagementEvent
}
