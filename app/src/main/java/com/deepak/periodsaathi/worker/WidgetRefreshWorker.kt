package com.deepak.periodsaathi.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.deepak.periodsaathi.data.repository.CycleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@HiltWorker
class WidgetRefreshWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val cycleRepository: CycleRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val today = LocalDate.now()
            val settings = cycleRepository.getSettings().first()
            val cycleDay = cycleRepository.getCurrentCycleDay().first()
            val phase = cycleRepository.getCurrentPhase().first()

            val entries = cycleRepository.getMonthEntries(today.year, today.monthValue).first()
            val todayEntry = entries.find { entry ->
                val entryDate = java.time.Instant.ofEpochMilli(entry.date)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                entryDate == today
            }
            val waterGlasses = todayEntry?.waterGlasses ?: 0

            val widgetState = CycleWidgetState(
                cycleDay = cycleDay,
                phaseName = phase.displayName,
                phaseEmoji = phase.emoji,
                waterCount = waterGlasses,
                totalWater = settings.averageCycleLength
            )

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "widget_refresh"
        private const val WORK_NAME_ONE_TIME = "widget_refresh_one_time"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val work = PeriodicWorkRequestBuilder<WidgetRefreshWorker>(
                4, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
        }

        fun scheduleOneTime(context: Context) {
            val work = OneTimeWorkRequestBuilder<WidgetRefreshWorker>()
                .addTag(WORK_NAME_ONE_TIME)
                .build()

            WorkManager.getInstance(context).enqueue(work)
        }
    }
}

data class CycleWidgetState(
    val cycleDay: Int,
    val phaseName: String,
    val phaseEmoji: String,
    val waterCount: Int,
    val totalWater: Int
)
