package com.andannn.melodify.core.data.util

import com.andannn.melodify.core.data.model.AudioItemModel
import kotlinx.coroutines.flow.Flow


expect val AudioItemModel.uri: String

expect fun allAudioChangedEventFlow(): Flow<Unit>

expect fun contentChangedEventFlow(uri: String): Flow<Unit>
