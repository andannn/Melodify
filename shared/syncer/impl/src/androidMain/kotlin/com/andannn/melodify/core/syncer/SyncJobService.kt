/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.andannn.melodify.core.syncer.model.FileChangeEvent
import com.andannn.melodify.core.syncer.model.FileChangeType
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.getKoin
import java.io.File
import kotlin.coroutines.CoroutineContext

private const val TAG = "SyncJobService"

class SyncJobService :
    JobService(),
    CoroutineScope {
    private val syncer: MediaLibrarySyncer = getKoin().get<MediaLibrarySyncer>()

    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()

    override fun onStartJob(params: JobParameters): Boolean {
        Napier.d(tag = TAG) { "onStartJob: $params" }

        launch {
            syncTask(params)

            Napier.d(tag = TAG) { "sync finished" }

            // Tell the system that the job has completed
            jobFinished(params, false)
            // Schedule job again to watch next change.
            scheduleSyncLibraryJob(this@SyncJobService)
        }

        // Return true to indicate that the job should continue running
        return true
    }

    private suspend fun syncTask(params: JobParameters) {
        val triggeredContentUris = params.triggeredContentUris
        if (triggeredContentUris != null) {
            Napier.d(tag = TAG) { "sync triggeredContentUris: ${params.triggeredContentUris?.toList()}" }
            if (triggeredContentUris.any { !it.isSpecificFile() }) {
                // Oops, there is some general change!
                syncer.syncAllMediaLibrary().collect()
            } else {
                syncer.syncMediaByChanges(
                    triggeredContentUris.mapToChangeEvent(contentResolver),
                )
            }
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Napier.d(tag = TAG) { "onStopJob: $params" }

        this.cancel()
        return false
    }

    companion object {
        private const val JOB_ID = 1

        /**
         * Schedules a job to sync the media library.
         */
        fun scheduleSyncLibraryJob(context: Context) {
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            val jobInfo =
                JobInfo
                    .Builder(
                        JOB_ID,
                        ComponentName(context, SyncJobService::class.java),
                    ).addTriggerContentUri(
                        JobInfo.TriggerContentUri(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS,
                        ),
                    ).addTriggerContentUri(
                        JobInfo.TriggerContentUri(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS,
                        ),
                    ).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(false)
                    .build()

            jobScheduler.schedule(jobInfo)
        }

        fun isScheduled(context: Context): Boolean {
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            return jobScheduler.allPendingJobs.any { it.id == JOB_ID }
        }
    }
}

private suspend fun Array<Uri>.mapToChangeEvent(contentResolver: ContentResolver): List<FileChangeEvent> {
    val validUri = this.filter { it.lastPathSegment?.toLongOrNull() != null }
    val audioUris = validUri.filter { it.isAudioUri() }
    val videoUris = validUri.filter { it.isVideoUri() }

    val validAudioIds =
        contentResolver.filterValidAudioFromIds(audioUris.map { it.lastPathSegment!!.toLong() })
    val validVideoIds =
        contentResolver.filterValidVideoFromIds(videoUris.map { it.lastPathSegment!!.toLong() })

    val audioChangeEvent = audioUris.mapToChangeEvent(validAudioIds)
    val videoChangeEvent = videoUris.mapToChangeEvent(validVideoIds)
    return audioChangeEvent + videoChangeEvent
}

private fun List<Uri>.mapToChangeEvent(validIds: List<Long>): List<FileChangeEvent> =
    this.map { audioUri ->
        val id = audioUri.lastPathSegment!!.toLong()
        if (validIds.contains(id)) {
            FileChangeEvent(
                fileUri = audioUri.toString(),
                fileChangeType = FileChangeType.MODIFY,
            )
        } else {
            FileChangeEvent(
                fileUri = audioUri.toString(),
                fileChangeType = FileChangeType.DELETE,
            )
        }
    }

private suspend fun ContentResolver.filterValidAudioFromIds(ids: List<Long>) =
    withContext(Dispatchers.IO) {
        query2(
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.DATA),
            selection = "${MediaStore.Audio.Media._ID} IN (${ids.joinToString(",") { "?" }})",
            selectionArgs = ids.map { it.toString() }.toTypedArray(),
        )?.use { cursor ->
            val validIds = mutableListOf<Long>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val path = cursor.getString(1)
                Napier.d(tag = TAG) { "id: $id, path: $path" }

                if (path == null || !File(path).exists()) {
                    continue
                }

                validIds.add(id)
            }
            validIds
        } ?: emptyList()
    }

private suspend fun ContentResolver.filterValidVideoFromIds(ids: List<Long>) =
    withContext(Dispatchers.IO) {
        query2(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.VideoColumns.DATA),
            selection = "${MediaStore.Video.Media._ID} IN (${ids.joinToString(",") { "?" }})",
            selectionArgs = ids.map { it.toString() }.toTypedArray(),
        )?.use { cursor ->
            val validIds = mutableListOf<Long>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val path = cursor.getString(1)
                Napier.d(tag = TAG) { "id: $id, path: $path" }

                if (path == null || !File(path).exists()) {
                    continue
                }

                validIds.add(id)
            }
            validIds
        } ?: emptyList()
    }

private val EXTERNAL_PATH_SEGMENTS = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.pathSegments

private fun Uri.isSpecificFile(): Boolean = pathSegments.size == EXTERNAL_PATH_SEGMENTS.size + 1
