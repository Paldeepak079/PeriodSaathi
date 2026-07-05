package com.deepak.periodsaathi.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.deepak.periodsaathi.worker.WidgetRefreshWorker

class PeriodCountdownWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: PeriodCountdownWidget = PeriodCountdownWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            WidgetRefreshWorker.scheduleOneTime(context)
        }
    }
}
