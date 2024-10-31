package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef

actual suspend fun getMediaListFromIds(playListItems: List<PlayListWithMediaCrossRef>): List<AudioItemModel> {
    return emptyList()
}