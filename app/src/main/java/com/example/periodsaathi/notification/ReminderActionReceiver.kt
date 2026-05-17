package com.example.periodsaathi.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_LOG_WATER -> handleLogWater(context)
            ACTION_MEDICINE_TAKEN -> handleMedicineTaken(context, intent)
            ACTION_LOG_PERIOD -> handleLogPeriod(context)
        }
    }

    private fun handleLogWater(context: Context) {
    }

    private fun handleMedicineTaken(context: Context, intent: Intent) {
        val medicineLabel = intent.getStringExtra(EXTRA_MEDICINE_LABEL) ?: "medication"
        NotificationHelper.showMedicineReminderNotification(context, "$medicineLabel (taken)")
    }

    private fun handleLogPeriod(context: Context) {
    }

    companion object {
        const val ACTION_LOG_WATER = "com.periodsaathi.app.ACTION_LOG_WATER"
        const val ACTION_MEDICINE_TAKEN = "com.periodsaathi.app.ACTION_MEDICINE_TAKEN"
        const val ACTION_LOG_PERIOD = "com.periodsaathi.app.ACTION_LOG_PERIOD"
        const val EXTRA_MEDICINE_LABEL = "medicine_label"
    }
}