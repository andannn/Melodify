package com.andannn.melodify.ui.common.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.scope.retainedScopeId
import org.koin.mp.KoinPlatform.getKoin

/**
 * get activity retained scope from context.
 */
@Composable
actual fun getUiRetainedScope() =
    (LocalContext.current as? ComponentActivity)?.retainedScopeId()?.let { scopeId ->
        getKoin().getScope(scopeId)
    }
