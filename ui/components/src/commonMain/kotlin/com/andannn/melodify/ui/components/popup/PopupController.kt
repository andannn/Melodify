package com.andannn.melodify.ui.components.popup

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.getAudios
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.ui.components.popup.dialog.OptionItem
import com.andannn.melodify.ui.components.popup.dialog.SleepTimerOption
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.snackbar.SnackBarMessage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

private const val TAG = "DrawerController"

sealed interface DialogAction {
    data object Dismissed : DialogAction

    interface AlertDialog : DialogAction {
        data object Accept : AlertDialog

        data object Decline : AlertDialog
    }

    interface NewPlaylistDialog : DialogAction {
        data class Accept(val playlistName: String) : NewPlaylistDialog

        object Decline : NewPlaylistDialog
    }

    interface MediaOptionDialog : DialogAction {
        data class ClickItem(val optionItem: OptionItem, val dialog: DialogId.MediaOption) :
            MediaOptionDialog
    }

    interface AddToPlayListDialog : DialogAction {
        data class OnAddToPlayList(
            val playList: PlayListItemModel,
            val audios: List<AudioItemModel>
        ) :
            AddToPlayListDialog

        object OnCreateNewPlayList : AddToPlayListDialog
    }

    interface SleepTimerOptionDialog : DialogAction {
        data class OnOptionClick(val option: SleepTimerOption) : SleepTimerOptionDialog
    }

    interface SleepTimerCountingDialog : DialogAction {
        data object OnCancelTimer : SleepTimerCountingDialog
    }
}

interface DialogData {
    val dialogId: DialogId

    fun performAction(action: DialogAction)
}

class DialogDataImpl(
    override val dialogId: DialogId,
    private val continuation: CancellableContinuation<DialogAction>,
) : DialogData {
    override fun performAction(action: DialogAction) {
        if (continuation.isActive) continuation.resume(action)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DialogDataImpl

        if (dialogId != other.dialogId) return false
        if (continuation != other.continuation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dialogId.hashCode()
        result = 31 * result + continuation.hashCode()
        return result
    }
}

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
        try {
            return suspendCancellableCoroutine { continuation ->
                _currentDialog = DialogDataImpl(dialogId, continuation)
            }
        } finally {
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
    }
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
    if (result is DialogAction.NewPlaylistDialog.Accept) {
        val name = result.playlistName
        Napier.d(tag = TAG) { "create new playlist start. name = $name" }
        val playListId = playListRepository.createNewPlayList(name)

        Napier.d(tag = TAG) { "playlist created. id = $playListId" }

        playListRepository.addMusicToPlayList(
            playListId = playListId,
            musics = getAudios(source)
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