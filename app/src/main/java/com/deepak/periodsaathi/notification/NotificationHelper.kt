package com.deepak.periodsaathi.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.deepak.periodsaathi.MainActivity
import com.deepak.periodsaathi.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NotificationHelper {

    private const val CHANNEL_PERIOD_REMINDERS = "period_reminders"
    private const val CHANNEL_HEALTH_REMINDERS = "health_reminders"
    private const val CHANNEL_INSIGHTS = "insights"

    const val NOTIFICATION_ID_PERIOD_PREDICTION = 1001
    const val NOTIFICATION_ID_WATER_REMINDER = 1002
    const val NOTIFICATION_ID_MEDICINE_REMINDER = 1003
    const val NOTIFICATION_ID_INSIGHT = 1004
    const val NOTIFICATION_ID_REST_DAY = 1005

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val periodChannel = NotificationChannel(
                CHANNEL_PERIOD_REMINDERS,
                "Period Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for period predictions and reminders"
                enableVibration(true)
                enableLights(true)
            }

            val healthChannel = NotificationChannel(
                CHANNEL_HEALTH_REMINDERS,
                "Health Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for water, medicine, and self-care"
            }

            val insightsChannel = NotificationChannel(
                CHANNEL_INSIGHTS,
                "Pattern Insights",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "AI-generated pattern insights and discoveries"
            }

            notificationManager.createNotificationChannels(
                listOf(periodChannel, healthChannel, insightsChannel)
            )
        }
    }

    fun showPeriodPredictionNotification(
        context: Context,
        daysUntil: Int,
        expectedDate: LocalDate
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "calendar")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_PERIOD_PREDICTION,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val logActionIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("screen", "log_today")
        }
        val logPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_PERIOD_PREDICTION + 100,
            logActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val viewActionIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("screen", "calendar")
        }
        val viewPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_PERIOD_PREDICTION + 200,
            viewActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentTitle = when (daysUntil) {
            0 -> "Your period is here today!"
            1 -> "Your period is expected tomorrow"
            else -> "Period expected in $daysUntil days"
        }

        val formattedDate = expectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
        val contentText = "Expected: $formattedDate"

        val bigText = when (daysUntil) {
            0 -> "Your period has arrived! Take it easy today - remember to stay hydrated and rest if needed. You've got this!"
            1 -> "Your period is expected tomorrow! Make sure you have supplies ready. A great day for self-care!"
            else -> "Your next period is expected on $formattedDate. Keep tracking your symptoms to improve prediction accuracy. Stay prepared!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_PERIOD_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_notification, "Log Today", logPendingIntent)
            .addAction(R.drawable.ic_notification, "View Calendar", viewPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PERIOD_PREDICTION, notification)
        } catch (e: SecurityException) {
        }
    }

    fun showWaterReminderNotification(context: Context, currentGlasses: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "water")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_WATER_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val remaining = 8 - currentGlasses
        val contentTitle = if (remaining > 0) "Time to hydrate!" else "Great job hydrating!"
        val contentText = if (remaining > 0) "$remaining more glasses to reach your daily goal" else "You've hit your 8 glasses today!"
        val bigText = if (remaining > 0) "Stay hydrated! Drinking water can help reduce cramps and improve your energy levels. You're at $currentGlasses/8 glasses today. Keep going!" else "Amazing! You've completed your water intake for today. Proper hydration helps manage bloating and keeps you energized. Great work!"

        val notification = NotificationCompat.Builder(context, CHANNEL_HEALTH_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_WATER_REMINDER, notification)
        } catch (e: SecurityException) {
        }
    }

    fun showMedicineReminderNotification(context: Context, label: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "medicine")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_MEDICINE_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HEALTH_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Medicine Reminder")
            .setContentText("Time to take your $label")
            .setStyle(NotificationCompat.BigTextStyle().bigText("It's time to take your $label. Stay consistent with your medication for best results!"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_MEDICINE_REMINDER, notification)
        } catch (e: SecurityException) {
        }
    }

    fun showPatternInsightNotification(context: Context, insight: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "insights")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_INSIGHT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_INSIGHTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New Pattern Insight")
            .setContentText(insight.take(50) + if (insight.length > 50) "..." else "")
            .setStyle(NotificationCompat.BigTextStyle().bigText(insight))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_INSIGHT, notification)
        } catch (e: SecurityException) {
        }
    }

    fun showRestDayNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "home")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_REST_DAY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PERIOD_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Rest Day Reminder")
            .setContentText("Your body is telling you to take it easy today")
            .setStyle(NotificationCompat.BigTextStyle().bigText("It's a rest day for you! Your body works hard during your cycle - today is the perfect day for self-care. Try some light stretching, stay hydrated, and be kind to yourself."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REST_DAY, notification)
        } catch (e: SecurityException) {
        }
    }

    fun showYogaReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "yoga")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_REST_DAY + 10,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HEALTH_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🧘 Time for Yoga!")
            .setContentText("A gentle yoga flow can help ease cramps and boost your mood")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Time for some self-care! A gentle yoga flow can help ease cramps, reduce stress, and boost your energy. Your body will thank you! 🌸"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REST_DAY + 10, notification)
        } catch (e: SecurityException) {
        }
    }

    fun showCustomReminderNotification(context: Context, label: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", "home")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_REST_DAY + 20,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_HEALTH_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("📝 $label")
            .setContentText("Time to check in with yourself")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Time for your $label reminder! Take a moment to check in with yourself. You've got this! 💕"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REST_DAY + 20, notification)
        } catch (e: SecurityException) {
        }
    }
}

