# S05 — FOUNDATION: DATABASE + THEME

> **Prerequisites:** S00 active · S04 complete · `assembleDebug` passing
> **Output:** 14 Kotlin files across `ui/theme/` and `data/local/`
> **Build check:** `./gradlew assembleDebug` — must pass before S06.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S05_report.md`

Include:
- 📄 All 14 files created (checklist)
- ✅ Build result (SUCCESS / FAILED)
- ❌ Errors and fixes applied
- 🎨 Design tokens confirmed (colors, typography, shapes)
- 🗄️ Database schema confirmed (all 5 entities, all 5 DAOs)
- ⚠️ Any issues with SQLCipher initialization

---

## PROMPT

Create ALL foundation files. Write every file completely — no placeholders, no TODOs.

---

## GROUP A: THEME FILES (4 files)

### A1: `ui/theme/Color.kt`
Define ALL color values with exact hex codes:
```kotlin
// Brand palette
val BlushPink = Color(0xFFFFB5C8)
val SoftLavender = Color(0xFFC9B8FF)
val BabyBlue = Color(0xFFB8DCFF)
val ButterYellow = Color(0xFFFFF3B0)
val SoftCoral = Color(0xFFFFB3A7)
val MintGreen = Color(0xFFB8F0DC)
val CreamWhite = Color(0xFFFFF8F5)
val DeepRose = Color(0xFFFF8FAB)
val SoftPurple = Color(0xFF9B8EC4)
val WarmGold = Color(0xFFFFD700)
val DarkNavy = Color(0xFF1A0E2E)
val WarmNavy = Color(0xFF1A1228)

// Semantic colors (period intensity, fertile window, PMS)
val PeriodLight = Color(0xFFFFE4EC)
val PeriodMedium = Color(0xFFFFB5C8)
val PeriodHeavy = Color(0xFFFF8FAB)
val PeriodVeryHeavy = Color(0xFFE05080)
val FertileGreen = Color(0xFFB8F0DC)
val FertileOrange = Color(0xFFFFDDB3)
val PMSLavender = Color(0xFFDDD0FF)
val RestDayRed = Color(0xFFFFB3A7)

// Glass effect
val GlassWhite = Color(0x73FFFFFF)
val GlassBorder = Color(0x99FFFFFF)
```
Also define gradient Brush extensions:
- GradientPrimary: horizontal BlushPink → DeepRose
- GradientSecondary: horizontal SoftLavender → SoftPurple
- GradientCycle: sweep BlushPink → SoftCoral → SoftLavender
- GradientWater: vertical BabyBlue → Color(0xFF7EC8E3)

Define `LightColorScheme` and `DarkColorScheme` using Material3 `lightColorScheme` / `darkColorScheme`.
Map brand colors to appropriate Material3 slots.

### A2: `ui/theme/Type.kt`
Load Nunito and Poppins from Google Fonts provider.
Define complete `Typography` object:
- displayLarge: Nunito, 32sp, weight 800
- displayMedium: Nunito, 28sp, weight 700
- headlineLarge: Nunito, 24sp, weight 700
- headlineMedium: Nunito, 20sp, weight 700
- titleLarge: Nunito, 18sp, weight 600
- titleMedium: Nunito, 16sp, weight 600
- bodyLarge: Poppins, 15sp, weight 400
- bodyMedium: Poppins, 14sp, weight 400
- bodySmall: Poppins, 12sp, weight 400
- labelLarge: Poppins, 13sp, weight 600
- labelMedium: Poppins, 12sp, weight 500
- labelSmall: Poppins, 11sp, weight 500

### A3: `ui/theme/Shapes.kt`
```kotlin
val CardShape = RoundedCornerShape(28.dp)
val ButtonShape = RoundedCornerShape(50.dp)
val ChipShape = RoundedCornerShape(20.dp)
val InputShape = RoundedCornerShape(16.dp)
val BottomSheetShape = RoundedCornerShape(topStart=32.dp, topEnd=32.dp)
val SmallCardShape = RoundedCornerShape(16.dp)
val BadgeShape = RoundedCornerShape(50.dp)

val AppShapes = Shapes(
  small = SmallCardShape,
  medium = CardShape,
  large = CardShape
)
```

### A4: `ui/theme/Theme.kt`
`PeriodSaathiTheme` composable:
- Parameters: `darkTheme: Boolean`, `dynamicColor: Boolean` (default true on API 31+)
- Status bar color: CreamWhite (light) / DarkNavy (dark)
- Navigation bar color: match theme
- `isAppearanceLightStatusBars` set correctly
- Use `WindowCompat.getInsetsController`
- Apply fonts (AppTypography), shapes (AppShapes) to `MaterialTheme`
- Add `CompositionLocal` for `LocalSpringSpec`

---

## GROUP B: ROOM DATABASE (10 files)

### B1: `data/local/entity/CycleEntryEntity.kt`
```kotlin
@Entity(tableName = "cycle_entries", indices = [Index(value = ["date"], unique = true)])
data class CycleEntryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: Long,                    // epoch millis, day only — strip time
  val flowIntensity: Int = 0,        // 0=no period logged, 1-5
  val symptoms: String = "[]",       // JSON array of strings
  val mood: String? = null,
  val notes: String? = null,
  val waterGlasses: Int = 0,
  val medicineTaken: Boolean = false,
  val isRestDay: Boolean = false,
  val isPeriodStart: Boolean = false,
  val isPeriodEnd: Boolean = false,
  val sleepHours: Float = 0f,
  val exerciseMinutes: Int = 0,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val syncedAt: Long? = null,        // null = not synced
  val remoteId: String? = null       // Supabase UUID
)
```

### B2: `data/local/entity/SettingsEntity.kt`
All 30+ fields including: userName, averageCycleLength (28), averagePeriodLength (5), lastPeriodStartDate, lastPeriodEndDate, cyclesLogged, partnerName, partnerPhone, language ("en"), contraceptionMode, fertilityMode, stealthModeEnabled, biometricLockEnabled, selectedTheme, soundEnabled, hapticEnabled, streakCount, totalPoints, lastStreakDate, premiumTier ("FREE"), adFreeEnabled, isPregnancyPauseActive, notificationsOptedIn, analyticsOptedIn, backupOptedIn, lastBackupDate, firstLaunchDate, consentAccepted, medicalDisclaimerAccepted, updatedAt.
Singleton: `@PrimaryKey val id: Int = 1`

### B3: `data/local/entity/JournalEntity.kt`
Fields: id, date, content (stored encrypted), moodEmojis (JSON String), cycleDay, cyclePhase, isTimeCapsule, capsuleRevealDate, isRevealed, createdAt, updatedAt, remoteId

### B4: `data/local/entity/ReminderEntity.kt`
Fields: id, type (WATER/MEDICINE/PERIOD/YOGA/CUSTOM), label, timeHour, timeMinute, intervalHours?, isEnabled, quietHoursEnabled, medicationName?, dosage?

### B5: `data/local/entity/PendingSyncEntity.kt`
Fields: id, operation (UPSERT/DELETE), tableName, recordLocalId, recordRemoteId?, payload (JSON), createdAt, retryCount (0), lastError?, status ("PENDING"/"PROCESSING"/"FAILED")

### B6: `data/local/dao/CycleEntryDao.kt`
All queries as listed:
- insert (REPLACE), update, delete
- getEntriesBetweenDates(start, end): Flow<List>
- getEntryByDate(date): Flow<CycleEntryEntity?>
- getAllPeriodDays(): Flow<List> (flowIntensity > 0, ordered DESC)
- getPeriodStartDates(limit): Flow<List>
- getWaterForDate(date): Flow<Int?>
- getRecentEntries(limit): Flow<List>
- markSynced(id, timestamp, remoteId): suspend

### B7: `data/local/dao/SettingsDao.kt`
- getSettings(): Flow<SettingsEntity?>
- upsertSettings(settings): suspend
- updateStreakAndPoints(streak, points, date): suspend
- incrementCyclesLogged(): suspend

### B8: `data/local/dao/JournalDao.kt`
Standard CRUD + Flow queries for journal entries

### B9: `data/local/dao/ReminderDao.kt`
Standard CRUD + getAllEnabled(): Flow<List<ReminderEntity>>

### B10: `data/local/dao/PendingSyncDao.kt`
- getAllPending(): List<PendingSyncEntity> (non-Flow for Worker)
- insertPending(op): suspend
- deleteByIds(ids: List<Long>): suspend
- incrementRetry(id, error): suspend
- markFailed(id): suspend

### B11: `data/local/database/Converters.kt`
TypeConverters:
- `List<String>` ↔ JSON String (using Gson)
- `LocalDate` ↔ Long (epochDay)

### B12: `data/local/database/PeriodSaathiDatabase.kt`
```kotlin
@Database(
  entities = [CycleEntryEntity::class, SettingsEntity::class,
              JournalEntity::class, ReminderEntity::class, PendingSyncEntity::class],
  version = 1,
  exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PeriodSaathiDatabase : RoomDatabase() {
  // abstract DAO functions
  
  companion object {
    fun create(context: Context): PeriodSaathiDatabase {
      val passphrase = SQLiteDatabase.getBytes(
        SecurePreferences.getDatabaseKey(context).toCharArray()
      )
      val factory = SupportFactory(passphrase)
      return Room.databaseBuilder(...)
        .openHelperFactory(factory)
        .fallbackToDestructiveMigrationOnDowngrade()
        .build()
    }
  }
}
```

### B13: `data/di/DatabaseModule.kt`
`@Module @InstallIn(SingletonComponent::class)` — provides @Singleton for database, all 5 DAOs.

### B14: `security/SecurePreferences.kt`
```kotlin
object SecurePreferences {
  fun getDatabaseKey(context: Context): String
    // Uses EncryptedSharedPreferences (AES256_SIV + AES256_GCM)
    // First call: generates random 32-char key, stores encrypted
    // Subsequent calls: returns same key
    
  fun getEncryptedPrefs(context: Context): SharedPreferences
    // Returns EncryptedSharedPreferences instance
}
```

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Fix ALL errors before writing the report.
