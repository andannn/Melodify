package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.onMediaOptionClick
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

private const val TAG = "TabContentPresenter"

class TabContentPresenter(
    private val selectedTab: CustomTab?,
    private val repository: Repository,
    private val popupController: PopupController?,
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
        val contentMap = remember {
            mutableStateMapOf<MediaItemModel, List<AudioItemModel>>()
        }

        scope.launch {
            getContentFlow().collect {
                Napier.d(tag = TAG) { "TabContentPresenter update" }
                contentMap.clear()
                contentMap.putAll(it)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                Napier.d(tag = TAG) { "TabContentPresenter onDispose $selectedTab" }
            }
        }

        return TabContentState(contentMap) { eventSink ->
            when (eventSink) {
                is TabContentEvent.OnPlayMusic -> scope.launch { playMusic(eventSink.mediaItemModel, allAudios = contentMap.values.flatten()) }
                is TabContentEvent.OnShowMusicItemOption -> scope.launch {
                    onShowMusicItemOption(
                        eventSink.mediaItemModel
                    )
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getContentFlow(): Flow<Map<MediaItemModel, List<AudioItemModel>>> {
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
                            repository.mediaContentRepository.getAlbumByAlbumId(id)
                                ?: AlbumItemModel(
                                    id = "unknown",
                                    name = "unknown",
                                    trackCount = 0,
                                    artWorkUri = "",
                                )
                        }
                        .mapValues {
                            it.value.sortedBy { audio ->
                                audio.cdTrackNumber
                            }
                        }
                }
        }
    }

    private suspend fun playMusic(mediaItem: AudioItemModel, allAudios: List<AudioItemModel>) {
        if (mediaItem.isValid()) {
            mediaControllerRepository.playMediaList(
                allAudios.toList(),
                allAudios.indexOf(mediaItem)
            )
        } else {
            Napier.d(tag = TAG) { "invalid media item click $mediaItem" }
            val result =
                popupController?.showDialog(DialogId.ConfirmDeletePlaylist)
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
                popupController?.showDialog(
                    DialogId.AudioOptionInPlayList(
                        playListId = currentTab.playListId,
                        mediaItemModel
                    )
                )
            } else {
                popupController?.showDialog(
                    DialogId.MediaOption.fromMediaModel(
                        item = mediaItemModel,
                    )
                )
            }
        if (result is DialogAction.MediaOptionDialog.ClickItem) {
            repository.onMediaOptionClick(
                optionItem = result.optionItem,
                dialog = result.dialog,
                popupController = popupController
            )
        }
    }
}

//class TabContentStateHolder(
//    val selectedTab: CustomTab?,
//    private val repository: Repository,
//    private val scope: CoroutineScope,
//    private val popupController: PopupController,
//) {
//    private val mediaControllerRepository = repository.mediaControllerRepository
//    private val playListRepository = repository.playListRepository
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val _mediaContentFlow: Flow<Map<MediaItemModel, List<AudioItemModel>>>
//        get() {
//            if (selectedTab == null) {
//                return flow { emit(emptyMap()) }
//            }
//            val contentsFlow = with(selectedTab) {
//                repository.contentFlow()
//            }
//            return contentsFlow.mapLatest { contents ->
//                contents
//                    .groupBy(
//                        keySelector = {
//                            it.albumId
//                        },
//                    )
//                    .let { idToContentsMap ->
//                        idToContentsMap
//                            .mapKeys { (id, _) ->
//                                repository.mediaContentRepository.getAlbumByAlbumId(id)
//                                    ?: AlbumItemModel(
//                                        id = "unknown",
//                                        name = "unknown",
//                                        trackCount = 0,
//                                        artWorkUri = "",
//                                    )
//                            }
//                            .mapValues {
//                                it.value.sortedBy { audio ->
//                                    audio.cdTrackNumber
//                                }
//                            }
//                    }
//            }
//        }
//
//    private val _state =
//        _mediaContentFlow.map { contentMap ->
//            TabContentState(
//                contentMap
//            )
//        }
//
//    var state by mutableStateOf(TabContentState())
//        private set
//
//    init {
//        scope.launch {
//            _state.collect {
//                state = it
//            }
//        }
//    }
//
//    fun playMusic(mediaItem: AudioItemModel) {
//        if (mediaItem.isValid()) {
//            val mediaItems = state.allAudio
//
//            mediaControllerRepository.playMediaList(
//                mediaItems.toList(),
//                mediaItems.indexOf(mediaItem)
//            )
//        } else {
//            Napier.d(tag = TAG) { "invalid media item click $mediaItem" }
//            scope.launch {
//                val result =
//                    popupController.showDialog(DialogId.ConfirmDeletePlaylist)
//                Napier.d(tag = TAG) { "ConfirmDeletePlaylist result: $result" }
//                if (result == DialogAction.AlertDialog.Accept) {
//                    val playListId = (selectedTab as CustomTab.PlayListDetail).playListId
//                    val mediaId = mediaItem.id.substringAfter(AudioItemModel.INVALID_ID_PREFIX)
//
//                    playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(mediaId))
//                }
//            }
//        }
//    }
//
//    fun onShowMusicItemOption(mediaItemModel: MediaItemModel) {
//        val currentTab = selectedTab
//        scope.launch {
//            val result =
//                if (mediaItemModel is AudioItemModel && currentTab is CustomTab.PlayListDetail) {
//                    popupController.showDialog(
//                        DialogId.AudioOptionInPlayList(
//                            playListId = currentTab.playListId,
//                            mediaItemModel
//                        )
//                    )
//                } else {
//                    popupController.showDialog(
//                        DialogId.MediaOption.fromMediaModel(
//                            item = mediaItemModel,
//                        )
//                    )
//                }
//            if (result is DialogAction.MediaOptionDialog.ClickItem) {
//                repository.onMediaOptionClick(
//                    optionItem = result.optionItem,
//                    dialog = result.dialog,
//                    popupController = popupController
//                )
//            }
//        }
//    }
//}

data class TabContentState(
    val contentMap: Map<MediaItemModel, List<AudioItemModel>> = emptyMap(),
    val eventSink: (TabContentEvent) -> Unit = {},
) : CircuitUiState

sealed interface TabContentEvent {
    data class OnShowMusicItemOption(val mediaItemModel: MediaItemModel) : TabContentEvent

    data class OnPlayMusic(val mediaItemModel: AudioItemModel) : TabContentEvent
}

//@Composable
//fun rememberTabContentStateHolder(
//    selectedTab: CustomTab?,
//    repository: Repository = getKoin().get(),
//    scope: CoroutineScope = rememberCoroutineScope(),
//    popupController: PopupController = LocalPopupController.current,
//) = remember(
//    selectedTab,
//    repository,
//    popupController,
//) {
//    TabContentStateHolder(
//        selectedTab = selectedTab,
//        repository = repository,
//        scope = scope,
//        popupController = popupController,
//    )
//}
