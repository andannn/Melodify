package com.andannn.melodify.core.library.mediastore

import com.andannn.melodify.core.library.mediastore.model.MediaDataModel

interface MediaLibrary {
    suspend fun getMediaData(): MediaDataModel
}