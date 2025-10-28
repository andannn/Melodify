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

    fun doOneTimeSyncWork(context: Context) {
        val oneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SyncAllMediaWorker>()
                .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest,
        )
    }
}

fun Data.toSyncStatus() =
    when (getString(EVENT_TYPE)) {
        EVENT_TYPE_VALUE_START -> SyncStatus.Start
        EVENT_TYPE_VALUE_COMPLETE -> SyncStatus.Complete
        EVENT_TYPE_VALUE_FAILED -> SyncStatus.Failed
        EVENT_TYPE_VALUE_PROGRESS -> {
            SyncStatus.Progress(
                type = SyncType.valueOf(getString(MEDIA_TYPE) ?: error("no media type")),
                total = getInt(TOTAL, 0),
                progress = getInt(PROGRESS, 0),
            )
        }

        else -> {}
    }

private const val TAG = "SyncAllWorker"

private const val MEDIA_TYPE = "Type"
private const val TOTAL = "total"
private const val PROGRESS = "progress"
private const val EVENT_TYPE = "EventType"
private const val EVENT_TYPE_VALUE_START = "START"
private const val EVENT_TYPE_VALUE_COMPLETE = "COMPLETE"
private const val EVENT_TYPE_VALUE_FAILED = "FAILED"
private const val EVENT_TYPE_VALUE_PROGRESS = "PROGRESS"

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
            SyncStatus.Start ->
                workDataOf(
                    EVENT_TYPE to EVENT_TYPE_VALUE_START,
                )

            SyncStatus.Complete ->
                workDataOf(
                    EVENT_TYPE to EVENT_TYPE_VALUE_COMPLETE,
                )

            is SyncStatus.Failed ->
                workDataOf(
                    EVENT_TYPE to EVENT_TYPE_VALUE_FAILED,
                )

            is SyncStatus.Progress ->
                workDataOf(
                    EVENT_TYPE to EVENT_TYPE_VALUE_PROGRESS,
                    MEDIA_TYPE to type.name,
                    TOTAL to total,
                    PROGRESS to progress,
                )
        }
}
