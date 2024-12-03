package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.common.util.getUiRetainedScope
import com.andannn.melodify.ui.components.menu.MenuController
import com.andannn.melodify.ui.components.menu.MenuEvent
import com.andannn.melodify.ui.components.menu.model.SheetModel
import com.andannn.melodify.ui.components.message.MessageController
import com.andannn.melodify.ui.components.message.dialog.Dialog
import com.andannn.melodify.ui.components.message.dialog.InteractionResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberTabContentStateHolder(
    selectedTab: CustomTab?,
    repository: Repository = getKoin().get(),
    scope: CoroutineScope = rememberCoroutineScope(),
    menuController: MenuController = getUiRetainedScope()?.get<MenuController>()
        ?: getKoin().get<MenuController>(),
    messageController: MessageController = getUiRetainedScope()?.get<MessageController>()
        ?: getKoin().get<MessageController>(),
) = remember(
    selectedTab,
    repository,
    menuController,
    messageController
) {
    TabContentStateHolder(
        selectedTab = selectedTab,
        repository = repository,
        scope = scope,
        messageController = messageController,
        menuController = menuController,
    )
}

private const val TAG = "TabContentState"

class TabContentStateHolder(
    private val selectedTab: CustomTab?,
    private val repository: Repository,
    private val scope: CoroutineScope,
    private val messageController: MessageController,
    private val menuController: MenuController,
) {
    private val mediaControllerRepository = repository.mediaControllerRepository
    private val playListRepository = repository.playListRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _mediaContentFlow: Flow<Map<MediaItemModel, List<AudioItemModel>>>
        get() {
            if (selectedTab == null) {
                return flow { emit(emptyMap()) }
            }
            val contentsFlow = with(selectedTab) {
                repository.contentFlow()
            }
            return contentsFlow.mapLatest { contents ->
                contents
                    .groupBy(
                        keySelector = {
                            it.albumId
                        },
                    )
                    .let { idToContentsMap ->
                        idToContentsMap
                            .mapKeys { (id, _) ->
                                repository.mediaContentRepository.getAlbumByAlbumId(id)!!
                            }
                            .mapValues {
                                it.value.sortedBy { audio ->
                                    audio.cdTrackNumber
                                }
                            }
                    }
            }
        }

    private val _state =
        _mediaContentFlow.map { contentMap ->
            TabContentState(
                contentMap
            )
        }

    var state by mutableStateOf(TabContentState())
        private set

    init {
        scope.launch {
            _state.collect {
                state = it
            }
        }
    }

    fun playMusic(mediaItem: AudioItemModel) {
        if (mediaItem.isValid()) {
            val mediaItems = state.allAudio

            mediaControllerRepository.playMediaList(
                mediaItems.toList(),
                mediaItems.indexOf(mediaItem)
            )
        } else {
            Napier.d(tag = TAG) { "invalid media item click $mediaItem" }
            scope.launch {
                val result =
                    messageController.showMessageDialogAndWaitResult(Dialog.ConfirmDeletePlaylist)
                Napier.d(tag = TAG) { "ConfirmDeletePlaylist result: $result" }
                if (result == InteractionResult.AlertDialog.ACCEPT) {
                    val playListId = (selectedTab as CustomTab.PlayListDetail).playListId
                    val mediaId = mediaItem.id.substringAfter(AudioItemModel.INVALID_ID_PREFIX)

                    playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(mediaId))
                }
            }
        }
    }

    fun onShowMusicItemOption(mediaItemModel: MediaItemModel) {
        val currentTab = selectedTab
        if (mediaItemModel is AudioItemModel && currentTab is CustomTab.PlayListDetail) {
            menuController.onEvent(
                MenuEvent.OnShowBottomMenu(
                    SheetModel.AudioOptionInPlayListSheet(
                        playListId = currentTab.playListId,
                        mediaItemModel
                    )
                )
            )
        } else {
            menuController.onEvent(
                MenuEvent.OnShowBottomMenu(
                    SheetModel.MediaOptionSheet.fromMediaModel(
                        item = mediaItemModel,
                    )
                )
            )
        }
    }
}

data class TabContentState(
    val contentMap: Map<MediaItemModel, List<AudioItemModel>> = emptyMap(),
) {
    val allAudio: List<AudioItemModel>
        get() = contentMap.values.flatten()
}