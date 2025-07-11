/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.popup.dialog

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.confirm_delete_playlist_item
import melodify.ui.common.generated.resources.decline
import melodify.ui.common.generated.resources.duplicated_alert_dialog_title
import melodify.ui.common.generated.resources.having_registered_track_in_playlist
import melodify.ui.common.generated.resources.invalid_path_alert_dialog_content
import melodify.ui.common.generated.resources.new_playlist_dialog_input_hint
import melodify.ui.common.generated.resources.new_playlist_dialog_title
import melodify.ui.common.generated.resources.ok
import melodify.ui.common.generated.resources.skip_registered_songs
import org.jetbrains.compose.resources.StringResource

sealed interface DialogId {
    abstract class AlertDialog(
        val title: StringResource? = null,
        val message: StringResource? = null,
        val positive: StringResource,
        val negative: StringResource? = null,
    ) : DialogId

    data object ConfirmDeletePlaylist : AlertDialog(
        message = Res.string.confirm_delete_playlist_item,
        positive = Res.string.ok,
        negative = Res.string.decline,
    )

    data object InvalidPathAlert : AlertDialog(
        message = Res.string.invalid_path_alert_dialog_content,
        positive = Res.string.ok,
    )

    data object DuplicatedAlert : AlertDialog(
        title = Res.string.duplicated_alert_dialog_title,
        message = Res.string.having_registered_track_in_playlist,
        positive = Res.string.skip_registered_songs,
    )

    data object NewPlayListDialog : DialogId {
        val title = Res.string.new_playlist_dialog_title
        val playListNameInputHint = Res.string.new_playlist_dialog_input_hint
        val positive = Res.string.ok
        val negative = Res.string.decline
    }

    data object AddLibraryPathDialog : DialogId {
        val title = Res.string.new_playlist_dialog_title
        val positive = Res.string.ok
        val negative = Res.string.decline
    }

    data class AddToPlayListDialog(
        val source: MediaItemModel,
    ) : DialogId

    data object SleepTimerOptionDialog : DialogId

    data object SleepCountingDialog : DialogId

    abstract class MediaOption(
        open val media: MediaItemModel,
        open val options: List<OptionItem>,
    ) : DialogId {
        companion object {
            fun fromMediaModel(item: MediaItemModel): MediaOption =
                when (item) {
                    is AlbumItemModel -> AlbumOption(item)
                    is ArtistItemModel -> ArtistOption(item)
                    is AudioItemModel -> AudioOption(item)
                    is GenreItemModel -> GenreOption(item)
                    is PlayListItemModel -> {
                        if (item.isFavorite) {
                            FavoritePlayListOption(item)
                        } else {
                            PlayListOption(item)
                        }
                    }
                }
        }
    }

    data class AudioOption(
        override val media: AudioItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_PLAYLIST,
                ),
        )

    data class AudioOptionInPlayList(
        val playListId: String,
        override val media: AudioItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.DELETE_FROM_PLAYLIST,
                    OptionItem.ADD_TO_PLAYLIST,
                ),
        )

    data class PlayerOption(
        override val media: AudioItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.SLEEP_TIMER,
                ),
        )

    data class PlayListOption(
        override val media: PlayListItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_PLAYLIST,
                    OptionItem.DELETE_PLAYLIST,
                    OptionItem.DELETE_TAB,
                ),
        )

    data class SearchedPlayListOption(
        override val media: PlayListItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_PLAYLIST,
                    OptionItem.ADD_TO_HOME_TAB,
                ),
        )

    data class FavoritePlayListOption(
        override val media: PlayListItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_PLAYLIST,
                    OptionItem.DELETE_TAB,
                ),
        )

    data class GenreOption(
        override val media: GenreItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_PLAYLIST,
                    OptionItem.DELETE_TAB,
                ),
        )

    data class SearchedGenreOption(
        override val media: GenreItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_HOME_TAB,
                    OptionItem.ADD_TO_PLAYLIST,
                ),
        )

    data class AlbumOption(
        override val media: AlbumItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_PLAYLIST,
                    OptionItem.DELETE_TAB,
                ),
        )

    data class SearchedAlbumOption(
        override val media: AlbumItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_HOME_TAB,
                    OptionItem.ADD_TO_PLAYLIST,
                ),
        )

    data class SearchedArtistOption(
        override val media: ArtistItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_HOME_TAB,
                    OptionItem.ADD_TO_PLAYLIST,
                ),
        )

    data class ArtistOption(
        override val media: ArtistItemModel,
    ) : MediaOption(
            media = media,
            options =
                listOf(
                    OptionItem.ADD_TO_QUEUE,
                    OptionItem.PLAY_NEXT,
                    OptionItem.ADD_TO_PLAYLIST,
                    OptionItem.DELETE_TAB,
                ),
        )
}
