package com.andannn.melodify.ui.components.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.dialog.OptionItem
import com.andannn.melodify.ui.components.popup.onMediaOptionClick
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "TabUiState"

object TabUiPresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): TabUiPresenter {
        return TabUiPresenter(
            getKoin().get(),
            null
        )
    }
}

class TabUiPresenter(
    private val repository: Repository,
    private val popupController: PopupController?,
) : Presenter<TabUiState> {
    private val userPreferenceRepository = repository.userPreferenceRepository
    private val mediaContentRepository = repository.mediaContentRepository
    private val playListRepository = repository.playListRepository

    @Composable
    override fun present(): TabUiState {
        val scope = rememberCoroutineScope()
        var selectedIndex by rememberSaveable { mutableStateOf(0) }
        val currentTabList by userPreferenceRepository.currentCustomTabsFlow.collectAsState(
            emptyList()
        )

        LaunchedEffect(Unit) {
            userPreferenceRepository.currentCustomTabsFlow
                .scan<List<CustomTab>, Pair<List<CustomTab>?, List<CustomTab>?>>(null to null) { pre, next ->
                    pre.second to next
                }.collect { (pre, next) ->
                    Napier.d(tag = TAG) { "tab changed pre: $pre, next: $next" }
                    if (pre == null || next == null) {
                        return@collect
                    }

                    val currentIndex = selectedIndex

                    val newIndex: Int = if (next.size < pre.size) {
                        // new tab list is smaller than the previous one.
                        // 1. select the previous tab
                        // 2. if the current tab is removed, select the previous tab
                        next.indexOf(pre.getOrNull(currentIndex))
                            .takeIf { it != -1 } ?: (currentIndex - 1).coerceAtLeast(0)
                    } else if (next.size > pre.size) {
                        // always select the new created tab
                        next.indexOf(next.firstOrNull { it !in pre })
                    } else {
                        next.indexOf(pre.getOrNull(currentIndex))
                    }
                    if (newIndex != -1) {
                        selectedIndex = newIndex
                    }
                }
        }
        return TabUiState(
            selectedIndex.coerceAtMost(currentTabList.size - 1),
            currentTabList
        ) { eventSink ->
            when (eventSink) {
                is TabUiEvent.OnClickTab -> selectedIndex = eventSink.index
                is TabUiEvent.OnShowTabOption -> scope.launch { onShowTabOption(eventSink.tab) }
            }
        }
    }

    private suspend fun onShowTabOption(tab: CustomTab) {
        val model = when (tab) {
            is CustomTab.AlbumDetail -> mediaContentRepository.getAlbumByAlbumId(tab.albumId)
            is CustomTab.ArtistDetail -> mediaContentRepository.getArtistByArtistId(tab.artistId)
            is CustomTab.GenreDetail -> mediaContentRepository.getGenreByGenreId(tab.genreId)
            is CustomTab.PlayListDetail -> playListRepository.getPlayListById(tab.playListId.toLong())
            CustomTab.AllMusic -> return
        }

        if (model == null) {
            return
        }
        val result = popupController?.showDialog(DialogId.MediaOption.fromMediaModel(model))

        if (result is DialogAction.MediaOptionDialog.ClickItem) {
            if (result.optionItem == OptionItem.DELETE_TAB) {
                repository.userPreferenceRepository.deleteCustomTab(tab)
            } else {
                repository.onMediaOptionClick(
                    optionItem = result.optionItem,
                    dialog = result.dialog,
                    popupController = popupController
                )
            }
        }
    }
}

data class TabUiState(
    val selectedIndex: Int = 0,
    val customTabList: List<CustomTab> = emptyList(),
    val eventSink: (TabUiEvent) -> Unit = {}
) : CircuitUiState {
    val selectedTab: CustomTab?
        get() = customTabList.getOrNull(selectedIndex)
}

sealed interface TabUiEvent {
    data class OnClickTab(val index: Int) : TabUiEvent

    data class OnShowTabOption(val tab: CustomTab) : TabUiEvent
}

