package com.example.periodsaathi.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.periodsaathi.data.model.Reminder
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val WORK_TAG_PREFIX = "reminder_"

    fun scheduleReminder(context: Context, reminder: Reminder) {
        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val inputData = Data.Builder()
            .putString(ReminderWorker.KEY_REMINDER_TYPE, mapReminderType(reminder.type))
            .putLong(ReminderWorker.KEY_REMINDER_ID, reminder.id)
            .apply {
                if (reminder.type == "medicine") {
                    putString(ReminderWorker.KEY_MEDICINE_LABEL, reminder.label)
                }
            }
            .build()

        val intervalHours = reminder.intervalHours ?: getDefaultInterval(reminder.type)

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            intervalHours.toLong(), TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag("${WORK_TAG_PREFIX}${reminder.id}")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "${WORK_TAG_PREFIX}${reminder.id}",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelReminder(context: Context, reminderId: Long) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("${WORK_TAG_PREFIX}${reminderId}")
    }

    fun rescheduleAllReminders(context: Context, reminders: List<Reminder>) {
        val enabledReminders = reminders.filter { it.isEnabled }

        enabledReminders.forEach { reminder ->
            scheduleReminder(context, reminder)
        }

        scheduleDefaultReminders(context)
    }

    private fun scheduleDefaultReminders(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val waterConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val waterData = Data.Builder()
            .putString(ReminderWorker.KEY_REMINDER_TYPE, ReminderWorker.REMINDER_TYPE_WATER)
            .build()

        val waterWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(waterConstraints)
            .setInputData(waterData)
            .addTag("${WORK_TAG_PREFIX}daily_water")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "${WORK_TAG_PREFIX}daily_water",
            ExistingPeriodicWorkPolicy.UPDATE,
            waterWorkRequest
        )

        val insightConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val insightData = Data.Builder()
            .putString(ReminderWorker.KEY_REMINDER_TYPE, ReminderWorker.REMINDER_TYPE_INSIGHT)
            .putString(
                ReminderWorker.KEY_INSIGHT_TEXT,
                "Your weekly cycle summary is ready! Check out your insights to see patterns in your data."
            )
            .build()

        val insightWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            168, TimeUnit.HOURS
        )
            .setConstraints(insightConstraints)
            .setInputData(insightData)
            .addTag("${WORK_TAG_PREFIX}weekly_insight")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "${WORK_TAG_PREFIX}weekly_insight",
            ExistingPeriodicWorkPolicy.KEEP,
            insightWorkRequest
        )
    }

    private fun mapReminderType(type: String): String {
        return when (type.lowercase()) {
            "period", "period_prediction" -> ReminderWorker.REMINDER_TYPE_PERIOD_PREDICTION
            "water", "hydration" -> ReminderWorker.REMINDER_TYPE_WATER
            "medicine", "medication" -> ReminderWorker.REMINDER_TYPE_MEDICINE
            "rest", "rest_day" -> ReminderWorker.REMINDER_TYPE_REST_DAY
            "insight", "pattern" -> ReminderWorker.REMINDER_TYPE_INSIGHT
            else -> type
        }
    }

    private fun getDefaultInterval(type: String): Int {
        return when (type.lowercase()) {
            "water", "hydration" -> 2
            "medicine", "medication" -> 12
            "period", "period_prediction" -> 24
            "rest", "rest_day" -> 24
            "insight", "pattern" -> 168
            else -> 24
        }
    }
}
