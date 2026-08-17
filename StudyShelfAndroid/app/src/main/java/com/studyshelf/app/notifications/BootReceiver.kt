package com.studyshelf.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Android clears all AlarmManager alarms on reboot, so we listen for
 * BOOT_COMPLETED and re-arm everything: routine alarms + the periodic
 * shared-book poll worker.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        NotificationChannels.createAll(context)
        NotificationScheduler.schedulePeriodicSharedBookPoll(context)
        // Routine alarms are re-armed by MainActivity on next app open,
        // since they require reading the current routine list from storage.
    }
}
