package com.andannn.melodify.ui.common.components.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.repository.UserPreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin


@Composable
fun rememberTabUiStateHolder(
    scope: CoroutineScope = rememberCoroutineScope(),
    userPreferenceRepository: UserPreferenceRepository = getKoin().get(),
) = remember(
    scope,
    userPreferenceRepository
) {
    TabUiStateHolder(
        scope,
        userPreferenceRepository
    )
}

class TabUiStateHolder(
    scope: CoroutineScope,
    userPreferenceRepository: UserPreferenceRepository,
) {
    private val _currentCustomTabsFlow = userPreferenceRepository.currentCustomTabsFlow
    private val _selectedTabIndexFlow = MutableStateFlow(0)

    var state by mutableStateOf(TabUiState())
        private set

    private val _stateFlow = combine(
        _selectedTabIndexFlow,
        _currentCustomTabsFlow
    ) { selectedIndex, customTabs ->
        TabUiState(
            selectedIndex = selectedIndex.coerceAtMost(customTabs.size - 1),
            customTabList = customTabs
        )
    }

    init {
        scope.launch {
            _stateFlow.collect {
                state = it
            }
        }

        // Ensure selected index is valid
        scope.launch {
            _currentCustomTabsFlow
                .scan<List<CustomTab>, Pair<List<CustomTab>?, List<CustomTab>?>>(null to null) { pre, next ->
                    pre.second to next
                }
                .collect { (pre, next) ->
                    if (pre == null || next == null) {
                        return@collect
                    }

                    val currentIndex = _selectedTabIndexFlow.value

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
                        _selectedTabIndexFlow.value = newIndex
                    }
                }
        }
    }

    fun onClickTab(index: Int) {
        _selectedTabIndexFlow.value = index
    }
}


data class TabUiState(
    val selectedIndex: Int = 0,
    val customTabList: List<CustomTab> = emptyList()
) {
    val selectedTab: CustomTab?
        get() = customTabList.getOrNull(selectedIndex)
}
