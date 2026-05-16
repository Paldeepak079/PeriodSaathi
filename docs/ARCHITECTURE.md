# Architecture Plan

## Period Saathi

**Version:** 1.0  
**Date:** May 16, 2026  
**Architecture:** MVVM + Clean Architecture + Hilt + Room + SQLCipher

---

## SECTION 1: MODULE STRUCTURE

```
app/src/main/java/com/periodsaathi/app/
├── PeriodSaathiApp.kt              — @HiltAndroidApp, notification channel creation, library init
├── MainActivity.kt                  — single activity, edge-to-edge, sets content with NavHost
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── PeriodSaathiDatabase.kt   — @Database, Room builder with SQLCipher, migrations
│   │   │   ├── DatabaseModule.kt          — Hilt @Module providing DB + DAOs
│   │   │   └── Converters.kt              — TypeConverters for Date, List<String>, Enum, etc.
│   │   ├── dao/
│   │   │   ├── CycleEntryDao.kt           — CRUD for cycle entries, queries by date/range
│   │   │   ├── SettingsDao.kt             — Key-value settings persistence
│   │   │   ├── JournalDao.kt              — Encrypted journal entries with search
│   │   │   ├── ReminderDao.kt             — Reminder CRUD with alarm scheduling
│   │   │   └── PendingSyncDao.kt          — Offline queue of operations awaiting sync
│   │   └── entity/
│   │       ├── CycleEntryEntity.kt        — Period day log: date, intensity, symptoms, mood
│   │       ├── SettingsEntity.kt          — Key-value pair for app settings
│   │       ├── JournalEntity.kt           — Encrypted journal entry with mood + timestamp
│   │       ├── ReminderEntity.kt          — Reminder config: type, time, interval, enabled
│   │       └── PendingSyncEntity.kt       — Queued write: table, operation, payload, retries
│   ├── remote/
│   │   └── supabase/
│   │       ├── SupabaseClient.kt          — Supabase client configuration
│   │       ├── SupabaseModule.kt          — Hilt @Module for remote data sources
│   │       └── dto/                       — API response DTOs (CycleDTO, SyncDTO, etc.)
│   ├── repository/
│   │   ├── CycleRepository.kt            — Interface + impl: local reads, queued writes
│   │   ├── SettingsRepository.kt         — Interface + impl for settings
│   │   ├── JournalRepository.kt          — Interface + impl for journal entries
│   │   └── AuthRepository.kt             — Interface + impl for auth operations
│   └── preferences/
│       └── SecurePreferences.kt          — EncryptedSharedPreferences wrapper
│
├── domain/
│   ├── model/
│   │   ├── CycleEntry.kt                 — Domain model for a single day's period data
│   │   ├── PeriodPrediction.kt           — Prediction result: date, confidence, accuracy
│   │   ├── CyclePhase.kt                 — Enum: MENSTRUAL, FOLLICULAR, OVULATORY, LUTEAL, PMS
│   │   ├── WellnessData.kt              — Water, sleep, exercise, diet, habits per day
│   │   ├── GamificationState.kt         — Points, streak, badges, challenges
│   │   └── Reminder.kt                   — Domain model for reminders
│   └── usecase/
│       ├── cycle/
│       │   ├── StartPeriodUseCase.kt     — Begin a new cycle entry
│       │   ├── EndPeriodUseCase.kt       — Mark current period as ended
│       │   ├── LogDayUseCase.kt          — Add symptoms, mood, notes to a specific day
│       │   ├── PredictNextPeriodUseCase.kt — Calculate next period prediction
│       │   ├── GetCyclePhaseUseCase.kt   — Determine current cycle phase
│       │   └── GetCalendarDataUseCase.kt — Aggregate calendar month data
│       ├── wellness/
│       │   ├── WaterUseCase.kt           — Log water, get daily total
│       │   ├── HabitUseCase.kt           — Manage habit checklists
│       │   └── WellnessDataUseCase.kt    — Combined wellness data access
│       ├── home/
│       │   └── GetHomeDataUseCase.kt     — Aggregate all home screen data
│       ├── sync/
│       │   ├── SyncDataUseCase.kt        — Orchestrate sync operations
│       │   └── ConflictResolutionUseCase.kt — Resolve sync conflicts
│       └── payment/
│           ├── CheckPremiumUseCase.kt    — Verify purchase status
│           └── ProcessPurchaseUseCase.kt — Handle purchase flow
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt                      — Palette constants (#FFF8F5, #FFB5C8, etc.)
│   │   ├── Type.kt                       — Nunito + Poppins typography scale
│   │   ├── Shapes.kt                     — 28dp corner radius, card elevations
│   │   └── Theme.kt                      — Dynamic theme with light/dark
│   ├── navigation/
│   │   ├── Screen.kt                     — Sealed class of all 17 routes
│   │   ├── NavGraph.kt                   — NavHost with composable destinations
│   │   └── BottomNavBar.kt              — 5-tab bottom navigation
│   ├── components/                       — 15 shared reusable composables
│   │   ├── PeriodSaathiButton.kt
│   │   ├── DayCell.kt
│   │   ├── IntensitySelector.kt
│   │   ├── MoodDial.kt
│   │   ├── WaterRing.kt
│   │   ├── RestDayBadge.kt
│   │   ├── StreakCounter.kt
│   │   ├── Mascot.kt
│   │   ├── ConfettiOverlay.kt
│   │   ├── FlipCard.kt
│   │   ├── BreathingCircle.kt
│   │   ├── MedicineCard.kt
│   │   ├── HabitCheckItem.kt
│   │   ├── SleepSlider.kt
│   │   └── ChallengeCard.kt
│   ├── screens/                          — 17 screen folders
│   │   ├── home/                         — Home screen
│   │   ├── calendar/                     — Calendar with monthly view
│   │   ├── tracking/                     — Daily log screen
│   │   ├── wellness/                     — Wellness dashboard
│   │   ├── remedies/                     — Remedies + yoga
│   │   ├── journal/                      — Encrypted journal
│   │   ├── moodmap/                      — Mood heatmap
│   │   ├── gamification/                 — Points, streaks, rewards
│   │   ├── challenges/                   — Weekly challenges
│   │   ├── partner/                      — Partner mode
│   │   ├── settings/                     — App settings
│   │   ├── premium/                      — Premium purchases
│   │   ├── account/                      — Account management
│   │   ├── security/                     — Security settings
│   │   ├── about/                        — About + legal
│   │   ├── notifications/                — Reminder management
│   │   └── splash/                       — Splash + onboarding
│   └── util/
│       ├── HapticFeedback.kt             — Haptic feedback wrapper
│       ├── AnimationUtils.kt             — Shared animation specs
│       ├── DateFormatter.kt              — Date display formatters
│       └── Extensions.kt                 — Kotlin extension functions
│
├── worker/
│   ├── SyncWorker.kt                     — Periodic sync via WorkManager
│   ├── ReminderWorker.kt                  — AlarmReceiver routed through Worker
│   └── WidgetRefreshWorker.kt            — 4-hour widget data refresh
│
├── widget/
│   ├── CycleDayWidget.kt                 — 2x2 Glance widget: day + mascot + water
│   └── PeriodCountdownWidget.kt          — 4x1 Glance widget: next period countdown
│
├── notification/
│   ├── NotificationHelper.kt             — Channel creation, notification building
│   └── BootReceiver.kt                   — Reschedule alarms on device reboot
│
└── security/
    ├── BiometricManager.kt               — Face/Fingerprint auth wrapper
    ├── StealthModeManager.kt             — App icon/name disguise, PIN gate
    └── CertificatePinner.kt              — SSL cert pinning for Supabase
```

---

## SECTION 2: DATA FLOW DIAGRAM

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      USER INTERACTION FLOW                             │
└─────────────────────────────────────────────────────────────────────────┘

User Tap → @Composable → ViewModel (StateFlow) → UseCase → Repository → DAO/Room
                                                                              │
                                                                              ▼
                                                                         SQLCipher DB
                                                                              │
                                                                              ▼
                                                                      PendingSyncDao
                                                                         (queued)
                                                                              │
                                                                              ▼
                                                                       SyncWorker
                                                                    (15 min periodic
                                                                     + on network)
                                                                              │
                                                                              ▼
                                                                     Supabase API
                                                                         (remote)

                            READ FLOW
       Composable ←collectAsStateWithLifecycle← ViewModel ← UseCase ← Repository ← DAO ← Room

                            WRITE FLOW (offline)
       Composable → ViewModel → UseCase → Repository → DAO (immediate)
                                                           └→ PendingSyncDao (queue)
                                                                      └→ SyncWorker (when online)

                            WRITE FLOW (online + sync)
       Same as above + SyncWorker sends queued ops to Supabase API
       Success → delete from PendingSyncDao
       Failure → increment retryCount, exponential backoff

┌─────────────────────────────────────────────────────────────────────────┐
│                    OFFLINE SYNC PATH                                   │
└─────────────────────────────────────────────────────────────────────────┘

  Local Write
       │
       ▼
  PendingSyncDao.insert(operation)
       │
       ▼
  SyncWorker (triggers: periodic 15min / ConnectivityManager callback)
       │
       ▼
  Fetch pending ops (batch 50)
       │
       ▼
  POST to Supabase API (batched)
       │
       ├─ Success → PendingSyncDao.delete(operation)
       │
       └─ Failure → incrementRetryCount
                      │
                      ├─ retryCount < 5 → wait (2^retry * 30s)
                      │
                      └─ retryCount >= 5 → mark FAILED
                                            notify user in Settings
```

---

## SECTION 3: OFFLINE SYNC STRATEGY

### PendingSyncEntity Schema

```kotlin
@Entity(tableName = "pending_sync")
data class PendingSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tableName: String,           // e.g., "cycle_entries"
    val operationType: String,       // INSERT / UPDATE / DELETE
    val entityId: String,            // UUID of the affected entity
    val payload: String,             // JSON serialized entity data
    val createdAt: Long,             // timestamp when queued
    val retryCount: Int = 0,         // attempts made so far
    val lastError: String? = null,   // error message from last failure
    val status: String = "PENDING"   // PENDING / FAILED / IN_PROGRESS
)
```

### 7-Step Sync Flow

1. **Queue on Write:** Every data write goes to Room immediately AND queues an operation in `PendingSyncDao`. The user sees their data instantly — no waiting for network.

2. **Trigger SyncWorker:** `SyncWorker` is triggered by two mechanisms:
   - Periodic: Every 15 minutes via WorkManager
   - Network: `ConnectivityManager.NetworkCallback` triggers on reconnection

3. **Batch Processing:** The worker fetches up to 50 pending operations, ordered by `createdAt` (FIFO). Operations are posted to Supabase in a single batch request.

4. **Success Handling:** On successful API response, the operation is deleted from `PendingSyncDao`. The local `updatedAt` timestamp is updated to match the server response.

5. **Failure Handling:** On failure, `retryCount` is incremented and `lastError` is recorded. Exponential backoff is applied: `delay = 2^retryCount * 30 seconds` (30s, 60s, 2min, 4min, 8min).

6. **Permanent Failure:** After 5 retries, the operation is marked `FAILED`. A notification banner appears in Settings: "N items failed to sync. Tap to retry." User can manually retry all failed items.

7. **Pull Sync on Login:** When user logs in, a full pull sync is triggered: all local data is compared with remote data by `updatedAt`. Remote entries newer than local are downloaded. Conflicting entries use last-write-wins (see Section 6).

### Conflict Resolution During Pull Sync

- Each entity has `updatedAt: Long` (epoch millis)
- Compare local vs remote `updatedAt`
- Higher timestamp wins — data is fully replaced
- For `CycleEntry` on same date:
  - Symptoms: merged (union of both sets)
  - Mood: most recent takes precedence
  - Flow intensity: take the higher value
  - Notes: concatenated with separator

---

## SECTION 4: DEPENDENCY INJECTION MAP

### SingletonComponent (@Singleton)

| Provider | Provides |
|----------|----------|
| `PeriodSaathiDatabase` | Room database instance with SQLCipher |
| All DAOs (5) | `CycleEntryDao`, `SettingsDao`, `JournalDao`, `ReminderDao`, `PendingSyncDao` |
| `CycleRepository` | Local data access for cycle entries |
| `SettingsRepository` | Key-value settings access |
| `JournalRepository` | Encrypted journal data access |
| `SupabaseClient` | Remote API client |
| `SecurePreferences` | EncryptedSharedPreferences |
| `CertificatePinner` | SSL pinning configuration |
| `NotificationHelper` | Notification channel + builder |
| `BiometricManager` | Biometric authentication |
| `StealthModeManager` | Stealth mode state management |

### ViewModelComponent (@ViewModelScoped)

| Provider | Provides |
|----------|----------|
| All UseCases (15+) | `StartPeriodUseCase`, `EndPeriodUseCase`, `LogDayUseCase`, `PredictNextPeriodUseCase`, etc. |

### ServiceComponent (@HiltWorker)

| Provider | Provides |
|----------|----------|
| `SyncWorker` | Periodic sync background task |
| `ReminderWorker` | Reminder notification scheduling |
| `WidgetRefreshWorker` | Widget data refresh |

### Module Organization

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): PeriodSaathiDatabase
    fun provideCycleEntryDao(db: PeriodSaathiDatabase): CycleEntryDao
    fun provideSettingsDao(db: PeriodSaathiDatabase): SettingsDao
    // ... remaining DAOs
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideCycleRepository(dao: CycleEntryDao, pendingSyncDao: PendingSyncDao): CycleRepository
    // ... remaining repositories
}

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides @ViewModelScoped
    fun provideStartPeriodUseCase(repo: CycleRepository): StartPeriodUseCase
    // ... remaining use cases
}
```

---

## SECTION 5: STATE MANAGEMENT PATTERN

### UiState Sealed Class

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### ViewModel Pattern

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeScreenData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeScreenData>> = _uiState.asStateFlow()

    init { loadHomeData() }

    private fun loadHomeData() {
        viewModelScope.launch {
            getHomeDataUseCase()
                .onStart { _uiState.value = UiState.Loading }
                .catch { e -> _uiState.value = UiState.Error(e.message ?: "Unknown error") }
                .collect { data -> _uiState.value = UiState.Success(data) }
        }
    }
}
```

### Composable Rendering

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (Screen) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UiState.Loading -> ShimmerLoading()
        is UiState.Success -> HomeContent(state.data, onNavigate)
        is UiState.Error -> ErrorScreen(state.message) { viewModel::loadHomeData }
    }
}

@Composable
private fun ShimmerLoading() {
    // Placeholder shimmer animation
}

@Composable
private fun HomeContent(data: HomeScreenData, onNavigate: (Screen) -> Unit) {
    // Actual content
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column {
        Text("Something went wrong")
        Text(message)
        PeriodSaathiButton(onClick = onRetry) { Text("Retry") }
    }
}
```

### Additional State Patterns

**One-shot Events (SnackBar, Navigation):**
```kotlin
// SharedFlow for one-shot events (not StateFlow)
private val _events = MutableSharedFlow<HomeEvent>()
val events: SharedFlow<HomeEvent> = _events.asSharedFlow()
```

**Forms & Input State:**
```kotlin
data class JournalFormState(
    val text: String = "",
    val mood: MoodEmoji = MoodEmoji.NEUTRAL,
    val isSaving: Boolean = false,
    val error: String? = null
)
```

---

## SECTION 6: CONFLICT RESOLUTION RULES

### Last-Write-Wins (LWW)

**Rule:** The most recent modification always wins, determined by `updatedAt` timestamp comparison.

```kotlin
fun resolveConflict(local: Entity, remote: Entity): Entity {
    return if (local.updatedAt >= remote.updatedAt) local else remote
}
```

### Entity-Specific Rules

| Entity | Conflict Resolution |
|--------|-------------------|
| `CycleEntryEntity` | Higher `updatedAt` wins. If same date: merge symptoms (union), max flowIntensity, latest mood, concatenate notes. |
| `SettingsEntity` | Higher `updatedAt` wins (full replacement). |
| `JournalEntity` | Higher `updatedAt` wins (full replacement). |
| `ReminderEntity` | Higher `updatedAt` wins (full replacement). |
| `PendingSyncEntity` | No conflict — server-idempotent, deduped by `entityId + operationType`. |

### CycleEntry Merge Logic

```kotlin
fun mergeCycleEntries(local: CycleEntryEntity, remote: CycleEntryEntity): CycleEntryEntity {
    require(local.date == remote.date) { "Can only merge same-date entries" }

    return CycleEntryEntity(
        id = if (remote.updatedAt >= local.updatedAt) remote.id else local.id,
        date = local.date,
        flowIntensity = maxOf(local.flowIntensity, remote.flowIntensity),
        hasClot = local.hasClot || remote.hasClot,
        symptoms = (local.symptoms + remote.symptoms).distinct(),
        mood = if (remote.updatedAt >= local.updatedAt) remote.mood else local.mood,
        notes = listOfNotNull(local.notes, remote.notes)
            .filter { it.isNotBlank() }
            .joinToString("\n---\n"),
        waterGlasses = maxOf(local.waterGlasses, remote.waterGlasses),
        sleepHours = if (remote.updatedAt >= local.updatedAt) remote.sleepHours else local.sleepHours,
        exerciseMinutes = if (remote.updatedAt >= local.updatedAt) remote.exerciseMinutes else local.exerciseMinutes,
        isPeriodDay = local.isPeriodDay || remote.isPeriodDay,
        updatedAt = System.currentTimeMillis()
    )
}
```

### Sync Decision Matrix

| Local exists? | Remote exists? | Action |
|:---:|:---:|---|
| No | No | Skip (ghost entry) |
| Yes | No | Upload local (INSERT) |
| No | Yes | Download remote (INSERT) |
| Yes | Yes | Compare timestamps → LWW or merge |

### Data Deletion Sync

- **Local delete:** Queue `DELETE` operation. On sync, server deletes the record.
- **Remote delete:** On pull sync, local entity is deleted if `deletedAt` is set on remote and is newer than local `updatedAt`.
- **Soft delete:** Entities use a `isDeleted: Boolean` flag for 30-day grace period before hard delete.

---

*Document Version: 1.0*  
*Last Updated: May 16, 2026*  
*Next Review: After S04 code generation*