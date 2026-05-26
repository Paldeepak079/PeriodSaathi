# S02 — ARCHITECTURE PLAN

> **Prerequisites:** S00 active · S01 complete
> **Output file:** `docs/ARCHITECTURE.md`
> **Build check:** No Gradle check. Verify file exists and structure is correct.

---

## REPORT INSTRUCTION
After completing this section, create:
`masterprompts/reports/S02_report.md`

Include:
- 📄 File created: `docs/ARCHITECTURE.md` (yes/no)
- 🗂️ Module structure documented (all folders listed)
- 📊 Data flow diagram included
- 🔄 Offline sync strategy documented
- ⚠️ Any design decisions or trade-offs noted

---

## PROMPT

Write the complete Architecture Plan document.

**Create:** `docs/ARCHITECTURE.md`

### SECTION 1: MODULE STRUCTURE

Document the EXACT folder/file structure:

```
app/src/main/java/com/periodsaathi/app/
├── PeriodSaathiApp.kt              — @HiltAndroidApp, channels, init
├── MainActivity.kt                  — single activity, edge-to-edge
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── PeriodSaathiDatabase.kt
│   │   │   ├── DatabaseModule.kt
│   │   │   └── Converters.kt
│   │   ├── dao/
│   │   │   ├── CycleEntryDao.kt
│   │   │   ├── SettingsDao.kt
│   │   │   ├── JournalDao.kt
│   │   │   ├── ReminderDao.kt
│   │   │   └── PendingSyncDao.kt
│   │   └── entity/
│   │       ├── CycleEntryEntity.kt
│   │       ├── SettingsEntity.kt
│   │       ├── JournalEntity.kt
│   │       ├── ReminderEntity.kt
│   │       └── PendingSyncEntity.kt
│   ├── remote/supabase/ (SupabaseClient, SupabaseModule, DTOs)
│   ├── repository/ (interfaces + impl for Cycle, Settings, Journal, Auth)
│   └── preferences/SecurePreferences.kt
│
├── domain/
│   ├── model/ (CycleEntry, PeriodPrediction, CyclePhase enum, etc.)
│   └── usecase/
│       ├── cycle/ (Start, End, Log, Predict, Phase, Calendar)
│       ├── wellness/ (Water, Habit, WellnessData)
│       ├── home/ (GetHomeData)
│       ├── sync/ (SyncData, ConflictResolution)
│       └── payment/ (CheckPremium, ProcessPurchase)
│
├── ui/
│   ├── theme/ (Color, Type, Shapes, Theme)
│   ├── navigation/ (Screen, NavGraph, BottomNavBar)
│   ├── components/ (15 shared components listed below)
│   ├── screens/ (17 screen folders)
│   └── util/ (HapticFeedback, AnimationUtils, DateFormatter, Extensions)
│
├── worker/ (SyncWorker, ReminderWorker, WidgetRefreshWorker)
├── widget/ (CycleDayWidget, PeriodCountdownWidget)
├── notification/ (NotificationHelper, BootReceiver)
└── security/ (BiometricManager, StealthModeManager, CertificatePinner)
```

For each folder, briefly explain its purpose (1 sentence each).

### SECTION 2: DATA FLOW DIAGRAM

Draw a text-art diagram showing:
```
User Tap → Composable → ViewModel (StateFlow) → UseCase → Repository → RoomDAO / SupabaseAPI
```
Include offline sync path via PendingSyncDao → SyncWorker.

### SECTION 3: OFFLINE SYNC STRATEGY

Document the `PendingSyncEntity` schema and full 7-step sync flow:
1. Every write → Room (immediate) + PendingSyncDao (queued)
2. SyncWorker triggers on: network available + periodic (15 min)
3. Batches 50 operations per request
4. Success: delete from PendingSyncDao
5. Failure: increment retryCount, exponential backoff
6. After 5 retries: mark FAILED, alert user in Settings
7. Pull sync on login: fetch remote, merge with timestamp comparison

### SECTION 4: DEPENDENCY INJECTION MAP

Show which Hilt components provide what:
- `SingletonComponent`: Database, DAOs, Repositories, SupabaseClient
- `ViewModelComponent`: UseCases (scoped to ViewModel)
- `ServiceComponent`: Workers (via @HiltWorker)

### SECTION 5: STATE MANAGEMENT PATTERN

Document the sealed class pattern for all UI states:
```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```
Show how this maps to Composable rendering with `when(state)`.

### SECTION 6: CONFLICT RESOLUTION RULES

Document last-write-wins logic:
- Every entity has `updatedAt: Long`
- Compare local `updatedAt` vs remote `updatedAt`
- Higher timestamp wins
- CycleEntry same date: MERGE symptoms/notes (union), take max flowIntensity

Write this complete architecture document with all 6 sections.
