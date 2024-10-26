package com.andannn.melodify.core.data.util

import com.andannn.melodify.core.data.model.AudioItemModel

actual val AudioItemModel.uri: String
    get() = source ?: ""