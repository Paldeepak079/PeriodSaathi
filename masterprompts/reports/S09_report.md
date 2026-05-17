# S09 — ViewModel Data Layer Wiring Report

> **Date:** May 17, 2026  
> **Status:** ✅ Complete  
> **Build Check:** `assembleDebug` — **SUCCESS**

---

## 📄 Files Modified (6 Kotlin files)

### 1. JournalViewModel.kt
- **Before:** In-memory hardcoded entries, no persistence
- **After:** Injects `JournalDao`, loads entries from Room DB on init, persists save/delete via DAO, converts between Room and UI models
- **Mapping:** Room `moodEmoji: String?` → UI `moods: List<String>`, Room `cyclePhase` → UI `phase`

### 2. WellnessViewModel.kt
- **Before:** All water/habits/sleep data in-memory, lost on restart
- **After:** Injects `CycleDao`, loads today's water total from DB on init, persists water glass changes to `cycle_entries` table (upserts by date)

### 3. InsightsViewModel.kt
- **Before:** Hardcoded 3 insights with static values
- **After:** Injects `CycleRepository`, computes real insights from actual cycle data:
  - Cycle length average from detected cycle intervals
  - Period duration from period entry count
  - Symptom tracking days from non-empty symptom fields
  - Mood trend from entries with mood data
  - All with dynamic confidence scores based on data quantity

### 4. ReportViewModel.kt
- **Before:** Mock export with delay only, no data
- **After:** Injects `CycleRepository`, fetches real cycle data and settings before export (still simulated with delay, but data is available for actual file generation)

### 5. MoodMapViewModel.kt
- **Before:** Randomly generated 90 days of mock mood data
- **After:** Injects `CycleDao`, loads real mood data from `cycle_entries` table where `mood` is non-null, displays actual user-tracked moods

### 6. SettingsViewModel.kt
- **Before:** Missing `biometricLock` StateFlow and `toggleBiometricLock()` (build error when recompiled)
- **After:** Added `_biometricLock` state and `toggleBiometricLock()` function, fixed compile error

---

## 🔧 Build Fixes Applied

### Fix 1: SettingsViewModel missing biometricLock
- **Issue:** `SettingsScreen.kt:32` referenced `viewModel.biometricLock` and `viewModel.toggleBiometricLock()` but neither existed
- **Root Cause:** Pre-existing bug exposed when task cache was invalidated by other file changes
- **Fix:** Added `_biometricLock: MutableStateFlow<Boolean>` and `toggleBiometricLock()` to `SettingsViewModel`

### Fix 2: gradle.properties JDK path
- **Issue:** `jlink.exe` not found in VS Code extension JDK path
- **Root Cause:** Android Gradle plugin's `JdkImageTransform` used wrong JDK
- **Fix:** Added `org.gradle.java.home=C:/Program Files/Microsoft/jdk-21.0.10.7-hotspot` to `gradle.properties`

---

## 📊 ViewModel Injection Status

| ViewModel | Before | After | DAO/Repo Injected |
|-----------|--------|-------|-------------------|
| SplashViewModel | ✅ SettingsDao | ✅ SettingsDao | SettingsDao |
| OnboardingViewModel | ❌ Empty | ❌ Empty (UI-only pager) | *(none needed)* |
| LoginViewModel | ❌ Empty | ❌ Empty (mock auth) | *(none needed)* |
| NameSetupViewModel | ✅ SettingsDao | ✅ SettingsDao | SettingsDao |
| HomeViewModel | ✅ CycleRepository | ✅ CycleRepository | CycleRepository |
| CalendarViewModel | ✅ CycleRepository | ✅ CycleRepository | CycleRepository |
| WellnessViewModel | ❌ Empty | ✅ CycleDao | CycleDao |
| BreathingModeViewModel | ❌ Empty | ❌ Empty (timer/UI-only) | *(none needed)* |
| DayLogViewModel | ✅ CycleRepository | ✅ CycleRepository | CycleRepository |
| InsightsViewModel | ❌ Empty | ✅ CycleRepository | CycleRepository |
| JournalViewModel | ❌ Empty | ✅ JournalDao | JournalDao |
| MoodMapViewModel | ❌ Empty | ✅ CycleDao | CycleDao |
| ChallengesViewModel | ❌ Empty | ❌ Empty (in-memory) | *(needs Challenge table)* |
| SettingsViewModel | ✅ 4 DAOs | ✅ 4 DAOs + biometricLock | SettingsDao, CycleDao, JournalDao, ReminderDao |
| PartnerViewModel | ❌ Empty | ❌ Empty (static data) | *(none needed)* |
| RemediesViewModel | ❌ Empty | ❌ Empty (static data) | *(none needed)* |
| YogaFlowViewModel | ❌ Empty | ❌ Empty (timer/UI-only) | *(none needed)* |
| WardrobeViewModel | ❌ Empty | ❌ Empty (in-memory) | *(needs Accessory table)* |
| PaymentViewModel | ❌ Empty | ❌ Empty (mock billing) | *(needs Play Billing)* |
| ReportViewModel | ❌ Empty | ✅ CycleRepository | CycleRepository |

**Wired: 10/20** (was 6, now 10 — +40%)

---

## ✅ Build Result: **SUCCESS**

```
BUILD SUCCESSFUL in 1m 9s
44 actionable tasks: 6 executed, 38 up-to-date
```

---

## ⚠️ Warnings

1. **InsightsViewModel.kt:89** — Condition `it.symptoms != null` is always `true` (symptoms is non-nullable `String`, defaulting to `"[]"`). This is a non-issue, just a compiler warning.

---

## 🗺️ Remaining Stub ViewModels (no data layer)

These 6 ViewModels remain intentionally stub because they need new tables or external SDKs:

| ViewModel | Needs |
|-----------|-------|
| OnboardingViewModel | UI-only pager — no persistence needed |
| LoginViewModel | Firebase Auth SDK |
| BreathingModeViewModel | Timer/UI-only — no persistence needed |
| ChallengesViewModel | New Challenges/Goals table |
| PartnerViewModel | Static data — no persistence needed |
| RemediesViewModel | Static data — no persistence needed |
| YogaFlowViewModel | Timer/UI-only — no persistence needed |
| WardrobeViewModel | New Accessory/Points table |
| PaymentViewModel | Google Play Billing SDK |

---

## 📊 Summary

| Metric | Value |
|--------|-------|
| Files Modified | 6 Kotlin files |
| ViewModels newly wired | 5 (Journal, Wellness, Insights, Report, MoodMap) |
| SettingsViewModel bugfix | 1 (biometricLock) |
| Build Fixes | 2 (SettingsVM + gradle JDK path) |
| Build Check | ✅ PASS |
| ViewModels with real deps | 10/20 (50%) |

---

*Report generated: May 17, 2026*
