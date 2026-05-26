# S11 — WIDGETS (JETPACK GLANCE)

> **Prerequisites:** S00 active · S10 complete · `assembleDebug` passing
> **Output:** 3 Kotlin files — CycleDayWidget, PeriodCountdownWidget, WidgetDataRepository
> **Build check:** `./gradlew assembleDebug` — must pass. Install on device and verify widget appears in picker.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S11_report.md`

Include:
- 📄 All 3 files + 2 XML files created (checklist)
- ✅ Build result (SUCCESS / FAILED)
- 📱 Widget appears in home screen widget picker (yes/no)
- 🔄 Widget updates on tap (yes/no)
- ❌ Errors and fixes applied
- ⚠️ Any Glance-specific issues (GlanceStateDefinition, etc.)

---

## PROMPT

Create all home screen widget files using Jetpack Glance.
Write all files completely.

---

### FILE 1: `widget/CycleDayWidget.kt`

```kotlin
class CycleDayWidget : GlanceAppWidget() {

  // State definition
  data class CycleWidgetState(
    val cycleDay: Int = 0,
    val phaseName: String = "Tracking...",
    val phaseEmoji: String = "🌸",
    val waterCount: Int = 0,
    val totalWater: Int = 8,
    val isLoading: Boolean = true
  )

  override val stateDefinition: GlanceStateDefinition<CycleWidgetState> =
    object : GlanceStateDefinition<CycleWidgetState> {
      // Implement using DataStore
    }

  override val sizeMode = SizeMode.Responsive(
    setOf(DpSize(110.dp, 110.dp), DpSize(220.dp, 110.dp))
  )

  @Composable
  override fun Content() {
    val state = currentState<CycleWidgetState>()
    val size = LocalSize.current

    GlanceTheme {
      Box(
        modifier = GlanceModifier
          .fillMaxSize()
          .background(Color(0xFFFFF8F5))
          .appWidgetBackground()
          .cornerRadius(16.dp)
          .clickable(actionRunCallback<RefreshWidgetAction>())
      ) {
        if (size.width >= 220.dp) {
          // MEDIUM layout (4×2)
          MediumWidgetContent(state)
        } else {
          // SMALL layout (2×2)
          SmallWidgetContent(state)
        }
      }
    }
  }
}

@Composable
private fun SmallWidgetContent(state: CycleWidgetState) {
  Column(
    modifier = GlanceModifier.fillMaxSize().padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      provider = ImageProvider(R.drawable.ic_mascot_happy),
      contentDescription = "Saathi",
      modifier = GlanceModifier.size(36.dp)
    )
    Text(
      text = "Day ${state.cycleDay}",
      style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorProvider(Color(0xFF3D2C35)))
    )
    Text(
      text = state.phaseEmoji,
      style = TextStyle(fontSize = 16.sp)
    )
    Text(
      text = "💧 ${state.waterCount}/${state.totalWater}",
      style = TextStyle(fontSize = 11.sp, color = ColorProvider(Color(0xFF6B5B95)))
    )
  }
}

@Composable
private fun MediumWidgetContent(state: CycleWidgetState) {
  Row(modifier = GlanceModifier.fillMaxSize().padding(12.dp)) {
    // Left: mascot
    Image(provider = ImageProvider(R.drawable.ic_mascot_happy), ...)
    Spacer(GlanceModifier.width(12.dp))
    // Right: cycle day + phase + water
    Column {
      Text("Day ${state.cycleDay}", ...) // Large bold
      Text("${state.phaseEmoji} ${state.phaseName}", ...)
      Text("💧 ${state.waterCount} of ${state.totalWater} glasses", ...)
    }
  }
}
```

**ActionCallback for refresh:**
```kotlin
class RefreshWidgetAction : ActionCallback {
  override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
    // Schedule one-time WidgetRefreshWorker
    WorkManager.getInstance(context).enqueue(
      OneTimeWorkRequestBuilder<WidgetRefreshWorker>().build()
    )
  }
}
```

**Tap deeplink action:**
Open MainActivity with Intent to navigate to Home screen.

**XML: `res/xml/cycle_day_widget_info.xml`**
```xml
<appwidget-provider
  android:description="@string/widget_cycle_day_desc"
  android:initialKeyguardLayout="@layout/widget_placeholder"
  android:minWidth="110dp"
  android:minHeight="110dp"
  android:previewImage="@drawable/widget_preview_cycle"
  android:resizeMode="horizontal|vertical"
  android:targetCellWidth="2"
  android:targetCellHeight="2"
  android:updatePeriodMillis="0"
  android:widgetCategory="home_screen"/>
```

---

### FILE 2: `widget/PeriodCountdownWidget.kt`

4×1 widget:

```kotlin
class PeriodCountdownWidget : GlanceAppWidget() {

  data class CountdownState(
    val daysUntilPeriod: Int? = null,   // null = tracking / not enough data
    val isInPeriod: Boolean = false,
    val cyclesLogged: Int = 0
  )

  @Composable
  override fun Content() {
    val state = currentState<CountdownState>()

    val text = when {
      state.isInPeriod -> "🌸 Period is here — rest and be kind to yourself 💕"
      state.daysUntilPeriod != null -> "🌸 Next period in ${state.daysUntilPeriod} days"
      state.cyclesLogged < 3 -> "🔮 Track ${3 - state.cyclesLogged} more cycle${if (3 - state.cyclesLogged != 1) "s" else ""} for prediction"
      else -> "🌸 Period tracking active"
    }

    Box(
      modifier = GlanceModifier
        .fillMaxSize()
        .background(Color(0xFFFFF8F5))
        .appWidgetBackground()
        .cornerRadius(16.dp)
        .clickable(actionStartActivity<MainActivity>())
        .padding(horizontal = 16.dp, vertical = 10.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      Text(
        text = text,
        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ColorProvider(Color(0xFF3D2C35))),
        maxLines = 1
      )
    }
  }
}
```

**XML: `res/xml/period_countdown_widget_info.xml`**
- targetCellWidth="4", targetCellHeight="1"
- minWidth="250dp", minHeight="54dp"

---

### FILE 3: `widget/WidgetDataRepository.kt`

```kotlin
class WidgetDataRepository(private val context: Context) {

  /**
   * Reads data for widgets directly from Room.
   * Must be called from a coroutine (Worker or suspend function).
   */
  suspend fun getCycleWidgetState(): CycleDayWidget.CycleWidgetState {
    val db = PeriodSaathiDatabase.create(context)
    val today = LocalDate.now().toEpochDay() * 86400000L
    val entry = db.cycleEntryDao().getEntryByDateSync(today)
    val settings = db.settingsDao().getSettingsSync()

    return CycleDayWidget.CycleWidgetState(
      cycleDay = calculateCycleDay(settings),
      phaseName = calculatePhase(settings).displayName,
      phaseEmoji = calculatePhase(settings).emoji,
      waterCount = entry?.waterGlasses ?: 0,
      totalWater = 8,
      isLoading = false
    )
  }

  suspend fun getCountdownState(): PeriodCountdownWidget.CountdownState {
    val db = PeriodSaathiDatabase.create(context)
    val settings = db.settingsDao().getSettingsSync()
    val recentStarts = db.cycleEntryDao().getPeriodStartDatesSync(limit = 10)
    
    // Calculate prediction (same algorithm as GetPredictionUseCase)
    // Return CountdownState
  }
}
```

Note: Use `getSettingsSync()` (non-suspending Room call) or wrap with `withContext(IO)`.

---

## ALSO UPDATE: `AndroidManifest.xml`

Register both widgets:
```xml
<receiver
  android:name=".widget.CycleDayWidget"
  android:exported="true"
  android:label="@string/widget_cycle_day_name">
  <intent-filter>
    <action android:name="android.appwidget.action.APPWIDGET_UPDATE"/>
  </intent-filter>
  <meta-data
    android:name="android.appwidget.provider"
    android:resource="@xml/cycle_day_widget_info"/>
</receiver>

<receiver
  android:name=".widget.PeriodCountdownWidget"
  android:exported="true"
  android:label="@string/widget_countdown_name">
  <intent-filter>
    <action android:name="android.appwidget.action.APPWIDGET_UPDATE"/>
  </intent-filter>
  <meta-data
    android:name="android.appwidget.provider"
    android:resource="@xml/period_countdown_widget_info"/>
</receiver>
```

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Install on device/emulator → long-press home → Widgets → search "Period Saathi" → verify both widgets appear.
Write the report.
