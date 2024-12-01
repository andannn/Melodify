package com.andannn.melodify.feature.home.tabcontent

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
import com.andannn.melodify.feature.common.util.getUiRetainedScope
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.drawer.DrawerEvent
import com.andannn.melodify.feature.drawer.model.SheetModel
import com.andannn.melodify.feature.message.MessageController
import com.andannn.melodify.feature.message.dialog.Dialog
import com.andannn.melodify.feature.message.dialog.InteractionResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@Composable
fun rememberTabContentStateHolder(
    selectedTab: CustomTab?,
    repository: Repository = getKoin().get(),
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerController: DrawerController = getUiRetainedScope()?.get<DrawerController>()
        ?: getKoin().get<DrawerController>(),
    messageController: MessageController = getUiRetainedScope()?.get<MessageController>()
        ?: getKoin().get<MessageController>(),
) = remember(
    selectedTab,
    repository,
    drawerController,
    messageController
) {
    TabContentStateHolder(
        selectedTab = selectedTab,
        repository = repository,
        scope = scope,
        messageController = messageController,
        drawerController = drawerController,
    )
}

private const val TAG = "TabContentState"

class TabContentStateHolder(
    private val selectedTab: CustomTab?,
    private val repository: Repository,
    private val scope: CoroutineScope,
    private val messageController: MessageController,
    private val drawerController: DrawerController,
) {
    private val mediaControllerRepository = repository.mediaControllerRepository
    private val playListRepository = repository.playListRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _mediaContentFlow = flowOf(selectedTab)
        .flatMapLatest { tab ->
            if (tab == null) {
                return@flatMapLatest flow { emit(emptyList()) }
            }
            with(tab) {
                repository.contentFlow()
            }
        }

    private val _state = _mediaContentFlow.map {
        TabContentState(
            it
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
            val mediaItems = state.itemList

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

data class TabContentState(
    val itemList: List<AudioItemModel> = emptyList()
)