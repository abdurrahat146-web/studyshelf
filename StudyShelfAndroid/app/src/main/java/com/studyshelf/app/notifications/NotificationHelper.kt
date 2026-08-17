package com.studyshelf.app.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.studyshelf.app.MainActivity
import com.studyshelf.app.R

object NotificationHelper {

    private fun hasPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openAppIntent(context: Context, deepLink: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("deep_link", deepLink)
        }
        return PendingIntent.getActivity(
            context,
            deepLink.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Fired when a friend shares a book with the current user. */
    fun notifySharedBook(context: Context, fromUsername: String, bookTitle: String, notificationId: Int) {
        if (!hasPermission(context)) return

        val notification = NotificationCompat.Builder(context, NotificationChannels.SHARED_BOOKS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🔗 $fromUsername shared a book with you")
            .setContentText(bookTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$fromUsername shared \"$bookTitle\" with you. Tap to read it now."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(context, "inbox"))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    /** Fired at the scheduled time for a study routine entry. */
    fun notifyRoutineReminder(context: Context, routineTitle: String, notificationId: Int) {
        if (!hasPermission(context)) return

        val notification = NotificationCompat.Builder(context, NotificationChannels.ROUTINE_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("📅 Study time: $routineTitle")
            .setContentText("Your scheduled study session is starting now.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(context, "routine"))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
