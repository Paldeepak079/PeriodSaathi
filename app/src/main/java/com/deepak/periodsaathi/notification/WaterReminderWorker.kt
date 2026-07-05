package com.deepak.periodsaathi.notification

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("water_reminder", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("enabled", false)
        if (!enabled) return Result.success()

        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (currentHour < 9 || currentHour >= 21) return Result.success()

        val currentGlasses = inputData.getInt("currentGlasses", 0)
        NotificationHelper.showWaterReminderNotification(applicationContext, currentGlasses)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "water_reminder_periodic"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
                2, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        fun isScheduled(context: Context): Boolean {
            val prefs = context.getSharedPreferences("water_reminder", Context.MODE_PRIVATE)
            return prefs.getBoolean("enabled", false)
        }

        fun setEnabled(context: Context, enabled: Boolean) {
            val prefs = context.getSharedPreferences("water_reminder", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("enabled", enabled).apply()
            if (enabled) schedule(context) else cancel(context)
        }
    }
}
