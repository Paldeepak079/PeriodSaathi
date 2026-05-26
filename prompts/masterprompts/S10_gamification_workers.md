# S10 — GAMIFICATION, SYNC WORKER + NOTIFICATIONS

> **Prerequisites:** S00 active · S09 complete · `assembleDebug` passing
> **Output:** 7 Kotlin files — GamificationManager, SyncWorker, 3 Workers, NotificationHelper, BootReceiver
> **Build check:** `./gradlew assembleDebug` — must pass.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S10_report.md`

Include:
- 📄 All 7 files created (checklist)
- ✅ Build result (SUCCESS / FAILED)
- 🔔 Notification channels registered (list all 3)
- 🔄 Workers registered in Hilt (yes/no)
- 🎮 Gamification points system verified (checklist)
- ❌ Errors and fixes applied
- ⚠️ Any worker scheduling issues

---

## PROMPT

Create all background processing, gamification, and notification files.
Write every file completely.

---

### FILE 1: `data/gamification/GamificationManager.kt`

```kotlin
@Singleton
class GamificationManager @Inject constructor(
  private val settingsRepository: SettingsRepository
) {
  // Point values
  companion object {
    const val POINTS_WATER = 1
    const val POINTS_HABIT = 2
    const val POINTS_CYCLE_LOG = 3
    const val POINTS_JOURNAL = 2
    const val POINTS_STREAK_BONUS = 5    // every 7-day streak
    const val POINTS_CHALLENGE = 10      // completing a challenge
  }

  /**
   * Awards points and updates streak.
   * Returns [GamificationResult] with new total + any rewards unlocked.
   */
  suspend fun awardPoints(event: PointEvent): GamificationResult

  /**
   * Checks and updates streak. Call on every log action.
   * Streak breaks if no log for 2+ consecutive days.
   */
  suspend fun updateStreak(): Int

  /**
   * Checks if any rewards are newly unlocked at current point total.
   * Returns list of unlocked rewards (themes, accessories, badges).
   */
  suspend fun checkNewRewards(points: Int): List<Reward>
}

enum class PointEvent {
  WATER_LOGGED, HABIT_COMPLETED, CYCLE_LOGGED, JOURNAL_SAVED,
  CHALLENGE_COMPLETED, WEEK_STREAK_BONUS
}

data class GamificationResult(
  val pointsEarned: Int,
  val newTotal: Int,
  val streakCount: Int,
  val newRewards: List<Reward> = emptyList()
)

data class Reward(
  val id: String,
  val name: String,
  val type: RewardType,
  val emoji: String,
  val pointsRequired: Int
)

enum class RewardType { THEME, MASCOT_ACCESSORY, BADGE }

// Reward catalog — 20 rewards from 10 points up to 500 points
val REWARD_CATALOG: List<Reward> = listOf(
  Reward("theme_lavender", "Lavender Dream", RewardType.THEME, "💜", 50),
  Reward("theme_ocean", "Ocean Breeze", RewardType.THEME, "🌊", 100),
  Reward("acc_crown", "Princess Crown", RewardType.MASCOT_ACCESSORY, "👑", 75),
  Reward("acc_bow", "Pink Bow", RewardType.MASCOT_ACCESSORY, "🎀", 10),
  // ... full list of 20 rewards
)
```

---

### FILE 2: `worker/SyncWorker.kt`

```kotlin
@HiltWorker
class SyncWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted params: WorkerParameters,
  private val syncDataUseCase: SyncDataUseCase,
  private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      val result = syncDataUseCase()
      if (result.isSuccess) {
        settingsRepository.updateLastSyncTime(System.currentTimeMillis())
        Result.success()
      } else {
        // Retry up to 3 times with exponential backoff
        if (runAttemptCount < 3) Result.retry()
        else Result.failure()
      }
    } catch (e: Exception) {
      if (runAttemptCount < 3) Result.retry()
      else Result.failure(workDataOf("error" to e.message))
    }
  }

  companion object {
    const val WORK_NAME = "period_saathi_sync"

    fun schedule(context: Context) {
      val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

      val work = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
        .build()

      WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        work
      )
    }
  }
}
```

---

### FILE 3: `worker/ReminderWorker.kt`

```kotlin
@HiltWorker
class ReminderWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParams: WorkerParameters,
  private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    val type = inputData.getString("TYPE") ?: return Result.failure()
    val label = inputData.getString("LABEL") ?: type

    when (type) {
      "WATER" -> notificationHelper.showWaterReminder(
        currentGlasses = inputData.getInt("CURRENT", 0)
      )
      "MEDICINE" -> notificationHelper.showMedicineReminder(label)
      "PERIOD" -> notificationHelper.showPeriodPredictionReminder(
        daysUntil = inputData.getInt("DAYS", 0)
      )
      "YOGA" -> notificationHelper.showYogaReminder()
      "CUSTOM" -> notificationHelper.showCustomReminder(label)
    }
    return Result.success()
  }
}
```

---

### FILE 4: `worker/WidgetRefreshWorker.kt`

`CoroutineWorker` that:
- Reads latest cycle data from Room (suspend query)
- Updates Glance widget state using `GlanceStateDefinition`
- Calls `GlanceAppWidgetManager.updateAll(context, CycleDayWidget::class.java)`
- Schedule: `PeriodicWorkRequest` every 4 hours, tag "widget_refresh"
- Also exposes `scheduleOneTime(context)` for immediate refresh on tap

```kotlin
@HiltWorker
class WidgetRefreshWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted params: WorkerParameters,
  private val cycleRepository: CycleRepository,
  private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    // 1. Get today's entry + settings
    // 2. Build CycleWidgetState
    // 3. GlanceAppWidgetManager.getInstance(applicationContext).updateAll(...)
    return Result.success()
  }
}
```

---

### FILE 5: `notification/NotificationHelper.kt`

```kotlin
@Singleton
class NotificationHelper @Inject constructor(
  @ApplicationContext private val context: Context
) {

  companion object {
    const val CHANNEL_REMINDERS = "reminders"
    const val CHANNEL_INSIGHTS = "insights"
    const val CHANNEL_SYNC = "sync"
  }

  fun createNotificationChannels() {
    // REMINDERS: importance HIGH, vibration [0,300,100,300]
    // INSIGHTS: importance DEFAULT
    // SYNC: importance MIN (silent background)
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannels(listOf(
      // Create all 3 channels here with NotificationChannel
    ))
  }

  /**
   * 💧 Water reminder — shows current vs target glasses
   * Action button "Log Glass" fires ReminderActionReceiver
   */
  fun showWaterReminder(currentGlasses: Int) {
    val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
      .setSmallIcon(R.drawable.ic_notification)
      .setContentTitle("💧 Time to hydrate!")
      .setContentText("You've had $currentGlasses glasses today — keep going!")
      .setStyle(NotificationCompat.BigTextStyle()
        .bigText("You've had $currentGlasses glasses today. Staying hydrated during your cycle helps with cramps and bloating! 🌸"))
      .addAction(R.drawable.ic_water, "Log Glass", getPendingIntent(ACTION_LOG_WATER))
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .build()
    NotificationManagerCompat.from(context).notify(NOTIF_WATER, notification)
  }

  /**
   * 💊 Medicine reminder
   */
  fun showMedicineReminder(medicineName: String) {
    // Build notification with "Taken" action button
  }

  /**
   * 🌸 Period prediction reminder — ONLY if user opted in
   * Never shows if daysUntil is negative (use "still tracking" language)
   */
  fun showPeriodPredictionReminder(daysUntil: Int) {
    val text = when {
      daysUntil > 0 -> "🌸 Your period may arrive in $daysUntil days — be prepared!"
      daysUntil == 0 -> "🌸 Your period may start today!"
      else -> return // Never show if "late" — no "overdue" language
    }
    // Build notification
  }

  /**
   * 🧘 Yoga reminder
   */
  fun showYogaReminder() { /* ... */ }

  /**
   * 📝 Custom reminder
   */
  fun showCustomReminder(label: String) { /* ... */ }

  /**
   * 🛌 Rest day notification
   */
  fun showRestDayNotification() {
    // "Today looks like a heavy day — rest is self-care 💕"
    // DO NOT say "heavy day may be draining" — empathy first
  }

  private fun getPendingIntent(action: String): PendingIntent {
    val intent = Intent(context, ReminderActionReceiver::class.java).apply {
      this.action = action
    }
    return PendingIntent.getBroadcast(
      context, 0, intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
  }
}
```

---

### FILE 6: `notification/ReminderActionReceiver.kt`

`BroadcastReceiver` for notification action buttons:
- `ACTION_LOG_WATER`: calls `AddWaterUseCase(1)` via `CoroutineScope(IO).launch`
- `ACTION_MEDICINE_TAKEN`: logs medicine taken in DB
- Actions handled in `onReceive()` with proper coroutine handling

---

### FILE 7: `notification/BootReceiver.kt`

```kotlin
class BootReceiver : BroadcastReceiver() {
  @Inject lateinit var reminderDao: ReminderDao

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

    // Re-schedule all active reminders from DB using WorkManager
    // Also restart SyncWorker periodic schedule
    SyncWorker.schedule(context)
    WidgetRefreshWorker.schedule(context)

    // For each enabled reminder in ReminderDao:
    // Schedule ReminderWorker with appropriate data
  }
}
```

Also add to `AndroidManifest.xml`:
```xml
<receiver android:name=".notification.BootReceiver" android:exported="true">
  <intent-filter>
    <action android:name="android.intent.action.BOOT_COMPLETED"/>
  </intent-filter>
</receiver>

<receiver android:name=".notification.ReminderActionReceiver" android:exported="false"/>
```

And `RECEIVE_BOOT_COMPLETED` permission in manifest:
```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
```

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Fix ALL errors. Write the report.
