package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.onMediaOptionClick
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

private const val TAG = "TabContentPresenter"

@Composable
fun rememberTabContentPresenter(
    selectedTab: CustomTab?,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
) = remember(
    selectedTab,
    repository,
    popupController,
) {
    TabContentPresenter(
        selectedTab,
        repository,
        popupController,
    )
}

class TabContentPresenter(
    private val selectedTab: CustomTab?,
    private val repository: Repository,
    private val popupController: PopupController,
) : Presenter<TabContentState> {
    private val mediaControllerRepository = repository.mediaControllerRepository
    private val playListRepository = repository.playListRepository

    init {
        Napier.d(tag = TAG) { "TabContentPresenter init $selectedTab" }
    }

    @Composable
    override fun present(): TabContentState {
        val scope = rememberCoroutineScope()
        Napier.d(tag = TAG) { "TabContentPresenter scope ${scope.hashCode()}" }

        val groupType by rememberRetained {
            mutableStateOf(GroupType.ALBUM)
        }
        val audioList by getContentFlow().collectAsRetainedState(emptyList())

        Napier.d(tag = TAG) { "TabContentPresenter audioList ${audioList.size}" }
        val contentMap =
            rememberRetained(groupType, audioList) {
                audioList.toMap(groupType)
            }

        Napier.d(tag = TAG) { "TabContentPresenter contentMap ${contentMap.size}" }

        DisposableEffect(Unit) {
            onDispose {
                Napier.d(tag = TAG) { "TabContentPresenter onDispose $selectedTab" }
            }
        }

        return TabContentState(contentMap) { eventSink ->
            when (eventSink) {
                is TabContentEvent.OnPlayMusic ->
                    scope.launch {
                        playMusic(
                            eventSink.mediaItemModel,
                            allAudios = contentMap.values.flatten(),
                        )
                    }

                is TabContentEvent.OnShowMusicItemOption ->
                    scope.launch {
                        onShowMusicItemOption(
                            eventSink.mediaItemModel,
                        )
                    }
            }
        }
    }

    private fun getContentFlow(): Flow<List<AudioItemModel>> {
        if (selectedTab == null) {
            return flow { emit(emptyList()) }
        }
        return with(selectedTab) {
            repository.contentFlow()
        }
    }

    private suspend fun playMusic(
        mediaItem: AudioItemModel,
        allAudios: List<AudioItemModel>,
    ) {
        if (mediaItem.isValid()) {
            mediaControllerRepository.playMediaList(
                allAudios.toList(),
                allAudios.indexOf(mediaItem),
            )
        } else {
            Napier.d(tag = TAG) { "invalid media item click $mediaItem" }
            val result =
                popupController.showDialog(DialogId.ConfirmDeletePlaylist)
            Napier.d(tag = TAG) { "ConfirmDeletePlaylist result: $result" }
            if (result == DialogAction.AlertDialog.Accept) {
                val playListId = (selectedTab as CustomTab.PlayListDetail).playListId
                val mediaId = mediaItem.id.substringAfter(AudioItemModel.INVALID_ID_PREFIX)

                playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(mediaId))
            }
        }
    }

    private suspend fun onShowMusicItemOption(mediaItemModel: MediaItemModel) {
        val currentTab = selectedTab
        val result =
            if (mediaItemModel is AudioItemModel && currentTab is CustomTab.PlayListDetail) {
                popupController.showDialog(
                    DialogId.AudioOptionInPlayList(
                        playListId = currentTab.playListId,
                        mediaItemModel,
                    ),
                )
            } else {
                popupController.showDialog(
                    DialogId.MediaOption.fromMediaModel(
                        item = mediaItemModel,
                    ),
                )
            }
        if (result is DialogAction.MediaOptionDialog.ClickItem) {
            repository.onMediaOptionClick(
                optionItem = result.optionItem,
                dialog = result.dialog,
                popupController = popupController,
            )
        }
    }
}

data class HeaderKey(
    val groupType: GroupType,
    val headerId: String?,
)

data class TabContentState(
    val contentMap: Map<HeaderKey, List<AudioItemModel>> = emptyMap(),
    val eventSink: (TabContentEvent) -> Unit = {},
) : CircuitUiState

sealed interface TabContentEvent {
    data class OnShowMusicItemOption(val mediaItemModel: MediaItemModel) : TabContentEvent

    data class OnPlayMusic(val mediaItemModel: AudioItemModel) : TabContentEvent
}

enum class GroupType {
    ARTIST,
    ALBUM,
    NONE,
}

private fun List<AudioItemModel>.toMap(groupType: GroupType): Map<HeaderKey, List<AudioItemModel>> {
    return this.groupBy {
        it.keyOf(groupType)
    }.mapValues {
        it.value.sortBy(groupType)
    }
}

fun AudioItemModel.keyOf(groupType: GroupType) =
    when (groupType) {
        GroupType.ARTIST -> HeaderKey(groupType, artistId)
        GroupType.ALBUM -> HeaderKey(groupType, albumId)
        GroupType.NONE -> HeaderKey(groupType, null)
    }

fun List<AudioItemModel>.sortBy(groupType: GroupType) =
    when (groupType) {
        GroupType.ARTIST -> sortedBy { it.name }
        GroupType.ALBUM -> sortedBy { it.cdTrackNumber }
        GroupType.NONE -> this
    }
