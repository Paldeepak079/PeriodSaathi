package com.deepak.periodsaathi.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.deepak.periodsaathi.widget.CycleDayWidget
import com.deepak.periodsaathi.widget.PeriodCountdownWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class WidgetRefreshWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val glanceManager = GlanceAppWidgetManager(context)
            val cycleWidgetIds = glanceManager.getGlanceIds(CycleDayWidget::class.java)
            cycleWidgetIds.forEach { id ->
                CycleDayWidget().update(context, id)
            }
            val countdownWidgetIds = glanceManager.getGlanceIds(PeriodCountdownWidget::class.java)
            countdownWidgetIds.forEach { id ->
                PeriodCountdownWidget().update(context, id)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME_PERIODIC = "widget_refresh_periodic"
        private const val WORK_NAME_ONE_TIME = "widget_refresh_one_time"

        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val work = PeriodicWorkRequestBuilder<WidgetRefreshWorker>(
                30, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .addTag(WORK_NAME_PERIODIC)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
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

        fun refreshAllWidgets(context: Context) {
            scheduleOneTime(context)
        }
    }
}
