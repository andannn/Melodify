package com.andannn.melodify.core.data.util

import android.net.Uri
import android.provider.MediaStore
import com.andannn.melodify.core.data.model.AudioItemModel

actual val AudioItemModel.uri: String
    get() = Uri.withAppendedPath(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        id
    ).toString()