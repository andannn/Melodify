/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.usecase

import com.andannn.melodify.core.data.MediaFileDeleteHelper
import com.andannn.melodify.core.data.PlayListRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.SleepTimerRepository
import com.andannn.melodify.core.data.UserPreferenceRepository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.TabKind
import com.andannn.melodify.core.data.model.VideoItemModel
import com.andannn.melodify.shared.compose.popup.AddMusicsToPlayListDialog
import com.andannn.melodify.shared.compose.popup.DialogAction
import com.andannn.melodify.shared.compose.popup.NewPlayListDialog
import com.andannn.melodify.shared.compose.popup.PopupController
import com.andannn.melodify.shared.compose.popup.SleepCountingDialog
import com.andannn.melodify.shared.compose.popup.SleepTimerOption
import com.andannn.melodify.shared.compose.popup.SleepTimerOptionDialog
import com.andannn.melodify.shared.compose.popup.SnackBarMessage
import com.andannn.melodify.shared.compose.popup.internal.content.DuplicatedAlert
import com.andannn.melodify.shared.compose.popup.showDialogAndWaitAction
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

            is PlayListItemModel -> if (this.isAudioPlayList) TabKind.AUDIO_PLAYLIST else TabKind.VIDEO_PLAYLIST

            is AudioItemModel,
            is VideoItemModel,
            -> error("invalid")
        }
    pinToHomeTab(
        externalId = id,
        tabName = name,
        tabKind = tabKind,
    )
}

context(userPreferenceRepository: UserPreferenceRepository, popupController: PopupController)
suspend fun pinAllMusicToHomeTab() {
    pinToHomeTab(
        externalId = "all_music",
        tabName = "All Music",
        tabKind = TabKind.ALL_MUSIC,
    )
}

context(userPreferenceRepository: UserPreferenceRepository, popupController: PopupController)
suspend fun pinAllVideoToHomeTab() {
    pinToHomeTab(
        externalId = "all_video",
        tabName = "All Video",
        tabKind = TabKind.ALL_VIDEO,
    )
}

context(userPreferenceRepository: UserPreferenceRepository, popupController: PopupController)
suspend fun pinToHomeTab(
    externalId: String,
    tabName: String,
    tabKind: TabKind,
) {
    val exist =
        userPreferenceRepository.isTabExist(
            externalId = externalId,
            tabName = tabName,
            tabKind = tabKind,
        )
    if (exist) {
        popupController.showSnackBar(SnackBarMessage.TabAlreadyExist)
    } else {
        userPreferenceRepository.addNewCustomTab(
            externalId = externalId,
            tabName = tabName,
            tabKind = tabKind,
        )
    }
}

context(repo: Repository, popupController: PopupController)
suspend fun addToNextPlay(items: List<MediaItemModel>) {
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
suspend fun addToQueue(items: List<MediaItemModel>) {
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
        val result = popupController.showDialogAndWaitAction(SleepCountingDialog)
        if (result is DialogAction.SleepTimerCountingDialog.OnCancelTimer) {
            sleepTimerRepository.cancelSleepTimer()
        }
    } else {
        val result = popupController.showDialogAndWaitAction(SleepTimerOptionDialog)
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
suspend fun addToPlaylist(items: List<MediaItemModel>) {
    Napier.d(tag = TAG) { "addToPlaylist E" }
    val audios = items.filterIsInstance<AudioItemModel>()
    val videos = items.filterIsInstance<VideoItemModel>()
    if (audios.isNotEmpty() && videos.isNotEmpty()) {
        error("can not add audio and video at the same time")
    }
    val isAudio = audios.isNotEmpty()

    val result = popupController.showDialogAndWaitAction(AddMusicsToPlayListDialog(items, isAudio))

    Napier.d(tag = TAG) { "AddMusicsToPlayListDialog result: $result" }

    when (result) {
        is DialogAction.AddToPlayListDialog.OnAddToPlayList -> {
            result.playList.addAll(items = result.items)
        }

        DialogAction.AddToPlayListDialog.OnCreateNewPlayList -> {
            createNewPlayList(items, isAudio)
        }

        else -> {}
    }
}

context(deleteHelper: MediaFileDeleteHelper, popupController: PopupController)
suspend fun deleteItems(items: List<MediaItemModel>) {
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
private suspend fun createNewPlayList(
    items: List<MediaItemModel>,
    isAudio: Boolean,
) {
    val result = popupController.showDialogAndWaitAction(NewPlayListDialog)
    Napier.d(tag = TAG) { "result. name = $result" }
    if (result is DialogAction.InputDialog.Accept) {
        val name = result.input
        Napier.d(tag = TAG) { "create new playlist start. name = $name, isAudio $isAudio" }
        val playListId = repo.createNewPlayList(name, isAudio)

        Napier.d(tag = TAG) { "playlist created. id = $playListId" }

        repo.addItemsToPlayList(
            playListId = playListId,
            items = items,
        )

        repo.addNewCustomTab(
            externalId = playListId.toString(),
            tabName = name,
            tabKind = if (isAudio) TabKind.AUDIO_PLAYLIST else TabKind.VIDEO_PLAYLIST,
        )
    }
}

context(playListRepository: PlayListRepository, popupController: PopupController)
private suspend fun PlayListItemModel.addAll(items: List<MediaItemModel>) {
    val duplicatedMedias =
        playListRepository.getDuplicatedMediaInPlayList(
            playListId = id.toLong(),
            items = items,
        )

    Napier.d(tag = TAG) { "onAddToPlayList. duplicated Medias: $duplicatedMedias" }
    when {
        duplicatedMedias.isEmpty() -> {
            playListRepository.addItemsToPlayList(
                playListId = id.toLong(),
                items = items,
            )

            popupController.showSnackBar(
                message = SnackBarMessage.AddPlayListSuccess(name),
            )
        }

        else -> {
            // invalidList.size > 1, Show alert message
            val result =
                popupController.showDialogAndWaitAction(DuplicatedAlert)

            if (result is DialogAction.AlertDialog.Accept) {
                playListRepository.addItemsToPlayList(
                    playListId = id.toLong(),
                    items = items.filter { it.id !in duplicatedMedias },
                )
                popupController.showSnackBar(
                    message = SnackBarMessage.AddPlayListSuccess(name),
                )
            }
        }
    }
}
