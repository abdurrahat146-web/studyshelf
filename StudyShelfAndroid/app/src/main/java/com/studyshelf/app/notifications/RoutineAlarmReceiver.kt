package com.studyshelf.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Fired by AlarmManager at the exact time a routine entry is scheduled.
 * Reschedules itself for the next occurrence (weekly repeat) after firing.
 */
class RoutineAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val routineId = intent.getStringExtra(EXTRA_ROUTINE_ID) ?: return
        val title = intent.getStringExtra(EXTRA_ROUTINE_TITLE) ?: "Study session"
        val notificationId = routineId.hashCode()

        NotificationChannels.createAll(context)
        NotificationHelper.notifyRoutineReminder(context, title, notificationId)

        // Reschedule for next week automatically (best-effort; app also
        // resyncs the full routine list on launch and after boot).
        RoutineAlarmScheduler.rescheduleNextWeek(context, intent)
    }

    companion object {
        const val EXTRA_ROUTINE_ID = "routine_id"
        const val EXTRA_ROUTINE_TITLE = "routine_title"
        const val EXTRA_HOUR = "hour"
        const val EXTRA_MINUTE = "minute"
        const val EXTRA_DAY_OF_WEEK = "day_of_week"
    }
}
