# PeriodSaathi Implementation Plan

## Model Assignment Legend

| Model | When to Use |
|-------|-------------|
| **DeepSeek V4 Flash Free** | Simple edits, imports, annotations, mojibake cleanup, mechanical fixes |
| **Nemotron 3 Super Free** | Moderate complexity — persistence logic, UI additions, error handling, repository switches |
| **Big Pickle** | Complex features — SDK integration, file generation, timer logic, cross-cutting refactors |

---

## Phase 1 — Critical Bugs (must fix before shipping)

### Task 1.1 — Wire Missing Navigation Callbacks
**Model: DeepSeek V4 Flash Free**

Files: `NavGraph.kt`

Changes:
- SettingsScreen: pass `onNavigateToPayment`, `onNavigateToReport`
- HomeScreen: pass `onNavigateToBreathing`  
- RemediesScreen: pass `onNavigateToYoga`

---

### Task 1.2 — Save Onboarding Completion
**Model: DeepSeek V4 Flash Free**

Files: `OnboardingViewModel.kt`, `SplashViewModel.kt`

Changes:
- Inject `SettingsDao` into `OnboardingViewModel`
- Call `settingsDao.upsertSettings(CycleSettings())` on `completeOnboarding()`
- This ensures `SplashViewModel` sees `settingsData != null` on next launch

---

### Task 1.3 — Fix Wellness Hardcoded Phase
**Model: DeepSeek V4 Flash Free**

Files: `WellnessViewModel.kt`

Changes:
- Replace `cyclePhase = "MENSTRUAL"` with `cycleRepository.getCurrentPhase()` result
- Collect phase in `init` block, pass to `addWater()`

---

### Task 1.4 — Fix DayLog Save Logic
**Model: Nemotron 3 Super Free**

Files: `DayLogViewModel.kt`, `DayLogScreen.kt`

Changes:
- Check for existing `CycleEntry` for given date before inserting
- If exists → call `cycleRepository.updateEntry()` instead of insert
- Add try-catch with error feedback to user

---

### Task 1.5 — Fix Biometric Toggle Not Saving
**Model: DeepSeek V4 Flash Free**

Files: `SettingsViewModel.kt`

Changes:
- Call `saveSettings()` inside `toggleBiometricLock()`

---

### Task 1.6 — Fix SignOut Navigation
**Model: DeepSeek V4 Flash Free**

Files: `SettingsViewModel.kt`, `NavGraph.kt`

Changes:
- After `deleteAllData()`, call a callback `onSignOut: () -> Unit`
- Wire callback in NavGraph to navigate to `Splash` or `Onboarding`

---

## Phase 2 — Data Integrity & Error Handling

### Task 2.1 — Wrap All DB Writes in Try-Catch
**Model: Nemotron 3 Super Free**

Files: `DayLogViewModel.kt`, `CalendarViewModel.kt`, `WellnessViewModel.kt`, `SettingsViewModel.kt`, `JournalViewModel.kt`

Changes:
- Wrap each DB write in try-catch
- Set error state or emit snackbar event on failure
- Consistent pattern across all ViewModels

---

### Task 2.2 — Fix Calendar Sheet Dismiss Race
**Model: DeepSeek V4 Flash Free**

Files: `CalendarViewModel.kt`

Changes:
- Move `hideLogSheet()` inside the coroutine, after DB insert completes

---

### Task 2.3 — Add Symptoms UI to LogEntryBottomSheet
**Model: Nemotron 3 Super Free**

Files: `CalendarScreen.kt`, `CalendarViewModel.kt`

Changes:
- Add symptom selection chips/chips to bottom sheet
- Pass real symptoms list to `onSave` callback
- Store symptoms when saving

---

### Task 2.4 — Add @Index on Date Columns
**Model: DeepSeek V4 Flash Free**

Files: `CycleEntry.kt`, `JournalEntry.kt`

Changes:
- Add `@Index(value = ["date"])` on both entities
- Increment database version + add migration

---

### Task 2.5 — Database Migration Strategy
**Model: Nemotron 3 Super Free**

Files: `PeriodSaathiDatabase.kt`

Changes:
- Replace `fallbackToDestructiveMigration(false)` with migration objects
- Add `Migration(1, 2)` for index changes
- Increment `version = 2`

---

## Phase 3 — Stub Features → Real Implementation

### Task 3.1 — ReportExport (CSV/PDF + Share)
**Model: Big Pickle**

Files: `ReportViewModel.kt`, `ReportExportScreen.kt`

Changes:
- Generate CSV string from cycle data
- Generate PDF using Android print framework or iText
- Create temp file, share via `Intent.ACTION_SEND`
- Loading/success/error states with proper feedback

---

### Task 3.2 — Payment/Razorpay Integration
**Model: Big Pickle**

Files: `PaymentViewModel.kt`, `PaymentScreen.kt`, `build.gradle.kts`

Changes:
- Wire Razorpay SDK (dependency exists but unused)
- Create order via API (or mock in dev)
- Handle payment success/failure callbacks
- Persist purchase state

---

### Task 3.3 — Wardrobe Persistence
**Model: Big Pickle**

Files: `WardrobeViewModel.kt`, `WardrobeScreen.kt`, `data/model/`, `data/dao/`

Changes:
- Create new Room entity `Accessory` (id, name, pointsCost, equipped, unlocked)
- Create `AccessoryDao` 
- ViewModel reads/writes to Room instead of in-memory
- Render `SaathiMascot` component instead of hardcoded emoji positions
- Wire confetti overlay component (already exists)

---

### Task 3.4 — Challenges Persistence
**Model: Nemotron 3 Super Free**

Files: `ChallengesViewModel.kt`, `ChallengesScreen.kt`, `data/model/`, `data/dao/`

Changes:
- Create new Room entity `ChallengeProgress` (id, challengeId, progress, completed, lastUpdated)
- Create `ChallengeDao`
- ViewModel persists progress to database
- Add daily reset tracking

---

### Task 3.5 — YogaFlow Timer Fix
**Model: Big Pickle**

Files: `YogaFlowViewModel.kt`

Changes:
- Rewrite timer to track per-pose elapsed time (not session-relative)
- Bind breathing cycle coroutine to timer lifecycle (pause both)
- Reset state properly on exit

---

## Phase 4 — Persistence & State Management

### Task 4.1 — Wellness Habits Persistence
**Model: Nemotron 3 Super Free**

Files: `WellnessViewModel.kt`, `data/model/`, `data/dao/`

Changes:
- Add `habit_completions` table (date, habitId, completed)
- Create `HabitDao`
- ViewModel saves/loads habits from DB instead of in-memory

---

### Task 4.2 — Add Loading States (Shimmer/Skeleton)
**Model: Nemotron 3 Super Free**

Files: `CalendarScreen.kt`, `InsightsScreen.kt`, `MoodMapScreen.kt`

Changes:
- Add `isLoading` state to ViewModels
- Show `ShimmerSkeleton` (component already exists) during loading
- Transition to content when data arrives

---

### Task 4.3 — Add Empty-State UI
**Model: DeepSeek V4 Flash Free**

Files: `CalendarScreen.kt`, `InsightsScreen.kt`, `JournalScreen.kt`, `ReportExportScreen.kt`

Changes:
- Show illustration + message when no data exists
- "Start tracking your cycle" CTA for Calendar
- "Journal entries will appear here" for Journal

---

### Task 4.4 — Fix Journal Delete Performance
**Model: DeepSeek V4 Flash Free**

Files: `JournalViewModel.kt`, `JournalDao.kt`

Changes:
- Add `@Query("SELECT * FROM journal_entries WHERE id = :id")` to `JournalDao`
- Use targeted query instead of `getAllEntries().first()`

---

## Phase 5 — Code Quality & Architecture

### Task 5.1 — Consolidate CycleEntry Model
**Model: Big Pickle**

Files: `HomeViewModel.kt`, `data/model/CycleEntry.kt`, `domain/model/`, any mappers

Changes:
- Remove `HomeViewModel.CycleEntry` (line 34)
- Use `data.model.CycleEntry` everywhere
- Update mappers and references across all files

---

### Task 5.2 — Consolidate MascotEmotion
**Model: Nemotron 3 Super Free**

Files: `HomeViewModel.kt:15`, `GetHomeDataUseCase.kt:28`, `ui/components/SaathiMascot.kt`, `HomeScreen.kt:46-50`

Changes:
- Pick one canonical `MascotEmotion` enum (component-level)
- Remove the other two
- Update all references, remove manual mapping functions

---

### Task 5.3 — Consolidate PatternInsight
**Model: DeepSeek V4 Flash Free**

Files: `InsightsViewModel.kt:17`, `CalculatePatternsUseCase.kt:13`

Changes:
- Merge into single class
- Update all references

---

### Task 5.4 — Switch DAO → Repository Injection
**Model: Nemotron 3 Super Free**

Files: `WellnessViewModel.kt`, `MoodMapViewModel.kt`, `JournalViewModel.kt`, `SettingsViewModel.kt`, `NameSetupViewModel.kt`

Changes:
- Replace `@Inject constructor(private val dao: XDao)` with repository
- Update method calls to use repository instead of DAO

---

### Task 5.5 — Remove Unused Components or Integrate
**Model: DeepSeek V4 Flash Free**

Files: `ConfettiOverlay.kt`, `ShimmerSkeleton.kt`, `PastelChip.kt`, `CycleRing.kt`

Changes:
- If component is wanted but not used → integrate into relevant screens
- If truly dead code → delete files + remove references

---

### Task 5.6 — Remove Unused Imports
**Model: DeepSeek V4 Flash Free**

Files: ~15 screen files

Changes:
- Remove unused `Brush`, `Dp`, `Path`, etc. imports across all files

---

## Phase 6 — Polish

### Task 6.1 — Fix Mojibake Emojis
**Model: DeepSeek V4 Flash Free**

Files: `MoodMapScreen.kt`, `PaymentScreen.kt`, `ReportExportScreen.kt`, `NameSetupScreen.kt`, `WardrobeScreen.kt`, `ChallengesScreen.kt`, `RemediesScreen.kt`

Changes:
- Replace corrupted byte sequences like `ðŸ˜Š` with proper Unicode emoji `😊`
- Replace `ðŸŒŸ` → `✨`, `ðŸŽ‰` → `🎉`, `âœ“` → `✓`, `âœ…` → `✅`, etc.

---

### Task 6.2 — Remove Hardcoded Values
**Model: Nemotron 3 Super Free**

Files: `PartnerViewModel.kt:37`, `WellnessViewModel.kt`, `JournalViewModel.kt`, `CalendarViewModel.kt`

Changes:
- Replace `"Aryan"` with value from settings/DB
- Replace magic numbers with named constants
- Replace hardcoded cycle phase strings with computed values

---

### Task 6.3 — Wire Settings → Report Navigation
**Model: DeepSeek V4 Flash Free**

Files: `NavGraph.kt`, `SettingsScreen.kt`

Changes:
- Pass `onNavigateToReport = { navController.navigate(ReportExport) }` in NavGraph

---

### Task 6.4 — Wire Settings → Payment Navigation
**Model: DeepSeek V4 Flash Free**

Files: `NavGraph.kt`, `SettingsScreen.kt`

Changes:
- Pass `onNavigateToPayment = { navController.navigate(Payment) }` in NavGraph

---

## Phase 7 — Testing & Verification

| Step | Check | Model |
|------|-------|-------|
| 7.1 | `./gradlew assembleDebug` — compilation passes | Any |
| 7.2 | Guest login → Home — no flash/return | Human QA |
| 7.3 | Google Sign-In → Home — no flash/return | Human QA |
| 7.4 | Settings → Payment navigates | Human QA |
| 7.5 | Settings → ReportExport navigates | Human QA |
| 7.6 | Home → Breathing navigates | Human QA |
| 7.7 | Remedies → YogaFlow navigates | Human QA |
| 7.8 | Create DayLog → reload — shows existing data | Human QA |
| 7.9 | Wellness water log — check phase is correct | Human QA |
| 7.10 | App restart — onboarding doesn't show again | Human QA |

---

## Quick Reference: Model × Phase

| Phase | DeepSeek V4 Flash Free | Nemotron 3 Super Free | Big Pickle |
|-------|----------------------|----------------------|------------|
| **1** | 1.1, 1.2, 1.3, 1.5, 1.6 | 1.4 | — |
| **2** | 2.2, 2.4 | 2.1, 2.3, 2.5 | — |
| **3** | — | 3.4 | 3.1, 3.2, 3.3, 3.5 |
| **4** | 4.3, 4.4 | 4.1, 4.2 | — |
| **5** | 5.3, 5.5, 5.6 | 5.2, 5.4 | 5.1 |
| **6** | 6.1, 6.3, 6.4 | 6.2 | — |
| **7** | QA | QA | QA |
