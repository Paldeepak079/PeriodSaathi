package com.example.periodsaathi

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PeriodSaathiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val remindersChannel = NotificationChannel(
                "reminders",
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Period, medicine, and water reminders"
                enableVibration(true)
            }

            val insightsChannel = NotificationChannel(
                "insights",
                "Insights",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pattern insights and predictions"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(remindersChannel)
            notificationManager.createNotificationChannel(insightsChannel)
        }
    }
}
