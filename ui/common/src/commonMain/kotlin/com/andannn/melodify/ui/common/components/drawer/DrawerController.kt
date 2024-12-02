package com.andannn.melodify.ui.common.components.drawer

import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.getAudios
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.ui.common.components.drawer.model.SheetModel
import com.andannn.melodify.ui.common.components.drawer.model.SheetOptionItem
import com.andannn.melodify.ui.common.components.drawer.model.SleepTimerOption
import com.andannn.melodify.ui.common.components.message.MessageController
import com.andannn.melodify.ui.common.components.message.dialog.Dialog
import com.andannn.melodify.ui.common.components.message.dialog.InteractionResult
import com.andannn.melodify.ui.common.components.message.snackbar.SnackBarMessage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val TAG = "DrawerController"

sealed interface DrawerEvent {
    data class OnShowBottomDrawer(val sheet: SheetModel) : DrawerEvent

    data object OnCancelTimer : DrawerEvent

    data class OnMediaOptionClick(
        val sheet: SheetModel.MediaOptionSheet,
        val clickedItem: SheetOptionItem,
    ) : DrawerEvent

    data class OnTimerOptionClick(
        val option: SleepTimerOption,
    ) : DrawerEvent

    data class OnDismissSheet(val bottomSheet: SheetModel) : DrawerEvent

    data object OnShowTimerSheet : DrawerEvent

    data class OnToggleFavorite(val audio: AudioItemModel) : DrawerEvent

    data class OnAddToPlayList(
        val playList: PlayListItemModel,
        val audioList: List<AudioItemModel>
    ) : DrawerEvent

    data class OnCreateNewPlayList(
        val interactingSource: MediaItemModel
    ) : DrawerEvent
}

interface DeleteMediaItemEventProvider {
    val deleteMediaItemEventFlow: SharedFlow<List<String>>
}

interface BottomSheetStateProvider {
    val bottomSheetModel: SharedFlow<SheetModel?>
}

interface PlaylistCreatedEventProvider {
    val playlistCreatedEventChannel: ReceiveChannel<Long>
}

interface DrawerController : BottomSheetStateProvider, DeleteMediaItemEventProvider,
    PlaylistCreatedEventProvider {
    fun onEvent(event: DrawerEvent)

    fun close()
}

class DrawerControllerImpl(
    private val repository: Repository,
    private val messageController: MessageController,
) : DrawerController, CoroutineScope {
    private val mediaControllerRepository: MediaControllerRepository =
        repository.mediaControllerRepository
    private val playerStateMonitoryRepository: PlayerStateMonitoryRepository =
        repository.playerStateMonitoryRepository
    private val playListRepository = repository.playListRepository

    private val _bottomSheetModelFlow = MutableSharedFlow<SheetModel?>()
    private val _deleteMediaItemEventFlow = MutableSharedFlow<List<String>>()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    override val bottomSheetModel: SharedFlow<SheetModel?> = _bottomSheetModelFlow.asSharedFlow()

    override val deleteMediaItemEventFlow: SharedFlow<List<String>> =
        _deleteMediaItemEventFlow.asSharedFlow()

    private val _playlistCreatedEventChannel = Channel<Long>()

    override val playlistCreatedEventChannel: ReceiveChannel<Long>
        get() = _playlistCreatedEventChannel


    override fun onEvent(event: DrawerEvent) {
        launch {
            Napier.d(tag = TAG) { "onEvent: $event" }
            when (event) {
                is DrawerEvent.OnTimerOptionClick -> {
                    closeSheet()
                    when (val option = event.option) {
                        SleepTimerOption.FIVE_MINUTES,
                        SleepTimerOption.FIFTEEN_MINUTES,
                        SleepTimerOption.THIRTY_MINUTES,
                        SleepTimerOption.SIXTY_MINUTES -> {
                            mediaControllerRepository.startSleepTimer(option.timeMinutes!!)
                        }

                        SleepTimerOption.SONG_FINISH -> {
// TODO:
//                        val duration = mediaControllerRepository.duration!!
//                        mediaControllerRepository.startSleepTimer(duration.milliseconds)
                        }
                    }
                }

                is DrawerEvent.OnMediaOptionClick -> {
                    closeSheet()
                    event.clickedItem.let {
                        when (it) {
                            SheetOptionItem.PLAY_NEXT -> onPlayNextClick(event.sheet.source)
                            SheetOptionItem.DELETE_LOCAL -> onDeleteMediaItem(event.sheet.source)
                            SheetOptionItem.ADD_TO_QUEUE -> onAddToQueue(event.sheet.source)
                            SheetOptionItem.SLEEP_TIMER -> onClickSleepTimer()
                            SheetOptionItem.DELETE_FROM_PLAYLIST -> onDeleteItemInPlayList(
                                (event.sheet as SheetModel.AudioOptionInPlayListSheet).playListId,
                                event.sheet.source
                            )

                            SheetOptionItem.ADD_TO_PLAYLIST -> onAddToPlaylistOptionClick(event.sheet.source)
                            SheetOptionItem.DELETE_PLAYLIST -> onDeletePlayList(event.sheet.source as PlayListItemModel)
                        }
                    }
                }

                DrawerEvent.OnCancelTimer -> {
                    mediaControllerRepository.cancelSleepTimer()
                    closeSheet()
                }

                is DrawerEvent.OnDismissSheet -> {
                    closeSheet()
                }

                is DrawerEvent.OnShowBottomDrawer -> {
                    _bottomSheetModelFlow.emit(event.sheet)
                }

                DrawerEvent.OnShowTimerSheet -> onClickSleepTimer()

                is DrawerEvent.OnToggleFavorite -> {
                    playListRepository.toggleFavoriteMedia(event.audio)
                }

                is DrawerEvent.OnAddToPlayList -> {
                    closeSheet()
                    onAddToPlayList(event.playList, event.audioList)
                }

                is DrawerEvent.OnCreateNewPlayList -> {
                    closeSheet()
                    onCreateNewPlayList(event.interactingSource)
                }
            }
        }
    }

    override fun close() {
        Napier.d(tag = TAG) { "scope is closed" }
        this.cancel()
    }

    private suspend fun onAddToPlayList(
        playList: PlayListItemModel,
        audioList: List<AudioItemModel>
    ) {
        val duplicatedMedias = playListRepository.getDuplicatedMediaInPlayList(
            playListId = playList.id.toLong(),
            musics = audioList
        )

        Napier.d(tag = TAG) { "onAddToPlayList. duplicated Medias: $duplicatedMedias" }
        when {
            duplicatedMedias.isEmpty() -> {
                playListRepository.addMusicToPlayList(
                    playListId = playList.id.toLong(),
                    musics = audioList
                )

                messageController.showSnackBarAndWaitResult(
                    message = SnackBarMessage.AddPlayListSuccess,
                    messageFormatArgs = listOf(playList.name),
                )
            }

            duplicatedMedias.size == 1 -> {
                // Show error toast message
                messageController.showSnackBarAndWaitResult(SnackBarMessage.AddPlayListFailed)
            }

            else -> {
                // invalidList.size > 1, Show alert message
                val result =
                    messageController.showMessageDialogAndWaitResult(Dialog.DuplicatedAlert)

                if (result == InteractionResult.AlertDialog.ACCEPT) {
                    playListRepository.addMusicToPlayList(
                        playListId = playList.id.toLong(),
                        musics = audioList.filter { it.id !in duplicatedMedias }
                    )
                }
            }
        }
    }

    private suspend fun onAddToPlaylistOptionClick(source: MediaItemModel) {
        _bottomSheetModelFlow.emit(SheetModel.AddToPlayListSheet(source))
    }

    private suspend fun onDeleteItemInPlayList(playListId: String, source: AudioItemModel) {
        playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(source.id))
    }

    private suspend fun closeSheet() {
        _bottomSheetModelFlow.emit(null)
    }

    private suspend fun onClickSleepTimer() {
        if (mediaControllerRepository.isCounting()) {
            _bottomSheetModelFlow.emit(SheetModel.TimerRemainTimeSheet)
        } else {
            _bottomSheetModelFlow.emit(SheetModel.TimerOptionSheet)
        }
    }

    private suspend fun onDeleteMediaItem(source: MediaItemModel) {
        val items = repository.getAudios(source)
        val uris = items.map { it.source }

        _deleteMediaItemEventFlow.emit(uris)
    }

    private suspend fun onPlayNextClick(source: MediaItemModel) {
        val items = repository.getAudios(source)
        val havePlayingQueue = playerStateMonitoryRepository.playListQueue.isNotEmpty()
        if (havePlayingQueue) {
            mediaControllerRepository.addMediaItems(
                index = playerStateMonitoryRepository.playingIndexInQueue + 1,
                mediaItems = items
            )
        } else {
            mediaControllerRepository.playMediaList(items, 0)
        }
    }

    private suspend fun onAddToQueue(source: MediaItemModel) {
        val items = repository.getAudios(source)
        val playListQueue = playerStateMonitoryRepository.playListQueue
        if (playListQueue.isNotEmpty()) {
            mediaControllerRepository.addMediaItems(
                index = playListQueue.size,
                mediaItems = items
            )
        } else {
            mediaControllerRepository.playMediaList(items, 0)
        }
    }

    private suspend fun onCreateNewPlayList(interactingSource: MediaItemModel) {
        val result = messageController.showMessageDialogAndWaitResult(Dialog.NewPlayListDialog)

        if (result is InteractionResult.NewPlaylistDialog.ACCEPT) {
            val name = result.playlistName
            Napier.d(tag = TAG) { "create new playlist start. name = $name" }

            val playListId = playListRepository.createNewPlayList(name)

            Napier.d(tag = TAG) { "playlist created. id = $playListId" }

            playListRepository.addMusicToPlayList(
                playListId = playListId,
                musics = repository.getAudios(interactingSource)
            )

            _playlistCreatedEventChannel.send(playListId)
        }
    }

    private suspend fun onDeletePlayList(playListItemModel: PlayListItemModel) {
        playListRepository.deletePlayList(playListItemModel.id.toLong())
    }
}
