package com.deepak.periodsaathi.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class ReminderActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_LOG_WATER -> handleLogWater(context)
            ACTION_MEDICINE_TAKEN -> handleMedicineTaken(context, intent)
            ACTION_LOG_PERIOD -> handleLogPeriod(context)
        }
    }

    private fun handleLogWater(context: Context) {
        runBlocking(Dispatchers.IO) {
            com.deepak.periodsaathi.data.database.PeriodSaathiDatabase.getInstance(context).cycleDao().insertEntry(
                com.deepak.periodsaathi.data.model.CycleEntry(
                    date = System.currentTimeMillis(),
                    waterGlasses = 1
                )
            )
        }
    }

    private fun handleMedicineTaken(context: Context, intent: Intent) {
        val medicineLabel = intent.getStringExtra(EXTRA_MEDICINE_LABEL) ?: "medication"
        NotificationHelper.showMedicineReminderNotification(context, "$medicineLabel (taken)")
    }

    private fun handleLogPeriod(context: Context) {
        runBlocking(Dispatchers.IO) {
            com.deepak.periodsaathi.data.database.PeriodSaathiDatabase.getInstance(context).cycleDao().insertEntry(
                com.deepak.periodsaathi.data.model.CycleEntry(
                    date = System.currentTimeMillis(),
                    flowIntensity = "Medium"
                )
            )
        }
    }

    companion object {
        const val ACTION_LOG_WATER = "com.periodsaathi.app.ACTION_LOG_WATER"
        const val ACTION_MEDICINE_TAKEN = "com.periodsaathi.app.ACTION_MEDICINE_TAKEN"
        const val ACTION_LOG_PERIOD = "com.periodsaathi.app.ACTION_LOG_PERIOD"
        const val EXTRA_MEDICINE_LABEL = "medicine_label"
    }
}
