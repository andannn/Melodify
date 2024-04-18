package com.andannn.musicplayer.common.drawer

import com.andanana.musicplayer.core.designsystem.icons.SmpIcon
import com.andanana.musicplayer.core.designsystem.icons.SimpleMusicIcons

enum class SheetItem(
    val smpIcon: SmpIcon,
    val text: String,
) {
    ADD_TO_FAVORITE(
        smpIcon = SimpleMusicIcons.AddFavorite,
        text = "Save to Favorite",
    ),
    PLAY_NEXT(
        smpIcon = SimpleMusicIcons.PlayNext,
        text = "Play next",
    ),
    ADD_TO_PLAY_LIST(
        smpIcon = SimpleMusicIcons.AddPlayList,
        text = "Save to PlayList",
    ),
    SHARE(
        smpIcon = SimpleMusicIcons.Share,
        text = "Share",
    ),
    INFORMATION(
        smpIcon = SimpleMusicIcons.Information,
        text = "Information",
    ),
    DELETE(
        smpIcon = SimpleMusicIcons.Delete,
        text = "Delete",
    ),
}

sealed class BottomSheet(
    val itemList: List<SheetItem>,
) {
    data object MusicBottomSheet : BottomSheet(
        listOf(
            SheetItem.ADD_TO_FAVORITE,
            SheetItem.ADD_TO_PLAY_LIST,
            SheetItem.PLAY_NEXT,
            SheetItem.INFORMATION,
        ),
    )

    data object AlbumBottomSheet : BottomSheet(
        listOf(
            SheetItem.PLAY_NEXT,
            SheetItem.INFORMATION,
        ),
    )

    data object ArtistBottomSheet : BottomSheet(
        listOf(
            SheetItem.PLAY_NEXT,
            SheetItem.INFORMATION,
        ),
    )

    data object PlayListBottomSheet : BottomSheet(
        listOf(
            SheetItem.PLAY_NEXT,
            SheetItem.INFORMATION,
            SheetItem.DELETE,
        ),
    )
}