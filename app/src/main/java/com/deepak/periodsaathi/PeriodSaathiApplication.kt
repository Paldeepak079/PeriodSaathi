package com.deepak.periodsaathi

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.deepak.periodsaathi.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid
import io.sentry.Sentry
import javax.inject.Inject

@HiltAndroidApp
class PeriodSaathiApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        initSentry()
        createNotificationChannels()
    }

    private fun initSentry() {
        val dsn = BuildConfig.SENTRY_DSN
        if (dsn.isNotBlank()) {
            try {
                SentryAndroid.init(this) { options ->
                    options.dsn = dsn
                    options.tracesSampleRate = 1.0
                    options.isEnableAutoSessionTracking = true
                    options.isEnableAppLifecycleBreadcrumbs = true
                    options.isEnableActivityLifecycleBreadcrumbs = true
                    options.isEnableSystemEventBreadcrumbs = true
                    options.isDebug = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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

            val waterChannel = NotificationChannel(
                "health_reminders",
                "Health Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for water, medicine, and self-care"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(periodChannel)
            notificationManager.createNotificationChannel(wellnessChannel)
            notificationManager.createNotificationChannel(waterChannel)
        }

        // Create notification channels using NotificationHelper
        NotificationHelper.createChannels(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
