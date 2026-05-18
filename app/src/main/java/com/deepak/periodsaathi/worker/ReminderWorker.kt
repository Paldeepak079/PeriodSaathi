package com.deepak.periodsaathi.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deepak.periodsaathi.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_TYPE) ?: return Result.failure()
        val label = inputData.getString(KEY_LABEL) ?: type

        return try {
            when (type) {
                TYPE_WATER -> {
                    val currentGlasses = inputData.getInt(KEY_CURRENT, 0)
                    NotificationHelper.showWaterReminderNotification(context, currentGlasses)
                }
                TYPE_MEDICINE -> NotificationHelper.showMedicineReminderNotification(context, label)
                TYPE_PERIOD -> {
                    val daysUntil = inputData.getInt(KEY_DAYS, 0)
                    val expectedDate = java.time.LocalDate.now().plusDays(daysUntil.toLong())
                    NotificationHelper.showPeriodPredictionNotification(context, daysUntil, expectedDate)
                }
                TYPE_YOGA -> NotificationHelper.showYogaReminderNotification(context)
                TYPE_CUSTOM -> NotificationHelper.showCustomReminderNotification(context, label)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_TYPE = "TYPE"
        const val KEY_LABEL = "LABEL"
        const val KEY_CURRENT = "CURRENT"
        const val KEY_DAYS = "DAYS"

        const val TYPE_WATER = "WATER"
        const val TYPE_MEDICINE = "MEDICINE"
        const val TYPE_PERIOD = "PERIOD"
        const val TYPE_YOGA = "YOGA"
        const val TYPE_CUSTOM = "CUSTOM"
    }
}
