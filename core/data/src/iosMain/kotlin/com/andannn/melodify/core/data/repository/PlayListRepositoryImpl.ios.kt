package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel

actual suspend fun getMediaListFromIds(mediaStoreIds: List<String>): List<AudioItemModel> {
    return emptyList()
}