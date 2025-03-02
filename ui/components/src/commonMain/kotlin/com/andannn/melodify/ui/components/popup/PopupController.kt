package com.andannn.melodify.ui.components.popup

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.getAudios
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.repository.UserPreferenceRepository
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogData
import com.andannn.melodify.ui.components.popup.dialog.DialogDataImpl
import com.andannn.melodify.ui.components.popup.dialog.OptionItem
import com.andannn.melodify.ui.components.popup.dialog.SleepTimerOption
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.snackbar.SnackBarMessage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

val LocalPopupController: ProvidableCompositionLocal<PopupController> =
    compositionLocalOf { error("no popup controller") }

private const val TAG = "PopupController"

interface PopupController {
    val currentDialog: DialogData?

    val snackBarMessageChannel: ReceiveChannel<SnackbarVisuals>

    val snackBarResultChannel: SendChannel<SnackbarResult>

    suspend fun showSnackBar(
        message: SnackBarMessage,
        messageFormatArgs: List<Any> = emptyList()
    ): SnackbarResult

    suspend fun showDialog(dialogId: DialogId): DialogAction
}

class PopupControllerImpl : PopupController {
    private val mutex = Mutex()

    private var _currentDialog by mutableStateOf<DialogData?>(null)

    override val currentDialog: DialogData?
        get() = _currentDialog

    override val snackBarMessageChannel: Channel<SnackbarVisuals> = Channel()
    override val snackBarResultChannel: Channel<SnackbarResult> = Channel()

    /**
     * Show snackbar and wait for user interaction.
     */
    override suspend fun showSnackBar(
        message: SnackBarMessage,
        messageFormatArgs: List<Any>
    ): SnackbarResult {
        snackBarMessageChannel.send(message.toSnackbarVisuals(messageFormatArgs))
        return snackBarResultChannel.receive()
    }

    /**
     * Show dialog and wait for user interaction.
     *
     * Dialog show at most one snackbar at a time.
     */
    override suspend fun showDialog(dialogId: DialogId): DialogAction = mutex.withLock {
        Napier.d(tag = TAG) { "show dialog. dialogId = $dialogId" }
        try {
            return suspendCancellableCoroutine { continuation ->
                _currentDialog = DialogDataImpl(dialogId, continuation)
            }
        } finally {
            Napier.d(tag = TAG) { "currentDialog closed = $dialogId" }
            _currentDialog = null
        }
    }
}

suspend fun Repository.onMediaOptionClick(
    optionItem: OptionItem,
    dialog: DialogId.MediaOption,
    popupController: PopupController,
) {
    when (optionItem) {
        OptionItem.PLAY_NEXT -> onPlayNextClick(dialog.source)
        OptionItem.ADD_TO_QUEUE -> onAddToQueue(dialog.source)
        OptionItem.SLEEP_TIMER -> onClickSleepTimer(popupController)
        OptionItem.DELETE_FROM_PLAYLIST -> onDeleteItemInPlayList(
            (dialog as DialogId.AudioOptionInPlayList).playListId,
            dialog.source
        )

        OptionItem.ADD_TO_PLAYLIST -> onAddToPlaylistOptionClick(popupController, dialog.source)
        OptionItem.DELETE_PLAYLIST -> onDeletePlayList(dialog.source as PlayListItemModel)
        OptionItem.ADD_TO_HOME_TAB -> onAddToHomeTab(dialog.source)
        OptionItem.DELETE_TAB -> error("never")
    }
}

private suspend fun Repository.onAddToHomeTab(source: MediaItemModel) {
    val tab = when (source) {
        is AlbumItemModel -> CustomTab.AlbumDetail(source.id, source.name)
        is ArtistItemModel -> CustomTab.ArtistDetail(source.id, source.name)
        is GenreItemModel -> CustomTab.GenreDetail(source.id, source.name)
        is PlayListItemModel -> CustomTab.PlayListDetail(source.id, source.name)
        is AudioItemModel -> error("invalid")
    }
    userPreferenceRepository.addNewCustomTab(tab)
}

private suspend fun Repository.onPlayNextClick(source: MediaItemModel) {
    val items = getAudios(source)
    val havePlayingQueue = playerStateMonitoryRepository.getPlayListQueue().isNotEmpty()
    if (havePlayingQueue) {
        mediaControllerRepository.addMediaItems(
            index = playerStateMonitoryRepository.getPlayingIndexInQueue() + 1,
            mediaItems = items
        )
    } else {
        mediaControllerRepository.playMediaList(items, 0)
    }
}

private suspend fun Repository.onAddToQueue(source: MediaItemModel) {
    val items = getAudios(source)
    val playListQueue = playerStateMonitoryRepository.getPlayListQueue()
    if (playListQueue.isNotEmpty()) {
        mediaControllerRepository.addMediaItems(
            index = playListQueue.size,
            mediaItems = items
        )
    } else {
        mediaControllerRepository.playMediaList(items, 0)
    }
}

private suspend fun Repository.onDeleteItemInPlayList(playListId: String, source: AudioItemModel) {
    playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(source.id))
}

private suspend fun Repository.onDeletePlayList(playListItemModel: PlayListItemModel) {
    playListRepository.deletePlayList(playListItemModel.id.toLong())

    val currentCustomTabs = userPreferenceRepository.currentCustomTabsFlow.first()
    val deletedTab =
        currentCustomTabs.firstOrNull { it is CustomTab.PlayListDetail && it.playListId == playListItemModel.id }

    Napier.d(tag = TAG) { "deletedTab $deletedTab" }
    if (deletedTab != null) {
        userPreferenceRepository.deleteCustomTab(deletedTab)
    }
}

private suspend fun Repository.onClickSleepTimer(popupController: PopupController) {
    if (sleepTimerRepository.isCounting()) {
        val result = popupController.showDialog(DialogId.SleepCountingDialog)
        if (result is DialogAction.SleepTimerCountingDialog.OnCancelTimer) {
            sleepTimerRepository.cancelSleepTimer()
        }
    } else {
        val result = popupController.showDialog(DialogId.SleepTimerOptionDialog)
        if (result is DialogAction.SleepTimerOptionDialog.OnOptionClick) {
            when (val option = result.option) {
                SleepTimerOption.FIVE_MINUTES,
                SleepTimerOption.FIFTEEN_MINUTES,
                SleepTimerOption.THIRTY_MINUTES,
                SleepTimerOption.SIXTY_MINUTES -> {
                    sleepTimerRepository.startSleepTimer(option.timeMinutes!!)
                }

                SleepTimerOption.SONG_FINISH -> {
// TODO:
//                        val duration = mediaControllerRepository.duration!!
//                        mediaControllerRepository.startSleepTimer(duration.milliseconds)
                }
            }
        }
    }
}

private suspend fun Repository.onAddToPlaylistOptionClick(
    popupController: PopupController,
    source: MediaItemModel
) {
    val result = popupController.showDialog(DialogId.AddToPlayListDialog(source))

    if (result is DialogAction.AddToPlayListDialog.OnAddToPlayList) {
        onAddToPlayList(
            playList = result.playList,
            audioList = result.audios,
            popupController = popupController
        )
    } else if (result is DialogAction.AddToPlayListDialog.OnCreateNewPlayList) {
        onCreateNewPlayList(popupController = popupController, source = source)
    }
}

private suspend fun Repository.onCreateNewPlayList(
    source: MediaItemModel,
    popupController: PopupController
) {
    val result = popupController.showDialog(DialogId.NewPlayListDialog)
    Napier.d(tag = TAG) { "result. name = $result" }
    if (result is DialogAction.InputDialog.Accept) {
        val name = result.input
        Napier.d(tag = TAG) { "create new playlist start. name = $name" }
        val playListId = playListRepository.createNewPlayList(name)

        Napier.d(tag = TAG) { "playlist created. id = $playListId" }

        playListRepository.addMusicToPlayList(
            playListId = playListId,
            musics = getAudios(source)
        )

        userPreferenceRepository.addNewCustomTab(
            CustomTab.PlayListDetail(
                playListId.toString(),
                name
            )
        )
    }
}

private suspend fun Repository.onAddToPlayList(
    popupController: PopupController,
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

            popupController.showSnackBar(
                message = SnackBarMessage.AddPlayListSuccess,
                messageFormatArgs = listOf(playList.name),
            )
        }

        duplicatedMedias.size == 1 -> {
            // Show error toast message
            popupController.showSnackBar(SnackBarMessage.AddPlayListFailed)
        }

        else -> {
            // invalidList.size > 1, Show alert message
            val result =
                popupController.showDialog(DialogId.DuplicatedAlert)

            if (result is DialogAction.AlertDialog.Accept) {
                playListRepository.addMusicToPlayList(
                    playListId = playList.id.toLong(),
                    musics = audioList.filter { it.id !in duplicatedMedias }
                )
            }
        }
    }
}