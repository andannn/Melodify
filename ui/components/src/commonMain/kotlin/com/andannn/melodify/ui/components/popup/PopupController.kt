/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.popup

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.audios
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.repository.SleepTimerRepository
import com.andannn.melodify.core.data.repository.UserPreferenceRepository
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogData
import com.andannn.melodify.ui.components.popup.dialog.DialogDataImpl
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.dialog.OptionItem
import com.andannn.melodify.ui.components.popup.dialog.SleepTimerOption
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

    var snackBarController: SnackbarHostState?

    suspend fun showSnackBar(
        message: SnackBarMessage,
        messageFormatArgs: List<Any> = emptyList(),
    ): SnackbarResult

    suspend fun showDialog(dialogId: DialogId): DialogAction
}

@Composable
fun rememberAndSetupSnackBarHostState(holder: PopupController = LocalPopupController.current): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }

    DisposableEffect(snackbarHostState) {
        holder.snackBarController = snackbarHostState

        onDispose {
            holder.snackBarController = null
        }
    }

    return snackbarHostState
}

class NoOpPopupController : PopupController {
    override val currentDialog: DialogData =
        object : DialogData {
            override val dialogId: DialogId
                get() = DialogId.SleepCountingDialog

            override fun performAction(action: DialogAction) {
            }
        }
    override var snackBarController: SnackbarHostState? = null

    override suspend fun showSnackBar(
        message: SnackBarMessage,
        messageFormatArgs: List<Any>,
    ): SnackbarResult = SnackbarResult.Dismissed

    override suspend fun showDialog(dialogId: DialogId): DialogAction = DialogAction.Dismissed
}

class PopupControllerImpl : PopupController {
    private val mutex = Mutex()

    private var _currentDialog by mutableStateOf<DialogData?>(null)

    override val currentDialog: DialogData?
        get() = _currentDialog

    override var snackBarController: SnackbarHostState? = null

    /**
     * Show snackbar and wait for user interaction.
     */
    override suspend fun showSnackBar(
        message: SnackBarMessage,
        messageFormatArgs: List<Any>,
    ): SnackbarResult =
        snackBarController?.showSnackbar(message.toSnackbarVisuals(messageFormatArgs))
            ?: error("Snackbar HostState is not setup. ")

    /**
     * Show dialog and wait for user interaction.
     *
     * Dialog show at most one snackbar at a time.
     */
    override suspend fun showDialog(dialogId: DialogId): DialogAction =
        mutex.withLock {
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

context(
    repo: Repository,
    popupController: PopupController,
)
suspend fun handleMediaOptionClick(
    optionItem: OptionItem,
    dialog: DialogId.MediaOption,
) {
    when (optionItem) {
        OptionItem.PLAY_NEXT -> dialog.media.addToNextPlay()
        OptionItem.ADD_TO_QUEUE -> dialog.media.addToQueue()
        OptionItem.SLEEP_TIMER -> handleClickSleepTimer()
        OptionItem.DELETE_FROM_PLAYLIST ->
            onDeleteItemInPlayList(
                (dialog as DialogId.AudioOptionInPlayList).playListId,
                dialog.media,
            )

        OptionItem.ADD_TO_PLAYLIST -> onAddToPlaylistOptionClick(dialog.media)
        OptionItem.DELETE_PLAYLIST -> (dialog.media as PlayListItemModel).delete()
        OptionItem.ADD_TO_HOME_TAB -> dialog.media.addToHomeTab()
        OptionItem.DELETE_TAB -> error("never")
    }
}

context(userPreferenceRepository: UserPreferenceRepository, popupController: PopupController)
private suspend fun MediaItemModel.addToHomeTab() {
    val tab =
        when (this) {
            is AlbumItemModel -> CustomTab.AlbumDetail(id, name)
            is ArtistItemModel -> CustomTab.ArtistDetail(id, name)
            is GenreItemModel -> CustomTab.GenreDetail(id, name)
            is PlayListItemModel -> CustomTab.PlayListDetail(id, name)
            is AudioItemModel -> error("invalid")
        }
    val current = userPreferenceRepository.currentCustomTabsFlow.first()
    if (current.contains(tab)) {
        popupController.showSnackBar(SnackBarMessage.TabAlreadyExist)
    } else {
        userPreferenceRepository.addNewCustomTab(tab)
    }
}

context(repo: Repository)
private suspend fun MediaItemModel.addToNextPlay() {
    val havePlayingQueue = repo.getPlayListQueue().isNotEmpty()
    if (havePlayingQueue) {
        repo.addMediaItems(
            index = repo.getPlayingIndexInQueue() + 1,
            mediaItems = audios(),
        )
    } else {
        repo.playMediaList(audios(), 0)
    }
}

context(repo: Repository)
private suspend fun MediaItemModel.addToQueue() {
    val playListQueue = repo.getPlayListQueue()
    if (playListQueue.isNotEmpty()) {
        repo.addMediaItems(
            index = playListQueue.size,
            mediaItems = audios(),
        )
    } else {
        repo.playMediaList(audios(), 0)
    }
}

context(playListRepository: PlayListRepository)
private suspend fun onDeleteItemInPlayList(
    playListId: String,
    source: AudioItemModel,
) {
    playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(source.id))
}

context(repo: Repository)
private suspend fun PlayListItemModel.delete() {
    repo.deletePlayList(id.toLong())

    val currentCustomTabs = repo.currentCustomTabsFlow.first()
    val deletedTab =
        currentCustomTabs.firstOrNull { it is CustomTab.PlayListDetail && it.playListId == id }

    Napier.d(tag = TAG) { "deletedTab $deletedTab" }
    if (deletedTab != null) {
        repo.deleteCustomTab(deletedTab)
    }
}

context(sleepTimerRepository: SleepTimerRepository, popupController: PopupController)
private suspend fun handleClickSleepTimer() {
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
                SleepTimerOption.SIXTY_MINUTES,
                -> {
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

context(repo: Repository, popupController: PopupController)
private suspend fun onAddToPlaylistOptionClick(source: MediaItemModel) {
    val result = popupController.showDialog(DialogId.AddToPlayListDialog(source))

    if (result is DialogAction.AddToPlayListDialog.OnAddToPlayList) {
        result.playList.addAll(audioList = result.audios)
    } else if (result is DialogAction.AddToPlayListDialog.OnCreateNewPlayList) {
        createNewPlayListFromSource(source = source)
    }
}

context(repo: Repository, popupController: PopupController)
private suspend fun createNewPlayListFromSource(source: MediaItemModel) {
    val result = popupController.showDialog(DialogId.NewPlayListDialog)
    Napier.d(tag = TAG) { "result. name = $result" }
    if (result is DialogAction.InputDialog.Accept) {
        val name = result.input
        Napier.d(tag = TAG) { "create new playlist start. name = $name" }
        val playListId = repo.createNewPlayList(name)

        Napier.d(tag = TAG) { "playlist created. id = $playListId" }

        repo.addMusicToPlayList(
            playListId = playListId,
            musics = source.audios(),
        )

        repo.addNewCustomTab(
            CustomTab.PlayListDetail(
                playListId.toString(),
                name,
            ),
        )
    }
}

context(playListRepository: PlayListRepository, popupController: PopupController)
private suspend fun PlayListItemModel.addAll(audioList: List<AudioItemModel>) {
    val duplicatedMedias =
        playListRepository.getDuplicatedMediaInPlayList(
            playListId = id.toLong(),
            musics = audioList,
        )

    Napier.d(tag = TAG) { "onAddToPlayList. duplicated Medias: $duplicatedMedias" }
    when {
        duplicatedMedias.isEmpty() -> {
            playListRepository.addMusicToPlayList(
                playListId = id.toLong(),
                musics = audioList,
            )

            popupController.showSnackBar(
                message = SnackBarMessage.AddPlayListSuccess,
                messageFormatArgs = listOf(name),
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
                    playListId = id.toLong(),
                    musics = audioList.filter { it.id !in duplicatedMedias },
                )
            }
        }
    }
}
