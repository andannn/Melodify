package com.andannn.musicplayer.common.drawer

import androidx.media3.common.MediaItem
import com.andanana.musicplayer.core.model.MediaSourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

data class ShowDrawerRequest(
    val source: MediaItem,
    val bottomSheet: BottomSheet,
)

data class MediaOptionResult(
    val source: MediaItem,
    val item: SheetItem,
)

interface BottomSheetEventSink {
    fun onRequestShowSheet(mediaItem: MediaItem)

    fun onDrawerOptionClick(item: SheetItem)
}

interface BottomSheetController : BottomSheetEventSink {
    fun observeShowDrawerRequest(): Flow<ShowDrawerRequest>

    fun observeMediaOptionResult(): Flow<ShowDrawerRequest>
}

internal class BottomSheetControllerImpl : BottomSheetController {
    private var lastRequestedMediaItem: MediaItem? = null

    private val showDrawerRequestFlow =
        MutableSharedFlow<ShowDrawerRequest>(extraBufferCapacity = 1)

    override fun onRequestShowSheet(mediaItem: MediaItem) {
        lastRequestedMediaItem = mediaItem

        showDrawerRequestFlow.tryEmit(
            ShowDrawerRequest(
                source = mediaItem,
                bottomSheet = buildDrawer(mediaItem),
            ),
        )
    }

    private fun buildDrawer(mediaItem: MediaItem): BottomSheet {
        val source =
            MediaSourceType.getMediaSourceType(mediaItem.mediaId!!)
                ?: error("no need to show drawer for ${mediaItem.mediaId}")
        return when (source) {
            MediaSourceType.MUSIC -> BottomSheet.MusicBottomSheet
            MediaSourceType.ARTIST -> BottomSheet.ArtistBottomSheet
            MediaSourceType.ALBUM -> BottomSheet.AlbumBottomSheet
        }
    }

    override fun onDrawerOptionClick(item: SheetItem) {
        TODO("Not yet implemented")
    }

    override fun observeShowDrawerRequest() = showDrawerRequestFlow

    override fun observeMediaOptionResult(): Flow<ShowDrawerRequest> {
        TODO("Not yet implemented")
    }
}
