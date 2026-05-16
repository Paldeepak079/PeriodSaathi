# S15 — MONITORING + ANALYTICS

> **Prerequisites:** S00 active · S14 complete · CI green
> **Output:** 3 Kotlin files — CrashlyticsManager, PerformanceMonitor, BugReportManager
> **Build check:** `./gradlew assembleDebug` — must pass. Verify Firebase initialized.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S15_report.md`

Include:
- 📄 All 3 files created (checklist)
- ✅ Build result (SUCCESS / FAILED)
- 🔥 Firebase Crashlytics: initialized (yes/no)
- 📊 Analytics: opt-in only confirmed (yes/no)
- 🐛 Bug report: shake trigger working (yes/no)
- 🔒 Health data stripped from crash reports (verified, yes/no)
- ❌ Errors and fixes applied
- ⚠️ Any Firebase configuration issues

---

## PROMPT

Create monitoring and analytics infrastructure.
Write all files completely.

---

### FILE 1: `monitoring/CrashlyticsManager.kt`

```kotlin
@Singleton
class CrashlyticsManager @Inject constructor(
  private val settingsRepository: SettingsRepository
) {

  private val crashlytics get() = FirebaseCrashlytics.getInstance()

  /**
   * Initialize Crashlytics based on user consent.
   * MUST be called on app startup from PeriodSaathiApp.
   */
  fun initialize(analyticsOptedIn: Boolean) {
    crashlytics.setCrashlyticsCollectionEnabled(analyticsOptedIn)
    if (BuildConfig.DEBUG) {
      // Never collect in debug builds
      crashlytics.setCrashlyticsCollectionEnabled(false)
    }
  }

  /**
   * Log a named event — ONLY if analytics opted in.
   * Never include health data in params.
   */
  fun logEvent(event: String, params: Map<String, String> = emptyMap()) {
    if (BuildConfig.DEBUG) return
    runCatching {
      val analytics = FirebaseAnalytics.getInstance(/* context */)
      val bundle = Bundle().apply {
        params.forEach { (k, v) -> putString(k, v) }
      }
      analytics.logEvent(event, bundle)
    }
  }

  /**
   * Set user ID — hashed, never raw email or health data.
   */
  fun setUserId(userId: String) {
    val hashed = userId.toByteArray().let {
      MessageDigest.getInstance("SHA-256").digest(it)
    }.joinToString("") { "%02x".format(it) }.take(16) // First 16 chars only
    crashlytics.setUserId(hashed)
  }

  /**
   * Record exception — always recorded (for crash-free monitoring).
   * STRIPS any health data from message before recording.
   */
  fun recordException(e: Throwable, context: String = "") {
    val sanitizedMessage = sanitizeForCrashlytics(e.message ?: "")
    val sanitizedException = RuntimeException("[$context] $sanitizedMessage", e.cause)
    crashlytics.recordException(sanitizedException)
  }

  /**
   * Log screen view — ONLY if analytics opted in.
   */
  fun logScreenView(screenName: String) {
    if (BuildConfig.DEBUG) return
    logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, mapOf(
      FirebaseAnalytics.Param.SCREEN_NAME to screenName
    ))
  }

  // Pre-approved health-data-free events:
  fun logWaterGoalReached() = logEvent("water_goal_reached")
  fun logCycleLogged() = logEvent("cycle_logged") // NO intensity data
  fun logPurchaseCompleted(productId: String) = logEvent("purchase_completed", mapOf("product" to productId))
  fun logWidgetInteraction() = logEvent("widget_tapped")
  fun logPartnerShareUsed() = logEvent("partner_share_used")
  fun logOnboardingCompleted() = logEvent("onboarding_completed")
  fun logBiometricSetup() = logEvent("biometric_setup")

  /**
   * Strip health keywords from crash messages before sending.
   * Never log: period, bleeding, cramps, flow, intensity, symptom, mood, etc.
   */
  private fun sanitizeForCrashlytics(message: String): String {
    val healthKeywords = listOf(
      "period", "bleeding", "flow", "cramps", "symptoms", "mood",
      "intensity", "menstrual", "cycle", "ovulation", "fertile",
      "pregnant", "contraception", "medicine", "medication"
    )
    var sanitized = message
    healthKeywords.forEach { keyword ->
      sanitized = sanitized.replace(keyword, "[redacted]", ignoreCase = true)
    }
    return sanitized
  }
}
```

---

### FILE 2: `monitoring/PerformanceMonitor.kt`

```kotlin
@Singleton
class PerformanceMonitor @Inject constructor() {

  /**
   * App startup trace — wrap around critical startup path.
   * Usage: val trace = startTrace(TRACE_STARTUP); ... trace.stop()
   */
  fun startTrace(traceName: String): FirebasePerformance.Trace? {
    return if (BuildConfig.DEBUG) null
    else FirebasePerformance.getInstance().newTrace(traceName).also { it.start() }
  }

  companion object {
    const val TRACE_STARTUP = "app_startup"
    const val TRACE_HOME_RENDER = "home_screen_render"
    const val TRACE_CALENDAR_SCROLL = "calendar_scroll"
    const val TRACE_DB_SYNC = "database_sync"
  }

  /**
   * ANR Watchdog — detects if main thread is blocked for more than 5 seconds.
   * Logs to Crashlytics if blocked.
   */
  fun startAnrWatchdog() {
    if (BuildConfig.DEBUG) return // Only in release
    AnrWatchdog(timeout = 5000L).start()
  }
}

/**
 * Simple ANR detection via main thread health check.
 * Posts a message to main thread; if it doesn't execute within [timeout]ms,
 * the main thread is likely blocked.
 */
class AnrWatchdog(private val timeout: Long = 5000L) : Thread("anr-watchdog") {
  @Volatile private var tick = 0

  init { isDaemon = true }

  override fun run() {
    val handler = Handler(Looper.getMainLooper())
    while (!isInterrupted) {
      val postedTick = ++tick
      handler.post { tick = postedTick }
      sleep(timeout)
      if (tick != postedTick) {
        // Main thread is responsive — all good
      } else {
        // Main thread BLOCKED for >5 seconds — potential ANR
        FirebaseCrashlytics.getInstance().recordException(
          RuntimeException("Potential ANR detected: main thread blocked >${timeout}ms")
        )
      }
    }
  }
}
```

---

### FILE 3: `monitoring/BugReportManager.kt`

```kotlin
@Singleton
class BugReportManager @Inject constructor(
  @ApplicationContext private val context: Context,
  private val crashlyticsManager: CrashlyticsManager
) {

  companion object {
    private const val SHAKE_THRESHOLD = 12.0f  // m/s²
    private const val BUG_REPORT_EMAIL = "bugreport@periodsaathi.app"
    private const val MAX_LOG_LINES = 500
  }

  // Shake detection state
  private var lastShakeTime = 0L
  private var shakeCount = 0
  private var onBugReportTriggered: (() -> Unit)? = null

  fun setOnBugReportTriggered(callback: () -> Unit) {
    onBugReportTriggered = callback
  }

  /**
   * Process accelerometer data for shake detection.
   * Call from SensorEventListener in MainActivity.
   * 5 shakes within 3 seconds triggers bug report.
   */
  fun onSensorEvent(event: SensorEvent) {
    if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

    val x = event.values[0]; val y = event.values[1]; val z = event.values[2]
    val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

    if (acceleration > SHAKE_THRESHOLD) {
      val now = System.currentTimeMillis()
      if (now - lastShakeTime < 3000) {
        shakeCount++
        if (shakeCount >= 5) {
          shakeCount = 0
          onBugReportTriggered?.invoke()
        }
      } else {
        shakeCount = 1
      }
      lastShakeTime = now
    }
  }

  /**
   * Collects bug report data — strips ALL health information.
   * Opens email intent with pre-populated report.
   */
  fun sendBugReport(
    activity: Activity,
    includeScreenshot: Boolean = false,
    screenshot: Bitmap? = null
  ) {
    val report = buildReportString()
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_EMAIL, arrayOf(BUG_REPORT_EMAIL))
      putExtra(Intent.EXTRA_SUBJECT, "Period Saathi Bug Report — v${BuildConfig.VERSION_NAME}")
      putExtra(Intent.EXTRA_TEXT, report)
    }

    // Attach screenshot if user opted in
    if (includeScreenshot && screenshot != null) {
      val screenshotUri = saveScreenshotToCache(screenshot)
      emailIntent.apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, screenshotUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
    }

    activity.startActivity(Intent.createChooser(emailIntent, "Send Bug Report"))
  }

  private fun buildReportString(): String {
    return buildString {
      appendLine("=== PERIOD SAATHI BUG REPORT ===")
      appendLine("Response SLA: Within 48 hours")
      appendLine()
      appendLine("--- App Info ---")
      appendLine("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
      appendLine("Debug: ${BuildConfig.DEBUG}")
      appendLine()
      appendLine("--- Device Info ---")
      appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
      appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
      appendLine("RAM: ${getAvailableRam()}MB available")
      appendLine()
      appendLine("--- Recent Logs (HEALTH DATA STRIPPED) ---")
      appendLine(getRecentLogs())
      appendLine()
      appendLine("--- User Description ---")
      appendLine("[Please describe what happened here]")
      appendLine()
      appendLine("--- What I was doing ---")
      appendLine("[Please describe the steps to reproduce]")
    }
  }

  /**
   * Reads recent logcat output, strips health-related content.
   * NEVER includes: period data, intensity values, symptoms, mood, journal content.
   */
  private fun getRecentLogs(): String {
    return try {
      val process = Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-t", MAX_LOG_LINES.toString(), "*:W"))
      val logs = process.inputStream.bufferedReader().readText()
      sanitizeLogs(logs).take(5000) // Max 5KB of logs
    } catch (e: Exception) {
      "Unable to collect logs: ${e.message}"
    }
  }

  private fun sanitizeLogs(raw: String): String {
    val healthPatterns = listOf(
      Regex("""flow[_\s]?intensity[=:\s]+\d""", RegexOption.IGNORE_CASE),
      Regex("""period[_\s]?(start|end|day)[=:\s]+\w+""", RegexOption.IGNORE_CASE),
      Regex("""(cramps?|bleeding|symptoms?)[^,\n]*""", RegexOption.IGNORE_CASE),
      Regex("""mood[=:\s]+\w+""", RegexOption.IGNORE_CASE),
      Regex("""journal[_\s]content[^,\n]*""", RegexOption.IGNORE_CASE)
    )
    var sanitized = raw
    healthPatterns.forEach { pattern ->
      sanitized = sanitized.replace(pattern, "[health-data-redacted]")
    }
    return sanitized
  }

  private fun getAvailableRam(): Long {
    val mi = ActivityManager.MemoryInfo()
    (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(mi)
    return mi.availMem / (1024 * 1024)
  }

  private fun saveScreenshotToCache(bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "bug_report_screenshot.png")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 90, it) }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
  }
}
```

---

## ALSO: Update `PeriodSaathiApp.kt`

Add initialization:
```kotlin
@HiltAndroidApp
class PeriodSaathiApp : Application() {

  @Inject lateinit var crashlyticsManager: CrashlyticsManager
  @Inject lateinit var notificationHelper: NotificationHelper
  @Inject lateinit var performanceMonitor: PerformanceMonitor

  override fun onCreate() {
    super.onCreate()

    // Notification channels
    notificationHelper.createNotificationChannels()

    // Crashlytics — only after reading settings
    // (Read synchronously on app start — acceptable for settings only)
    val analyticsOptedIn = getSharedPreferences("prefs", MODE_PRIVATE)
      .getBoolean("analytics_opted_in", false)
    crashlyticsManager.initialize(analyticsOptedIn)

    // ANR watchdog (release only)
    performanceMonitor.startAnrWatchdog()
  }
}
```

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Verify Firebase is initialized (check Logcat for Firebase logs).
Write the report.
