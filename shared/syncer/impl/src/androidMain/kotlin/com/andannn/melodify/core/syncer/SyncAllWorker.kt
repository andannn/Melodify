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

object SyncWorkHelper {
    private const val PERIODIC_SYNC_WORK_NAME = "periodic_sync_work_name"
    private const val ONE_TIME_SYNC_WORK_NAME = "one_time_sync_work_name"

    fun registerPeriodicSyncWork(context: Context) {
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

    /**
     *
     */
    fun doOneTimeSyncWork(context: Context): UUID {
        val oneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SyncAllMediaWorker>()
                .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest,
        )

        return oneTimeWorkRequest.id
    }
}

internal fun Data.toSyncStatus() =
    when (getInt(DataKey.EVENT_TYPE, -1)) {
        EventTypeValue.EVENT_TYPE_VALUE_START -> {
            SyncStatus.Start
        }

        EventTypeValue.EVENT_TYPE_VALUE_COMPLETE -> {
            SyncStatus.Complete
        }

        EventTypeValue.EVENT_TYPE_VALUE_FAILED -> {
            SyncStatus.Failed
        }

        EventTypeValue.EVENT_TYPE_VALUE_PROGRESS -> {
            SyncStatus.Progress(
                type = SyncType.valueOf(getString(DataKey.MEDIA_TYPE) ?: error("no media type")),
                total = getInt(DataKey.TOTAL, 0),
                progress = getInt(DataKey.PROGRESS, 0),
            )
        }

        EventTypeValue.EVENT_TYPE_VALUE_DELETE -> {
            SyncStatus.Delete(
                type = SyncType.valueOf(getString(DataKey.MEDIA_TYPE) ?: error("no media type")),
                items = getStringArray(DataKey.ITEMS)?.toList() ?: emptyList(),
            )
        }

        EventTypeValue.EVENT_TYPE_VALUE_INSERT -> {
            SyncStatus.Insert(
                type = SyncType.valueOf(getString(DataKey.MEDIA_TYPE) ?: error("no media type")),
                items = getStringArray(DataKey.ITEMS)?.toList() ?: emptyList(),
            )
        }

        else -> {
            null
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

        var status: SyncStatus? = null
        mediaLibrarySyncer.syncAllMediaLibrary().collect {
            status = it
            setProgress(it.toData())
        }

        return if (status is SyncStatus.Complete) {
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

    private fun SyncStatus.toData() =
        when (this) {
            SyncStatus.Start -> {
                workDataOf(
                    DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_START,
                )
            }

            SyncStatus.Complete -> {
                workDataOf(
                    DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_COMPLETE,
                )
            }

            is SyncStatus.Failed -> {
                workDataOf(
                    DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_FAILED,
                )
            }

            is SyncStatus.Progress -> {
                workDataOf(
                    DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_PROGRESS,
                    DataKey.MEDIA_TYPE to type.name,
                    DataKey.TOTAL to total,
                    DataKey.PROGRESS to progress,
                )
            }

            is SyncStatus.Delete -> {
                workDataOf(
                    DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_DELETE,
                    DataKey.MEDIA_TYPE to type.name,
                    DataKey.ITEMS to items.toTypedArray(),
                )
            }

            is SyncStatus.Insert -> {
                workDataOf(
                    DataKey.EVENT_TYPE to EventTypeValue.EVENT_TYPE_VALUE_INSERT,
                    DataKey.MEDIA_TYPE to type.name,
                    DataKey.ITEMS to items.toTypedArray(),
                )
            }
        }
}
