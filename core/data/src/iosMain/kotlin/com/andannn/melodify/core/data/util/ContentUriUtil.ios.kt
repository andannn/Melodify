package com.andannn.melodify.core.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual fun contentChangedEventFlow(uri: String): Flow<Unit> {
    return flow { emit(Unit) }
}

actual fun allAudioChangedEventFlow(): Flow<Unit> {
    return flow { emit(Unit) }
}