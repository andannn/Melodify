/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.syncer.SyncMediaStoreHandler
import com.andannn.melodify.core.syncer.SyncStatus
import com.andannn.melodify.core.syncer.SyncType
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.SnackBarMessage
import com.andannn.melodify.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.LibraryScreen
import com.andannn.melodify.ui.SearchScreen
import com.andannn.melodify.ui.TabManageScreen
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.components.tab.TabUi
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tab.rememberTabUiPresenter
import com.andannn.melodify.ui.components.tabcontent.TabContent
import com.andannn.melodify.ui.components.tabcontent.TabContentState
import com.andannn.melodify.ui.components.tabcontent.rememberTabContentPresenter
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.andannn.melodify.ui.widgets.DropDownMenuIconButton
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import melodify.composeapp.generated.resources.Res
import melodify.composeapp.generated.resources.default_sort_order
import melodify.composeapp.generated.resources.re_sync_media_library
import org.jetbrains.compose.resources.StringResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun rememberHomeUiPresenter(
    navigator: Navigator,
    popController: PopupController = LocalPopupController.current,
    syncMediaStoreHandler: SyncMediaStoreHandler = getKoin().get(),
): Presenter<HomeState> =
    remember(
        navigator,
        popController,
        syncMediaStoreHandler,
    ) {
        HomePresenter(
            navigator,
            popController,
            syncMediaStoreHandler,
        )
    }

private const val TAG = "HomeScreen"

private class HomePresenter(
    private val navigator: Navigator,
    private val popController: PopupController,
    private val syncMediaStoreHandler: SyncMediaStoreHandler,
) : Presenter<HomeState> {
    @Composable
    override fun present(): HomeState {
        Napier.d(tag = "HomePresenter") { "HomePresenter present" }
        val scope = rememberCoroutineScope()
        val tabUiPresenter = rememberTabUiPresenter()
        val tabUiState = tabUiPresenter.present()
        val tabContentPresenter = rememberTabContentPresenter(tabUiState.selectedTab)
        return HomeState(
            tabUiState = tabUiState,
            tabContentState = tabContentPresenter.present(),
        ) { eventSink ->
            with(popController) {
                with(syncMediaStoreHandler) {
                    when (eventSink) {
                        HomeUiEvent.LibraryButtonClick -> navigator.goTo(LibraryScreen)
                        HomeUiEvent.SearchButtonClick -> navigator.goTo(SearchScreen)
                        is HomeUiEvent.OnMenuSelected -> {
                            when (eventSink.selected) {
                                MenuOption.DEFAULT_SORT -> scope.launch { changeSortRule() }
                                MenuOption.RE_SYNC_ALL_MEDIA -> scope.launch { resyncAllSongs() }
                            }
                        }

                        HomeUiEvent.OnTabManagementClick -> navigator.goTo(TabManageScreen)
                    }
                }
            }
        }
    }
}

context(syncMediaStoreHandler: SyncMediaStoreHandler, popController: PopupController)
private suspend fun resyncAllSongs() =
    coroutineScope {
        var job: Job? = null
        var mediaCount: Int? = null
        syncMediaStoreHandler.reSyncAllMedia().collect {
            Napier.d(tag = TAG) { "sync status update $it" }
            job?.cancel()
            job =
                launch {
                    when (it) {
                        SyncStatus.Complete ->
                            popController.showSnackBar(
                                SnackBarMessage.SyncCompleted,
                                "${mediaCount ?: 0} Songs",
                            )

                        SyncStatus.Failed ->
                            popController.showSnackBar(
                                SnackBarMessage.SyncFailed,
                            )

                        is SyncStatus.Progress -> {
                            if (it.type == SyncType.MEDIA) {
                                mediaCount = it.total
                            }
                            popController.showSnackBar(
                                SnackBarMessage.SyncProgress,
                                it.toSnackBarInfoString(),
                            )
                        }

                        SyncStatus.Start ->
                            popController.showSnackBar(
                                SnackBarMessage.SyncStatusStart,
                            )
                    }
                }
        }
    }

context(popupController: PopupController)
private suspend fun changeSortRule() {
    popupController.showDialog(
        DialogId.ChangeSortRuleDialog(),
    )
}

internal data class HomeState(
    val tabUiState: TabUiState,
    val tabContentState: TabContentState,
    val eventSink: (HomeUiEvent) -> Unit = {},
) : CircuitUiState

internal sealed interface HomeUiEvent {
    data object SearchButtonClick : HomeUiEvent

    data object LibraryButtonClick : HomeUiEvent

    data class OnMenuSelected(
        val selected: MenuOption,
    ) : HomeUiEvent

    data object OnTabManagementClick : HomeUiEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeUiScreen(
    homeState: HomeState,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = 64.dp),
                hostState = rememberAndSetupSnackBarHostState(),
            )
        },
        topBar = {
            TopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors().run {
                        copy(scrolledContainerColor = containerColor)
                    },
                title = {
                    Text(text = "Melodify")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { homeState.eventSink.invoke(HomeUiEvent.LibraryButtonClick) },
                        content = {
                            Icon(Icons.Rounded.Menu, contentDescription = "")
                        },
                    )
                },
                actions = {
                    IconButton(
                        onClick = { homeState.eventSink.invoke(HomeUiEvent.SearchButtonClick) },
                        content = {
                            Icon(Icons.Rounded.Search, contentDescription = "")
                        },
                    )
                    val options = MenuOption.entries
                    DropDownMenuIconButton(
                        options.map { it.textRes },
                        onSelectIndex = {
                            val selected = options[it]
                            homeState.eventSink.invoke(
                                HomeUiEvent.OnMenuSelected(
                                    selected = selected,
                                ),
                            )
                        },
                        imageVector = Icons.Rounded.MoreVert,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
        ) {
            TabUi(homeState.tabUiState, onTabManagementClick = {
                homeState.eventSink.invoke(HomeUiEvent.OnTabManagementClick)
            })

            TabContent(homeState.tabContentState)
        }
    }

    Player()
    ActionDialogContainer()
}

internal enum class MenuOption(
    val textRes: StringResource,
) {
    DEFAULT_SORT(
        textRes = Res.string.default_sort_order,
    ),
    RE_SYNC_ALL_MEDIA(
        textRes = Res.string.re_sync_media_library,
    ),
}

private fun SyncStatus.Progress.toSnackBarInfoString() =
    when (this.type) {
        SyncType.MEDIA -> "Song ${this.progress} / ${this.total}"
        SyncType.ALBUM -> "Album ${this.progress} / ${this.total}"
        SyncType.ARTIST -> "Artist ${this.progress} / ${this.total}"
        SyncType.GENRE -> "Genre ${this.progress} / ${this.total}"
    }
