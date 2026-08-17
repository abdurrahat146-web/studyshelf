package com.studyshelf.app.notifications

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val SHARED_BOOK_POLL_WORK_NAME = "shared_book_poll_work"

    /**
     * WorkManager's minimum periodic interval is 15 minutes — that's the
     * fastest Android allows for battery-friendly background polling.
     */
    fun schedulePeriodicSharedBookPoll(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<SharedBookPollWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SHARED_BOOK_POLL_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelSharedBookPoll(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SHARED_BOOK_POLL_WORK_NAME)
    }
}
