package com.andannn.melodify.navigation.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.andannn.melodify.ui.components.tab.ReactiveTab
import com.andannn.melodify.ui.components.tab.TabUiPresenter
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tabcontent.TabContent
import com.andannn.melodify.ui.components.tabcontent.TabContentPresenter
import com.andannn.melodify.ui.components.tabcontent.TabContentState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import org.koin.mp.KoinPlatform.getKoin

const val HOME_ROUTE = "home_route"

object HomeUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is HomeScreen -> ui<HomeState> { state, modifier ->
                HomeUiScreen(state, modifier)
            }

            else -> null
        }
    }
}

object HomePresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): Presenter<*>? {
        return when (screen) {
            is HomeScreen -> HomePresenter(navigator, context)
            else -> null
        }
    }
}

private class HomePresenter(
    private val navigator: Navigator, context: CircuitContext
) :
    Presenter<HomeState> {
    @Composable
    override fun present(): HomeState {
        val tabUiPresenter = remember {
            TabUiPresenter(getKoin().get(), null)
        }
        val tabUiState = tabUiPresenter.present()
        val tabContentPresenter = remember(tabUiState.selectedTab) {
            TabContentPresenter(tabUiState.selectedTab, getKoin().get(), null)
        }
        return HomeState(
            tabUiState = tabUiState,
            tabContentState = tabContentPresenter.present()
        ) { eventSink ->
            when (eventSink) {
                HomeUiEvent.LibraryButtonClick -> navigator.goTo(LibraryScreen)
                HomeUiEvent.SearchButtonClick -> TODO()
                HomeUiEvent.SettingButtonClick -> TODO()
            }
        }
    }
}

private data class HomeState(
    val tabUiState: TabUiState,
    val tabContentState: TabContentState,
    val eventSink: (HomeUiEvent) -> Unit = {},
) : CircuitUiState

private sealed interface HomeUiEvent {
    data object SettingButtonClick : HomeUiEvent
    data object SearchButtonClick : HomeUiEvent
    data object LibraryButtonClick : HomeUiEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeUiScreen(
    homeState: HomeState,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors =
                TopAppBarDefaults.centerAlignedTopAppBarColors().run {
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
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = { homeState.eventSink.invoke(HomeUiEvent.SearchButtonClick) },
                        content = {
                            Icon(Icons.Rounded.Search, contentDescription = "")
                        }
                    )
                    IconButton(
                        onClick = { homeState.eventSink.invoke(HomeUiEvent.SettingButtonClick) },
                        content = {
                            Icon(Icons.Rounded.Settings, contentDescription = "")
                        }
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
        ) {
            ReactiveTab(homeState.tabUiState)

            TabContent(homeState.tabContentState)
        }
    }
}
