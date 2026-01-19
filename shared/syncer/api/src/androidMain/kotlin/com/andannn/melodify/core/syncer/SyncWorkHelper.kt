/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import android.content.Context

interface SyncWorkHelper {
    fun registerPeriodicSyncWork(context: Context)
}
