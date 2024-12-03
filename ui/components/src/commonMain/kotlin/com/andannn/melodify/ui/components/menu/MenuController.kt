package com.andannn.melodify.ui.components.menu

import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.getAudios
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import com.andannn.melodify.ui.components.menu.model.SheetModel
import com.andannn.melodify.ui.components.menu.model.SheetOptionItem
import com.andannn.melodify.ui.components.menu.model.SleepTimerOption
import com.andannn.melodify.ui.components.message.MessageController
import com.andannn.melodify.ui.components.message.dialog.Dialog
import com.andannn.melodify.ui.components.message.dialog.InteractionResult
import com.andannn.melodify.ui.components.message.snackbar.SnackBarMessage
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

sealed interface MenuEvent {
    data class OnShowBottomMenu(val sheet: SheetModel) : MenuEvent

    data object OnCancelTimer : MenuEvent

    data class OnMediaOptionClick(
        val sheet: SheetModel.MediaOptionSheet,
        val clickedItem: SheetOptionItem,
    ) : MenuEvent

    data class OnTimerOptionClick(
        val option: SleepTimerOption,
    ) : MenuEvent

    data class OnDismissSheet(val bottomSheet: SheetModel) : MenuEvent

    data object OnShowTimerSheet : MenuEvent

    data class OnToggleFavorite(val audio: AudioItemModel) : MenuEvent

    data class OnAddToPlayList(
        val playList: PlayListItemModel,
        val audioList: List<AudioItemModel>
    ) : MenuEvent

    data class OnCreateNewPlayList(
        val interactingSource: MediaItemModel
    ) : MenuEvent
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

interface MenuController : BottomSheetStateProvider, DeleteMediaItemEventProvider,
    PlaylistCreatedEventProvider {
    fun onEvent(event: MenuEvent)

    fun close()
}

class MenuControllerImpl(
    private val repository: Repository,
    private val messageController: MessageController,
) : MenuController, CoroutineScope {
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


    override fun onEvent(event: MenuEvent) {
        launch {
            Napier.d(tag = TAG) { "onEvent: $event" }
            when (event) {
                is MenuEvent.OnTimerOptionClick -> {
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

                is MenuEvent.OnMediaOptionClick -> {
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

                MenuEvent.OnCancelTimer -> {
                    mediaControllerRepository.cancelSleepTimer()
                    closeSheet()
                }

                is MenuEvent.OnDismissSheet -> {
                    closeSheet()
                }

                is MenuEvent.OnShowBottomMenu -> {
                    _bottomSheetModelFlow.emit(event.sheet)
                }

                MenuEvent.OnShowTimerSheet -> onClickSleepTimer()

                is MenuEvent.OnToggleFavorite -> {
                    playListRepository.toggleFavoriteMedia(event.audio)
                }

                is MenuEvent.OnAddToPlayList -> {
                    closeSheet()
                    onAddToPlayList(event.playList, event.audioList)
                }

                is MenuEvent.OnCreateNewPlayList -> {
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
