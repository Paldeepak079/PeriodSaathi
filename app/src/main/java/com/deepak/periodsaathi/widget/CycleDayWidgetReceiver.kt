package com.deepak.periodsaathi.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.deepak.periodsaathi.worker.WidgetRefreshWorker

class CycleDayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: CycleDayWidget = CycleDayWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            WidgetRefreshWorker.scheduleOneTime(context)
        }
    }
}
