package com.andannn.melodify.core.syncer

import android.content.Context
import java.util.UUID

interface SyncWorkHelper {
    fun registerPeriodicSyncWork(context: Context)

    fun doOneTimeSyncWork(context: Context): UUID
}
