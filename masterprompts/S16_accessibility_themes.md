# S16 — ACCESSIBILITY + MULTI-THEME

> **Prerequisites:** S00 active · S15 complete · `assembleDebug` passing
> **Output:** 3 files — AccessibilityUtils, ContentDescriptions, gender-neutral theme additions
> **Build check:** `./gradlew assembleDebug` — must pass. Test with TalkBack enabled.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S16_report.md`

Include:
- 📄 All 3 files created (checklist)
- ✅ Build result (SUCCESS / FAILED)
- ♿ TalkBack: can navigate Home screen with eyes closed (yes/no)
- 🎨 Gender-neutral Teal theme: renders correctly (yes/no)
- 🔤 Font size 200%: no clipped text (yes/no)
- ✅ Contrast ratio ≥4.5:1 for body text (verified, yes/no)
- ✅ All 8 accessibility requirements met (checklist)
- ❌ Errors and fixes applied

---

## PROMPT

Create accessibility utilities and ensure all screens comply.
Write all 3 files completely.

---

### FILE 1: `ui/util/AccessibilityUtils.kt`

```kotlin
package com.periodsaathi.app.ui.util

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════
// MINIMUM TOUCH TARGET ENFORCEMENT (48dp x 48dp)
// ═══════════════════════════════════════════════

/**
 * Ensures any composable meets the 48dp minimum touch target requirement.
 * Apply to ALL interactive elements: Button, IconButton, Checkbox, etc.
 */
fun Modifier.minimumTouchTarget(size: Dp = 48.dp): Modifier =
  this.defaultMinSize(minWidth = size, minHeight = size)

/**
 * Touch target with custom width and height.
 */
fun Modifier.minimumTouchTarget(width: Dp = 48.dp, height: Dp = 48.dp): Modifier =
  this.defaultMinSize(minWidth = width, minHeight = height)

// ═══════════════════════════════════════════════
// SEMANTIC HELPERS
// ═══════════════════════════════════════════════

/**
 * Marks a composable as a section heading for screen readers.
 * TalkBack will announce this with "Heading" role.
 */
fun Modifier.accessibilityHeading(): Modifier =
  this.semantics { heading() }

/**
 * Marks a composable as decorative (no screen reader announcement).
 */
fun Modifier.decorative(): Modifier =
  this.semantics { contentDescription = ""; hideFromAccessibility() }

/**
 * Custom interactive element with role and action label.
 * Use when standard composables don't carry enough semantic info.
 */
fun Modifier.accessibleInteractive(
  role: Role = Role.Button,
  label: String,
  onClick: () -> Unit
): Modifier = this.semantics {
  this.role = role
  this.contentDescription = label
  this.onClick(label = label, action = { onClick(); true })
}

/**
 * Live region — screen reader reads this when content changes.
 * Use for: loading status, error messages, countdown timers.
 */
fun Modifier.liveRegionPolite(): Modifier =
  this.semantics { liveRegion = LiveRegionMode.Polite }

fun Modifier.liveRegionAssertive(): Modifier =
  this.semantics { liveRegion = LiveRegionMode.Assertive }

/**
 * Group related composables for screen reader traversal.
 * Example: label + value pairs should be merged.
 */
fun Modifier.semanticGroup(): Modifier =
  this.semantics(mergeDescendants = true) {}

// ═══════════════════════════════════════════════
// TEXT SCALING SAFETY
// ═══════════════════════════════════════════════

/**
 * Clamps font scaling to avoid layout breaks at extreme accessibility sizes.
 * Use ONLY for non-body text where scaling would break layout.
 * Body text should ALWAYS scale freely.
 */
fun TextUnit.scaleSafe(maxScale: Float = 1.3f): TextUnit {
  // This should be applied via LocalDensity, not TextUnit directly
  // Use in combination with LocalTextStyle.current.fontSize
  return this  // Placeholder — actual implementation uses CompositionLocal
}

// ═══════════════════════════════════════════════
// FOCUS MANAGEMENT
// ═══════════════════════════════════════════════

/**
 * Draw a visible focus ring using drawBehind.
 * Required for keyboard/D-pad navigation visibility.
 */
fun Modifier.focusRing(
  color: Color = BlushPink,
  strokeWidth: Dp = 2.dp,
  cornerRadius: Dp = 8.dp
): Modifier = this.onFocusChanged { focusState ->
  // drawBehind { if (focusState.isFocused) drawRoundRect(...) }
  // Full implementation with drawBehind and Indication
}

// ═══════════════════════════════════════════════
// CYCLE RING ACCESSIBILITY
// ═══════════════════════════════════════════════

/**
 * Semantic for CycleRing — announces full state to screen readers.
 */
fun Modifier.cycleRingSemantics(currentDay: Int, totalDays: Int, phaseName: String): Modifier =
  this.semantics {
    contentDescription = "Cycle ring, day $currentDay of $totalDays. Phase: $phaseName"
    role = Role.Image
  }

/**
 * Semantic for WaterRing — announces hydration state.
 */
fun Modifier.waterRingSemantics(current: Int, total: Int): Modifier =
  this.semantics {
    contentDescription = "Water intake ring, $current of $total glasses today"
    role = Role.Image
    stateDescription = if (current >= total) "Goal reached" else "${total - current} glasses remaining"
  }
```

---

### FILE 2: `ui/util/ContentDescriptions.kt`

All content descriptions as constants — supports future localization via `strings.xml`:

```kotlin
package com.periodsaathi.app.ui.util

/**
 * ALL content descriptions for accessibility.
 * 
 * Rules:
 * - Never null for interactive elements
 * - Use string resource when possible (for i18n)
 * - Format strings use %d/%s format (format with String.format or .format())
 */
object ContentDescriptions {

  // MASCOT
  const val MASCOT_HAPPY = "Saathi mascot, happy and waving"
  const val MASCOT_SAD = "Saathi mascot, looking sad"
  const val MASCOT_SLEEPING = "Saathi mascot, sleeping"
  const val MASCOT_EXCITED = "Saathi mascot, very excited"
  const val MASCOT_PAIN = "Saathi mascot, feeling discomfort"
  const val MASCOT_LISTENING = "Saathi mascot, listening attentively"

  // CYCLE RING — use String.format(CYCLE_RING, day, total)
  const val CYCLE_RING = "Cycle progress ring, day %d of %d"
  const val CYCLE_PHASE = "Current phase: %s"

  // WATER
  const val WATER_RING = "Water intake, %d of %d glasses today"
  const val ADD_WATER_ONE = "Add one glass of water"
  const val ADD_WATER_500ML = "Add 500 millilitres of water"
  const val WATER_GOAL_REACHED = "Water goal reached for today"

  // CALENDAR
  const val CALENDAR_DAY = "Calendar day %s"
  const val CALENDAR_DAY_PERIOD = "Calendar day %s, period logged, intensity %s"
  const val CALENDAR_DAY_PREDICTED = "Calendar day %s, predicted period"
  const val CALENDAR_DAY_FERTILE = "Calendar day %s, fertile window"
  const val CALENDAR_DAY_PMS = "Calendar day %s, PMS phase"
  const val CALENDAR_DAY_TODAY = "Today, %s"
  const val CALENDAR_DAY_SELECTED = "Selected: %s"
  const val CALENDAR_NAV_PREV = "Go to previous month"
  const val CALENDAR_NAV_NEXT = "Go to next month"

  // PERIOD LOGGING
  const val PERIOD_START = "Mark period start for today"
  const val PERIOD_END = "Mark period end for today"
  const val FLOW_INTENSITY = "Flow intensity: %s"
  const val FLOW_LIGHT = "Light flow"
  const val FLOW_MEDIUM = "Medium flow"
  const val FLOW_HEAVY = "Heavy flow"

  // NAVIGATION
  const val NAV_HOME = "Home tab, shows today's cycle summary"
  const val NAV_CALENDAR = "Calendar tab, view and log cycle history"
  const val NAV_WELLNESS = "Wellness tab, track water and habits"
  const val NAV_SETTINGS = "Settings tab, configure your preferences"

  // BUTTONS
  const val LOG_PERIOD = "Log period for today"
  const val OPEN_JOURNAL = "Open journal"
  const val DISMISS = "Dismiss"
  const val CLOSE = "Close"
  const val BACK = "Go back"
  const val SAVE = "Save"
  const val DELETE = "Delete"
  const val EDIT = "Edit"
  const val SHARE = "Share"

  // WELLNESS
  const val HABIT_ITEM = "Habit: %s"
  const val HABIT_CHECKED = "Habit %s, completed"
  const val HABIT_UNCHECKED = "Habit %s, not yet completed"
  const val SLEEP_SLIDER = "Sleep hours slider, currently %s hours"
  const val EXERCISE_CHIP = "Exercise type: %s"

  // PARTNER MODE
  const val CARE_REQUEST = "Care request: %s"
  const val CARE_REQUEST_SELECTED = "Care request selected: %s"
  const val SEND_CARE_REQUESTS = "Send selected care requests to %s"

  // GAMIFICATION
  const val STREAK_COUNT = "Current streak: %d days"
  const val POINTS_TOTAL = "Total points: %d"
  const val REWARD_LOCKED = "Reward locked: %s, requires %d points"
  const val REWARD_UNLOCKED = "Reward unlocked: %s"
  const val REWARD_EQUIPPED = "Reward equipped: %s"

  // SECURITY
  const val BIOMETRIC_BUTTON = "Authenticate with fingerprint or face to unlock"
  const val PIN_DOT_FILLED = "PIN digit entered"
  const val PIN_DOT_EMPTY = "PIN digit empty"
  const val PIN_DELETE = "Delete last PIN digit"
  const val STEALTH_MODE_TOGGLE = "Stealth mode, disguises app icon and name"

  // PREDICTION
  const val PREDICTION_CARD = "Period prediction, approximately %d days away, %s confidence"
  const val PREDICTION_NOT_READY = "Period prediction not yet available, tracking %d of 3 required cycles"

  // LOADING STATES
  const val LOADING = "Loading, please wait"
  const val LOADING_CALENDAR = "Loading calendar data"
  const val LOADING_INSIGHTS = "Loading pattern insights"

  // ERRORS
  const val ERROR_GENERIC = "An error occurred. Please try again."
  const val ERROR_NETWORK = "No internet connection. Working offline."
  const val ERROR_SYNC = "Sync failed. Your data is safe locally."
}
```

---

### FILE 3: `ui/theme/ThemeExtensions.kt`

Gender-neutral theme system:

```kotlin
package com.periodsaathi.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════
// THEME SELECTION
// ═══════════════════════════════════════════════

enum class AppTheme(val displayName: String, val emoji: String) {
  BLUSH_PINK("Blush Pink", "🌸"),
  TEAL_NEUTRAL("Teal Neutral", "🌿"),
  LAVENDER_PURPLE("Lavender Purple", "💜"),
  MIDNIGHT_OCEAN("Midnight Ocean", "🌊"),
  SUNSET_CORAL("Sunset Coral", "🌅")
}

// ═══════════════════════════════════════════════
// TEAL NEUTRAL PALETTE (Gender-neutral)
// ═══════════════════════════════════════════════

val TealPrimary = Color(0xFF80CBC4)
val TealSecondary = Color(0xFF90A4AE)
val SkyTertiary = Color(0xFF81D4FA)
val NeutralBackground = Color(0xFFF5F7F8)
val TealAccent = Color(0xFF26A69A)
val SlateDark = Color(0xFF37474F)
val TealCard = Color(0xFFF0F7F7)

val TealNeutralColorScheme = lightColorScheme(
  primary = TealPrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFB2DFDB),
  onPrimaryContainer = SlateDark,
  secondary = TealSecondary,
  onSecondary = Color.White,
  tertiary = SkyTertiary,
  background = NeutralBackground,
  surface = TealCard,
  onBackground = SlateDark,
  onSurface = SlateDark,
  error = Color(0xFFB00020)
)

// ═══════════════════════════════════════════════
// LAVENDER PURPLE PALETTE
// ═══════════════════════════════════════════════

val LavenderPrimary = Color(0xFF9C27B0)
val LavenderLight = Color(0xFFCE93D8)
val LavenderBackground = Color(0xFFF9F4FF)

val LavenderColorScheme = lightColorScheme(
  primary = LavenderPrimary,
  onPrimary = Color.White,
  primaryContainer = LavenderLight,
  onPrimaryContainer = Color(0xFF1A0032),
  secondary = Color(0xFF6A1B9A),
  background = LavenderBackground,
  surface = Color(0xFFF3E5F5),
  onBackground = Color(0xFF1A0032),
  onSurface = Color(0xFF1A0032),
  error = Color(0xFFB00020)
)

// ═══════════════════════════════════════════════
// MIDNIGHT OCEAN PALETTE
// ═══════════════════════════════════════════════

val OceanDark = Color(0xFF0D1B2A)
val OceanMid = Color(0xFF1B2838)
val OceanPrimary = Color(0xFF00BCD4)
val OceanAccent = Color(0xFF80DEEA)

val MidnightOceanColorScheme = darkColorScheme(
  primary = OceanPrimary,
  onPrimary = OceanDark,
  primaryContainer = OceanMid,
  secondary = OceanAccent,
  background = OceanDark,
  surface = OceanMid,
  onBackground = Color.White,
  onSurface = Color.White
)

// ═══════════════════════════════════════════════
// THEME RESOLVER
// ═══════════════════════════════════════════════

fun getColorSchemeForTheme(theme: AppTheme, isDark: Boolean) = when (theme) {
  AppTheme.BLUSH_PINK -> if (isDark) DarkColorScheme else LightColorScheme
  AppTheme.TEAL_NEUTRAL -> TealNeutralColorScheme  // Always light
  AppTheme.LAVENDER_PURPLE -> LavenderColorScheme
  AppTheme.MIDNIGHT_OCEAN -> MidnightOceanColorScheme  // Always dark
  AppTheme.SUNSET_CORAL -> LightColorScheme  // Sunset uses same structure, different primaries
}

// ═══════════════════════════════════════════════
// UPDATED PeriodSaathiTheme — add themeSelection param
// ═══════════════════════════════════════════════

/**
 * Update the existing PeriodSaathiTheme in Theme.kt to accept:
 *   themeSelection: AppTheme = AppTheme.BLUSH_PINK
 *
 * Replace the colorScheme resolution with:
 *   val colorScheme = getColorSchemeForTheme(themeSelection, darkTheme)
 */

// ═══════════════════════════════════════════════
// ACCESSIBILITY COMPLIANCE IMPLEMENTATION NOTES
// ═══════════════════════════════════════════════

/**
 * MANDATORY ACCESSIBILITY CHECKLIST — implement in ALL screens:
 *
 * ✅ 1. Icons: ALL non-decorative icons have non-null contentDescription
 *          Decorative icons: Modifier.semantics { contentDescription = ""; hideFromAccessibility() }
 *
 * ✅ 2. Custom components: semantics { } block with role and state description
 *          CycleRing: .cycleRingSemantics(day, total, phaseName)
 *          WaterRing: .waterRingSemantics(current, total)
 *
 * ✅ 3. All buttons: semantics { role = Role.Button; onClick(label = "...") { ... } }
 *          OR use ScaleButton which inherits clickable semantics
 *
 * ✅ 4. Toggle states: semantics { stateDescription = if(enabled) "On" else "Off" }
 *
 * ✅ 5. Loading states: .liveRegionPolite() so screen reader announces changes
 *
 * ✅ 6. Error messages: .liveRegionAssertive() for immediate announcement
 *
 * ✅ 7. Section headings: Text with Modifier.accessibilityHeading()
 *
 * ✅ 8. LazyColumn lists: semantics { collectionInfo = CollectionInfo(rowCount, columnCount) }
 *          On each item: semantics { collectionItemInfo = CollectionItemInfo(index, 1, 0, 1) }
 *
 * FONT SCALE TEST PROCEDURE:
 *   1. Settings → Display → Font size → Largest (200%)
 *   2. Launch app — verify NO text is clipped on any screen
 *   3. If text clips: add scrollable Modifier or use scaleSafe() for non-body text
 *
 * COLOR CONTRAST VERIFICATION:
 *   BlushPink (#FFB5C8) on CreamWhite (#FFF8F5): Contrast ~1.5:1 — use for decorative only
 *   DarkNavy (#1A0E2E) on CreamWhite (#FFF8F5): Contrast ~14:1 — ✅ use for all body text
 *   BlushPink on DarkNavy: Contrast ~7.5:1 — ✅ sufficient for body text on dark bg
 *
 *   RULE: All body text must use DarkNavy on CreamWhite — NEVER BlushPink on CreamWhite for text
 */
```

---

## ACCESSIBILITY AUDIT SCREENS CHECKLIST

After implementing the above files, audit these 5 screens manually:

### Home Screen Audit
- [ ] Mascot: `contentDescription = ContentDescriptions.MASCOT_HAPPY`
- [ ] CycleRing: `.cycleRingSemantics(day, total, phase.displayName)`
- [ ] WaterRing: `.waterRingSemantics(current, total)`
- [ ] "+1 Glass" button: `contentDescription = ContentDescriptions.ADD_WATER_ONE`
- [ ] "Log Period" chip: `contentDescription = ContentDescriptions.LOG_PERIOD`
- [ ] Greeting text: `Modifier.accessibilityHeading()`
- [ ] Stats grid: `semantics { collectionInfo = CollectionInfo(2, 2) }`

### Calendar Screen Audit
- [ ] Each DayCell: `contentDescription` based on day state (uses ContentDescriptions.CALENDAR_DAY_* constants)
- [ ] Prev/Next month buttons: `.contentDescription(ContentDescriptions.CALENDAR_NAV_PREV/NEXT)`
- [ ] Month header: `Modifier.accessibilityHeading()`
- [ ] Fertility toggle: `semantics { stateDescription = "Fertility mode: On/Off" }`

### Settings Screen Audit
- [ ] All Toggle switches: `stateDescription`
- [ ] Stealth mode toggle: `ContentDescriptions.STEALTH_MODE_TOGGLE`
- [ ] Delete data button: proper `onClick` label "Delete all data"

### Partner Mode Audit
- [ ] Each care request chip: `ContentDescriptions.CARE_REQUEST` + selected state
- [ ] Send button: `ContentDescriptions.SEND_CARE_REQUESTS`
- [ ] Privacy info: `Modifier.accessibilityHeading()`

### Lock Screen Audit
- [ ] Biometric button: `ContentDescriptions.BIOMETRIC_BUTTON`
- [ ] PIN dots: `ContentDescriptions.PIN_DOT_FILLED/EMPTY` per dot
- [ ] Delete PIN button: `ContentDescriptions.PIN_DELETE`

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Enable TalkBack on device: Settings → Accessibility → TalkBack
Navigate through Home screen with eyes closed — all elements should be announced.
Write the report.
