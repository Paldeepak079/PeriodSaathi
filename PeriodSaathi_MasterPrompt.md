# 🌸 PERIOD SAATHI — PREMIUM MASTER BUILD PROMPT
## Complete Production-Grade Android App
### Version 2.0 | OpenCode / DeepSeek / Minimax Ready

---

> **HOW TO USE THIS DOCUMENT**
> Paste Section 0 (System Context) as your very first message in every OpenCode session.
> Then paste each numbered prompt block sequentially. Never skip a block.
> After every block: `./gradlew assembleDebug` — fix ALL errors before continuing.
> If a response is cut off, say: **"Continue the file from the last complete line."**

---

# SECTION 0 — SYSTEM CONTEXT (PASTE IN EVERY SESSION)

```
════════════════════════════════════════════════════════════
  PERIOD SAATHI — MASTER SYSTEM CONTEXT v2.0
  Keep this active for the entire build session.
════════════════════════════════════════════════════════════

PROJECT IDENTITY:
  App Name: Period Saathi
  Package: com.periodsaathi.app
  Type: Android Native — Kotlin + Jetpack Compose
  Purpose: Premium period companion — emotional, cute, offline-first

ENVIRONMENT (EXACT — do not assume newer versions):
  Kotlin: 2.2.10
  Compose BOM: 2026.02.01
  Android Gradle Plugin: 9.2.1
  Min SDK: 24 | Target SDK: 36
  Java: 17

ARCHITECTURE — NON-NEGOTIABLE:
  Pattern: MVVM + Clean Architecture + Repository Pattern
  Layers: UI → ViewModel → UseCase → Repository → DataSource
  DI: Hilt 2.51.1 (every ViewModel, Repository, Worker)
  Database: Room 2.7.0 + SQLCipher 4.5.4 (encrypted at rest)
  State: StateFlow + collectAsStateWithLifecycle (NEVER collectAsState)
  Navigation: Navigation3 (type-safe, shared element transitions)
  Coroutines: viewModelScope only, no GlobalScope ever
  Backend: Supabase (optional account, offline-first)
  Payments: Razorpay SDK (one-time purchase only)

CRITICAL RULES — NEVER VIOLATE:
  ✅ Every button MUST have an onClick handler — no empty lambdas
  ✅ Every Composable MUST have @Preview with realistic fake data
  ✅ Every screen MUST handle Loading / Success / Error states
  ✅ Every ViewModel MUST have @HiltViewModel + @Inject constructor
  ✅ All data persists in Room — no data loss on app restart
  ✅ App works 100% offline — no feature gated behind internet
  ✅ All clickable elements: 48dp minimum touch target
  ✅ Haptic feedback on every significant interaction
  ✅ Spring physics on ALL animations — no linear/tween unless noted
  ✅ Never auto-end a period — only manual start + manual end
  ✅ Never show "late" warning — use "still tracking" language
  ✅ No predictions until 3 full cycles logged
  ✅ No forced ads before core interactions
  ✅ Location permission: NEVER request it
  ✅ Notifications: opt-in only, never forced

DESIGN SYSTEM (apply everywhere):
  Background: #FFF8F5 (Cream White)
  Primary: #FFB5C8 (Blush Pink)
  Secondary: #C9B8FF (Soft Lavender)
  Tertiary: #B8DCFF (Baby Blue)
  Accent1: #FFF3B0 (Butter Yellow)
  Accent2: #FFB3A7 (Soft Coral)
  Accent3: #B8F0DC (Mint Green)
  Error: #FF5252
  OnPrimary: #3D2C35
  CardBg: rgba(255,255,255,0.45) + blur
  AllCorners: 28dp cards, 50dp buttons, 20dp chips, 16dp inputs
  Font: Nunito (display/headings) + Poppins (body)
  Elevation: custom soft pink shadow — NOT Material elevation

ANIMATION STANDARD (every tap must feel alive):
  Press: scale(0.94f) — 120ms ease-out
  Release: scale(1.02f) — 160ms spring(stiffness=300, damping=0.7)
  Settle: scale(1.0f) — 80ms ease-in
  Card entry: translateY(40dp→0) + alpha(0→1), spring, staggered 80ms
  Screen transition: shared element morph + crossfade
  Success: confetti burst (60 pastel particles, physics-based)
  All springs: spring(dampingRatio=Spring.DampingRatioMediumBouncy,
                       stiffness=Spring.StiffnessMedium)

WHEN WRITING CODE:
  1. Write COMPLETE files — no "// TODO", no "// rest of code here"
  2. Include ALL imports at top of every file
  3. Add @Preview with fake data for EVERY Composable
  4. Use sealed classes for all UI state
  5. Handle ALL error cases — never swallow exceptions silently
  6. Add kdoc comments on public functions
  7. Use resource strings — never hardcode user-visible text
  8. Every modifier chain: order matters (size → padding → clip → bg → click)
════════════════════════════════════════════════════════════
```

---

# SECTION 1 — TECHNICAL REQUIREMENTS DOCUMENT (TRD)

```
Write the complete Technical Requirements Document for Period Saathi.

Create: docs/TRD.md

Include all of these sections with full detail:

═══════════════════════════════════════
1. PROJECT OVERVIEW
═══════════════════════════════════════
- App purpose and core philosophy
- Target users: women in India aged 15-45
- Supported languages: English, Hindi, Marathi, Tamil, Telugu, Bengali
- Offline-first design principle
- Privacy-by-design: no health data leaves device without consent
- Account: optional (sync feature, not core)

═══════════════════════════════════════
2. FUNCTIONAL REQUIREMENTS
═══════════════════════════════════════
FR-01: Period Tracking
  - Manual period start (tap date → confirm)
  - Manual period end (tap "End period today" in active period)
  - Bleeding intensity: 1 spotting, 2 light, 3 medium, 4 heavy, 5 very heavy
  - Bleeding days: 1 to 30+ (no upper cap enforcement)
  - Clot logging: yes/no toggle per day
  - REST DAY: auto-suggested when intensity ≥ 4 for 2 consecutive days
              User CONFIRMS rest day — never auto-set
  - REST DAY badge prominently shown on home screen when active

FR-02: Cycle Prediction
  - MINIMUM 3 complete cycles before any prediction shown
  - Prediction confidence: LOW (3 cycles), MEDIUM (4-5), HIGH (6+)
  - Confidence displayed visually (LOW/MED/HIGH badge) on every prediction
  - Accuracy shown as "±N days" based on cycle variance
  - Support irregular cycles: 1-120 days cycle length
  - Support two periods in one month (polymenorrhea)
  - Algorithm: weighted moving average (recent cycles weighted higher)
  - Never use "overdue" or "late" language
  - Use: "Still tracking... your cycle may be taking its time 🌙"

FR-03: Calendar
  - Full month view with day cells
  - Log per day: intensity, symptoms, mood, notes, water, medicine
  - Fertile window shown with NEUTRAL default (no green/orange without user choice)
  - PMS window: last 7 days before predicted period
  - Navigate: prev/next month with swipe gesture
  - Long-press day: quick log bottom sheet
  - Future dates: not loggable (disabled cells)

FR-04: Wellness Tracking
  - Water: 1-16 glasses per day, +1 and +500ml quick add
  - Habits: customizable checklist (default 5, add up to 15)
  - Medicine: name + time + dosage, reminder linkable
  - Sleep hours: 0-12 in 0.5 step increments
  - Exercise: minutes, type (walk/yoga/gym/other)
  - Diet: meal emoji tags (no calorie counting)

FR-05: Reminders
  - Types: Water, Medicine, Period prediction, Yoga, Custom
  - Interval options: custom time, repeating (every N hours)
  - Schedule: exact time via AlarmManager (not WorkManager for precise)
  - Opt-in: all reminders OFF by default
  - Quiet hours: configurable (default 10pm-7am no reminders)
  - Notification permission: requested contextually, not on first launch

FR-06: Gamification
  - Points: earned by logging water (+1), habits (+2), cycle data (+3)
  - Streak: consecutive days with any log
  - Rewards: themes, mascot accessories — unlocked by points
  - Monthly Wrapped: generated after each cycle ends
  - Challenges: 3 active per week, special rewards only from challenges
  - Seasonal drops: time-limited accessories (limited time badge)

FR-07: Partner Mode
  - Select care requests from preset list + custom message
  - Opens Android share intent (SMS/WhatsApp/any app)
  - ZERO health data in shared content — only care requests
  - No backend involvement — entirely local + share intent
  - Partner linking: local code only, no server pairing

FR-08: Remedies + Yoga
  - Flip cards for 8+ home remedies (kitchen ingredients only)
  - Yoga flow: 5 poses, 4-minute flow, breathing circle
  - Hot bag slider: safety warning at danger zone, snap-back behavior
  - Content: fully offline, embedded in app

FR-09: Journal + Mood Map
  - Private journal: encrypted in Room with SQLCipher
  - Mood dial: 7-point scale with emoji
  - Time capsule: write message, revealed on next cycle day 1
  - Mood map: heatmap of last 90 days, cycle overlay toggle
  - Export: journal entries in PDF or plain text

FR-10: Payments (Razorpay)
  - One-time purchase ONLY:
    "Premium Themes Pack" — ₹99 one-time
    "Ad-Free Forever" — ₹149 one-time
    "Full Premium Bundle" — ₹199 one-time (both above)
  - NEVER: subscription, recurring charges, paywalling old logs
  - Purchases stored locally (encrypted) + in Supabase if logged in
  - Banner ads only (AdMob) — only for non-premium users
  - No interstitial ads, no rewarded ads, no video ads
  - Ad-free by default in first 7 days (grace period)

FR-11: Accounts + Sync (Optional)
  - Account creation: Google Sign-In OR email+password
  - Password reset: MUST work via email (Supabase auth)
  - Account is optional: app works fully without account
  - Sync: logs, settings, streak (on login, restore all previous data)
  - Conflict resolution: last-write-wins with timestamp comparison
  - Auto-backup: opt-in Google Drive backup (encrypted JSON)
  - Export: free CSV and PDF export always available (no paywall)

FR-12: Privacy + Security
  - SQLCipher encrypted Room database
  - EncryptedSharedPreferences for all sensitive prefs
  - FLAG_SECURE on all screens (prevents screenshots in app switcher)
  - Certificate pinning for Supabase API calls
  - Stealth mode: disguise app icon/name, PIN-protected
  - Biometric lock: optional, Face ID / Fingerprint
  - Privacy policy: DPDP Act compliant (India)
  - No analytics without opt-in
  - Bug reports: opt-in, stripped of health data before sending
  - Pregnancy pause mode: hides period tracking, no warnings
  - Medical disclaimer: shown on first launch and in settings

FR-13: Widget
  - 2×2 home screen widget: cycle day, mascot, water ring
  - 4×1 home screen widget: next period countdown
  - Updates every 4 hours via WorkManager
  - Force refresh: tap widget refreshes immediately
  - Widget data: read from Room directly (no ViewModel)

FR-14: Accessibility
  - TalkBack: contentDescription on ALL interactive elements
  - Minimum touch targets: 48×48dp enforced
  - Text scaling: layouts tested at 85%, 100%, 130%, 200% font scale
  - Color contrast: WCAG AA minimum (4.5:1 for text)
  - Non-pink gender-neutral theme option (teal/grey/blue scheme)
  - Screen reader announcements for state changes
  - No flashing animations that could trigger photosensitivity

═══════════════════════════════════════
3. NON-FUNCTIONAL REQUIREMENTS
═══════════════════════════════════════
Performance:
  - App cold start: < 1.5 seconds on mid-range device
  - Frame rate: 60fps minimum, 120fps where device supports
  - ANR: zero tolerance — all DB ops on IO dispatcher
  - Memory: < 150MB RAM in normal use
  - APK size: < 30MB (use App Bundle for Play Store)
  - Startup trace: measure with Macrobenchmark
  - Lazy loading: all lists, no loading entire datasets

Reliability:
  - Crash-free rate target: 99.5%+
  - Offline: all core features work without internet
  - Database corruption: auto-recover with backup
  - Battery: < 2% battery drain per day (WorkManager optimized)

Testing targets:
  - Unit test coverage: ≥ 70%
  - UI tests: all happy paths covered
  - Screenshot tests: all main screens
  - Device matrix: test on 10+ devices including Redmi 9A (low-end)

═══════════════════════════════════════
4. MUST-NEVER LIST (HARDCODED RULES)
═══════════════════════════════════════
NEVER do any of these — reject if asked in any prompt:
  ❌ Forced data wipe on any user action
  ❌ Paywall for accessing old period logs
  ❌ Video ads or interstitials before logging
  ❌ Auto-ending a period (only manual end)
  ❌ Removing free features (water log, basic tracking always free)
  ❌ Requesting location permission for any reason
  ❌ "Late period" or "overdue" language
  ❌ Predictions before 3 full cycles
  ❌ Forcing account creation
  ❌ Subscriptions or recurring payments
  ❌ Sending health data to partner in Partner Mode
  ❌ Showing notifications without permission grant

═══════════════════════════════════════
5. LEGAL REQUIREMENTS
═══════════════════════════════════════
India DPDP Act 2023 Compliance:
  - Consent screen on first launch with explicit data collection disclosure
  - Data stored only on device by default
  - User can delete all data with single tap in settings
  - Export all data: free, immediate, CSV/JSON format
  - Privacy policy URL: required in Play Store listing
  - No data fiduciary registration required (individual developer)
  
Medical Disclaimer (show on first launch + Settings):
  "Period Saathi is a personal tracking tool, not a medical device.
   Information provided is not medical advice. Consult a qualified 
   healthcare provider for medical concerns about your menstrual health."

Play Store Data Safety Section (pre-fill this):
  Data collected: None (if no account)
  Data collected with account: Email, Display name
  Health data: Stored on device only, not sent to developer
  Purpose: App functionality (sync, account recovery)
  Encryption in transit: Yes (TLS 1.3)
  Users can request deletion: Yes (in-app + email)
  
═══════════════════════════════════════
6. PRIORITY MATRIX
═══════════════════════════════════════
P0 — Ship-blocker (must work at launch):
  - Offline period logging with manual start/end
  - All buttons functional with spring animations
  - Navigation between all screens
  - Room database persisting data
  - All 17 screens previewable
  - Haptic feedback working
  - Medical disclaimer shown
  - DPDP consent on first launch

P1 — Launch quality (needed for good reviews):
  - Google backup (opt-in)
  - CSV + PDF export
  - Home screen widget
  - Razorpay one-time purchase
  - Biometric lock
  - Prediction (after 3 cycles) with confidence
  - Reminders (Water, Medicine, Period)
  - Streak + points system

P2 — Post-launch polish (v1.1):
  - Partner mode share intent
  - Irregular cycle support (polymenorrhea)
  - 2D/3D mascot depth effect
  - Supabase sync (cross-device)
  - Google Sign-In
  - Mood map heatmap
  - Monthly Wrapped

P3 — Future roadmap (v1.2+):
  - Pregnancy pause mode
  - Non-pink gender-neutral theme
  - Voice logging
  - Vernacular full UI
  - Time capsule journal
  - Seasonal mascot wardrobe drops
  - AI cycle whisperer

Write this complete TRD document now.
```

---

# SECTION 2 — ARCHITECTURE PLAN

```
Write the complete Architecture Plan document.

Create: docs/ARCHITECTURE.md

Include:

═══════════════════════════════════════
1. MODULE STRUCTURE
═══════════════════════════════════════

app/
├── src/main/
│   ├── java/com/periodsaathi/app/
│   │   ├── PeriodSaathiApp.kt          — @HiltAndroidApp, channels, init
│   │   ├── MainActivity.kt              — single activity, edge-to-edge
│   │   │
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── database/
│   │   │   │   │   ├── PeriodSaathiDatabase.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   └── Converters.kt
│   │   │   │   ├── dao/
│   │   │   │   │   ├── CycleEntryDao.kt
│   │   │   │   │   ├── SettingsDao.kt
│   │   │   │   │   ├── JournalDao.kt
│   │   │   │   │   ├── ReminderDao.kt
│   │   │   │   │   └── PendingSyncDao.kt
│   │   │   │   └── entity/
│   │   │   │       ├── CycleEntryEntity.kt
│   │   │   │       ├── SettingsEntity.kt
│   │   │   │       ├── JournalEntity.kt
│   │   │   │       ├── ReminderEntity.kt
│   │   │   │       └── PendingSyncEntity.kt
│   │   │   │
│   │   │   ├── remote/
│   │   │   │   ├── supabase/
│   │   │   │   │   ├── SupabaseClient.kt
│   │   │   │   │   ├── SupabaseModule.kt
│   │   │   │   │   └── dto/
│   │   │   │   │       ├── CycleEntryDto.kt
│   │   │   │   │       └── SettingsDto.kt
│   │   │   │   └── auth/
│   │   │   │       └── AuthManager.kt
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── CycleRepository.kt (interface)
│   │   │   │   ├── CycleRepositoryImpl.kt
│   │   │   │   ├── SettingsRepository.kt (interface)
│   │   │   │   ├── SettingsRepositoryImpl.kt
│   │   │   │   ├── JournalRepository.kt (interface)
│   │   │   │   ├── JournalRepositoryImpl.kt
│   │   │   │   └── AuthRepository.kt
│   │   │   │
│   │   │   └── preferences/
│   │   │       └── SecurePreferences.kt  — EncryptedSharedPreferences
│   │   │
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── CycleEntry.kt         — domain model (not entity)
│   │   │   │   ├── CycleSettings.kt
│   │   │   │   ├── PeriodPrediction.kt
│   │   │   │   ├── CyclePhase.kt (enum)
│   │   │   │   ├── FlowIntensity.kt (enum)
│   │   │   │   ├── MascotEmotion.kt (enum)
│   │   │   │   ├── PatternInsight.kt
│   │   │   │   └── JournalEntry.kt
│   │   │   │
│   │   │   └── usecase/
│   │   │       ├── cycle/
│   │   │       │   ├── StartPeriodUseCase.kt
│   │   │       │   ├── EndPeriodUseCase.kt
│   │   │       │   ├── LogDailyEntryUseCase.kt
│   │   │       │   ├── GetPredictionUseCase.kt  — min 3 cycles guard
│   │   │       │   ├── GetCurrentPhaseUseCase.kt
│   │   │       │   └── GetMonthCalendarUseCase.kt
│   │   │       ├── wellness/
│   │   │       │   ├── LogWaterUseCase.kt
│   │   │       │   ├── LogHabitUseCase.kt
│   │   │       │   └── GetWellnessDataUseCase.kt
│   │   │       ├── home/
│   │   │       │   └── GetHomeDataUseCase.kt
│   │   │       ├── sync/
│   │   │       │   ├── SyncDataUseCase.kt
│   │   │       │   └── ConflictResolutionUseCase.kt
│   │   │       └── payment/
│   │   │           ├── CheckPremiumStatusUseCase.kt
│   │   │           └── ProcessPurchaseUseCase.kt
│   │   │
│   │   ├── ui/
│   │   │   ├── theme/
│   │   │   │   ├── Color.kt
│   │   │   │   ├── Type.kt
│   │   │   │   ├── Shapes.kt
│   │   │   │   └── Theme.kt
│   │   │   │
│   │   │   ├── navigation/
│   │   │   │   ├── Screen.kt            — type-safe routes
│   │   │   │   ├── NavGraph.kt
│   │   │   │   └── BottomNavBar.kt
│   │   │   │
│   │   │   ├── components/              — SHARED COMPONENTS
│   │   │   │   ├── SaathiMascot.kt
│   │   │   │   ├── GlassCard.kt
│   │   │   │   ├── PrimaryButton.kt
│   │   │   │   ├── SecondaryButton.kt
│   │   │   │   ├── PastelChip.kt
│   │   │   │   ├── CycleRing.kt
│   │   │   │   ├── WaterRingWithWave.kt
│   │   │   │   ├── ConfettiOverlay.kt
│   │   │   │   ├── ShimmerSkeleton.kt
│   │   │   │   ├── BottomSheetContainer.kt
│   │   │   │   ├── PhaseIndicatorBanner.kt
│   │   │   │   ├── RestDayBanner.kt
│   │   │   │   ├── EmptyState.kt
│   │   │   │   ├── ErrorState.kt
│   │   │   │   └── ScaleButton.kt       — reusable scale-on-press
│   │   │   │
│   │   │   ├── screens/
│   │   │   │   ├── splash/
│   │   │   │   ├── onboarding/
│   │   │   │   ├── auth/
│   │   │   │   ├── home/
│   │   │   │   ├── calendar/
│   │   │   │   ├── wellness/
│   │   │   │   ├── partner/
│   │   │   │   ├── remedies/
│   │   │   │   ├── yoga/
│   │   │   │   ├── journal/
│   │   │   │   ├── insights/
│   │   │   │   ├── settings/
│   │   │   │   ├── report/
│   │   │   │   ├── wardrobe/
│   │   │   │   ├── challenges/
│   │   │   │   ├── breathing/
│   │   │   │   └── payment/
│   │   │   │
│   │   │   └── util/
│   │   │       ├── HapticFeedback.kt
│   │   │       ├── AnimationUtils.kt
│   │   │       ├── DateFormatter.kt
│   │   │       └── Extensions.kt
│   │   │
│   │   ├── worker/
│   │   │   ├── SyncWorker.kt
│   │   │   ├── ReminderWorker.kt
│   │   │   └── WidgetRefreshWorker.kt
│   │   │
│   │   ├── widget/
│   │   │   ├── CycleDayWidget.kt
│   │   │   └── PeriodCountdownWidget.kt
│   │   │
│   │   ├── notification/
│   │   │   ├── NotificationHelper.kt
│   │   │   └── BootReceiver.kt
│   │   │
│   │   └── security/
│   │       ├── BiometricManager.kt
│   │       ├── StealthModeManager.kt
│   │       └── CertificatePinner.kt
│   │
│   └── res/
│       ├── values/strings.xml
│       ├── values-hi/strings.xml
│       ├── xml/
│       │   ├── file_paths.xml
│       │   ├── network_security_config.xml  — cert pinning
│       │   └── backup_rules.xml
│       └── drawable/ (vector assets)
│
├── test/                               — unit tests
├── androidTest/                        — UI tests
└── screenshotTest/                     — screenshot tests

═══════════════════════════════════════
2. DATA FLOW DIAGRAM (text art)
═══════════════════════════════════════

User Tap
   ↓
Composable (stateless, hoisted state)
   ↓
ViewModel (StateFlow<UiState>)
   ↓
UseCase (business logic, validation)
   ↓
Repository Interface
   ↙           ↘
RoomDAO       SupabaseAPI (if online+logged in)
   ↓               ↓
SQLCipher DB   Supabase Postgres
   ↓
PendingSyncDao (queues offline changes)
   ↓ (on network restore)
SyncWorker (WorkManager) → Supabase

═══════════════════════════════════════
3. OFFLINE SYNC STRATEGY
═══════════════════════════════════════

PendingSyncEntity:
  id: Long
  operation: String (INSERT/UPDATE/DELETE)
  tableName: String
  recordId: String
  payload: String (JSON)
  createdAt: Long
  retryCount: Int
  lastError: String?

Sync flow:
  1. Every write → Room (immediate) + PendingSyncDao (queued)
  2. SyncWorker runs on: network available + periodic (15 min)
  3. SyncWorker reads all pending, sends to Supabase in batch
  4. On success: delete from PendingSyncDao
  5. On failure: increment retryCount, exponential backoff
  6. After 5 retries: mark as FAILED, alert user in Settings
  7. Pull sync on login: fetch remote, compare timestamps, merge

Conflict resolution (last-write-wins):
  - Every entity has updatedAt: Long field
  - Compare local updatedAt vs remote updatedAt
  - Higher timestamp wins
  - CycleEntry: if same date, MERGE symptoms/notes (union), 
    take max flowIntensity

Write this complete architecture document.
```

---

# SECTION 3 — BACKEND API SPEC (SUPABASE)

```
Write the complete Supabase backend specification.

Create: docs/BACKEND_SPEC.md

═══════════════════════════════════════
1. SUPABASE TABLE DEFINITIONS
═══════════════════════════════════════

Write exact SQL CREATE TABLE statements for:

TABLE: profiles
  id: UUID PRIMARY KEY REFERENCES auth.users(id)
  username: TEXT NOT NULL
  created_at: TIMESTAMPTZ DEFAULT NOW()
  updated_at: TIMESTAMPTZ DEFAULT NOW()
  premium_tier: TEXT DEFAULT 'FREE' -- FREE/PREMIUM
  premium_purchased_at: TIMESTAMPTZ
  razorpay_order_id: TEXT
  cycle_length_avg: INT DEFAULT 28
  period_length_avg: INT DEFAULT 5
  selected_theme: TEXT DEFAULT 'DEFAULT'
  language: TEXT DEFAULT 'en'

TABLE: cycle_entries
  id: UUID PRIMARY KEY DEFAULT gen_random_uuid()
  user_id: UUID REFERENCES profiles(id) ON DELETE CASCADE
  date: DATE NOT NULL
  flow_intensity: INT -- 0=none, 1=spot, 2=light, 3=medium, 4=heavy, 5=very heavy
  symptoms: TEXT[] -- array of symptom strings
  mood: TEXT
  water_glasses: INT DEFAULT 0
  notes: TEXT
  is_rest_day: BOOLEAN DEFAULT FALSE
  is_period_start: BOOLEAN DEFAULT FALSE
  is_period_end: BOOLEAN DEFAULT FALSE
  updated_at: TIMESTAMPTZ DEFAULT NOW()
  created_at: TIMESTAMPTZ DEFAULT NOW()
  UNIQUE(user_id, date)

TABLE: wellness_logs
  id: UUID PRIMARY KEY DEFAULT gen_random_uuid()
  user_id: UUID REFERENCES profiles(id) ON DELETE CASCADE
  date: DATE NOT NULL
  habits_completed: TEXT[] -- habit ids completed
  sleep_hours: DECIMAL(4,1)
  exercise_minutes: INT
  medicine_taken: BOOLEAN DEFAULT FALSE
  updated_at: TIMESTAMPTZ DEFAULT NOW()
  UNIQUE(user_id, date)

TABLE: journal_entries
  id: UUID PRIMARY KEY DEFAULT gen_random_uuid()
  user_id: UUID REFERENCES profiles(id) ON DELETE CASCADE
  date: DATE NOT NULL
  content: TEXT NOT NULL  -- encrypted client-side before upload
  mood_emojis: TEXT[]
  cycle_day: INT
  cycle_phase: TEXT
  is_time_capsule: BOOLEAN DEFAULT FALSE
  capsule_reveal_date: DATE
  is_revealed: BOOLEAN DEFAULT FALSE
  updated_at: TIMESTAMPTZ DEFAULT NOW()

TABLE: user_settings
  user_id: UUID PRIMARY KEY REFERENCES profiles(id) ON DELETE CASCADE
  notification_prefs: JSONB DEFAULT '{}'
  fertility_mode: TEXT DEFAULT 'NEUTRAL'
  contraception_mode: TEXT DEFAULT 'NONE'
  stealth_mode_enabled: BOOLEAN DEFAULT FALSE
  streak_count: INT DEFAULT 0
  total_points: INT DEFAULT 0
  last_sync_at: TIMESTAMPTZ
  updated_at: TIMESTAMPTZ DEFAULT NOW()

TABLE: purchases
  id: UUID PRIMARY KEY DEFAULT gen_random_uuid()
  user_id: UUID REFERENCES profiles(id)
  razorpay_payment_id: TEXT UNIQUE
  razorpay_order_id: TEXT
  product_id: TEXT -- 'THEMES_PACK', 'AD_FREE', 'PREMIUM_BUNDLE'
  amount: INT -- in paise
  currency: TEXT DEFAULT 'INR'
  status: TEXT DEFAULT 'PENDING' -- PENDING/COMPLETED/FAILED/REFUNDED
  purchased_at: TIMESTAMPTZ DEFAULT NOW()

═══════════════════════════════════════
2. ROW LEVEL SECURITY (RLS) POLICIES
═══════════════════════════════════════

Write SQL for RLS on every table:

-- Enable RLS
ALTER TABLE cycle_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE wellness_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE journal_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE purchases ENABLE ROW LEVEL SECURITY;

-- Policies: users can only access their own data
CREATE POLICY "Users own their cycle entries"
  ON cycle_entries FOR ALL
  USING (auth.uid() = user_id)
  WITH CHECK (auth.uid() = user_id);

-- Same pattern for all other tables
-- Purchases: read-only after insert (no update/delete by user)
CREATE POLICY "Users can insert own purchases"
  ON purchases FOR INSERT
  WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can read own purchases"
  ON purchases FOR SELECT
  USING (auth.uid() = user_id);
-- No UPDATE/DELETE policy on purchases (admin only)

═══════════════════════════════════════
3. SUPABASE EDGE FUNCTIONS
═══════════════════════════════════════

Function 1: verify-razorpay-payment
  Trigger: POST /functions/v1/verify-razorpay-payment
  Input: { razorpay_order_id, razorpay_payment_id, razorpay_signature, user_id, product_id }
  Logic:
    1. Verify HMAC signature using Razorpay key secret
    2. If valid: update purchases table status=COMPLETED
    3. Update profiles.premium_tier based on product_id
    4. Return: { success: true, premium_tier: "PREMIUM" }
  Auth: Requires valid Supabase JWT

Function 2: batch-sync
  Trigger: POST /functions/v1/batch-sync
  Input: { operations: [{operation, table, id, payload, updated_at}] }
  Logic:
    1. Verify JWT, extract user_id
    2. For each operation: upsert if updated_at > existing, skip if older
    3. Return: { synced: N, skipped: M, failed: [] }
  Rate limit: 100 operations per request

Function 3: export-user-data
  Trigger: GET /functions/v1/export-user-data
  Logic: Export all tables for user as JSON
  Returns: signed URL to download within 10 minutes

Function 4: delete-account
  Trigger: DELETE /functions/v1/delete-account
  Logic: Cascade delete all user data + auth.users record
  Returns: { success: true }

═══════════════════════════════════════
4. ANDROID SUPABASE CLIENT SETUP
═══════════════════════════════════════

Dependencies to add:
  implementation("io.github.jan-tennert.supabase:gotrue-kt:3.0.0")
  implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.0")
  implementation("io.github.jan-tennert.supabase:realtime-kt:3.0.0")
  implementation("io.ktor:ktor-client-android:2.3.12")

SupabaseClient.kt:
  @Singleton Hilt-provided SupabaseClient
  URL from BuildConfig.SUPABASE_URL (gradle local.properties)
  KEY from BuildConfig.SUPABASE_ANON_KEY
  Config: custom HTTP client with certificate pinning
  Session: persisted in EncryptedSharedPreferences

═══════════════════════════════════════
5. SYNC IMPLEMENTATION
═══════════════════════════════════════

SyncWorker.kt:
  - Triggered by: NetworkConstraint(CONNECTED) + Periodic(15 min)
  - Reads all PendingSyncEntity rows
  - Batches into groups of 50
  - Calls batch-sync Edge Function
  - On success: deletes synced rows from PendingSyncDao
  - On 429 (rate limit): exponential backoff (1min, 2min, 4min)
  - On 401 (auth): notify user to re-login
  - Timeout: 30 seconds per batch

Pull-on-login sync:
  - On successful login: call pull-sync immediately
  - Fetch all remote entries updated_at > last_sync_at (from user_settings)
  - Merge into local Room using upsert with conflict resolution
  - Update user_settings.last_sync_at = NOW()

Write this complete backend specification document.
```

---

# SECTION 4 — GRADLE BUILD FILES

```
Write ALL gradle and build configuration files for Period Saathi.

Complete these files:

═══════════════════════════════════════
FILE 1: settings.gradle.kts
═══════════════════════════════════════
pluginManagement {
  repositories {
    google() + gradlePluginPortal() + mavenCentral()
  }
}
dependencyResolutionManagement {
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
  repositories { google(), mavenCentral() }
}
rootProject.name = "PeriodSaathi"
include(":app")

═══════════════════════════════════════
FILE 2: build.gradle.kts (root)
═══════════════════════════════════════
Plugins block (apply false):
  - com.android.application: 9.2.1
  - com.android.library: 9.2.1
  - org.jetbrains.kotlin.android: 2.2.10
  - org.jetbrains.kotlin.plugin.compose: 2.2.10
  - com.google.dagger.hilt.android: 2.51.1
  - com.google.devtools.ksp: 2.2.10-1.0.31
  - org.jetbrains.kotlin.plugin.serialization: 2.2.10
  - com.google.gms.google-services: 4.4.2 (for Firebase Crashlytics)
  - com.google.firebase.crashlytics: 3.0.2
  - io.gitlab.arturbosch.detekt: 1.23.6

═══════════════════════════════════════
FILE 3: app/build.gradle.kts
═══════════════════════════════════════
Apply plugins:
  com.android.application, kotlin.android, kotlin.plugin.compose,
  kotlin.plugin.serialization, com.google.dagger.hilt.android,
  com.google.devtools.ksp, com.google.gms.google-services,
  com.google.firebase.crashlytics, io.gitlab.arturbosch.detekt

android {
  namespace = "com.periodsaathi.app"
  compileSdk = 36
  
  defaultConfig {
    applicationId = "com.periodsaathi.app"
    minSdk = 24; targetSdk = 36
    versionCode = 1; versionName = "1.0.0"
    
    // Read Supabase keys from local.properties
    buildConfigField("String", "SUPABASE_URL", properties["supabase.url"] ?: "\"\"")
    buildConfigField("String", "SUPABASE_ANON_KEY", properties["supabase.anon.key"] ?: "\"\"")
    buildConfigField("String", "RAZORPAY_KEY_ID", properties["razorpay.key.id"] ?: "\"\"")
    buildConfigField("String", "ADMOB_APP_ID", properties["admob.app.id"] ?: "\"ca-app-pub-test~test\"")
    
    ksp { arg("room.schemaLocation", "$projectDir/schemas") }
  }
  
  buildTypes {
    debug { 
      applicationIdSuffix = ".debug"
      buildConfigField("Boolean", "DEBUG_MODE", "true") 
    }
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(...)
      buildConfigField("Boolean", "DEBUG_MODE", "false")
      // Signing config reads from local.properties
    }
  }
  
  compileOptions { sourceCompatibility/targetCompatibility = JavaVersion.VERSION_17 }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures { compose = true; buildConfig = true }
  
  testOptions {
    unitTests.isReturnDefaultValues = true
    unitTests.isIncludeAndroidResources = true
  }
  
  bundle {
    language.enableSplit = true
    density.enableSplit = true
    abi.enableSplit = true
  }
}

COMPLETE DEPENDENCIES (exact versions):

// Compose BOM
implementation(platform("androidx.compose:compose-bom:2026.02.01"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")
implementation("androidx.compose.animation:animation")
implementation("androidx.compose.foundation:foundation")
debugImplementation("androidx.compose.ui:ui-tooling")
debugImplementation("androidx.compose.ui:ui-test-manifest")

// Navigation3 (type-safe)
implementation("androidx.navigation:navigation-compose:2.9.0")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")

// Hilt
implementation("com.google.dagger:hilt-android:2.51.1")
ksp("com.google.dagger:hilt-compiler:2.51.1")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
implementation("androidx.hilt:hilt-work:1.2.0")
ksp("androidx.hilt:hilt-compiler:1.2.0")

// Room + SQLCipher
implementation("androidx.room:room-runtime:2.7.0")
implementation("androidx.room:room-ktx:2.7.0")
ksp("androidx.room:room-compiler:2.7.0")
implementation("net.zetetic:android-database-sqlcipher:4.5.4")
implementation("androidx.sqlite:sqlite-ktx:2.4.0")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.1.2")

// WorkManager
implementation("androidx.work:work-runtime-ktx:2.10.0")

// Supabase
implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
implementation("io.github.jan-tennert.supabase:gotrue-kt")
implementation("io.github.jan-tennert.supabase:postgrest-kt")
implementation("io.ktor:ktor-client-android:2.3.12")

// Google Sign-In
implementation("com.google.android.gms:play-services-auth:21.2.0")
implementation("com.google.api-client:google-api-client-android:2.6.0")

// Razorpay
implementation("com.razorpay:checkout:1.6.41")

// AdMob
implementation("com.google.android.gms:play-services-ads:23.4.0")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
implementation("com.google.firebase:firebase-crashlytics-ktx")
implementation("com.google.firebase:firebase-analytics-ktx")

// Biometrics
implementation("androidx.biometric:biometric:1.2.0-alpha05")

// Security
implementation("androidx.security:security-crypto:1.1.0-alpha06")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

// JSON
implementation("com.google.code.gson:gson:2.11.0")

// Fonts
implementation("androidx.compose.ui:ui-text-google-fonts")

// Accompanist
implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
implementation("com.google.accompanist:accompanist-permissions:0.36.0")

// Glance (Widgets)
implementation("androidx.glance:glance-appwidget:1.1.1")
implementation("androidx.glance:glance-material3:1.1.1")

// PDF generation
implementation("com.itextpdf:itext7-core:8.0.5")

// Screenshot testing
screenshotTestImplementation("androidx.compose.ui:ui-tooling")

// Testing
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("app.cash.turbine:turbine:1.1.0")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
testImplementation("androidx.arch.core:core-testing:2.2.0")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
androidTestImplementation("androidx.test.ext:junit:1.2.1")
androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
kspAndroidTest("com.google.dagger:hilt-compiler:2.51.1")

═══════════════════════════════════════
FILE 4: local.properties (template — never commit)
═══════════════════════════════════════
# Android SDK path (auto-generated)
sdk.dir=/Users/you/Library/Android/sdk

# Supabase
supabase.url=https://YOUR_PROJECT.supabase.co
supabase.anon.key=YOUR_ANON_KEY

# Razorpay
razorpay.key.id=rzp_test_XXXXXX

# AdMob
admob.app.id=ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX

# Signing (release builds)
keystore.path=../period-saathi-key.jks
keystore.password=CHANGE_ME
key.alias=period-saathi
key.password=CHANGE_ME

FILE 5: .gitignore additions:
  local.properties, *.jks, google-services.json, .env
  /schemas/ (Room schemas — optional to commit)

Write all 5 files completely.
```

---

# SECTION 5 — FOUNDATION (DATABASE + THEME)

```
Create ALL foundation files. Write every file completely.

══════════════════════════════
GROUP A: THEME FILES
══════════════════════════════

A1: ui/theme/Color.kt
Define ALL color values:
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

  // Semantic colors
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
  
  // Gradient definitions as Brush extensions
  val GradientPrimary = Brush.horizontalGradient(listOf(BlushPink, DeepRose))
  val GradientSecondary = Brush.horizontalGradient(listOf(SoftLavender, SoftPurple))
  val GradientCycle = Brush.sweepGradient(listOf(BlushPink, SoftCoral, SoftLavender))
  val GradientWater = Brush.verticalGradient(listOf(BabyBlue, Color(0xFF7EC8E3)))
  
  // Theme-aware schemes
  Define LightColorScheme and DarkColorScheme using Material3 colorScheme()
  Map brand colors to appropriate Material3 slots

A2: ui/theme/Type.kt
  Load Nunito from GoogleFont provider
  Load Poppins from GoogleFont provider
  Define complete Typography object:
    displayLarge: Nunito, 32sp, weight 800
    displayMedium: Nunito, 28sp, weight 700
    headlineLarge: Nunito, 24sp, weight 700
    headlineMedium: Nunito, 20sp, weight 700
    titleLarge: Nunito, 18sp, weight 600
    titleMedium: Nunito, 16sp, weight 600
    bodyLarge: Poppins, 15sp, weight 400
    bodyMedium: Poppins, 14sp, weight 400
    bodySmall: Poppins, 12sp, weight 400
    labelLarge: Poppins, 13sp, weight 600
    labelMedium: Poppins, 12sp, weight 500
    labelSmall: Poppins, 11sp, weight 500

A3: ui/theme/Shapes.kt
  val CardShape = RoundedCornerShape(28.dp)
  val ButtonShape = RoundedCornerShape(50.dp)
  val ChipShape = RoundedCornerShape(20.dp)
  val InputShape = RoundedCornerShape(16.dp)
  val BottomSheetShape = RoundedCornerShape(topStart=32.dp, topEnd=32.dp)
  val SmallCardShape = RoundedCornerShape(16.dp)
  val BadgeShape = RoundedCornerShape(50.dp)
  
  Material3 shapes:
  val AppShapes = Shapes(
    small = SmallCardShape, medium = CardShape, large = CardShape
  )

A4: ui/theme/Theme.kt
  PeriodSaathiTheme composable:
  - Parameter: darkTheme, dynamicColor (default true on API 31+)
  - Apply status bar color: CreamWhite for light, DarkNavy for dark
  - Apply navigation bar color: match theme
  - Set isAppearanceLightStatusBars
  - Use WindowCompat.getInsetsController
  - Apply fonts and shapes to MaterialTheme
  
  Add CompositionLocal for:
  LocalHapticFeedback (custom haptic controller)
  LocalSpringSpec (shared spring animation spec)

══════════════════════════════
GROUP B: ROOM DATABASE
══════════════════════════════

B1: data/local/entity/CycleEntryEntity.kt
@Entity(tableName = "cycle_entries",
  indices = [Index(value = ["date"], unique = true)])
data class:
  id: Long (autoGenerate = true)
  date: Long (epoch millis, day only — strip time)
  flowIntensity: Int (0-5, 0=no period logged)
  symptoms: String (JSON array)
  mood: String?
  notes: String?
  waterGlasses: Int (0)
  medicineTaken: Boolean (false)
  isRestDay: Boolean (false)
  isPeriodStart: Boolean (false)
  isPeriodEnd: Boolean (false)
  sleepHours: Float (0f)
  exerciseMinutes: Int (0)
  createdAt: Long
  updatedAt: Long
  syncedAt: Long? (null = not synced)
  remoteId: String? (Supabase UUID)

B2: data/local/entity/SettingsEntity.kt
@Entity(tableName = "settings")
data class:
  id: Int = 1 (singleton)
  userName: String = "Friend"
  averageCycleLength: Int = 28
  averagePeriodLength: Int = 5
  lastPeriodStartDate: Long?
  lastPeriodEndDate: Long?
  cyclesLogged: Int = 0 (increment on period end)
  partnerName: String?
  partnerPhone: String?
  language: String = "en"
  contraceptionMode: String = "NONE"
  fertilityMode: String = "NEUTRAL"
  stealthModeEnabled: Boolean = false
  biometricLockEnabled: Boolean = false
  selectedTheme: String = "DEFAULT"
  soundEnabled: Boolean = true
  hapticEnabled: Boolean = true
  streakCount: Int = 0
  totalPoints: Int = 0
  lastStreakDate: Long?
  premiumTier: String = "FREE"
  adFreeEnabled: Boolean = false
  isPregnancyPauseActive: Boolean = false
  notificationsOptedIn: Boolean = false
  analyticsOptedIn: Boolean = false
  backupOptedIn: Boolean = false
  lastBackupDate: Long?
  firstLaunchDate: Long?
  consentAccepted: Boolean = false
  medicalDisclaimerAccepted: Boolean = false
  updatedAt: Long

B3: data/local/entity/JournalEntity.kt
@Entity(tableName = "journal_entries")
  id: Long (autoGenerate)
  date: Long
  content: String (stored encrypted — encrypt before insert, decrypt after read)
  moodEmojis: String (JSON array)
  cycleDay: Int
  cyclePhase: String
  isTimeCapsule: Boolean = false
  capsuleRevealDate: Long?
  isRevealed: Boolean = false
  createdAt: Long
  updatedAt: Long
  remoteId: String?

B4: data/local/entity/ReminderEntity.kt
@Entity(tableName = "reminders")
  id: Long (autoGenerate)
  type: String (WATER/MEDICINE/PERIOD/YOGA/CUSTOM)
  label: String
  timeHour: Int
  timeMinute: Int
  intervalHours: Int? (for repeating)
  isEnabled: Boolean = true
  quietHoursEnabled: Boolean = true
  medicationName: String?
  dosage: String?

B5: data/local/entity/PendingSyncEntity.kt
@Entity(tableName = "pending_sync")
  id: Long (autoGenerate)
  operation: String (UPSERT/DELETE)
  tableName: String
  recordLocalId: Long
  recordRemoteId: String?
  payload: String (JSON)
  createdAt: Long
  retryCount: Int = 0
  lastError: String?
  status: String = "PENDING" (PENDING/PROCESSING/FAILED)

B6: data/local/dao/CycleEntryDao.kt
@Dao interface with:
  @Insert(onConflict = REPLACE) suspend fun insert(entry: CycleEntryEntity): Long
  @Update suspend fun update(entry: CycleEntryEntity)
  @Delete suspend fun delete(entry: CycleEntryEntity)
  @Query("SELECT * FROM cycle_entries WHERE date BETWEEN :start AND :end ORDER BY date ASC")
  fun getEntriesBetweenDates(start: Long, end: Long): Flow<List<CycleEntryEntity>>
  @Query("SELECT * FROM cycle_entries WHERE date = :date LIMIT 1")
  fun getEntryByDate(date: Long): Flow<CycleEntryEntity?>
  @Query("SELECT * FROM cycle_entries WHERE flowIntensity > 0 ORDER BY date DESC")
  fun getAllPeriodDays(): Flow<List<CycleEntryEntity>>
  @Query("SELECT * FROM cycle_entries WHERE isPeriodStart = 1 ORDER BY date DESC LIMIT :limit")
  fun getPeriodStartDates(limit: Int): Flow<List<CycleEntryEntity>>
  @Query("SELECT SUM(waterGlasses) FROM cycle_entries WHERE date = :date")
  fun getWaterForDate(date: Long): Flow<Int?>
  @Query("SELECT * FROM cycle_entries ORDER BY date DESC LIMIT :limit")
  fun getRecentEntries(limit: Int): Flow<List<CycleEntryEntity>>
  @Query("UPDATE cycle_entries SET syncedAt = :timestamp, remoteId = :remoteId WHERE id = :id")
  suspend fun markSynced(id: Long, timestamp: Long, remoteId: String)

B7: data/local/dao/SettingsDao.kt
  @Query("SELECT * FROM settings WHERE id = 1")
  fun getSettings(): Flow<SettingsEntity?>
  @Insert(onConflict = REPLACE) suspend fun upsertSettings(settings: SettingsEntity)
  @Query("UPDATE settings SET streakCount = :streak, totalPoints = :points, lastStreakDate = :date WHERE id = 1")
  suspend fun updateStreakAndPoints(streak: Int, points: Int, date: Long)
  @Query("UPDATE settings SET cyclesLogged = cyclesLogged + 1 WHERE id = 1")
  suspend fun incrementCyclesLogged()

B8: data/local/dao/JournalDao.kt
B9: data/local/dao/ReminderDao.kt
B10: data/local/dao/PendingSyncDao.kt
(Standard CRUD + Flow queries for each)

B11: data/local/database/Converters.kt
TypeConverters:
  List<String> ↔ JSON String (Gson)
  LocalDate ↔ Long (epochDay)

B12: data/local/database/PeriodSaathiDatabase.kt
@Database(
  entities = [CycleEntryEntity::class, SettingsEntity::class, 
              JournalEntity::class, ReminderEntity::class, PendingSyncEntity::class],
  version = 1,
  exportSchema = true
)
@TypeConverters(Converters::class)
abstract class with:
  abstract fun cycleEntryDao(): CycleEntryDao
  abstract fun settingsDao(): SettingsDao
  abstract fun journalDao(): JournalDao
  abstract fun reminderDao(): ReminderDao
  abstract fun pendingSyncDao(): PendingSyncDao
  
  companion object {
    fun create(context: Context): PeriodSaathiDatabase {
      val passphrase = SQLiteDatabase.getBytes(
        SecurePreferences.getDatabaseKey(context).toCharArray()
      )
      val factory = SupportFactory(passphrase)
      return Room.databaseBuilder(context, PeriodSaathiDatabase::class.java, "period_saathi.db")
        .openHelperFactory(factory)
        .fallbackToDestructiveMigrationOnDowngrade() // prod: use migrations instead
        .build()
    }
  }

B13: data/di/DatabaseModule.kt
@Module @InstallIn(SingletonComponent::class)
Provides @Singleton:
  database, cycleEntryDao, settingsDao, journalDao, reminderDao, pendingSyncDao

B14: security/SecurePreferences.kt
Object with:
  fun getDatabaseKey(context: Context): String
    - Uses EncryptedSharedPreferences with AES256_SIV + AES256_GCM
    - On first call: generates random 32-char key, stores encrypted
    - Returns same key on subsequent calls
  fun getEncryptedPrefs(context: Context): SharedPreferences
    - Returns EncryptedSharedPreferences instance

Write all 14 files completely.
```

---

# SECTION 6 — NAVIGATION (FULLY WORKING)

```
Create complete, working navigation for all 17 screens.
Every route must compile and navigate correctly.

FILE 1: ui/navigation/Screen.kt

import kotlinx.serialization.Serializable

// Every route as @Serializable — required for Nav3 type-safe routing
@Serializable object Splash
@Serializable object Onboarding
@Serializable object Login
@Serializable data class NameSetup(val fromGoogle: Boolean = false)
@Serializable object Home
@Serializable object Calendar
@Serializable object Wellness
@Serializable object PartnerMode
@Serializable object Remedies
@Serializable object YogaFlow
@Serializable object Journal
@Serializable object MoodMap
@Serializable object Insights
@Serializable object Settings
@Serializable object ReportExport
@Serializable object Wardrobe
@Serializable object Challenges
@Serializable object BreathingMode
@Serializable object Payment
@Serializable data class DayLog(val dateEpoch: Long)

FILE 2: ui/navigation/NavGraph.kt

@Composable
fun PeriodSaathiNavGraph(
  navController: NavHostController = rememberNavController(),
  startDestination: Any = Splash
) {
  NavHost(
    navController = navController,
    startDestination = startDestination,
    enterTransition = { 
      slideInHorizontally(
        initialOffsetX = { it }, 
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessMedium
        )
      ) + fadeIn(animationSpec = tween(300))
    },
    exitTransition = {
      slideOutHorizontally(
        targetOffsetX = { -it/3 },
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
      ) + fadeOut(animationSpec = tween(200))
    },
    popEnterTransition = {
      slideInHorizontally(
        initialOffsetX = { -it/3 },
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
      ) + fadeIn(animationSpec = tween(300))
    },
    popExitTransition = {
      slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
      ) + fadeOut()
    }
  ) {
    composable<Splash> { SplashScreen(onNavigate = { navController.navigate(it) { popUpTo<Splash> { inclusive = true } } }) }
    composable<Onboarding> { OnboardingScreen(onComplete = { navController.navigate(Login) }) }
    composable<Login> { LoginScreen(navController) }
    composable<NameSetup> { NameSetupScreen(navController) }
    composable<Home> { HomeScreen(navController) }
    composable<Calendar> { CalendarScreen(navController) }
    composable<Wellness> { WellnessScreen(navController) }
    composable<PartnerMode> { PartnerModeScreen(navController) }
    composable<Remedies> { RemediesScreen(navController) }
    composable<YogaFlow> { YogaFlowScreen(navController) }
    composable<Journal> { JournalScreen(navController) }
    composable<MoodMap> { MoodMapScreen(navController) }
    composable<Insights> { InsightsScreen(navController) }
    composable<Settings> { SettingsScreen(navController) }
    composable<ReportExport> { ReportExportScreen(navController) }
    composable<Wardrobe> { WardrobeScreen(navController) }
    composable<Challenges> { ChallengesScreen(navController) }
    composable<BreathingMode> { BreathingModeScreen(navController) }
    composable<Payment> { PaymentScreen(navController) }
    composable<DayLog> { backStackEntry ->
      val dayLog = backStackEntry.toRoute<DayLog>()
      DayLogScreen(dateEpoch = dayLog.dateEpoch, navController = navController)
    }
  }
}

FILE 3: MainActivity.kt (complete rewrite)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  
  private lateinit var biometricManager: AppBiometricManager
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    WindowCompat.setDecorFitsSystemWindows(window, false)
    // FLAG_SECURE: prevents screenshots in recent apps
    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, 
                    WindowManager.LayoutParams.FLAG_SECURE)
    
    biometricManager = AppBiometricManager(this)
    
    setContent {
      PeriodSaathiTheme {
        val navController = rememberNavController()
        // Show scaffold with bottom nav only for main screens
        val currentRoute by navController.currentBackStackEntryAsState()
        val showBottomNav = currentRoute?.destination?.route in mainScreenRoutes
        
        Scaffold(
          bottomBar = {
            AnimatedVisibility(
              visible = showBottomNav,
              enter = slideInVertically(initialOffsetY = { it }),
              exit = slideOutVertically(targetOffsetY = { it })
            ) {
              BottomNavBar(navController = navController)
            }
          },
          contentWindowInsets = WindowInsets.navigationBars
        ) { paddingValues ->
          Box(modifier = Modifier.padding(paddingValues)) {
            PeriodSaathiNavGraph(navController = navController)
          }
        }
      }
    }
  }
}

val mainScreenRoutes = setOf(
  Home::class.qualifiedName,
  Calendar::class.qualifiedName,
  Wellness::class.qualifiedName,
  Settings::class.qualifiedName
)

FILE 4: ui/navigation/BottomNavBar.kt
Custom bottom nav composable.
4 tabs: Home (house icon), Calendar (calendar icon), Wellness (drop icon), More (grid icon)
- Fully custom: do NOT use NavigationBar composable
- Frosted glass background: Surface + alpha(0.92f) + background(CreamWhite)
- Active indicator: animated pill that slides to active tab position
  Use animateDpAsState(targetValue = tabOffset, animationSpec = spring()) for X position
- Tab icons: use Icons.Rounded.* from material-icons-extended
- Active tab: icon scale 1.2 + icon color BlushPink + label visible
- Inactive tab: icon scale 1.0 + icon color 40% opacity + no label
- Icon scale: animateFloatAsState with spring
- Height: 72.dp + WindowInsets.navigationBars
- Every tab button:
  onClick: navController.navigate with launchSingleTop=true, restoreState=true
  pointerInput for press scale 0.9 → release 1.0
  
Include @Preview showing all 4 tabs, first tab active.
Write all 4 files.
```

---

# SECTION 7 — SHARED COMPONENTS (ALL INTERACTIVE)

```
Create every shared UI component. ALL must be:
1. Fully interactive (real onClick, real state changes)
2. Have @Preview with realistic fake data
3. Have proper haptic feedback
4. Have spring physics animations

FILE 1: ui/util/HapticFeedback.kt
fun Modifier.hapticClick(
  type: HapticFeedbackType = HapticFeedbackType.LongPress,
  onClick: () -> Unit
): Modifier
  - Combines clickable + haptic in one modifier
  - Uses LocalHapticFeedback.current.performHapticFeedback

Also create:
  enum class HapticType { LIGHT, MEDIUM, HEAVY, SUCCESS, ERROR }
  fun performHaptic(context: Context, type: HapticType)
    - LIGHT: VibrationEffect.createOneShot(30, 80)
    - MEDIUM: VibrationEffect.createOneShot(60, 150)
    - HEAVY: VibrationEffect.createOneShot(100, 255)
    - SUCCESS: VibrationEffect.createWaveform([0,30,50,30], [-1,200,-1,150], -1)
    - ERROR: VibrationEffect.createWaveform([0,50,30,50,30,50], [-1,255,-1,255,-1,255], -1)

FILE 2: ui/components/ScaleButton.kt
A reusable composable that wraps any content with press-scale animation.
val scale by animateFloatAsState(
  targetValue = if (pressed) 0.94f else 1.0f,
  animationSpec = if (pressed) 
    tween(120, easing = FastOutLinearInEasing) 
  else 
    spring(dampingRatio = 0.6f, stiffness = 500f)
)
Use interactionSource to detect press state.
Apply Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
Also apply shadow reduction on press: graphicsLayer { shadowElevation = if (pressed) 2f else 8f }

FILE 3: ui/components/GlassCard.kt
Parameters: 
  modifier, shape, onClick?, enabled, tint (color tint overlay), content
Implementation:
  Surface(
    shape = shape,
    color = Color.White.copy(alpha = 0.45f),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
    modifier = modifier.shadow(
      elevation = 8.dp,
      shape = shape,
      ambientColor = BlushPink.copy(alpha = 0.18f),
      spotColor = BlushPink.copy(alpha = 0.18f)
    )
  )
  Wrap in ScaleButton if onClick != null
  Add tint overlay Box if tint != Color.Transparent
Include @Preview light and dark

FILE 4: ui/components/PrimaryButton.kt
Parameters: text, onClick, modifier, isLoading, enabled, icon (optional)
States:
  NORMAL: gradient background (BlushPink → DeepRose)
  LOADING: circular progress replaces text, button stays same size
  DISABLED: 50% opacity
  
AnimatedContent(targetState = isLoading) handles text ↔ loading swap
Scale animation on press via ScaleButton wrapper
Haptic: MEDIUM on tap
Min height: 56.dp, min width: 120.dp
Include @Preview for all 3 states

FILE 5: ui/components/PastelChip.kt
Parameters: label, selected, onClick, color, leadingIcon, trailingIcon
Selected: gradient fill (using color param) + scale 1.03
Unselected: white with border 1dp color.copy(alpha=0.4f)
Selection toggle: animate background color + scale
Height: 36.dp, padding: 12.dp horizontal
Include @Preview grid of chips, some selected

FILE 6: ui/components/CycleRing.kt
Parameters: currentDay, totalDays, phase, modifier, size
Canvas-based donut ring:
  backgroundArc: 220 degree arc, grey, strokeWidth 16.dp
  progressArc: animated from 0 to (currentDay/totalDays * 220), 
    gradient Brush.sweepGradient(phase.colors)
  Glow: second arc same path, paint with BlurMaskFilter(16f, NORMAL)
  Pulsing glow: infiniteTransition animateFloat for glow alpha 0.3↔0.8
  Center text: currentDay (large) + "Day" label (small)
  Phase emoji: shown just below center text
animateFloatAsState for progress with spring animation on currentDay change
Include @Preview

FILE 7: ui/components/WaterRingWithWave.kt
Parameters: currentGlasses, totalGlasses, modifier, size, onGoalReached
Canvas-based with wave animation:
  Background ring: grey, 12dp stroke
  Progress arc: BabyBlue gradient fill
  Wave inside circle:
    val phase by infiniteTransition.animateFloat(0f, 2*PI.toFloat(), 1800ms linear)
    fillLevel = currentGlasses / totalGlasses.toFloat()
    Draw Path:
      Start at left of circle at fillLevel height
      Sine wave: y = centerY + amplitude * sin(2π * x/wavelength + phase)
      Close path at right → down → back left
    Fill with BabyBlue.copy(alpha=0.6f)
    Second wave: same but phase offset by PI, alpha 0.4
  Center: "N/M" text + drop emoji
When currentGlasses == totalGlasses → call onGoalReached()
Include @Preview with 4/8 glasses state

FILE 8: ui/components/SaathiMascot.kt
Full Canvas-based mascot:
enum class MascotEmotion { HAPPY, SAD, SLEEPING, EXCITED, PAIN, LISTENING, HUGGING }

Draw mascot in DrawScope:
  Body: RoundRect with blush-to-coral gradient
  Arms: two rounded Rect stubs
  
Eyes per emotion:
  HAPPY: two filled ovals (eyes open) + highlight dots + cheek blush circles
  SAD: eyes with drooped outer corners + teardrop animated below left eye
  SLEEPING: two curved lines (drawn as arcs, closed eyes)
  EXCITED: large round eyes + highlight
  PAIN: compressed ovals (squinting)
  LISTENING: normal eyes + two side bumps (ears)

Mouth per emotion:
  HAPPY/EXCITED: arc smiling upward
  SAD: arc curving downward  
  SLEEPING: gentle small smile
  PAIN: slightly open downward curve

ANIMATIONS:
  Idle float: val floatOffset by infiniteTransition.animateFloat(-6f, 6f, 3000ms)
  Apply as translationY to Canvas

  Sleeping Zzz: 3 Text elements animated:
    Each animatable: startY=0 → endY=-50dp, alpha 1→0, 2000ms, staggered 700ms

  Sad teardrop: Animatable Y offset loops downward, repeats

  Excited bounce: val bounceOffset by infiniteTransition.animateFloat(0f, -8f, 600ms)

CLICK:
  val scale = remember { Animatable(1f) }
  On click: launch { scale.animateTo(1.3f, spring()); scale.animateTo(1f, spring()) }
  Apply graphicsLayer { scaleX = scale.value; scaleY = scale.value }
  Show speech bubble above mascot on click (state: showTip)

SPEECH BUBBLE:
  Custom Path shape: rounded rect + triangular tail pointing down
  draw using Canvas drawPath
  Overlay Box with MascotTipBubble composable
  Typewriter text animation

Include 7 @Preview composables, one per emotion

FILE 9: ui/components/ConfettiOverlay.kt
60 particles, physics simulation:
  data class Particle(x, y, vx, vy, color, size, rotation)
  Initialize 60 particles at random x positions along top, random velocities
  Use withFrameNanos loop:
    LaunchedEffect(visible) {
      while(visible && isActive) {
        withFrameNanos { frameNanos ->
          val dt = /* delta time */
          particles.forEach { p ->
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.vy += GRAVITY * dt  // 980 pixels/s²
            p.rotation += p.rotationSpeed * dt
          }
        }
      }
    }
  Draw each particle as a rounded rect on Canvas
  Colors: brand palette (BlushPink, Lavender, BabyBlue, ButterYellow, MintGreen)
  Auto-dismiss when all particles below screen bottom

FILE 10: ui/components/ShimmerSkeleton.kt
Reusable shimmer loading placeholders:
  Shimmer brush:
    val shimmerColors = listOf(
      Color.White.copy(alpha = 0.2f),
      Color.White.copy(alpha = 0.5f),
      Color.White.copy(alpha = 0.2f)
    )
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(0f, 1000f, 1500ms linear)
    val brush = Brush.linearGradient(
      colors = shimmerColors,
      start = Offset(translateAnim - 300f, 0f),
      end = Offset(translateAnim, 0f)
    )
  ShimmerBox(modifier): Box with background(brush, shape)
  ShimmerHomeScreen(): full home screen shimmer layout
  ShimmerCalendarGrid(): calendar grid shimmer
  ShimmerCard(height): single card shimmer

Write all 10 files completely with ALL imports.
```

---

# SECTION 8 — ALL 17 SCREENS (COMPLETE, WORKING)

```
Create ALL screen files. Every screen MUST have:
✅ Real ViewModel with @HiltViewModel
✅ All buttons have onClick handlers
✅ @Preview annotation with fake preview data
✅ Loading/Success/Error state handling
✅ Haptic feedback on interactions
✅ Spring animations on all transitions
✅ Back navigation handling

Write these in order. For EACH screen write BOTH the ViewModel AND the Screen composable.

═══════════════════
SCREEN 1: Splash
═══════════════════
SplashViewModel:
  - determineStartDestination(): 
    Check SettingsDao for firstLaunch, consentAccepted, biometricEnabled
    Return: Onboarding / Login / Home / LockScreen
  - StateFlow<SplashState>: Loading, NavigateTo(destination)

SplashScreen:
  - Fullscreen gradient background (animated mesh, 4 orbs)
  - Saathi mascot drops in from top with spring physics
    Animatable(initialValue = -300f) → animateTo(0f, spring(dampingRatio=0.4f))
  - App name types in letter by letter (LaunchedEffect + delay per char)
  - Auto-navigate after mascot animation + 500ms delay
  - NO buttons (auto-transition)
  
@Preview SplashPreview: show static state with mascot centered

═══════════════════
SCREEN 2: Onboarding
═══════════════════
OnboardingViewModel:
  - currentPage: StateFlow<Int>
  - fun nextPage() / fun skip()
  - fun completeOnboarding()

OnboardingScreen:
  - HorizontalPager, 3 pages
  - Custom page transition: pages scale 0.92 when not active
    Use pagerState.currentPageOffsetFraction for interpolation
  - Page 1: Mascot HAPPY + welcome text
  - Page 2: Calendar illustration (drawn with Canvas) + tracking text  
  - Page 3: Stars/rewards illustration + CTA button
  - Progress dots: pill shape for active, circle for inactive
    animateDpAsState for width transition (8.dp → 20.dp for active)
  - Skip button: visible on pages 1-2, hidden on page 3
    AnimatedVisibility with fadeIn/fadeOut
  - "Get Started" button on page 3: PrimaryButton → confetti + navigate

@Preview OnboardingPage1Preview

═══════════════════
SCREEN 3: Login
═══════════════════
LoginViewModel:
  - loginState: StateFlow<LoginState> (Idle/Loading/Success/Error)
  - fun signInWithGoogle(credential: GoogleIdTokenCredential)
  - fun signInWithEmail(email: String, password: String)
  - fun continueAsGuest()

LoginScreen:
  - Animated gradient mesh background (4 floating orbs on Canvas)
  - Floating particles (25 items, loop from bottom to top)
  - Mascot entrance: springs down from top
  - App name letter-by-letter animation
  - GlassCard login container
  - Google Sign-In button:
    Implementation: use Credential Manager API (Android 14+) 
    with GetGoogleIdOption
    Fallback: ActivityResultLauncher for older APIs
    UI: custom button NOT Material button
    Animations: shimmer idle, press scale, loading circle, success green
  - Email/password option (secondary, collapsible)
  - "Continue without account" ghost button → navigate Home
  - Privacy note at bottom
  - All states handled: idle/loading/success/error

IMPORTANT — Google Sign-In implementation:
  val credentialManager = CredentialManager.create(context)
  val googleIdOption = GetGoogleIdOption.Builder()
    .setFilterByAuthorizedAccounts(false)
    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
    .build()
  val request = GetCredentialRequest.Builder()
    .addCredentialOption(googleIdOption)
    .build()
  // Launch in coroutine, handle GetCredentialException

@Preview LoginPreview: show with card visible, mascot visible

═══════════════════
SCREEN 4: Home
═══════════════════
HomeViewModel:
  UiState data class:
    userName, cycleDay, totalCycleDays, phase, 
    todayEntry, prediction (nullable until 3 cycles),
    waterGlasses, streakCount, points,
    mascotEmotion, showRestDay, showConfetti,
    isFirstLaunch (show medical disclaimer)
  
  All 6 StateFlows combined with combine() operator
  fun logWater(amount: Int): awards points, checks goal
  fun dismissRestDay()
  fun dismissMedicalDisclaimer()
  fun onMascotTapped(): returns random tip

HomeScreen:
  LazyColumn with these sections (all animated on first load):
  1. Medical disclaimer banner (if first launch) — dismissible
  2. Greeting header (time-aware: morning/afternoon/evening)
  3. Mascot hero card (GlassCard, mascot centered, tap for tips)
  4. Cycle ring centered with prediction text
  5. REST DAY banner (conditional, animated slide from top)
  6. Quick action buttons row (4 chips → navigate on tap)
  7. Stats grid (2×2: Water, Mood, Symptoms, Streak)
  8. Prediction banner (if ≥3 cycles, expandable)
  9. AdMob banner (if non-premium, at bottom)
  
  Staggered entry animation: each section has LaunchedEffect(Unit)
    delay(index * 80ms) then animate alpha+translateY
    Use remember { Animatable(0f) } for each section
  
  Floating action button: "+" → DayLog(today's date)
  
  Pull to refresh: PullToRefreshBox → refreshes all data
    Custom indicator: mascot grabs rope animation (Canvas)

@Preview HomePreview with fake HomeUiState

═══════════════════
SCREEN 5: Calendar
═══════════════════
CalendarViewModel:
  currentYearMonth: StateFlow<YearMonth>
  calendarDays: StateFlow<List<CalendarDayData>>
  selectedDate: StateFlow<LocalDate?>
  showLogSheet: StateFlow<Boolean>
  fertilityMode: StateFlow<FertilityMode>
  
  CalendarDayData: date, entry?, isToday, isPredicted, isFertile, isPMS
  
  fun previousMonth() / fun nextMonth()
  fun selectDate(date: LocalDate)
  fun logEntry(date, intensity, symptoms, mood)
  fun toggleFertilityMode(mode: FertilityMode)

CalendarScreen:
  Month header with swipe gesture navigation
  AnimatedContent on yearMonth: slideInHorizontally with direction
  Day-of-week row
  LazyVerticalGrid (7 columns) of DayCell composables
  
  DayCell (extract as separate composable):
    All states: normal/period(1-5)/predicted/fertile/pms/today/selected
    Tap: scale bounce + selectedDate update
    Long press: DayLog navigation
    All visual states as described in design system
  
  Day Log BottomSheet:
    ModalBottomSheet
    Flow intensity selector (3 buttons)
    Symptom chips (FlowRow)
    Mood selector (5 emoji buttons)
    Water stepper
    Notes TextField
    Save button → logEntry → close sheet
    
  Fertility mode toggle at bottom:
    3-state segmented control (Neutral/Planning/Avoiding)
    Sliding pill indicator

@Preview CalendarPreview / @Preview DayCellPreview (all states)

═══════════════════
SCREEN 6: Wellness
═══════════════════
WellnessViewModel:
  wellnessState: StateFlow<WellnessUiState>
  showConfetti: StateFlow<Boolean>
  
  fun addWater(glasses: Int)
  fun toggleHabit(habitId: String)
  fun addCustomHabit(name: String, emoji: String)
  fun deleteHabit(habitId: String)

WellnessScreen:
  Sections:
  1. Water intake: WaterRingWithWave (200dp) + add buttons
     Goal reached → ConfettiOverlay + haptic SUCCESS
  2. Habits checklist: animated checkbox rows
     Completing all → mascot excited state + celebratory animation
  3. Sleep log: Slider (0-12 hours, 0.5 step)
  4. Exercise: quick type chips (Walk/Yoga/Gym/Other) + minutes input
  5. Rewards progress: points bar + reward preview cards
     Point earned: "+N pts" float-up animation
  6. Daily streak counter: flame emoji + count, scale bounce on increment

@Preview WellnessPreview

═══════════════════
SCREEN 7: Partner Mode
═══════════════════
PartnerViewModel:
  selectedRequests: StateFlow<Set<String>>
  customMessage: StateFlow<String>
  sendState: StateFlow<SendState>
  partnerName: StateFlow<String>
  isPartnerLinked: StateFlow<Boolean>
  
  fun toggleRequest(id: String)
  fun updateCustomMessage(msg: String)
  fun sendViaShareIntent(context: Context)
    → builds care message text (NO health data)
    → launches Intent.ACTION_SEND chooser

PartnerModeScreen:
  Privacy header with expandable explanation
  Care request 2-column grid (10 request cards)
  Selected cards: fill with BlushPink gradient + pulse heart
  Multi-selection: each selection bounces independently
  Custom message TextField
  Send button with 3 states (Idle/Sending/Sent)
  Not-linked state: code generation UI

@Preview PartnerModePreview

═══════════════════
SCREEN 8: Remedies
═══════════════════
RemediesViewModel:
  flippedCards: StateFlow<Set<String>>
  hotBagPosition: StateFlow<Float> (0f-1f)
  fun flipCard(id: String)
  fun updateHotBag(position: Float)

RemediesScreen:
  "Natural Remedies 🌿" section:
    LazyRow of FlipCard composables
    3D flip via graphicsLayer { rotationY = animated 0→180 }
    Front: emoji + name + "Tap to reveal"
    Back: ingredients + steps + helpful-for chips
    
  "Hot Bag Safety" section:
    Custom slider with track gradient (green → yellow → red)
    Thumb: animated color change with animateColorAsState
    Danger zone (>80%): thumb shakes + warning card appears
    On release in danger: snap back to 0.6f with spring()
    
  "Yoga Flow" section:
    Preview card showing first pose
    "Start 4-min Flow" button → navigate(YogaFlow)

@Preview RemediesPreview / @Preview FlipCardBothSides

═══════════════════
SCREEN 9: Yoga Flow
═══════════════════
YogaFlowViewModel:
  currentPoseIndex: StateFlow<Int>
  isPlaying: StateFlow<Boolean>
  timeRemaining: StateFlow<Int>
  breathingPhase: StateFlow<BreathingPhase> (INHALE/HOLD/EXHALE)
  
  fun nextPose() / fun previousPose()
  fun togglePause()
  fun exit()
  
  LaunchedEffect timer: countdown per pose, auto-advance

YogaFlowScreen:
  FULLSCREEN (hide system UI with WindowInsetsController)
  Dark background
  Pose illustration (Canvas: simplified stick figure or emoji)
  Pose name + description + benefits
  
  BREATHING CIRCLE:
    Animatable radius: 120f → 170f → 120f in sync with breathing phase
    Glow: BlurMaskFilter in Canvas, alpha pulses with radius
    Color: shifts blue → pink on inhale
    Text inside: "Breathe In..." / "Hold..." / "Breathe Out..."
    AnimatedContent for text crossfade
    
    LaunchedEffect(isPlaying) {
      while(isPlaying) {
        breathingPhase = INHALE
        animatableRadius.animateTo(170f, tween(4000))
        breathingPhase = HOLD
        delay(2000)
        breathingPhase = EXHALE
        animatableRadius.animateTo(120f, tween(6000))
      }
    }
  
  Bottom: pose indicator dots + Next Pose button
  Swipe left gesture: detectHorizontalDragGestures → next pose
  Exit button: top-right X

@Preview YogaFlowPreview

═══════════════════
SCREEN 10: Journal
═══════════════════
JournalViewModel:
  entries: StateFlow<List<JournalEntry>>
  draft: StateFlow<String>
  selectedMoods: StateFlow<Set<String>>
  timeCapsule: StateFlow<JournalEntry?>
  showTimeCapsuleReveal: StateFlow<Boolean>
  
  fun updateDraft(text: String)
  fun toggleMood(emoji: String)
  fun saveEntry(isTimeCapsule: Boolean = false)
  fun revealTimeCapsule()
  fun deleteEntry(id: Long)

JournalScreen:
  Dark aesthetic (Color(0xFF1A1228) background)
  
  TIME CAPSULE REVEAL (if due):
    Full-screen overlay animation:
    Envelope icon → opens → text fades in word by word
    Keep/Let go buttons with animations
    
  Write entry section:
    Large TextField (transparent background, Lora/serif style)
    Mood toolbar: horizontal scroll of emoji buttons
    Selected moods: float up as stickers with arc trajectory
    Phase auto-tag pill
    
  Save buttons: "Save Entry" + "Seal for next month 💌"
  
  Entry list: LazyColumn of journal entry cards
    Each card: date + phase pill + mood emojis + content preview
    Swipe to delete: reveal delete background on horizontal swipe
    Tap: expand to full entry view (AnimatedVisibility)

@Preview JournalPreview

═══════════════════
SCREEN 11: Mood Map
═══════════════════
MoodMapViewModel:
  moodData: StateFlow<Map<LocalDate, MoodData>>
  showCycleOverlay: StateFlow<Boolean>
  selectedDay: StateFlow<LocalDate?>
  
  fun toggleCycleOverlay()
  fun selectDay(date: LocalDate)

MoodMapScreen:
  Title + cycle overlay toggle pill
  Custom Canvas heatmap:
    7 columns × 14 rows (last 90 days)
    Each cell: 28dp square, 4dp gap, RoundedCornerShape(4.dp)
    Color per mood (blue/red/gold/green/purple/grey)
    Cycle overlay: drawCircle with phase-colored stroke when enabled
  Tap cell: tooltip with mood + journal preview
    Tooltip: Popup composable with frosted glass card
  Month labels above each month section

@Preview MoodMapPreview

═══════════════════
SCREEN 12: Insights
═══════════════════
InsightsViewModel:
  insights: StateFlow<List<PatternInsight>>
  cyclesLogged: StateFlow<Int>
  minimumCyclesReached: StateFlow<Boolean>
  
  Insights calculated by CalculatePatternsUseCase

InsightsScreen:
  If < 3 cycles: EmptyState with "Still gathering data..." 
    Progress bar: "N of 3 cycles tracked"
    Mascot: detective pose
    
  If ≥ 3 cycles: insight cards
    Each card: colored left bar + insight text + confidence bar
    Confidence bar fills with animateFloatAsState on scroll-into-view
    Pattern type icon (water/sleep/cramps/mood)

@Preview InsightsPreview (with 3 fake insights)

═══════════════════
SCREEN 13: Settings
═══════════════════
SettingsViewModel:
  settings: StateFlow<CycleSettings>
  premiumTier: StateFlow<PremiumTier>
  reminderList: StateFlow<List<Reminder>>
  
  fun updateUserName(name: String)
  fun updateCycleLengths(cycle: Int, period: Int)
  fun toggleNotifications(enabled: Boolean)
  fun toggleBiometricLock(enabled: Boolean)
  fun enableStealthMode(pin: String)
  fun toggleSound(enabled: Boolean)
  fun toggleHaptic(enabled: Boolean)
  fun deleteAllData() (with confirmation dialog)
  fun addReminder(reminder: Reminder)
  fun deleteReminder(id: Long)
  fun exportData(context: Context)
  fun signOut()

SettingsScreen:
  Section cards with headers:
  1. Profile: name edit, cycle/period length pickers
  2. Privacy: data stays on device (locked), stealth mode toggle
  3. Reminders: list with swipe-to-delete, add reminder button
  4. Sound & Haptics: master toggle + individual toggles
  5. Account: Google sign-in status, sync status, sign out
  6. Premium: current tier, "Upgrade" button → Payment screen
  7. Export Data: CSV + PDF export buttons (always free)
  8. Legal: Privacy policy, Medical disclaimer, Consent history
  9. Delete all data: destructive button with confirmation dialog
  
  Each toggle: smooth animateDpAsState spring for thumb position

@Preview SettingsPreview

═══════════════════
SCREEN 14: Report Export
═══════════════════
ReportViewModel:
  reportData: StateFlow<ReportData>
  selectedCycles: StateFlow<Int> (3 or 6)
  exportState: StateFlow<ExportState> (Idle/Generating/Done/Error)
  prepAnswers: StateFlow<PrepAnswers?>
  
  fun selectCycles(n: Int)
  fun generatePdf(context: Context)
  fun generateCsv(context: Context)
  fun savePrepAnswers(answers: PrepAnswers)

ReportExportScreen:
  Report preview card (visual representation)
  Cycle selector: 3 or 6 cycles
  Included data chips (all pre-checked)
  "Prep for appointment" → PrepInterview bottom sheet
  Export PDF button: Idle → Loading (doc animation) → Done (share sheet)
  Export CSV button: simpler, immediate

PDF Generation:
  Use PdfDocument API (Android built-in, no external library needed):
    Page 1: Cover with Period Saathi branding + user name + date range
    Page 2: Cycle summary table (start date, length, flow intensity)
    Page 3: Symptom frequency chart (Canvas-drawn bar chart in PDF)
    Page 4: Ovulation and fertile window dates
    Page 5: Doctor prep questions (if filled)
  Save to MediaStore Downloads folder
  Open via FileProvider + Intent.ACTION_VIEW

@Preview ReportPreview

═══════════════════
SCREEN 15: Payment (Razorpay)
═══════════════════
PaymentViewModel:
  products: StateFlow<List<Product>>
  premiumStatus: StateFlow<PremiumStatus>
  paymentState: StateFlow<PaymentState>
  
  fun initiatePurchase(product: Product, activity: Activity)
  fun verifyPayment(paymentId: String, orderId: String, signature: String)
  fun restorePurchases()

PaymentScreen:
  "Go Premium 🌟" heading
  Current plan card
  3 product cards:
    "Premium Themes Pack — ₹99"
    "Ad-Free Forever — ₹149"
    "Full Premium Bundle — ₹199 (Best Value)"
  Each card: features list + price + "Buy" button
  
  Razorpay integration:
    val checkout = Checkout()
    checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)
    val options = JSONObject().apply {
      put("name", "Period Saathi")
      put("description", product.name)
      put("currency", "INR")
      put("amount", product.pricePaise) // in paise
      put("prefill", JSONObject().apply {
        put("email", userEmail)
      })
      put("theme", JSONObject().apply {
        put("color", "#FFB5C8")
      })
    }
    checkout.open(activity, options)
  
  Implement PaymentResultListener on Activity
  On success: viewModel.verifyPayment(...)
  On failure: show error state with retry
  
  "Already purchased? Restore" link
  Refund policy note at bottom

@Preview PaymentPreview

═══════════════════
SCREEN 16: Wardrobe
═══════════════════
WardrobeViewModel:
  accessories: StateFlow<List<Accessory>>
  equippedItems: StateFlow<Set<String>>
  totalPoints: StateFlow<Int>
  
  fun equipAccessory(id: String)
  fun unequipAccessory(id: String)
  fun unlockAccessory(id: String) (deducts points)

WardrobeScreen:
  Live mascot preview (right side, 50% width)
  Accessories grid (left side + below)
  Each card: locked/unlocked/equipped states
  Unlock animation: 3D flip + confetti
  Apply: mascot preview updates immediately

@Preview WardrobePreview

═══════════════════
SCREEN 17: Challenges
═══════════════════
ChallengesViewModel:
  activeChallenges: StateFlow<List<Challenge>>
  completedChallenges: StateFlow<List<Challenge>>
  
  fun acceptChallenge(id: String)
  fun checkDailyProgress(challengeId: String)

ChallengesScreen:
  Active challenges: horizontal scroll of challenge cards
  Each card: animated progress bar + time remaining + accept button
  Accept: stamp animation "ACCEPTED!" overlay on card
  Completed: trophy animation + reward reveal
  Past challenges: vertical list, greyed out with completion status

@Preview ChallengesPreview

══════════════════════════════════
For EACH screen above, write BOTH files in full:
  1. ui/screens/[name]/[Name]ViewModel.kt
  2. ui/screens/[name]/[Name]Screen.kt

Every screen composable MUST end with a @Preview annotation.
```

---

# SECTION 9 — PAYMENTS (RAZORPAY COMPLETE)

```
Write complete Razorpay integration for Period Saathi.

Requirements:
- One-time purchase ONLY (no subscriptions)
- Three products: THEMES_PACK (₹99), AD_FREE (₹149), PREMIUM_BUNDLE (₹199)
- Verify payment server-side via Supabase Edge Function
- Store purchase locally (EncryptedSharedPreferences + Room)
- Restore purchases on login

FILE 1: payment/RazorpayManager.kt
Wrapper around Razorpay Checkout SDK:

class RazorpayManager @Inject constructor(
  private val securePrefs: SecurePreferences,
  private val settingsRepository: SettingsRepository
) {
  fun initiatePayment(
    activity: Activity,
    product: Product,
    userEmail: String,
    onSuccess: (paymentId: String, orderId: String, signature: String) -> Unit,
    onFailure: (code: Int, description: String) -> Unit
  ) {
    val checkout = Checkout()
    checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID)
    checkout.setImage(R.drawable.ic_launcher_foreground)
    
    val options = JSONObject().apply {
      put("name", "Period Saathi")
      put("description", product.displayName)
      put("currency", "INR")
      put("amount", product.priceInPaise)
      put("send_sms_hash", true)
      put("prefill", JSONObject().apply {
        put("email", userEmail)
      })
      put("theme", JSONObject().apply {
        put("color", "#FFB5C8")
        put("hide_topbar", false)
      })
      put("modal", JSONObject().apply {
        put("confirm_close", true)
        put("animation", true)
      })
    }
    
    // Store callbacks for Activity to call
    currentSuccessCallback = onSuccess
    currentFailureCallback = onFailure
    
    checkout.open(activity, options)
  }
}

MainActivity must implement com.razorpay.PaymentResultWithDataListener:
  override fun onPaymentSuccess(razorpayPaymentId: String?, data: PaymentData?) {
    // Extract orderId and signature from data
    // Call viewModel.verifyPayment(...)
  }
  override fun onPaymentError(code: Int, response: String?, data: PaymentData?) {
    // Show error to user
  }

FILE 2: payment/VerifyPaymentUseCase.kt
  Calls Supabase Edge Function "verify-razorpay-payment"
  On success: 
    - Update Room settings.premiumTier
    - Update EncryptedSharedPreferences with purchase record
    - If logged in: record in Supabase purchases table
  On failure:
    - Log error, show user-friendly message
    - NOTE: never lose the payment — save paymentId locally for manual recovery

FILE 3: payment/PremiumManager.kt
  Object or @Singleton:
  fun isPremium(type: PremiumType): Boolean
    - Check EncryptedSharedPreferences first (offline-capable)
    - PremiumType: THEMES, AD_FREE, FULL
  fun unlockPremium(type: PremiumType)
  fun restorePurchases(userId: String): Flow<RestoreResult>
    - Query Supabase purchases table
    - Restore all completed purchases locally

FILE 4: payment/AdManager.kt
  Manages AdMob banner ads:
  - Only show when !isPremium(AD_FREE)
  - Grace period: 7 days from first install → no ads
  - Initialize MobileAds in Application class
  - BannerAdComposable: wraps AndroidView(AdView)
    Shows only at HOME screen bottom and CALENDAR screen bottom
    NEVER on: Logging screens, Remedies, Yoga, Breathing mode
  - AdRequest with test device IDs in debug builds

Write all 4 files with complete implementation.
```

---

# SECTION 10 — OFFLINE SYNC + WORKMANAGER

```
Create complete offline sync infrastructure.

FILE 1: worker/SyncWorker.kt
@HiltWorker
class SyncWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParams: WorkerParameters,
  private val pendingSyncDao: PendingSyncDao,
  private val supabaseRepository: SupabaseRepository,
  private val authManager: AuthManager
) : CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    // 1. Check if user is logged in
    val user = authManager.currentUser() ?: return Result.success() // nothing to sync
    
    // 2. Get all pending operations
    val pending = pendingSyncDao.getAllPending()
    if (pending.isEmpty()) return Result.success()
    
    // 3. Batch into groups of 50
    pending.chunked(50).forEach { batch ->
      val result = supabaseRepository.batchSync(batch)
      when {
        result.isSuccess -> {
          pendingSyncDao.deleteByIds(batch.map { it.id })
        }
        result.isRateLimited() -> return Result.retry()
        result.isAuthError() -> {
          // Notify user via notification they need to re-login
          return Result.failure()
        }
        else -> {
          batch.forEach { op ->
            pendingSyncDao.incrementRetry(op.id, result.errorMessage)
          }
        }
      }
    }
    
    // 4. Pull sync: get remote changes since last sync
    val lastSync = settingsDao.getLastSyncTimestamp()
    val remoteChanges = supabaseRepository.getChangesSince(lastSync)
    remoteChanges.forEach { change ->
      // Merge with conflict resolution (last-write-wins)
      val localEntry = cycleEntryDao.getById(change.id)
      if (localEntry == null || change.updatedAt > localEntry.updatedAt) {
        cycleEntryDao.insert(change.toEntity())
      }
    }
    
    settingsDao.updateLastSyncTimestamp(System.currentTimeMillis())
    return Result.success()
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

FILE 2: worker/ReminderWorker.kt
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
      "WATER" -> notificationHelper.showWaterReminder(currentGlasses = inputData.getInt("CURRENT", 0))
      "MEDICINE" -> notificationHelper.showMedicineReminder(label)
      "PERIOD" -> notificationHelper.showPeriodPredictionReminder(
        daysUntil = inputData.getInt("DAYS", 0)
      )
      "CUSTOM" -> notificationHelper.showCustomReminder(label)
    }
    return Result.success()
  }
}

FILE 3: worker/WidgetRefreshWorker.kt
CoroutineWorker that:
  - Reads latest cycle data from Room
  - Updates Glance widget state using GlanceStateDefinition
  - Calls GlanceAppWidgetManager.updateAll(context, CycleDayWidget::class.java)
  - Schedules: PeriodicWorkRequest every 4 hours

FILE 4: notification/NotificationHelper.kt
@Singleton class:
  Notification channels setup:
    REMINDERS channel: importance HIGH, vibration enabled
    INSIGHTS channel: importance DEFAULT
    SYNC channel: importance MIN (silent)
  
  Functions for each notification type:
  - showWaterReminder(currentGlasses): 
    "💧 Time to hydrate! You've had N glasses today."
    Action button: "Log Glass" → fires ReminderActionReceiver
  - showMedicineReminder(label):
    "💊 Time for your medicine: [label]"
    Action: "Taken" → marks taken in DB
  - showPeriodPredictionReminder(daysUntil):
    "🌸 Your period may arrive in N days — be prepared!"
    Only shown if user has opted in
  - showRestDayNotification():
    "🛌 Today looks like a heavy day — consider resting"
  
  All notifications:
    setSmallIcon(R.drawable.ic_notification)
    setAutoCancel(true)
    setPendingIntent to open relevant screen
    setStyle(BigTextStyle) for longer messages

FILE 5: notification/BootReceiver.kt
BroadcastReceiver for ACTION_BOOT_COMPLETED:
  Re-schedules all active reminders from ReminderDao
  Uses WorkManager (not exact alarms — battery-friendly)
  Note: for exact timing (e.g. medicine), use AlarmManager with 
    SCHEDULE_EXACT_ALARM permission for Android 12+

Write all 5 files completely.
```

---

# SECTION 11 — WIDGETS (GLANCE)

```
Create home screen widgets using Jetpack Glance.

FILE 1: widget/CycleDayWidget.kt
Glance AppWidget showing:
  - Saathi mascot (use ImageProvider with vector)
  - Current cycle day: "Day 14"
  - Phase name
  - Water ring (use Canvas + ImageProvider)
  
Sizes: SMALL (2×2) and MEDIUM (4×2)
  
Widget state via GlanceStateDefinition:
  data class CycleWidgetState(cycleDay, phaseName, waterCount, totalWater)

Content composable (Glance composable):
  GlanceTheme {
    Box(modifier = GlanceModifier
      .fillMaxSize()
      .background(Color(0xFFFFF8F5))
      .appWidgetBackground()
      .cornerRadius(16.dp)
    ) {
      Column {
        Text("Day $cycleDay", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
        Text(phaseName, style = TextStyle(fontSize = 14.sp, color = Color(0xFFFFB5C8)))
        Text("💧 $waterCount/$totalWater", style = TextStyle(fontSize = 12.sp))
      }
      Image(ImageProvider(R.drawable.ic_mascot_happy), contentDescription = "Saathi")
    }
  }

Action: ActionCallback on tap → opens MainActivity with deeplink to Home

FILE 2: widget/PeriodCountdownWidget.kt
4×1 widget:
  - "🌸 Next period in N days"
  - Or "🌸 Period tracking active" (if in period)
  - Or "Track 3 cycles for prediction" (if < 3 cycles)
  - Tap → open Calendar screen

FILE 3: widget/WidgetDataRepository.kt
Reads from Room directly (NO ViewModel, no coroutines from UI thread):
  - Synchronous read using runBlocking { db.settingsDao().getSettingsSync() }
  - Or use suspend function with coroutineScope in Widget update

Widget update scheduling:
  Override onUpdate in GlanceAppWidget
  Also: WidgetRefreshWorker schedules updates every 4 hours
  Force refresh: widget tap ActionCallback triggers immediate refresh

Write all 3 files.
```

---

# SECTION 12 — SECURITY

```
Create all security files for Period Saathi.

FILE 1: security/AppBiometricManager.kt
class AppBiometricManager(private val activity: FragmentActivity) {
  fun isBiometricAvailable(): BiometricAvailability {
    val manager = BiometricManager.from(activity)
    return when (manager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
      BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NOT_ENROLLED
      else -> BiometricAvailability.NOT_AVAILABLE
    }
  }
  
  fun authenticate(
    onSuccess: () -> Unit,
    onFailed: () -> Unit,
    onError: (String) -> Unit
  ) {
    val executor = ContextCompat.getMainExecutor(activity)
    val callback = object : BiometricPrompt.AuthenticationCallback() {
      override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        onSuccess()
      }
      override fun onAuthenticationFailed() { onFailed() }
      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        onError(errString.toString())
      }
    }
    
    val prompt = BiometricPrompt(activity, executor, callback)
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle("Wake up Saathi 🌸")
      .setSubtitle("Verify to access Period Saathi")
      .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
      .build()
    
    prompt.authenticate(promptInfo)
  }
}

enum class BiometricAvailability { AVAILABLE, NOT_ENROLLED, NOT_AVAILABLE }

FILE 2: security/StealthModeManager.kt
@Singleton class:
  fun isStealthModeEnabled(): Boolean
  fun enableStealthMode(pin: String)  — store hashed PIN in EncryptedSharedPrefs
  fun disableStealthMode(pin: String): Boolean  — verify PIN first
  fun verifyPin(input: String): Boolean  — compare SHA-256 hash
  fun getDisguiseName(): String  — from EncryptedSharedPrefs
  fun setDisguiseName(name: String)
  
  Note: actual icon change requires different app icons in manifest
  Implement launcher alias switching:
    Use PackageManager.setComponentEnabledSetting() to switch between
    MainActivityAlias (real) and StealthActivityAlias (disguise)
  
  Add to AndroidManifest.xml:
    <activity-alias android:name=".StealthAlias"
      android:targetActivity=".MainActivity"
      android:label="My Notes"
      android:icon="@drawable/ic_stealth_notes"
      android:enabled="false">
      <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
      </intent-filter>
    </activity-alias>

FILE 3: security/CertificatePinner.kt
Configure OkHttpClient with certificate pinning for Supabase domain:
  val certificatePinner = CertificatePinner.Builder()
    .add("*.supabase.co", "sha256/AAAAAAAAAAAAAAAA") // real hash from supabase cert
    .build()
  
  Also configure network_security_config.xml:
    <network-security-config>
      <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">supabase.co</domain>
        <pin-set>
          <pin digest="SHA-256">HASH_HERE</pin>
        </pin-set>
      </domain-config>
    </network-security-config>

FILE 4: ui/lock/LockScreen.kt (complete)
  Full dark screen with star field (Canvas, 50 stars drifting upward)
  Sleeping Saathi mascot with Zzz animations
  Biometric prompt button
  PIN fallback button
  Success: mascot wakes up → radial reveal animation → navigate to Home
  Failure: screen shake + mascot startled

FILE 5: ui/lock/PinEntryScreen.kt (complete)
  4-dot indicator
  3×4 numpad
  Each tap: scale bounce + haptic
  Wrong PIN: shake + flash red + clear
  Correct: flash green → navigate

Write all 5 files.
```

---

# SECTION 13 — TESTING

```
Create the complete test suite for Period Saathi.

FILE 1: test/usecase/GetPredictionUseCaseTest.kt
@RunWith(MockitoJUnitRunner::class)
class GetPredictionUseCaseTest {
  @Mock private lateinit var repository: CycleRepository
  private lateinit var useCase: GetPredictionUseCase
  
  @Before fun setup() { useCase = GetPredictionUseCase(repository) }
  
  @Test fun `returns null when less than 3 cycles`() = runTest {
    whenever(repository.getCycleCount()).thenReturn(flowOf(2))
    val result = useCase().first()
    assertNull(result)
  }
  
  @Test fun `returns prediction with LOW confidence for exactly 3 cycles`() = runTest { ... }
  @Test fun `returns prediction with HIGH confidence for 6+ cycles`() = runTest { ... }
  @Test fun `handles irregular cycles without crashing`() = runTest { ... }
  @Test fun `prediction accuracy increases with more cycles`() = runTest { ... }
}

FILE 2: test/usecase/LogCycleEntryUseCaseTest.kt
  Test: future dates are rejected
  Test: flow intensity 1-5 accepted, 0 and 6 rejected
  Test: rest day auto-suggested (not auto-set) for heavy flow
  Test: points awarded correctly on log
  Test: streak incremented correctly

FILE 3: test/viewmodel/HomeViewModelTest.kt
Using Turbine for Flow testing:
  @Test fun `mascot is SAD when water < 3 glasses`() = runTest {
    viewModel.homeState.test {
      val state = awaitItem()
      assertEquals(MascotEmotion.SAD, state.mascotEmotion)
    }
  }
  @Test fun `confetti shown when water goal reached`()
  @Test fun `REST DAY shown correctly`()
  @Test fun `no prediction shown before 3 cycles`()

FILE 4: test/repository/CycleRepositoryTest.kt
  Integration test with in-memory Room database:
  val db = Room.inMemoryDatabaseBuilder(context, PeriodSaathiDatabase::class.java)
    .allowMainThreadQueries().build()
  
  @Test fun `insert and retrieve cycle entry by date`()
  @Test fun `prediction algorithm calculates correct average`()
  @Test fun `cycle phase changes correctly by day`()

FILE 5: androidTest/HomeScreenTest.kt
Compose UI test:
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  
  @Test fun `home screen loads with mascot visible`() {
    composeTestRule.onNodeWithTag("saathi_mascot").assertIsDisplayed()
  }
  @Test fun `water add button logs water`() {
    composeTestRule.onNodeWithText("+1 Glass").performClick()
    composeTestRule.onNodeWithTag("water_count").assertTextContains("1")
  }
  @Test fun `navigating to calendar works`() {
    composeTestRule.onNodeWithContentDescription("Calendar tab").performClick()
    composeTestRule.onNodeWithTag("calendar_screen").assertIsDisplayed()
  }
  @Test fun `all bottom nav tabs navigate correctly`()

FILE 6: screenshotTest/HomeScreenshotTest.kt
@RunWith(AndroidJUnit4::class)
class HomeScreenshotTest {
  @get:Rule val screenshotRule = AndroidComposeTestRule(...)
  
  @ScreenshotTest
  fun homeScreen_defaultState() {
    screenshotRule.setContent {
      PeriodSaathiTheme { HomeScreen(fakeNavController) }
    }
    screenshotRule.onRoot().captureToImage()
  }
  // One @ScreenshotTest per main screen
}

FILE 7: test/util/TestData.kt
Fake data factory for all tests:
  fun fakeCycleEntry(date: LocalDate = LocalDate.now(), intensity: Int = 3): CycleEntryEntity
  fun fakeSettings(userName: String = "Test User", cyclesLogged: Int = 5): SettingsEntity
  fun fakeHomeUiState(mascotEmotion: MascotEmotion = MascotEmotion.HAPPY): HomeUiState
  // etc for all models

Write all 7 files completely.
```

---

# SECTION 14 — CI/CD + QUALITY

```
Create CI/CD pipeline and code quality configuration.

FILE 1: .github/workflows/android_ci.yml
name: Period Saathi CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  lint_and_detekt:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin' }
      - uses: gradle/actions/setup-gradle@v3
      - name: Run Detekt
        run: ./gradlew detekt
      - name: Run Android Lint
        run: ./gradlew lint
      - name: Upload lint results
        uses: actions/upload-artifact@v4
        with:
          name: lint-results
          path: app/build/reports/lint-results-debug.html

  unit_tests:
    runs-on: ubuntu-latest
    needs: lint_and_detekt
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin' }
      - name: Run unit tests
        run: ./gradlew testDebugUnitTest
      - name: Upload test results
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: app/build/reports/tests/testDebugUnitTest/
      - name: Check coverage threshold (70%)
        run: ./gradlew jacocoTestReport
        # Fail if coverage < 70%

  build_debug:
    runs-on: ubuntu-latest
    needs: unit_tests
    steps:
      - uses: actions/checkout@v4
      - name: Build debug APK
        run: ./gradlew assembleDebug
      - name: Upload debug APK
        uses: actions/upload-artifact@v4
        with:
          name: debug-apk
          path: app/build/outputs/apk/debug/

  build_release:
    runs-on: ubuntu-latest
    needs: unit_tests
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v4
      - name: Setup signing
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore.jks
          echo "keystore.path=../keystore.jks" >> local.properties
          echo "keystore.password=${{ secrets.KEYSTORE_PASSWORD }}" >> local.properties
          echo "key.alias=${{ secrets.KEY_ALIAS }}" >> local.properties
          echo "key.password=${{ secrets.KEY_PASSWORD }}" >> local.properties
          echo "supabase.url=${{ secrets.SUPABASE_URL }}" >> local.properties
          echo "supabase.anon.key=${{ secrets.SUPABASE_ANON_KEY }}" >> local.properties
          echo "razorpay.key.id=${{ secrets.RAZORPAY_KEY_ID }}" >> local.properties
      - name: Build release bundle
        run: ./gradlew bundleRelease
      - name: Upload AAB
        uses: actions/upload-artifact@v4
        with:
          name: release-aab
          path: app/build/outputs/bundle/release/
      - name: Auto-increment version code
        run: |
          # Read current versionCode, increment, write back
          # This is a simple script using sed

FILE 2: detekt.yml
Detekt configuration:
  complexity:
    LongMethod: { threshold: 60 }
    LargeClass: { threshold: 600 }
    CyclomaticComplexity: { threshold: 15 }
  style:
    MaxLineLength: { maxLineLength: 120 }
    MagicNumber: { active: false } // Too strict for Compose
    WildcardImport: { active: true }
  naming:
    FunctionNaming: { active: true }
    ClassNaming: { active: true }
  performance:
    SpreadOperator: { active: true }
  coroutines:
    SuspendFunWithFlowReturnType: { active: true }
    GlobalCoroutineUsage: { active: true }

FILE 3: fastlane/Fastfile
platform :android do
  lane :test do
    gradle(task: "test", build_type: "Debug")
  end
  
  lane :beta do
    gradle(
      task: "bundle",
      build_type: "Release",
      print_command: false,
      properties: {
        "android.injected.signing.store.file" => ENV["KEYSTORE_PATH"],
        "android.injected.signing.store.password" => ENV["KEYSTORE_PASSWORD"],
        "android.injected.signing.key.alias" => ENV["KEY_ALIAS"],
        "android.injected.signing.key.password" => ENV["KEY_PASSWORD"],
      }
    )
    upload_to_play_store(
      track: "internal",
      aab: "app/build/outputs/bundle/release/app-release.aab",
      release_status: "draft"
    )
  end
  
  lane :production do
    upload_to_play_store(
      track: "production",
      rollout: "0.1",  # 10% staged rollout
      release_status: "completed"
    )
  end
end

FILE 4: app/src/main/res/xml/backup_rules.xml
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
  <!-- Exclude the encrypted database from auto-backup (we handle manually) -->
  <exclude domain="database" path="period_saathi.db"/>
  <!-- Include preferences -->
  <include domain="sharedpref" path="period_saathi_prefs.xml"/>
</full-backup-content>

Also: for Android 12+ data extraction rules:
FILE 5: app/src/main/res/xml/data_extraction_rules.xml

Write all 5 files completely.
```

---

# SECTION 15 — MONITORING + ANALYTICS

```
Create monitoring and analytics infrastructure.

FILE 1: monitoring/CrashlyticsManager.kt
@Singleton class:
  fun initialize(optedIn: Boolean) 
    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(optedIn)
  
  fun logEvent(event: String, params: Map<String, String> = emptyMap())
    Only logs if analyticsOptedIn == true
  
  fun setUserId(userId: String) — hashed, never raw email
  
  fun recordException(e: Throwable, context: String)
    Always records exceptions regardless of analytics opt-in
    But strips any health data from exception messages
  
  fun logScreenView(screenName: String)
    Only if analytics opted in
  
  // Predefined events (health-data-free):
  fun logWaterGoalReached()
  fun logCycleLogged() // no intensity data
  fun logPurchaseCompleted(product: String)
  fun logWidgetInteraction()
  fun logPartnerShareUsed()

FILE 2: monitoring/PerformanceMonitor.kt
  Macrobenchmark traces:
  - App startup trace
  - Home screen render trace
  - Calendar scroll performance trace
  
  ANR detection: custom watchdog thread
  class ANRWatchdog(private val timeout: Long = 5000) : Thread() {
    override fun run() {
      // Post to main thread, check if completed within timeout
      // If not: log to Crashlytics + notify dev
    }
  }

FILE 3: monitoring/BugReportManager.kt
  In-app bug report feature:
  - User triggers: shake phone 5 times OR Settings → Report a bug
  - Collects: app version, Android version, device model, 
              app logs (last 500 lines of Logcat, STRIPPED of dates/health data),
              screenshot (opt-in, user confirms)
  - NEVER collects: health data, journal entries, user names
  - Opens email intent: "bugreport@periodsaathi.app"
  - Response SLA: 48 hours (shown to user in UI)

Write all 3 files.
```

---

# SECTION 16 — ACCESSIBILITY

```
Create accessibility utilities and ensure all screens comply.

FILE 1: ui/util/AccessibilityUtils.kt
Extensions and helpers:

// Minimum touch target enforcement
fun Modifier.minimumTouchTarget(size: Dp = 48.dp): Modifier =
  this.defaultMinSize(minWidth = size, minHeight = size)

// Announce state changes to screen readers
fun announceSemanticsChange(scope: SemanticsModifier, message: String) {
  // Use AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS
}

// Scale-safe typography
fun TextUnit.scaleSafe(maxScale: Float = 1.3f): TextUnit {
  // Clamp text scaling for layouts that break at extreme scales
}

FILE 2: ui/util/ContentDescriptions.kt
ALL content descriptions as constants (support localization):
  const val MASCOT_HAPPY = "Saathi mascot, happy"
  const val CYCLE_RING = "Cycle progress ring, day %d of %d"
  const val WATER_RING = "Water intake, %d of %d glasses"
  const val ADD_WATER_BUTTON = "Add one glass of water"
  const val CALENDAR_DAY = "Calendar day %s, %s"
  const val PERIOD_LOGGED = "Period logged, intensity: %s"
  // etc for every interactive element

Accessibility requirements checklist (implement all):
  ✅ All icons: contentDescription non-null or decorative flag
  ✅ Custom components: semantics { } block with role and state
  ✅ CycleRing: semantics { contentDescription = "Day N of M" }
  ✅ WaterRing: semantics { contentDescription = "N of M glasses" }
  ✅ All buttons: semantics { role = Role.Button; onClick(label) {} }
  ✅ Toggle states: semantics { stateDescription = if(on) "On" else "Off" }
  ✅ Loading states: semantics { liveRegion = LiveRegionMode.Polite }
  ✅ Error messages: semantics { liveRegion = LiveRegionMode.Assertive }

FILE 3: Ensure these accessibility behaviors in ALL screens:
  1. Focus order follows visual order (default in Compose — verify)
  2. No color-only information: use icons + color (not color alone)
  3. Focus visible indicator (FocusRequester with drawBehind ring)
  4. Heading semantics on section titles: semantics { heading() }
  5. List semantics on LazyColumns: semantics { collectionInfo(...) }
  6. Font scale test: all screens manually tested at:
     Settings → Display → Font Size → Largest
     Verify: no text clipped, no UI overlap, scrollable where needed

GENDER-NEUTRAL THEME (P3 but add color data now):
  Define alternative color palette:
  val TealPrimary = Color(0xFF80CBC4)
  val SlateSecondary = Color(0xFF90A4AE)
  val SkyTertiary = Color(0xFF81D4FA)
  val NeutralBackground = Color(0xFFF5F5F5)
  
  ThemeSelection enum: BLUSH_PINK, TEAL_NEUTRAL, LAVENDER_PURPLE
  Store in Settings, apply via PeriodSaathiTheme(themeSelection=...)

Write all 3 files.
```

---

# SECTION 17 — PLAY STORE DEPLOYMENT

```
Create all Play Store deployment assets and documentation.

FILE 1: docs/PLAY_STORE_LISTING.md

SHORT DESCRIPTION (80 chars):
"Your cute period bestie 🌸 Track, rest & earn rewards. Offline & private."

FULL DESCRIPTION (4000 chars — write it):
Write complete app description in English:
- Opening hook paragraph
- Key features bullet list (15 features)
- Privacy statement paragraph
- What makes Period Saathi different (emotional companion, not clinical)
- Permissions explanation (why each permission is needed)
- Contact/support info
- Medical disclaimer (brief version)

HINDI DESCRIPTION (also 4000 chars):
Translate above to Hindi

FILE 2: docs/PRIVACY_POLICY.html
Complete DPDP Act 2023 compliant privacy policy:

Sections required:
1. Who we are (individual developer / company)
2. What data we collect:
   - Without account: NOTHING leaves device
   - With account: email, display name (for account recovery)
   - Health data: stored ONLY on device, never transmitted to us
3. Why we collect it (purpose)
4. How we protect it (SQLCipher, EncryptedSharedPrefs, TLS)
5. Your rights under DPDP Act:
   - Right to access your data
   - Right to correction
   - Right to erasure (in-app delete button)
   - Right to nominate (grievance officer contact)
6. Data retention: delete on account deletion, export before
7. Third parties: AdMob (non-premium), Razorpay (payment), 
                  Firebase Crashlytics (opt-in analytics)
8. Contact: support@periodsaathi.app
9. Grievance Officer: [Name], [Email], respond within 72 hours
10. Effective date and version

FILE 3: docs/PLAY_STORE_DATA_SAFETY.md
Answers to Play Store Data Safety questionnaire:

Data collected and shared:
  App activity: Yes (only if analytics opted in) — not shared
  App info and performance: Yes (crash reports) — shared with Firebase
  
Data collected and used:
  Name: Optional (account only) — Account management
  Email address: Optional (account only) — Account management, password reset
  
No data collected without account:
  Health and fitness data: NOT collected by us (stays on device)
  
Security:
  Data encrypted in transit: Yes (TLS 1.3)
  User can request deletion: Yes (in-app feature)
  
FILE 4: docs/MEDICAL_DISCLAIMER.md
Full medical disclaimer text:
  "Period Saathi is a personal menstrual tracking tool designed to help 
   you log and understand your cycle. It is NOT a medical device, does NOT 
   provide medical diagnoses, and is NOT a substitute for professional 
   medical advice, diagnosis, or treatment.
   
   The information, insights, and predictions provided are based on 
   patterns in your logged data and general wellness information. They 
   are for informational purposes only.
   
   Always consult a qualified healthcare provider regarding any medical 
   concerns, including irregular periods, severe pain, unusual symptoms, 
   or questions about fertility, contraception, or reproductive health.
   
   In case of a medical emergency, contact your local emergency services."

FILE 5: docs/RELEASE_CHECKLIST.md
Complete pre-release checklist:

BEFORE SUBMITTING:
  □ versionCode incremented
  □ versionName updated (semantic versioning: 1.0.0)
  □ All debug logging removed (BuildConfig.DEBUG guards)
  □ Test API keys replaced with production keys
  □ google-services.json is production (not test)
  □ Razorpay key is rzp_live_* (not rzp_test_*)
  □ AdMob test device IDs removed
  □ Medical disclaimer shown on first launch (verified)
  □ DPDP consent screen shown on first launch (verified)
  □ Privacy policy URL in Play Store listing updated
  □ Proguard rules tested (install release APK, test all features)
  □ Tested on Android 7.0 (API 24) — min SDK
  □ Tested on Android 15 (API 35) — latest
  □ Tested on low-end device (Redmi 9A or similar)
  □ No crash in 30 minutes of normal usage
  □ Offline mode works (airplane mode test)
  □ Screenshots taken for Play Store listing
  □ Feature graphic created (1024×500px)
  □ AAB generated: ./gradlew bundleRelease

STAGED ROLLOUT PLAN:
  Week 1: Internal testing (team only)
  Week 2: Closed alpha (50 testers via Play Console)
  Week 3: Open beta (Google Play Early Access)
  Week 4: Production — 10% rollout
  Week 5: 25% rollout (if crash-free rate > 99.5%)
  Week 6: 50% rollout
  Week 7: 100% rollout

TARGET METRICS BEFORE FULL ROLLOUT:
  - Crash-free users: > 99.5%
  - ANR rate: < 0.1%
  - Rating: > 4.0 (if enough reviews)
  - Day 1 retention: > 40%

FILE 6: docs/KEYSTORE_SETUP.md
Step-by-step keystore generation:
  keytool -genkey -v \
    -keystore period-saathi-release.jks \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -alias period-saathi-key \
    -dname "CN=Period Saathi, OU=Dev, O=YourName, L=City, S=State, C=IN"
  
  CRITICAL: 
  - Store jks file securely (password manager)
  - Back up to Google Drive or similar
  - If you lose this keystore: you CANNOT update your app on Play Store
  - The keystore password is permanent (Google locks it to your app)
  
  Add to local.properties (NEVER commit this file to git)

Write all 6 files completely.
```

---

# SECTION 18 — FINAL INTEGRATION CHECKLIST

```
Do the final integration pass. Review and fix EVERYTHING.

Run through this checklist and fix any issues found:

══════════════════════════════════════
COMPILATION CHECKS
══════════════════════════════════════
□ ./gradlew assembleDebug — must succeed with zero errors
□ ./gradlew testDebugUnitTest — must have zero failures
□ ./gradlew lint — zero errors (warnings OK)
□ ./gradlew detekt — zero rule violations
□ All @Preview annotations compile and render in Android Studio

══════════════════════════════════════
NAVIGATION CHECKS
══════════════════════════════════════
□ Every Screen route is registered in NavGraph.kt
□ Every composable<T> has a corresponding @Serializable class
□ Back navigation works on every screen (system back)
□ Deep links work (test by running adb shell am start with deeplink)
□ Bottom nav: all 4 tabs navigate correctly
□ launchSingleTop=true on all bottom nav navigations

══════════════════════════════════════
BUTTON / INTERACTION CHECKS
══════════════════════════════════════
□ Every Button, IconButton, clickable Modifier has an onClick lambda
□ Zero empty onClick = {} without intentional reasoning
□ Every tap produces: visual feedback (scale) + haptic
□ Loading buttons show CircularProgressIndicator during async ops
□ Disabled buttons are visually distinct AND not clickable

══════════════════════════════════════
STATE MANAGEMENT CHECKS
══════════════════════════════════════
□ Every ViewModel: @HiltViewModel + @Inject constructor
□ All StateFlows initialized with default values (not null)
□ All collectAsStateWithLifecycle (never collectAsState)
□ All Room queries on Dispatchers.IO
□ All UI updates on Dispatchers.Main via collect in ViewModel
□ No GlobalScope usage anywhere
□ No runBlocking on Main thread

══════════════════════════════════════
DATABASE CHECKS
══════════════════════════════════════
□ SQLCipher properly initialized before Room opens database
□ All Entities have @Entity annotation
□ All DAOs have @Dao annotation
□ TypeConverters registered in @Database
□ Room schema exported (for future migrations)
□ PendingSyncEntity used for ALL write operations when logged in

══════════════════════════════════════
ANIMATION CHECKS
══════════════════════════════════════
□ No LinearEasing on user-facing animations (spring() everywhere)
□ animateFloatAsState with spring for scale animations
□ animateColorAsState with spring for color transitions
□ Entry animations: staggered 80ms, alpha+translateY
□ ConfettiOverlay triggered on: water goal, habit completion, reward unlock
□ Page transitions: slideInHorizontally with spring spec

══════════════════════════════════════
SECURITY CHECKS  
══════════════════════════════════════
□ FLAG_SECURE set on all windows
□ SQLCipher key generated on first launch, stored in EncryptedSharedPrefs
□ No health data in logs, crash reports, or analytics
□ No health data in Partner Mode share text
□ Biometric: only enabled if available, graceful degradation
□ Certificate pinning active for Supabase (disable for debug/emulator)
□ BuildConfig.DEBUG gates all debug logging

══════════════════════════════════════
OFFLINE FIRST CHECKS
══════════════════════════════════════
□ Test in airplane mode: ALL core features work
□ Period logging works offline
□ Water tracking works offline
□ Habits work offline
□ Calendar works offline
□ Changes queued in PendingSyncDao when offline
□ SyncWorker runs when network restored

══════════════════════════════════════
BUSINESS RULE CHECKS
══════════════════════════════════════
□ NO prediction shown until cyclesLogged >= 3
□ Prediction shows confidence badge (LOW/MEDIUM/HIGH)
□ Period start: only manual (tap day → confirm "Start period")
□ Period end: only manual (tap "End period today")
□ No "late" or "overdue" language anywhere in strings.xml
□ No location permission requested anywhere
□ Notifications: all require explicit opt-in
□ Premium: one-time purchase only, no subscription anywhere
□ Ads: banner only, never on logging screens, not in first 7 days

══════════════════════════════════════
ACCESSIBILITY CHECKS
══════════════════════════════════════
□ TalkBack: navigate through Home screen with eyes closed
□ All interactive elements have contentDescription
□ Minimum 48dp touch targets on all interactive elements
□ Test with font size at maximum: no text clipped
□ Color contrast ratio ≥ 4.5:1 for all body text

══════════════════════════════════════
MISSING FILES (create if absent)
══════════════════════════════════════
□ PeriodSaathiApp.kt (@HiltAndroidApp)
□ res/xml/network_security_config.xml
□ res/xml/backup_rules.xml
□ res/xml/data_extraction_rules.xml
□ res/xml/file_paths.xml (FileProvider)
□ google-services.json (from Firebase console)
□ res/values/strings.xml (all strings, no hardcoding)
□ res/values-hi/strings.xml (Hindi translations)
□ res/drawable/ic_launcher_foreground.xml (mascot icon)
□ res/drawable/ic_notification.xml (notification icon, white, 24dp)

For any missing file found, write the complete file immediately.

After this checklist, output:
"BUILD READY ✅ — [List any remaining warnings]"
OR
"BLOCKERS FOUND ❌ — [List what needs fixing]"
```

---

# QUICK REFERENCE CARD

```
══════════════════════════════════════════════════════
  PERIOD SAATHI — OPENCODE SESSION QUICK REFERENCE
══════════════════════════════════════════════════════

PASTE ORDER (never skip, never reorder):
  S0  → System Context (EVERY session)
  S1  → TRD document
  S2  → Architecture plan
  S3  → Backend (Supabase) spec
  S4  → Gradle files
  S5  → Foundation (DB + Theme)
  S6  → Navigation
  S7  → Shared components
  S8  → All 17 screens
  S9  → Razorpay payments
  S10 → Offline sync + WorkManager
  S11 → Widgets
  S12 → Security
  S13 → Testing
  S14 → CI/CD
  S15 → Monitoring
  S16 → Accessibility
  S17 → Play Store deployment
  S18 → Final integration check

AFTER EACH SECTION:
  → ./gradlew assembleDebug
  → Fix ALL red errors before next section
  → Warnings: fix if easy, document if complex

IF RESPONSE IS CUT OFF:
  "Continue from the last complete code line in [filename]"

IF FILE IS TOO LONG:
  "Write [FileName].kt Part 1: lines 1-200"
  "Write [FileName].kt Part 2: lines 201-400"

FOR SPECIFIC BUGS:
  "In [FileName].kt line N, [describe bug]. Fix it."

MODEL ASSIGNMENT:
  DeepSeek V4: Architecture, UseCases, Repositories, complex logic
  Minimax 2.5: UI composables, animations, Canvas drawing

NEVER-DO REMINDERS FOR AI:
  ❌ Empty onClick = {}
  ❌ // TODO comments in final code
  ❌ Missing @Preview
  ❌ collectAsState (use collectAsStateWithLifecycle)
  ❌ Any prediction before 3 cycles
  ❌ Auto-ending period
  ❌ "late period" or "overdue" language
  ❌ Requesting location
  ❌ Subscription payments (only one-time)
  ❌ Health data in Partner Mode
══════════════════════════════════════════════════════
```
