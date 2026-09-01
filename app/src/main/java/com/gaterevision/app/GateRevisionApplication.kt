package com.gaterevision.app

import android.app.Application
import com.gaterevision.app.notification.NotificationScheduler

class GateRevisionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationScheduler.scheduleDailyReminder(this)
    }
}
