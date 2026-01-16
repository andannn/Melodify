/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.usecase

import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.PlayListRepository
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.SleepTimerRepository
import com.andannn.melodify.domain.UserPreferenceRepository
import com.andannn.melodify.domain.model.AlbumItemModel
import com.andannn.melodify.domain.model.ArtistItemModel
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.GenreItemModel
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.TabKind
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.shared.compose.popup.DialogHostState
import com.andannn.melodify.shared.compose.popup.entry.alert.AlertDialogAction
import com.andannn.melodify.shared.compose.popup.entry.alert.DuplicatedAlert
import com.andannn.melodify.shared.compose.popup.entry.play.list.AddMusicsToPlayListDialog
import com.andannn.melodify.shared.compose.popup.entry.play.list.AddToPlayListDialogResult
import com.andannn.melodify.shared.compose.popup.entry.play.list.InputDialogResult
import com.andannn.melodify.shared.compose.popup.entry.play.list.NewPlayListDialog
import com.andannn.melodify.shared.compose.popup.entry.sleep.timer.SleepCountingDialog
import com.andannn.melodify.shared.compose.popup.entry.sleep.timer.SleepTimerCountingDialog
import com.andannn.melodify.shared.compose.popup.entry.sleep.timer.SleepTimerOption
import com.andannn.melodify.shared.compose.popup.entry.sleep.timer.SleepTimerOptionDialog
import com.andannn.melodify.shared.compose.popup.entry.sleep.timer.SleepTimerOptionDialogAction
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarController
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarMessage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first

private const val TAG = "MediaOptionUseCase"

context(userPreferenceRepository: UserPreferenceRepository, snackBarController: SnackBarController)
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

context(userPreferenceRepository: UserPreferenceRepository, snackBarController: SnackBarController)
suspend fun pinAllMusicToHomeTab() {
    pinToHomeTab(
        externalId = "all_music",
        tabName = "All Music",
        tabKind = TabKind.ALL_MUSIC,
    )
}

context(userPreferenceRepository: UserPreferenceRepository, snackBarController: SnackBarController)
suspend fun pinAllVideoToHomeTab() {
    pinToHomeTab(
        externalId = "all_video",
        tabName = "All Video",
        tabKind = TabKind.ALL_VIDEO,
    )
}

context(userPreferenceRepository: UserPreferenceRepository, snackBarController: SnackBarController)
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
        snackBarController.showSnackBar(SnackBarMessage.TabAlreadyExist)
    } else {
        userPreferenceRepository.addNewCustomTab(
            externalId = externalId,
            tabName = tabName,
            tabKind = tabKind,
        )
    }
}

context(repo: Repository, snackBarController: SnackBarController)
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
    snackBarController.showSnackBar(SnackBarMessage.AddedToPlayNext)
}

context(repo: Repository, snackBarController: SnackBarController)
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
    snackBarController.showSnackBar(SnackBarMessage.AddedToPlayQueue)
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

context(sleepTimerRepository: SleepTimerRepository, dialogHostState: DialogHostState)
suspend fun openSleepTimer() {
    if (sleepTimerRepository.isCounting()) {
        val result = dialogHostState.showDialog(SleepCountingDialog)
        if (result is SleepTimerCountingDialog.OnCancelTimer) {
            sleepTimerRepository.cancelSleepTimer()
        }
    } else {
        val result = dialogHostState.showDialog(SleepTimerOptionDialog)
        if (result is SleepTimerOptionDialogAction.OnOptionClick) {
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

context(repo: Repository, dialogHostState: DialogHostState, _: SnackBarController)
suspend fun addToPlaylist(items: List<MediaItemModel>) {
    Napier.d(tag = TAG) { "addToPlaylist E" }
    val audios = items.filterIsInstance<AudioItemModel>()
    val videos = items.filterIsInstance<VideoItemModel>()
    if (audios.isNotEmpty() && videos.isNotEmpty()) {
        error("can not add audio and video at the same time")
    }
    val isAudio = audios.isNotEmpty()

    when (val result = dialogHostState.showDialog(AddMusicsToPlayListDialog(items, isAudio))) {
        is AddToPlayListDialogResult.OnAddToPlayListResult -> {
            result.playList.addAll(items = result.items)
        }

        AddToPlayListDialogResult.OnCreateNewPlayListResult -> {
            createNewPlayList(items, isAudio)
        }

        else -> {}
    }
}

context(deleteHelper: MediaFileDeleteHelper, snackBarController: SnackBarController)
suspend fun deleteItems(items: List<MediaItemModel>) {
    if (items.isEmpty()) {
        return
    }

    when (deleteHelper.deleteMedias(items)) {
        MediaFileDeleteHelper.Result.Success -> {
            if (items.size == 1) {
                snackBarController.showSnackBar(SnackBarMessage.OneDeleteSuccess)
            } else {
                snackBarController.showSnackBar(SnackBarMessage.MultipleDeleteSuccess(items.size))
            }
        }

        MediaFileDeleteHelper.Result.Failed -> {
            snackBarController.showSnackBar(SnackBarMessage.DeleteFailed)
        }

        MediaFileDeleteHelper.Result.Denied -> {
            // Noop
        }
    }
}

context(repo: Repository, dialogHostState: DialogHostState)
private suspend fun createNewPlayList(
    items: List<MediaItemModel>,
    isAudio: Boolean,
) {
    val result = dialogHostState.showDialog(NewPlayListDialog)
    Napier.d(tag = TAG) { "result. name = $result" }
    if (result is InputDialogResult.Accept) {
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

context(playListRepository: PlayListRepository, dialogHostState: DialogHostState, snackBarController: SnackBarController)
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

            snackBarController.showSnackBar(
                message = SnackBarMessage.AddPlayListSuccess(name),
            )
        }

        else -> {
            // invalidList.size > 1, Show alert message
            val result = dialogHostState.showDialog(DuplicatedAlert)
            if (result is AlertDialogAction.Accept) {
                playListRepository.addItemsToPlayList(
                    playListId = id.toLong(),
                    items = items.filter { it.id !in duplicatedMedias },
                )
                snackBarController.showSnackBar(
                    message = SnackBarMessage.AddPlayListSuccess(name),
                )
            }
        }
    }
}
