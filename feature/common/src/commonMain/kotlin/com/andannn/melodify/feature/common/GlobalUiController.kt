package com.andannn.melodify.feature.common

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.MediaControllerRepository
import com.andannn.melodify.core.data.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.util.uri
import com.andannn.melodify.feature.common.drawer.SheetModel
import com.andannn.melodify.feature.common.drawer.SheetOptionItem
import com.andannn.melodify.feature.common.drawer.SleepTimerOption
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

private const val TAG = "GlobalUiController"

sealed interface UiEvent {
    data object OnCancelTimer : UiEvent

    data class OnMediaOptionClick(
        val sheet: SheetModel.MediaOptionSheet,
        val clickedItem: SheetOptionItem,
    ) : UiEvent

    data class OnTimerOptionClick(
        val option: SleepTimerOption,
    ) : UiEvent

    data class OnDismissSheet(val bottomSheet: SheetModel) : UiEvent
}

interface DeleteMediaItemEventProvider {
    val deleteMediaItemEventFlow: SharedFlow<List<String>>
}

interface BottomSheetStateProvider {
    val bottomSheetModel: SharedFlow<SheetModel?>
}

interface GlobalUiController : BottomSheetStateProvider, DeleteMediaItemEventProvider {
    suspend fun updateBottomSheet(sheet: SheetModel)

    /**
     * Handle the dismiss request from the bottom sheet.
     */
    suspend fun onEvent(event: UiEvent)
}

internal class GlobalUiControllerImpl(
    private val mediaContentRepository: MediaContentRepository,
    private val mediaControllerRepository: MediaControllerRepository,
    private val playerStateMonitoryRepository: PlayerStateMonitoryRepository,
) : GlobalUiController {
    override val bottomSheetModel: SharedFlow<SheetModel?>
        get() = _bottomSheetModelFlow.asSharedFlow()

    override val deleteMediaItemEventFlow: SharedFlow<List<String>>
        get() = _deleteMediaItemEventFlow

    private val _bottomSheetModelFlow = MutableSharedFlow<SheetModel?>(1)
    private val _deleteMediaItemEventFlow = MutableSharedFlow<List<String>>(1)

    private var collectingJob: Job? = null

    override suspend fun updateBottomSheet(sheet: SheetModel) {
        _bottomSheetModelFlow.emit(sheet)
    }

    override suspend fun onEvent(event: UiEvent) {
        Napier.d(tag = TAG) { "onEvent: $event" }
        when (event) {
            is UiEvent.OnTimerOptionClick -> {
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

            is UiEvent.OnMediaOptionClick -> {
                closeSheet()
                event.clickedItem.let {
                    when (it) {
                        SheetOptionItem.PLAY_NEXT -> onPlayNextClick(event.sheet.source)
                        SheetOptionItem.DELETE -> onDeleteMediaItem(event.sheet.source)
                        SheetOptionItem.ADD_TO_QUEUE -> onAddToQueue(event.sheet.source)
                        SheetOptionItem.SLEEP_TIMER -> onClickSleepTimer()
                    }
                }
            }

            UiEvent.OnCancelTimer -> {
                mediaControllerRepository.cancelSleepTimer()
                cancelCollectingRemainTime()
                closeSheet()
            }

            is UiEvent.OnDismissSheet -> {
                when (event.bottomSheet) {
                    is SheetModel.MediaOptionSheet,
                    SheetModel.TimerOptionSheet -> {
                        closeSheet()
                    }

                    is SheetModel.TimerRemainTimeSheet -> {
                        cancelCollectingRemainTime()
                        closeSheet()
                    }
                }
            }
        }
    }

    private fun cancelCollectingRemainTime() {
        collectingJob?.cancel()
    }

    private suspend fun closeSheet() {
        _bottomSheetModelFlow.emit(null)
    }

    private suspend fun onClickSleepTimer() {
        if (mediaControllerRepository.isCounting()) {
            collectRemainTimeToShowCountingSheet()
        } else {
            updateBottomSheet(SheetModel.TimerOptionSheet)
        }
    }

    private suspend fun collectRemainTimeToShowCountingSheet() = coroutineScope {
        collectingJob = launch {
            mediaControllerRepository.observeRemainTime().collect {
                updateBottomSheet(SheetModel.TimerRemainTimeSheet(it))
            }
        }
    }

    private suspend fun onDeleteMediaItem(source: MediaItemModel) {
        val items = when (source) {
            is AlbumItemModel -> {
                mediaContentRepository.getAudiosOfAlbum(source.id)
            }

            is ArtistItemModel -> {
                mediaContentRepository.getAudiosOfArtist(source.id)
            }

            is GenreItemModel -> {
                mediaContentRepository.getAudiosOfGenre(source.id)
            }

            is AudioItemModel -> {
                listOf(source)
            }

            is PlayListItemModel -> TODO("implement later")
        }
        val uris = items.map { it.uri }

        _deleteMediaItemEventFlow.tryEmit(uris)
    }

    private suspend fun onPlayNextClick(source: MediaItemModel) {
        val items = getAudios(source)
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
        val items = getAudios(source)
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

    private suspend fun getAudios(source: MediaItemModel): List<AudioItemModel> {
        return when (source) {
            is AlbumItemModel -> {
                mediaContentRepository.getAudiosOfAlbum(source.id)
            }

            is ArtistItemModel -> {
                mediaContentRepository.getAudiosOfArtist(source.id)
            }

            is GenreItemModel -> {
                mediaContentRepository.getAudiosOfGenre(source.id)
            }

            is AudioItemModel -> {
                listOf(source)
            }

            is PlayListItemModel -> {
                mediaContentRepository.getAudiosOfPlayList(source.id.toLong())
            }
        }
    }
}
