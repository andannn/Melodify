package com.andannn.melodify.feature.drawer.model

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.feature.common.icons.SimpleMusicIcons
import com.andannn.melodify.feature.common.icons.SmpIcon
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

sealed interface SheetModel {
    abstract class MediaOptionSheet(
        open val source: MediaItemModel,
        open val options: List<SheetOptionItem>,
    ) : SheetModel {
        companion object {
            fun fromMediaModel(item: MediaItemModel): SheetModel {
                return when (item) {
                    is AlbumItemModel -> AlbumOptionSheet(item)
                    is ArtistItemModel -> ArtistOptionSheet(item)
                    is AudioItemModel -> AudioOptionSheet(item)
                    is GenreItemModel -> GenreOptionSheet(item)
                    is PlayListItemModel -> PlayListOptionSheet(item)
                }
            }
        }
    }

    data class AudioOptionSheet(
        override val source: AudioItemModel,
    ) : MediaOptionSheet(
        source = source,
        options = listOf(
            SheetOptionItem.ADD_TO_QUEUE,
            SheetOptionItem.PLAY_NEXT,
            SheetOptionItem.ADD_TO_PLAYLIST,
            SheetOptionItem.DELETE,
        ),
    )

    data class AudioOptionInPlayListSheet(
        val playListId: String,
        override val source: AudioItemModel,
    ) : MediaOptionSheet(
        source = source,
        options = listOf(
            SheetOptionItem.ADD_TO_QUEUE,
            SheetOptionItem.PLAY_NEXT,
            SheetOptionItem.DELETE_FROM_PLAYLIST,
            SheetOptionItem.ADD_TO_PLAYLIST,
            SheetOptionItem.DELETE,
        ),
    )

    data class PlayerOptionSheet(override val source: AudioItemModel) : MediaOptionSheet(
        source = source,
        options = listOf(
            SheetOptionItem.ADD_TO_QUEUE,
            SheetOptionItem.PLAY_NEXT,
            SheetOptionItem.SLEEP_TIMER,
        ),
    )

    data class PlayListOptionSheet(override val source: PlayListItemModel) : MediaOptionSheet(
        source = source,
        options = listOf(
            SheetOptionItem.ADD_TO_QUEUE,
            SheetOptionItem.PLAY_NEXT,
            SheetOptionItem.ADD_TO_PLAYLIST,
            SheetOptionItem.DELETE,
        ),
    )

    data class GenreOptionSheet(override val source: GenreItemModel) : MediaOptionSheet(
        source = source,
        options = listOf(
            SheetOptionItem.ADD_TO_QUEUE,
            SheetOptionItem.PLAY_NEXT,
            SheetOptionItem.ADD_TO_PLAYLIST,
            SheetOptionItem.DELETE,
        )
    )

    data class AlbumOptionSheet(override val source: AlbumItemModel) : MediaOptionSheet(
        source = source,
        options = listOf(
            SheetOptionItem.ADD_TO_QUEUE,
            SheetOptionItem.PLAY_NEXT,
            SheetOptionItem.ADD_TO_PLAYLIST,
            SheetOptionItem.DELETE,
        ),
    )

    data class ArtistOptionSheet(override val source: ArtistItemModel) : MediaOptionSheet(
        source = source,
        options = listOf(
            SheetOptionItem.ADD_TO_QUEUE,
            SheetOptionItem.PLAY_NEXT,
            SheetOptionItem.ADD_TO_PLAYLIST,
            SheetOptionItem.DELETE,
        ),
    )

    data class AddToPlayListSheet(
        val source: MediaItemModel,
    ) : SheetModel

    data object TimerOptionSheet : SheetModel

    data object TimerRemainTimeSheet : SheetModel
}

enum class SheetOptionItem(
    val smpIcon: SmpIcon,
    val text: StringResource,
) {
    PLAY_NEXT(
        smpIcon = SimpleMusicIcons.PlayNext,
        text = Res.string.play_next,
    ),
    DELETE(
        smpIcon = SimpleMusicIcons.Delete,
        text = Res.string.delete,
    ),
    ADD_TO_PLAYLIST(
        smpIcon = SimpleMusicIcons.AddPlayList,
        text = Res.string.add_to_playlist,
    ),
    DELETE_FROM_PLAYLIST(
        smpIcon = SimpleMusicIcons.PlayListRemove,
        text = Res.string.delete_from_playlist,
    ),
    ADD_TO_QUEUE(
        smpIcon = SimpleMusicIcons.Delete,
        text = Res.string.add_to_queue,
    ),
    SLEEP_TIMER(
        smpIcon = SimpleMusicIcons.Timer,
        text = Res.string.sleep_timer,
    ),
}

enum class SleepTimerOption(
    val timeMinutes: Duration?,
) {
    FIVE_MINUTES(5.minutes),
    FIFTEEN_MINUTES(15.minutes),
    THIRTY_MINUTES(30.minutes),
    SIXTY_MINUTES(60.minutes),
    SONG_FINISH(null),
}

