package com.andannn.melodify.feature.common.util

import androidx.compose.runtime.Composable
import org.koin.core.scope.Scope

/**
 * Desktop app have no destroy-reconstruction lifecycle like android.
 * Just return mull.
 */
@Composable
actual fun getUiRetainedScope(): Scope? {
    return null
}