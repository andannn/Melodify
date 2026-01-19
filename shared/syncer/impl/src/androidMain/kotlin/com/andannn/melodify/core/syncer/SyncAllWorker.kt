/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.andannn.melodify.core.datastore.UserSettingPreferences
import io.github.aakira.napier.Napier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID
import java.util.concurrent.TimeUnit

internal class SyncWorkHelperImpl : SyncWorkHelper {
    companion object {
        private const val PERIODIC_SYNC_WORK_NAME = "periodic_sync_work_name"
    }

    override fun registerPeriodicSyncWork(context: Context) {
        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncAllMediaWorker>(12, TimeUnit.HOURS)
                .setInitialDelay(2, TimeUnit.HOURS)
                .build()

        val workManager = WorkManager.getInstance(context = context)
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest,
        )
    }
}

private const val TAG = "SyncAllWorker"

private object DataKey {
    const val MEDIA_TYPE = "MediaType"
    const val TOTAL = "Total"
    const val PROGRESS = "Progress"
    const val ITEMS = "Items"
    const val EVENT_TYPE = "EventType"
}

private object EventTypeValue {
    const val EVENT_TYPE_VALUE_START = 0
    const val EVENT_TYPE_VALUE_COMPLETE = 1
    const val EVENT_TYPE_VALUE_FAILED = 2
    const val EVENT_TYPE_VALUE_PROGRESS = 3
    const val EVENT_TYPE_VALUE_INSERT = 4
    const val EVENT_TYPE_VALUE_DELETE = 5
}

internal class SyncAllMediaWorker(
    private val appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params),
    KoinComponent {
    private val mediaLibrarySyncer: MediaLibrarySyncer by inject()
    private val userPreferenceRepository: UserSettingPreferences by inject()

    override suspend fun doWork(): Result {
        Napier.d(tag = TAG) { "doWork" }

        if (!haveMediaPermission()) {
            Napier.d(tag = TAG) { "no permission finish task." }
            return Result.failure()
        }

        var status: SyncStatusEvent? = null
        mediaLibrarySyncer.syncAllMediaLibrary().collect {
            status = it
            setProgress(it.toData())
        }

        return if (status is SyncStatusEvent.Complete) {
            logSuccessSyncTimestamp()
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun haveMediaPermission(): Boolean {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        return when (ContextCompat.checkSelfPermission(appContext, permission)) {
            PackageManager.PERMISSION_DENIED -> false
            else -> true
        }
    }

    private suspend fun logSuccessSyncTimestamp() {
        userPreferenceRepository.setLastSuccessfulSyncTime(
            System.currentTimeMillis(),
        )
    }
}

internal fun Data.toSyncStatus() =
    when (getInt(DataKey.EVENT_TYPE, -1)) {
        EventTypeValue.EVENT_TYPE_VALUE_START -> {
            SyncStatusEvent.Start
        }

        EventTypeValue.EVENT_TYPE_VALUE_COMPLETE -> {
            SyncStatusEvent.Complete
        }

        EventTypeValue.EVENT_TYPE_VALUE_FAILED -> {
            SyncStatusEvent.Failed
        }

        EventTypeValue.EVENT_TYPE_VALUE_PROGRESS -> {
            SyncStatusEvent.Progress(
                type = ContentType.valueOf(getString(DataKey.MEDIA_TYPE) ?: error("no media type")),
                total = getInt(DataKey.TOTAL, 0),
                progress = getInt(DataKey.PROGRESS, 0),
            )
        }

        EventTypeValue.EVENT_TYPE_VALUE_DELETE -> {
            SyncStatusEvent.Delete(
                type = ContentType.valueOf(getString(DataKey.MEDIA_TYPE) ?: error("no media type")),
                item = getString(DataKey.ITEMS) ?: error("no item"),
            )
        }

        EventTypeValue.EVENT_TYPE_VALUE_INSERT -> {
            SyncStatusEvent.Insert(
                type = ContentType.valueOf(getString(DataKey.MEDIA_TYPE) ?: error("no media type")),
                item = getString(DataKey.ITEMS) ?: error("no item"),
            )
        }

        else -> {
            null
        }
    }

private fun SyncStatusEvent.toData() =
    when (this) {
        SyncStatusEvent.Start -> {
            workDataOf(
                DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_START,
            )
        }

        SyncStatusEvent.Complete -> {
            workDataOf(
                DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_COMPLETE,
            )
        }

        is SyncStatusEvent.Failed -> {
            workDataOf(
                DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_FAILED,
            )
        }

        is SyncStatusEvent.Progress -> {
            workDataOf(
                DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_PROGRESS,
                DataKey.MEDIA_TYPE to type.name,
                DataKey.TOTAL to total,
                DataKey.PROGRESS to progress,
            )
        }

        is SyncStatusEvent.Delete -> {
            workDataOf(
                DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_DELETE,
                DataKey.MEDIA_TYPE to type.name,
                DataKey.ITEMS to item,
            )
        }

        is SyncStatusEvent.Insert -> {
            workDataOf(
                DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_INSERT,
                DataKey.MEDIA_TYPE to type.name,
                DataKey.ITEMS to item,
            )
        }
    }
