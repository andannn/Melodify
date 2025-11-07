/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.usecase

import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.core.data.PlayListRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.internal.SleepTimerRepository
import com.andannn.melodify.core.data.internal.UserPreferenceRepository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.TabKind
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.SleepTimerOption
import com.andannn.melodify.model.SnackBarMessage
import com.andannn.melodify.ui.core.PopupController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first

private const val TAG = "MediaOptionUseCase"

context(userPreferenceRepository: UserPreferenceRepository, popupController: PopupController)
suspend fun MediaItemModel.pinToHomeTab() {
    val tabKind =
        when (this) {
            is AlbumItemModel -> TabKind.ALBUM
            is ArtistItemModel -> TabKind.ARTIST
            is GenreItemModel -> TabKind.GENRE
            is PlayListItemModel -> TabKind.PLAYLIST
            is AudioItemModel -> error("invalid")
        }
    val exist =
        userPreferenceRepository.isTabExist(externalId = id, tabName = name, tabKind = tabKind)
    if (exist) {
        popupController.showSnackBar(SnackBarMessage.TabAlreadyExist)
    } else {
        userPreferenceRepository.addNewCustomTab(externalId = id, tabName = name, tabKind = tabKind)
    }
}

context(repo: Repository, popupController: PopupController)
suspend fun addToNextPlay(items: List<AudioItemModel>) {
    val havePlayingQueue = repo.getPlayListQueue().isNotEmpty()
    if (havePlayingQueue) {
        repo.addMediaItems(
            index = repo.getPlayingIndexInQueue() + 1,
            mediaItems = items,
        )
    } else {
        repo.playMediaList(items, 0)
    }
    popupController.showSnackBar(SnackBarMessage.AddedToPlayNext)
}

context(repo: Repository, popupController: PopupController)
suspend fun addToQueue(items: List<AudioItemModel>) {
    val playListQueue = repo.getPlayListQueue()
    if (playListQueue.isNotEmpty()) {
        repo.addMediaItems(
            index = playListQueue.size,
            mediaItems = items,
        )
    } else {
        repo.playMediaList(items, 0)
    }
    popupController.showSnackBar(SnackBarMessage.AddedToPlayQueue)
}

context(playListRepository: PlayListRepository)
suspend fun deleteItemInPlayList(
    playListId: String,
    source: AudioItemModel,
) {
    playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(source.id))
}

context(repo: Repository)
suspend fun PlayListItemModel.delete() {
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
suspend fun openSleepTimer() {
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
suspend fun addToPlaylist(items: List<AudioItemModel>) {
    Napier.d(tag = TAG) { "addToPlaylist E" }
    val result = popupController.showDialog(DialogId.AddMusicsToPlayListDialog(items))

    Napier.d(tag = TAG) { "AddMusicsToPlayListDialog result: $result" }

    if (result is DialogAction.AddToPlayListDialog.OnAddToPlayList) {
        result.playList.addAll(audioList = result.audios)
    } else if (result is DialogAction.AddToPlayListDialog.OnCreateNewPlayList) {
        createNewPlayList(items)
    }
}

context(deleteHelper: MediaFileDeleteHelper, popupController: PopupController)
suspend fun deleteItems(items: List<AudioItemModel>) {
    if (items.isEmpty()) {
        return
    }

    when (deleteHelper.deleteMedias(items)) {
        MediaFileDeleteHelper.Result.Success -> {
            if (items.size == 1) {
                popupController.showSnackBar(SnackBarMessage.OneDeleteSuccess)
            } else {
                popupController.showSnackBar(SnackBarMessage.MultipleDeleteSuccess(items.size))
            }
        }

        MediaFileDeleteHelper.Result.Failed -> {
            popupController.showSnackBar(SnackBarMessage.DeleteFailed)
        }

        MediaFileDeleteHelper.Result.Denied -> {
            // Noop
        }
    }
}

context(repo: Repository, popupController: PopupController)
private suspend fun createNewPlayList(items: List<AudioItemModel>) {
    val result = popupController.showDialog(DialogId.NewPlayListDialog)
    Napier.d(tag = TAG) { "result. name = $result" }
    if (result is DialogAction.InputDialog.Accept) {
        val name = result.input
        Napier.d(tag = TAG) { "create new playlist start. name = $name" }
        val playListId = repo.createNewPlayList(name)

        Napier.d(tag = TAG) { "playlist created. id = $playListId" }

        repo.addMusicToPlayList(
            playListId = playListId,
            musics = items,
        )

        repo.addNewCustomTab(
            externalId = playListId.toString(),
            tabName = name,
            tabKind = TabKind.PLAYLIST,
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
                message = SnackBarMessage.AddPlayListSuccess(name),
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
