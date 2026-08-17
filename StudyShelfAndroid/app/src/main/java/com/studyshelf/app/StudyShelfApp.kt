package com.studyshelf.app

import android.app.Application
import com.studyshelf.app.notifications.NotificationChannels
import com.studyshelf.app.notifications.NotificationScheduler
import com.studyshelf.app.premium.PremiumGate
import com.studyshelf.app.premium.PremiumGateImpl

class StudyShelfApp : Application() {

    /**
     * Resolves to the `free` or `premium` flavor's PremiumGateImpl depending
     * on which flavor this build is — see PremiumGate.kt for how the swap works.
     */
    val premiumGate: PremiumGate by lazy { PremiumGateImpl(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.createAll(this)
        NotificationScheduler.schedulePeriodicSharedBookPoll(this)
    }
}
