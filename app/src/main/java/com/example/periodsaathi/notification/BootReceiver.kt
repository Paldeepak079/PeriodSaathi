package com.example.periodsaathi.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.periodsaathi.data.database.PeriodSaathiDatabase
import com.example.periodsaathi.notifications.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAllReminders(context)
        }
    }

    private fun rescheduleAllReminders(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = PeriodSaathiDatabase.getInstance(context)
                val reminders = database.reminderDao().getEnabledReminders()
                reminders.collect { reminderList ->
                    if (reminderList.isNotEmpty()) {
                        ReminderScheduler.rescheduleAllReminders(context, reminderList)
                    } else {
                        // Schedule default reminders even if no custom reminders
                        ReminderScheduler.rescheduleAllReminders(context, emptyList())
                    }
                }
            } catch (e: Exception) {
                // If database not available, schedule default reminders
                ReminderScheduler.rescheduleAllReminders(context, emptyList())
            }
        }
    }
}