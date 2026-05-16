package com.example.periodsaathi.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.periodsaathi.data.dao.CycleDao
import com.example.periodsaathi.data.dao.ReminderDao
import com.example.periodsaathi.data.repository.CycleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderDao: ReminderDao,
    private val cycleRepository: CycleRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val reminderType = inputData.getString(KEY_REMINDER_TYPE) ?: return Result.failure()
        val reminderId = inputData.getLong(KEY_REMINDER_ID, -1L)

        return try {
            when (reminderType) {
                REMINDER_TYPE_PERIOD_PREDICTION -> {
                    val prediction = cycleRepository.predictNextPeriod().first()
                    NotificationHelper.showPeriodPredictionNotification(
                        context = context,
                        daysUntil = prediction.daysUntil,
                        expectedDate = prediction.expectedDate
                    )
                }

                REMINDER_TYPE_WATER -> {
                    val today = LocalDate.now()
                    val entries = cycleRepository.getMonthEntries(today.year, today.monthValue).first()
                    val todayEntry = entries.find { entry ->
                        val entryDate = Instant.ofEpochMilli(entry.date)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        entryDate == today
                    }
                    val currentGlasses = todayEntry?.waterGlasses ?: 0
                    NotificationHelper.showWaterReminderNotification(context, currentGlasses)
                }

                REMINDER_TYPE_MEDICINE -> {
                    val label = inputData.getString(KEY_MEDICINE_LABEL) ?: "medication"
                    NotificationHelper.showMedicineReminderNotification(context, label)
                }

                REMINDER_TYPE_REST_DAY -> {
                    NotificationHelper.showRestDayNotification(context)
                }

                REMINDER_TYPE_INSIGHT -> {
                    val insight = inputData.getString(KEY_INSIGHT_TEXT) ?: "New pattern detected in your cycle data!"
                    NotificationHelper.showPatternInsightNotification(context, insight)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_REMINDER_TYPE = "reminder_type"
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_MEDICINE_LABEL = "medicine_label"
        const val KEY_INSIGHT_TEXT = "insight_text"

        const val REMINDER_TYPE_PERIOD_PREDICTION = "period_prediction"
        const val REMINDER_TYPE_WATER = "water"
        const val REMINDER_TYPE_MEDICINE = "medicine"
        const val REMINDER_TYPE_REST_DAY = "rest_day"
        const val REMINDER_TYPE_INSIGHT = "insight"
    }
}