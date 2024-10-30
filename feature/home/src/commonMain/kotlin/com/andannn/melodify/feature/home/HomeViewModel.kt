package com.andannn.melodify.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaPreviewMode
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.UserPreferenceRepository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.drawer.DrawerEvent
import com.andannn.melodify.feature.drawer.model.SheetModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

sealed interface HomeUiEvent {
    data class OnSelectedCategoryChanged(val tabIndex: Int) : HomeUiEvent
    data class OnPlayMusic(val mediaItem: AudioItemModel) : HomeUiEvent
    data class OnShowItemOption(val audioItemModel: MediaItemModel) : HomeUiEvent
    data object OnTogglePreviewMode : HomeUiEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val mediaControllerRepository: MediaControllerRepository,
    private val drawerController: DrawerController,
    private val mediaContentRepository: MediaContentRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {
    private val _userSettingFlow = userPreferenceRepository.userSettingFlow
    private val _selectedTabIndexFlow = MutableStateFlow(0)

    private val _tabStatusFlow = combine(
        _selectedTabIndexFlow,
        _userSettingFlow
    ) { selectedIndex, userSetting ->
        val customTabs = userSetting.currentCustomTabs.customTabs
        TabStatus(
            selectedIndex = selectedIndex.coerceAtMost(customTabs.size - 1),
            customTabList = userSetting.currentCustomTabs.customTabs
        )
    }

    private val _mediaContentFlow = _tabStatusFlow
        .map { it.selectedTab }
        .distinctUntilChanged()
        .flatMapLatest { tab ->
            if (tab == null) {
                return@flatMapLatest flow { emit(emptyList()) }
            }
            with(tab) {
                mediaContentRepository.contentFlow()
            }
        }

    val state = combine(
        _tabStatusFlow,
        _mediaContentFlow,
        _userSettingFlow,
    ) { tabStatus, mediaContents, userSetting ->
        HomeUiState(
            selectedIndex = tabStatus.selectedIndex,
            customTabList = tabStatus.customTabList,
            mediaItems = mediaContents.toImmutableList(),
            previewMode = userSetting.mediaPreviewMode,
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeUiState())

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnSelectedCategoryChanged -> onSelectedCategoryChanged(event.tabIndex)
            is HomeUiEvent.OnPlayMusic -> playMusic(event.mediaItem)
            is HomeUiEvent.OnShowItemOption -> onShowMusicItemOption(event.audioItemModel)
            is HomeUiEvent.OnTogglePreviewMode -> onTogglePreviewMode()
        }
    }

    private fun onTogglePreviewMode() {
        viewModelScope.launch {
            userPreferenceRepository.setPreviewMode(state.value.previewMode.next())
        }
    }

    private fun onSelectedCategoryChanged(category: Int) {
        _selectedTabIndexFlow.value = category
    }

    private fun playMusic(mediaItem: AudioItemModel) {
        val mediaItems = state.value.mediaItems.toList() as? List<AudioItemModel>
            ?: error("invalid state")

        mediaControllerRepository.playMediaList(
            mediaItems.toList(),
            mediaItems.indexOf(mediaItem)
        )
    }

    private fun onShowMusicItemOption(mediaItemModel: MediaItemModel) {
        drawerController.onEvent(
            DrawerEvent.OnShowBottomDrawer(
                SheetModel.MediaOptionSheet.fromMediaModel(
                    item = mediaItemModel,
                )
            )
        )
    }
}

private data class TabStatus(
    val selectedIndex: Int = 0,
    val customTabList: List<CustomTab> = emptyList()
) {
    val selectedTab: CustomTab?
        get() = customTabList.getOrNull(selectedIndex)
}

data class HomeUiState(
    val selectedIndex: Int = 0,
    val customTabList: List<CustomTab> = emptyList(),
    val mediaItems: ImmutableList<MediaItemModel> = emptyList<MediaItemModel>().toImmutableList(),
    val previewMode: MediaPreviewMode = MediaPreviewMode.GRID_PREVIEW,
)