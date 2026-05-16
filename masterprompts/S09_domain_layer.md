# S09 — DOMAIN LAYER: REPOSITORIES + USE CASES

> **Prerequisites:** S00 active · S08 complete · `assembleDebug` passing
> **Output:** 20 Kotlin files — 4 repositories (interface + impl each) + 12 use cases
> **Build check:** `./gradlew assembleDebug` — must pass. Run: `./gradlew testDebugUnitTest`.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S09_report.md`

Include:
- 📄 All 20 files created (checklist)
- ✅ Build result (SUCCESS / FAILED)
- 🧪 Unit test result (`testDebugUnitTest`) — pass count / fail count
- 🔧 All business rules verified (checklist format from TRD)
- ❌ Errors and fixes applied
- ⚠️ Any use case logic gaps found

---

## PROMPT

Create all domain layer files: models, repository interfaces/implementations, and use cases.
Write every file completely — no TODOs, no placeholder logic.

---

## PART A: DOMAIN MODELS (`domain/model/`)

### A1: `CycleEntry.kt`
```kotlin
data class CycleEntry(
  val id: Long = 0,
  val date: LocalDate,
  val flowIntensity: Int,           // 0=none, 1=spot..5=very heavy
  val symptoms: List<Symptom> = emptyList(),
  val mood: MoodType? = null,
  val notes: String? = null,
  val waterGlasses: Int = 0,
  val medicineTaken: Boolean = false,
  val isRestDay: Boolean = false,
  val isPeriodStart: Boolean = false,
  val isPeriodEnd: Boolean = false
)

enum class Symptom {
  CRAMPS, BLOATING, HEADACHE, FATIGUE, BACKACHE,
  NAUSEA, ACNE, MOOD_SWINGS, BREAST_TENDERNESS,
  SPOTTING, CLOTS, INSOMNIA
}

enum class MoodType(val emoji: String) {
  GREAT("😄"), GOOD("🙂"), NEUTRAL("😐"), MEH("😕"), BAD("😞"), TERRIBLE("😢"), ANGRY("😤")
}
```

### A2: `CyclePhase.kt`
```kotlin
enum class CyclePhase(val displayName: String, val emoji: String, val colors: List<Color>) {
  MENSTRUAL("Menstrual", "🌑", listOf(Color(0xFFFF8FAB), Color(0xFFFFB5C8))),
  FOLLICULAR("Follicular", "🌱", listOf(Color(0xFFB8F0DC), Color(0xFFBabyBlue))),
  OVULATION("Ovulation", "🌕", listOf(Color(0xFFFFF3B0), Color(0xFFFFD700))),
  LUTEAL("Luteal", "🌖", listOf(Color(0xFFDDD0FF), Color(0xFFC9B8FF))),
  PMS("PMS", "🌗", listOf(Color(0xFFFFB3A7), Color(0xFFFF8FAB)))
}

fun calculatePhase(cycleDay: Int, cycleLength: Int = 28, periodLength: Int = 5): CyclePhase
```

### A3: `PeriodPrediction.kt`
```kotlin
data class PeriodPrediction(
  val predictedStartDate: LocalDate,
  val predictedEndDate: LocalDate,
  val daysUntil: Int,
  val confidence: ConfidenceLevel,
  val accuracyDays: Int              // ± margin
)

enum class ConfidenceLevel(val badge: String, val description: String) {
  LOW("Estimating 🔮", "Based on 3 cycles"),
  MEDIUM("Getting there ✨", "Based on 4–5 cycles"),
  HIGH("Spot on 🎯", "Based on 6+ cycles")
}
```

### A4: `PatternInsight.kt`
```kotlin
data class PatternInsight(
  val type: InsightType,
  val title: String,
  val description: String,
  val icon: String,
  val color: Color,
  val confidence: Float              // 0.0–1.0
)

enum class InsightType { CYCLE_LENGTH, SYMPTOM_PATTERN, MOOD_PATTERN, WATER_CORRELATION, REST_DAY_PATTERN }
```

---

## PART B: REPOSITORY INTERFACES (`domain/repository/`)

### B1: `CycleRepository.kt` (interface)
```kotlin
interface CycleRepository {
  fun getEntriesBetweenDates(start: LocalDate, end: LocalDate): Flow<List<CycleEntry>>
  fun getEntryByDate(date: LocalDate): Flow<CycleEntry?>
  fun getAllPeriodDays(): Flow<List<CycleEntry>>
  fun getPeriodStartDates(limit: Int): Flow<List<LocalDate>>
  fun getRecentEntries(limit: Int): Flow<List<CycleEntry>>
  suspend fun insertOrUpdateEntry(entry: CycleEntry)
  suspend fun deleteEntry(date: LocalDate)
}
```

### B2: `SettingsRepository.kt` (interface)
All settings CRUD operations + `Flow<Settings>`.

### B3: `JournalRepository.kt` (interface)
Journal CRUD + `Flow<List<JournalEntry>>` + `suspend fun export(context): File`.

### B4: `AuthRepository.kt` (interface)
```kotlin
interface AuthRepository {
  fun isLoggedIn(): Boolean
  suspend fun signInWithGoogle(token: String): Result<UserProfile>
  suspend fun signInWithEmail(email: String, password: String): Result<UserProfile>
  suspend fun signOut()
  suspend fun deleteAccount()
}
```

---

## PART C: REPOSITORY IMPLEMENTATIONS (`data/repository/`)

### C1: `CycleRepositoryImpl.kt`

```kotlin
@Singleton
class CycleRepositoryImpl @Inject constructor(
  private val cycleEntryDao: CycleEntryDao,
  private val pendingSyncDao: PendingSyncDao
) : CycleRepository {

  override suspend fun insertOrUpdateEntry(entry: CycleEntry) {
    val entity = entry.toEntity()
    cycleEntryDao.insert(entity)
    // Queue sync if logged in
    pendingSyncDao.insertPending(
      PendingSyncEntity(
        operation = "UPSERT",
        tableName = "cycle_entries",
        recordLocalId = entity.id,
        payload = gson.toJson(entity)
      )
    )
  }
  
  // ... all other implementations
}
```

Include mappers: `CycleEntry.toEntity()`, `CycleEntryEntity.toDomain()` as extension functions.

### C2: `SettingsRepositoryImpl.kt`
Implement all settings methods.

### C3: `JournalRepositoryImpl.kt`
Implement + `suspend fun export()` using `PdfDocument` or CSV generation.

### C4: `AuthRepositoryImpl.kt`
- Google: `SupabaseClient.auth.signInWith(Google)` using token from Credential Manager
- Email: `SupabaseClient.auth.signInWith(Email)` with try/catch → Result
- Sessions persisted via Supabase's built-in session storage

---

## PART D: USE CASES (`domain/usecase/`)

### D1: `GetPredictionUseCase.kt`
**Algorithm (Weighted Moving Average):**
```kotlin
class GetPredictionUseCase @Inject constructor(
  private val repository: CycleRepository
) {
  operator fun invoke(): Flow<PeriodPrediction?> = repository.getPeriodStartDates(limit = 10)
    .map { starts ->
      if (starts.size < 3) return@map null
      
      val cycleLengths = starts.zipWithNext().map { (a, b) -> ChronoUnit.DAYS.between(b, a).toInt() }
      
      // Weighted average: recent cycles get higher weight
      val weights = (1..cycleLengths.size).map { it.toDouble() }
      val totalWeight = weights.sum()
      val weightedAvg = cycleLengths.zip(weights).sumOf { (len, w) -> len * w } / totalWeight
      
      val variance = cycleLengths.map { (it - weightedAvg).pow(2) }.average()
      val stdDev = sqrt(variance)
      
      val confidence = when {
        starts.size >= 6 -> ConfidenceLevel.HIGH
        starts.size >= 4 -> ConfidenceLevel.MEDIUM
        else -> ConfidenceLevel.LOW
      }
      
      val lastStart = starts.first()
      val nextStart = lastStart.plusDays(weightedAvg.toLong())
      
      PeriodPrediction(
        predictedStartDate = nextStart,
        predictedEndDate = nextStart.plusDays(5),
        daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), nextStart).toInt(),
        confidence = confidence,
        accuracyDays = ceil(stdDev).toInt().coerceAtLeast(1)
      )
    }
}
```

### D2: `GetCurrentPhaseUseCase.kt`
Calculate current `CyclePhase` from last period start + cycle length. Return Flow.

### D3: `LogCycleEntryUseCase.kt`
Business rules:
- Reject future dates: `if (date.isAfter(LocalDate.now())) return Result.failure(IllegalArgumentException("Cannot log future dates"))`
- Validate intensity: 0..5 only
- Auto-suggest REST DAY if flowIntensity ≥ 4 for today AND yesterday
- Never auto-set isRestDay — emit `SuggestRestDay` event only
- Award +3 points on log (call settings repository)
- Update streak

### D4: `StartPeriodUseCase.kt`
- Set `isPeriodStart = true` on today's entry
- Set previous period's `isPeriodEnd = null` (don't auto-end)
- Queue PendingSyncEntity

### D5: `EndPeriodUseCase.kt`
- Set `isPeriodEnd = true` on today's entry
- Calculate last period length → `settingsRepository.updateAveragePeriodLength()`
- Increment `cyclesLogged`

### D6: `GetCalendarDataUseCase.kt`
- Fetch entries for given month
- Calculate which days are period, fertile (if fertility mode on), PMS, predicted
- Return `List<CalendarDayData>` with all state flags

### D7: `GetHomeDataUseCase.kt`
- Combine: settings, today's entry, prediction, phase
- Calculate `mascotEmotion`:
  - `waterGlasses >= totalGlasses` → EXCITED
  - `isRestDay` → SLEEPING
  - `waterGlasses < 3` → SAD
  - else → HAPPY
- Return `HomeUiState`

### D8: `GeneratePatternInsightsUseCase.kt`
- Requires ≥3 cycles
- Detects: average cycle length trend, dominant symptoms per phase, mood correlations with cycle phase
- Returns `List<PatternInsight>` with confidence scores

### D9: `SyncDataUseCase.kt`
- Reads all pending from `PendingSyncDao`
- Batches by 50
- Calls `SupabaseClient.functions.invoke("batch-sync")`
- Deletes synced entries on success
- Handles 401 (sign out) + 429 (backoff) + timeout

### D10: `CheckPremiumStatusUseCase.kt`
- Reads from Room SettingsEntity
- Validates against Supabase profiles table (if online)
- Returns `PremiumTier` enum

### D11: `AddWaterUseCase.kt`
- Validates: 0..16 glasses per day
- Saves to `CycleEntryEntity.waterGlasses` via Room
- Awards +1 point
- Emits `WaterGoalReached` event when hitting target

### D12: `GetWellnessDataUseCase.kt`
- Combine today's wellness: water, habits, sleep, exercise, streak, points
- Returns `WellnessUiState`

---

## PART E: DI MODULE

### E1: `di/RepositoryModule.kt`
```kotlin
@Module @InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  @Binds abstract fun bindCycleRepository(impl: CycleRepositoryImpl): CycleRepository
  @Binds abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
  @Binds abstract fun bindJournalRepository(impl: JournalRepositoryImpl): JournalRepository
  @Binds abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
```

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Run: `./gradlew testDebugUnitTest`
Write the report.
