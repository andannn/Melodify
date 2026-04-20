package com.andannn.melodify.ui.routes.home.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import com.andannn.melodify.shared.compose.common.widgets.ActionType
import com.andannn.melodify.shared.compose.common.widgets.ListTileItemView
import kotlinx.coroutines.launch

@Composable
internal fun TabSourceItem(
    tabSource: TabSource,
    modifier: Modifier = Modifier,
) {
    val state = retainTabSourceItemPresenter(tabSource).present()
    TabSourceItemContent(
        modifier = modifier,
        isSelected = state.isSelected,
        tabSource = tabSource,
        onItemClick = {
            state.eventSink.invoke(TabSourceItemUiEvent.OnToggleSelect)
        }
    )
}

@Composable
private fun TabSourceItemContent(
    isSelected: Boolean,
    tabSource: TabSource,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
) {
    ListTileItemView(
        modifier = modifier,
        isActive = isSelected,
        actionType = ActionType.NONE,
        title = tabSource.label,
        onItemClick = onItemClick
    )
}

@Composable
internal fun retainTabSourceItemPresenter(
    tabSource: TabSource,
    repository: Repository = LocalRepository.current,
) = retainPresenter(tabSource, repository) {
    TabSourceItemPresenter(tabSource, repository)
}

internal data class TabSourceItemState(
    val isSelected: Boolean,
    val eventSink: (TabSourceItemUiEvent) -> Unit
)

internal sealed interface TabSourceItemUiEvent {
    data object OnToggleSelect : TabSourceItemUiEvent
}

internal class TabSourceItemPresenter(
    val tabSource: TabSource,
    val repository: Repository
) : RetainedPresenter<TabSourceItemState>() {
    val isSelectedFlow = repository.isTabExistFlow(
        externalId = tabSource.externalId.toString(),
        tabName = tabSource.label,
        tabKind = tabSource.tabKind
    ).stateInRetainedModel(false)

    @Composable
    override fun present(): TabSourceItemState {
        val isSelected by isSelectedFlow.collectAsStateWithLifecycle()
        return TabSourceItemState(isSelected) { event ->
            when (event) {
                TabSourceItemUiEvent.OnToggleSelect -> {
                    retainedScope.launch {
                        if (isSelected) {
                            repository.deleteCustomTabByInfo(
                                externalId = tabSource.externalId.toString(),
                                tabName = tabSource.label,
                                tabKind = tabSource.tabKind
                            )
                        } else {
                            repository.addNewCustomTab(
                                externalId = tabSource.externalId.toString(),
                                tabName = tabSource.label,
                                tabKind = tabSource.tabKind
                            )
                        }
                    }
                }
            }
        }
    }
}
