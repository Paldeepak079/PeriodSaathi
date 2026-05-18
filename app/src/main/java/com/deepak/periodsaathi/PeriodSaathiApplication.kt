package com.deepak.periodsaathi

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.deepak.periodsaathi.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PeriodSaathiApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val periodChannel = NotificationChannel(
                "period_reminders",
                "Period Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for period reminders and predictions"
                enableVibration(true)
                enableLights(true)
            }

            val wellnessChannel = NotificationChannel(
                "wellness_tips",
                "Wellness Tips",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily wellness tips and self-care reminders"
                enableVibration(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(periodChannel)
            notificationManager.createNotificationChannel(wellnessChannel)
        }

        // Create notification channels using NotificationHelper
        NotificationHelper.createChannels(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
