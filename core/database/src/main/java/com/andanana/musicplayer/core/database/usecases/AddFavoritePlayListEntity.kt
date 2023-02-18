package com.andanana.musicplayer.core.database.usecases

import com.andanana.musicplayer.core.database.dao.PlayListDao
import com.andanana.musicplayer.core.database.entity.PlayList
import javax.inject.Inject

const val FAVORITE_PLAY_LIST_ID = Long.MAX_VALUE

class AddFavoritePlayListEntity @Inject constructor(
    private val playListDao: PlayListDao
) {
    suspend operator fun invoke() {
        playListDao.insertPlayListEntities(
            PlayList(
                playListId = FAVORITE_PLAY_LIST_ID,
                name = "Favorite",
                createdDate = 0L
            )
        )
    }
}