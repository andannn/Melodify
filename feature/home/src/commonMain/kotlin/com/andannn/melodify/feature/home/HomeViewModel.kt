package com.andannn.melodify.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaPreviewMode
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.drawer.DrawerEvent
import com.andannn.melodify.feature.drawer.model.SheetModel
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.message.dialog.Dialog
import com.andannn.melodify.feature.message.dialog.InteractionResult
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
    data class OnMusicItemClick(val mediaItem: AudioItemModel) : HomeUiEvent
    data class OnShowItemOption(val audioItemModel: MediaItemModel) : HomeUiEvent
    data object OnTogglePreviewMode : HomeUiEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: Repository,
    private val drawerController: DrawerController,
    private val messageController: MessageController,
) : ViewModel() {
    private val mediaControllerRepository = repository.mediaControllerRepository
    private val userPreferenceRepository = repository.userPreferenceRepository
    private val playListRepository = repository.playListRepository
    private val playlistCreatedEventChannel = drawerController.playlistCreatedEventChannel

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
                repository.contentFlow()
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

    init {
        viewModelScope.launch {
            for (createdPlayListId in playlistCreatedEventChannel) {
                Napier.d(tag = TAG) { "new playlist created $createdPlayListId" }
                val playList = playListRepository.getPlayListById(createdPlayListId)
                    ?: error("no such playlist")
                val currentCustomTabs = userPreferenceRepository.currentCustomTabsFlow.first()
                userPreferenceRepository.updateCurrentCustomTabs(
                    listOf(
                        *currentCustomTabs.toTypedArray(),
                        CustomTab.PlayListDetail(playList.id, playList.name),
                    )
                )

                delay(300)

                Napier.d(tag = TAG) { "new playlist created ${state.value.customTabList}" }
                // navigate to new created playlist
                _selectedTabIndexFlow.value = state.value.customTabList.size -1
            }
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnSelectedCategoryChanged -> onSelectedCategoryChanged(event.tabIndex)
            is HomeUiEvent.OnMusicItemClick -> playMusic(event.mediaItem)
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
        if (mediaItem.isValid()) {
            val mediaItems = state.value.mediaItems.toList() as? List<AudioItemModel>
                ?: error("invalid state")

            mediaControllerRepository.playMediaList(
                mediaItems.toList(),
                mediaItems.indexOf(mediaItem)
            )
        } else {
            Napier.d(tag = TAG) { "invalid media item click $mediaItem" }
            viewModelScope.launch {
                val result =
                    messageController.showMessageDialogAndWaitResult(Dialog.ConfirmDeletePlaylist)
                Napier.d(tag = TAG) { "ConfirmDeletePlaylist result: $result" }
                if (result == InteractionResult.AlertDialog.ACCEPT) {
                    val playListId = (state.value.currentTab as CustomTab.PlayListDetail).playListId
                    val mediaId = mediaItem.id.substringAfter(AudioItemModel.INVALID_ID_PREFIX)

                    playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(mediaId))
                }
            }
        }
    }

    private fun onShowMusicItemOption(mediaItemModel: MediaItemModel) {
        val currentTab = state.value.currentTab
        if (mediaItemModel is AudioItemModel && currentTab is CustomTab.PlayListDetail) {
            drawerController.onEvent(
                DrawerEvent.OnShowBottomDrawer(
                    SheetModel.AudioOptionInPlayListSheet(
                        playListId = currentTab.playListId,
                        mediaItemModel
                    )
                )
            )
        } else {
            drawerController.onEvent(
                DrawerEvent.OnShowBottomDrawer(
                    SheetModel.MediaOptionSheet.fromMediaModel(
                        item = mediaItemModel,
                    )
                )
            )
        }
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
) {
    val currentTab: CustomTab
        get() = customTabList[selectedIndex]
}