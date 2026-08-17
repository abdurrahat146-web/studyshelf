package com.studyshelf.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {

    const val SHARED_BOOKS_CHANNEL = "shared_books_channel"
    const val ROUTINE_CHANNEL = "routine_channel"

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val sharedBooksChannel = NotificationChannel(
            SHARED_BOOKS_CHANNEL,
            "Shared Books",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies you when a friend shares a book with you"
        }

        val routineChannel = NotificationChannel(
            ROUTINE_CHANNEL,
            "Study Routine Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you when a scheduled study session starts"
        }

        manager.createNotificationChannel(sharedBooksChannel)
        manager.createNotificationChannel(routineChannel)
    }
}
