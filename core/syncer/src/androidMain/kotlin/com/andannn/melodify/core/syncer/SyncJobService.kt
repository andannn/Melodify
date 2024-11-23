package com.andannn.melodify.core.syncer

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.provider.MediaStore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import kotlin.coroutines.CoroutineContext

private const val TAG = "SyncJobService"

class SyncJobService : JobService(), CoroutineScope {
    private val syncer: MediaLibrarySyncer = getKoin().get<MediaLibrarySyncer>()

    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()

    override fun onStartJob(params: JobParameters?): Boolean {
        Napier.d(tag = TAG) { "onStartJob: $params" }

        launch {
            syncer.syncMediaLibrary()

            Napier.d(tag = TAG) { "sync finished" }

            // Tell the system that the job has completed
            jobFinished(params, false)
            // Schedule job again to watch next change.
            scheduleSyncLibraryJob(this@SyncJobService)
        }

        // Return true to indicate that the job should continue running
        return true
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

            val jobInfo = JobInfo.Builder(
                JOB_ID,
                ComponentName(context, SyncJobService::class.java)
            )
                .addTriggerContentUri(
                    JobInfo.TriggerContentUri(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS
                    )
                )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
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