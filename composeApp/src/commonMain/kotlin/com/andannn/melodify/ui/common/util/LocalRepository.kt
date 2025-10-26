/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.common.util

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.andannn.melodify.core.data.Repository

val LocalRepository: ProvidableCompositionLocal<Repository> =
    compositionLocalOf { error("no local repository") }
