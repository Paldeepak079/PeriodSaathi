package com.deepak.periodsaathi.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.deepak.periodsaathi.worker.WidgetRefreshWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PeriodCountdownWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: PeriodCountdownWidget = PeriodCountdownWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val manager = GlanceAppWidgetManager(context)
                        val ids = manager.getGlanceIds(PeriodCountdownWidget::class.java)
                        ids.forEach { id -> glanceAppWidget.update(context, id) }
                    } catch (_: Exception) {
                        WidgetRefreshWorker.scheduleOneTime(context)
                    }
                }
            }
            AppWidgetManager.ACTION_APPWIDGET_ENABLED -> {
                WidgetRefreshWorker.schedulePeriodic(context)
            }
        }
    }
}
