package com.studyshelf.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.studyshelf.app.data.model.RoutineEntry
import java.util.Calendar

/**
 * Schedules exact, repeating (weekly) alarms for each enabled RoutineEntry
 * using AlarmManager.setExactAndAllowWhileIdle + manual weekly reschedule
 * (AlarmManager has no native "repeat weekly on these days" primitive).
 */
object RoutineAlarmScheduler {

    fun scheduleAll(context: Context, routines: List<RoutineEntry>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        routines.filter { it.enabled }.forEach { routine ->
            routine.daysOfWeek.forEach { dayOfWeek ->
                scheduleOne(context, alarmManager, routine, dayOfWeek)
            }
        }
    }

    fun cancelAll(context: Context, routines: List<RoutineEntry>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        routines.forEach { routine ->
            routine.daysOfWeek.forEach { dayOfWeek ->
                val pi = buildPendingIntent(context, routine.id, routine.title, routine.hour, routine.minute, dayOfWeek)
                alarmManager.cancel(pi)
            }
        }
    }

    private fun scheduleOne(
        context: Context,
        alarmManager: AlarmManager,
        routine: RoutineEntry,
        dayOfWeek: Int
    ) {
        val triggerAt = nextOccurrence(routine.hour, routine.minute, dayOfWeek)
        val pi = buildPendingIntent(context, routine.id, routine.title, routine.hour, routine.minute, dayOfWeek)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback to inexact if exact-alarm permission not granted
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    /** Called by [RoutineAlarmReceiver] right after firing, to queue up next week's occurrence. */
    fun rescheduleNextWeek(context: Context, firedIntent: Intent) {
        val routineId = firedIntent.getStringExtra(RoutineAlarmReceiver.EXTRA_ROUTINE_ID) ?: return
        val title = firedIntent.getStringExtra(RoutineAlarmReceiver.EXTRA_ROUTINE_TITLE) ?: return
        val hour = firedIntent.getIntExtra(RoutineAlarmReceiver.EXTRA_HOUR, 0)
        val minute = firedIntent.getIntExtra(RoutineAlarmReceiver.EXTRA_MINUTE, 0)
        val dayOfWeek = firedIntent.getIntExtra(RoutineAlarmReceiver.EXTRA_DAY_OF_WEEK, Calendar.MONDAY)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = nextOccurrence(hour, minute, dayOfWeek, forceNextWeek = true)
        val pi = buildPendingIntent(context, routineId, title, hour, minute, dayOfWeek)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    private fun buildPendingIntent(
        context: Context,
        routineId: String,
        title: String,
        hour: Int,
        minute: Int,
        dayOfWeek: Int
    ): PendingIntent {
        val intent = Intent(context, RoutineAlarmReceiver::class.java).apply {
            putExtra(RoutineAlarmReceiver.EXTRA_ROUTINE_ID, routineId)
            putExtra(RoutineAlarmReceiver.EXTRA_ROUTINE_TITLE, title)
            putExtra(RoutineAlarmReceiver.EXTRA_HOUR, hour)
            putExtra(RoutineAlarmReceiver.EXTRA_MINUTE, minute)
            putExtra(RoutineAlarmReceiver.EXTRA_DAY_OF_WEEK, dayOfWeek)
        }
        // Unique request code per routine+day so different days don't overwrite each other
        val requestCode = (routineId + dayOfWeek).hashCode()
        return PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextOccurrence(hour: Int, minute: Int, dayOfWeek: Int, forceNextWeek: Boolean = false): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (forceNextWeek || target.before(now) || target == now) {
            target.add(Calendar.DAY_OF_YEAR, 7)
        }
        return target.timeInMillis
    }
}
