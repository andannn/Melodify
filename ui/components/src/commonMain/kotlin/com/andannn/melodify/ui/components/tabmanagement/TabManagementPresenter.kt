package com.andannn.melodify.ui.components.tabmanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "TabManagementPresenter"

@Composable
fun rememberTabManagementPresenter(
    navigator: Navigator,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
) = remember(
    navigator,
    repository,
    popupController,
) {
    TabManagementPresenter(
        navigator,
        repository,
        popupController,
    )
}

class TabManagementPresenter(
    private val navigator: Navigator,
    private val repository: Repository,
    private val popupController: PopupController,
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
                TabManagementEvent.OnBackKeyPressed -> navigator.pop()
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
    data object OnBackKeyPressed : TabManagementEvent

    data class OnSwapFinished(
        val from: Int,
        val to: Int,
    ) : TabManagementEvent

    data class OnDeleteFinished(
        val index: Int,
    ) : TabManagementEvent
}
