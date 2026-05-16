# S08 — ALL 17 SCREENS (COMPLETE, WORKING)

> **Prerequisites:** S00 active · S07 complete · `assembleDebug` passing
> **Output:** 34 Kotlin files — 1 ViewModel + 1 Screen per screen = 17 pairs
> **Build check:** `./gradlew assembleDebug` — must pass. All 17 screens must be navigable.

> ⚠️ **This is the largest section. If the AI cuts off, say:**
> `"Continue from the last complete code line in [FileName].kt"`

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S08_report.md`

Include:
- 📄 All 34 files created (checklist — 17 ViewModels + 17 Screens)
- ✅ Build result (SUCCESS / FAILED)
- 🖥️ Screens navigable from emulator (list which ones pass/fail)
- 🧪 @Preview renders for all 17 screens (yes/no)
- ❌ Errors and fixes applied
- ⚠️ Any incomplete screens (note for follow-up)

---

## PROMPT

Create ALL 17 screen files. For EACH screen write BOTH:
1. `ui/screens/[name]/[Name]ViewModel.kt`
2. `ui/screens/[name]/[Name]Screen.kt`

Every screen MUST have:
- ✅ Real ViewModel with `@HiltViewModel + @Inject constructor`
- ✅ All buttons with real `onClick` handlers (no empty lambdas)
- ✅ `@Preview` annotation with fake data
- ✅ Loading/Success/Error state handling
- ✅ Haptic feedback on interactions
- ✅ Spring animations on all transitions
- ✅ Back navigation handling

---

### SCREEN 1: Splash (`ui/screens/splash/`)

**SplashViewModel:**
- `fun determineStartDestination()`: check SettingsDao for firstLaunch, consentAccepted, biometricEnabled
- Returns: Onboarding / Login / Home / LockScreen as destination
- `StateFlow<SplashState>`: Loading → NavigateTo(destination)

**SplashScreen:**
- Fullscreen animated gradient mesh (4 color orbs moving on Canvas)
- Saathi mascot drops in from top: `Animatable(-300f).animateTo(0f, spring(dampingRatio=0.4f))`
- App name types letter-by-letter: `LaunchedEffect + delay(80ms) per char`
- Auto-navigate after animation + 500ms delay
- NO buttons (auto-transition)
- `@Preview` showing mascot centered

---

### SCREEN 2: Onboarding (`ui/screens/onboarding/`)

**OnboardingViewModel:**
- `currentPage: StateFlow<Int>`
- `fun nextPage()`, `fun skip()`, `fun completeOnboarding()`

**OnboardingScreen:**
- `HorizontalPager` with 3 pages
- Custom transition: off-page scales to 0.92f (use `pagerState.currentPageOffsetFraction`)
- Page 1: Mascot HAPPY + "Meet your Saathi 🌸" welcome text
- Page 2: Calendar illustration (drawn with Canvas) + tracking features text
- Page 3: Stars/rewards illustration + CTA "Get Started" button
- Progress dots: pill shape active (20dp wide), circle inactive (8dp wide), `animateDpAsState`
- Skip button: `AnimatedVisibility` (visible pages 1–2, hidden page 3)
- "Get Started" on page 3: `PrimaryButton` → confetti + `completeOnboarding()` → navigate Login

---

### SCREEN 3: Login (`ui/screens/auth/`)

**LoginViewModel:**
- `loginState: StateFlow<LoginState>` — Idle/Loading/Success/Error
- `fun signInWithGoogle(credential: GoogleIdTokenCredential)`
- `fun signInWithEmail(email: String, password: String)`
- `fun continueAsGuest()`

**LoginScreen:**
- Animated gradient mesh background (4 floating color orbs)
- 25 floating particles looping bottom to top (Canvas)
- Mascot springs down from top
- App name types in letter by letter
- `GlassCard` login container containing:
  - Custom Google Sign-In button (NOT Material button) with shimmer idle / press scale / loading circle / success green
  - Google Sign-In via Credential Manager API: `GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false).setServerClientId(BuildConfig.GOOGLE_CLIENT_ID).build()`
  - Collapsible email/password section (secondary)
  - "Continue without account" ghost button → navigate Home
  - Privacy note at bottom
- Handle all states: Idle / Loading / Success / Error

---

### SCREEN 4: Home (`ui/screens/home/`)

**HomeViewModel:**
```kotlin
data class HomeUiState(
  val userName: String = "Friend",
  val cycleDay: Int = 0,
  val totalCycleDays: Int = 28,
  val phase: CyclePhase = CyclePhase.FOLLICULAR,
  val todayEntry: CycleEntry? = null,
  val prediction: PeriodPrediction? = null,
  val waterGlasses: Int = 0,
  val streakCount: Int = 0,
  val points: Int = 0,
  val mascotEmotion: MascotEmotion = MascotEmotion.HAPPY,
  val showRestDay: Boolean = false,
  val showConfetti: Boolean = false,
  val isFirstLaunch: Boolean = false
)
```
- `fun logWater(amount: Int)`: awards +1 point, checks 8-glass goal
- `fun dismissRestDay()`, `fun dismissMedicalDisclaimer()`
- `fun onMascotTapped()`: cycles through tip messages

**HomeScreen:**
`LazyColumn` with staggered entry animations (each section: `delay(index * 80ms)`, alpha+translateY from Animatable):
1. Medical disclaimer dismissible banner (if first launch)
2. Greeting header (morning/afternoon/evening time-aware)
3. GlassCard: Mascot (centered) + tap for speech bubble tips
4. Cycle ring (CycleRing component) + phase text + prediction (if ≥3 cycles)
5. REST DAY banner (animated slide from top when active)
6. Quick action chips row: Log Period / Water / Mood / Symptoms
7. Stats 2×2 grid: Water, Mood, Symptoms logged, Streak
8. AdMob banner (if non-premium, at bottom)

FAB: "+" → navigate DayLog(today)
Pull-to-refresh: `PullToRefreshBox` → refreshes all StateFlows

---

### SCREEN 5: Calendar (`ui/screens/calendar/`)

**CalendarViewModel:**
- `currentYearMonth: StateFlow<YearMonth>`
- `calendarDays: StateFlow<List<CalendarDayData>>`
- `selectedDate: StateFlow<LocalDate?>`
- `showLogSheet: StateFlow<Boolean>`
- `fun previousMonth()`, `fun nextMonth()`, `fun selectDate()`, `fun logEntry()`, `fun toggleFertilityMode()`

**CalendarScreen:**
- Month header + swipe gesture navigation
- `AnimatedContent(yearMonth)`: slide direction based on prev/next
- Day-of-week header row
- `LazyVerticalGrid(7 columns)` of `DayCell` composables

**DayCell** (separate composable with all states):
- normal / period-1-5 (color-coded intensity) / predicted / fertile / pms / today / selected
- Tap: scale bounce + selected update; Long-press: navigate DayLog

**Day Log BottomSheet** (ModalBottomSheet):
- Flow intensity: 3-button selector (Light/Medium/Heavy)
- Symptom chips in `FlowRow` (cramps, bloating, headache, fatigue, etc.)
- Mood: 5 emoji buttons
- Water stepper (+/-)
- Notes TextField
- Save → `logEntry()` → close sheet

Fertility mode 3-state segmented control at bottom (Neutral/Planning/Avoiding) with animated pill.

---

### SCREEN 6: Wellness (`ui/screens/wellness/`)

**WellnessViewModel:**
- `wellnessState: StateFlow<WellnessUiState>` (water, habits list, sleep, exercise, streak, points)
- `showConfetti: StateFlow<Boolean>`
- `fun addWater(glasses: Int)`, `fun toggleHabit(id: String)`, `fun addCustomHabit()`, `fun deleteHabit()`

**WellnessScreen:**
1. Water section: `WaterRingWithWave(200dp)` + "+1 Glass" + "+500ml" buttons. Goal reached → `ConfettiOverlay` + haptic SUCCESS
2. Habits checklist: animated checkbox rows with spring tick animation. All complete → mascot EXCITED state
3. Sleep log: `Slider(0f..12f, steps=23)` with current value display
4. Exercise: quick chips (Walk/Yoga/Gym/Other) + minutes number input
5. Rewards progress: points bar + next reward preview card. Point earned → "+N pts" float-up animation
6. Daily streak: 🔥 emoji + count, scale bounce on increment

---

### SCREEN 7: Partner Mode (`ui/screens/partner/`)

**PartnerViewModel:**
- `selectedRequests: StateFlow<Set<String>>`
- `customMessage: StateFlow<String>`
- `sendState: StateFlow<SendState>` — Idle/Sending/Sent
- `partnerName: StateFlow<String>`
- `fun toggleRequest(id: String)`, `fun sendViaShareIntent(context)`

**PartnerModeScreen:**
- Privacy header (expandable: "Zero health data shared")
- 2-column grid of 10 care request cards (preset: hug emoji, food delivery, heating pad, etc.)
- Selected cards: BlushPink gradient fill + pulsing heart animation
- Multi-selection: each bounces independently on selection
- Custom message TextField
- Send button: Idle → "Sending…" → "Sent 💌"
- `sendViaShareIntent`: builds care text (NO health data) → `Intent.ACTION_SEND` chooser

---

### SCREEN 8: Remedies (`ui/screens/remedies/`)

**RemediesViewModel:**
- `flippedCards: StateFlow<Set<String>>`
- `hotBagPosition: StateFlow<Float>` (0f–1f)
- `fun flipCard(id: String)`, `fun updateHotBag(position: Float)`

**RemediesScreen:**
**Natural Remedies section:**
- `LazyRow` of 8 `FlipCard` composables
- 3D flip: `graphicsLayer { rotationY = animated 0→180 }`
- Front: emoji + name + "Tap to reveal"
- Back: ingredients + steps + "Helpful for" chips
- Include: Ginger tea, Turmeric milk, Hot compress, Magnesium foods, Dark chocolate, Chamomile, Fennel seeds, Heating pad

**Hot Bag Safety section:**
- Custom slider with track gradient (green→yellow→red)
- Thumb color: `animateColorAsState(spring)` based on position
- Danger zone (>80%): thumb shakes + warning card appears
- On release in danger: `snap-back to 0.6f with spring()`

**Yoga Flow section:**
- Preview card showing first pose
- "Start 4-min Flow" → navigate YogaFlow

---

### SCREEN 9: Yoga Flow (`ui/screens/yoga/`)

**YogaFlowViewModel:**
- `currentPoseIndex: StateFlow<Int>`, `isPlaying: StateFlow<Boolean>`, `timeRemaining: StateFlow<Int>`, `breathingPhase: StateFlow<BreathingPhase>` (INHALE/HOLD/EXHALE)
- `fun nextPose()`, `fun previousPose()`, `fun togglePause()`, `fun exit()`
- Timer: `LaunchedEffect` countdown, auto-advance pose

**YogaFlowScreen:**
- FULLSCREEN (hide system UI)
- Dark background (Color(0xFF0D0A14))
- 5 poses: Child's Pose / Cat-Cow / Supine Twist / Bridge / Savasana
- Pose name + description + benefits text

**BREATHING CIRCLE:**
```kotlin
val breathingRadius = remember { Animatable(120f) }
LaunchedEffect(isPlaying) {
  while (isPlaying) {
    breathingPhase = INHALE
    breathingRadius.animateTo(170f, tween(4000))
    breathingPhase = HOLD; delay(2000)
    breathingPhase = EXHALE
    breathingRadius.animateTo(120f, tween(6000))
  }
}
```
- Canvas circle with `BlurMaskFilter` glow
- Color shifts blue→pink on inhale: `animateColorAsState`
- Center text: "Breathe In…" / "Hold…" / "Breathe Out…" with `AnimatedContent` crossfade

- Pose dots indicator + Next Pose button
- Swipe left: `detectHorizontalDragGestures` → next pose
- Exit X button top-right

---

### SCREEN 10: Journal (`ui/screens/journal/`)

**JournalViewModel:**
- `entries: StateFlow<List<JournalEntry>>`
- `draft: StateFlow<String>`, `selectedMoods: StateFlow<Set<String>>`
- `timeCapsule: StateFlow<JournalEntry?>`, `showTimeCapsuleReveal: StateFlow<Boolean>`
- `fun updateDraft()`, `fun toggleMood()`, `fun saveEntry(isTimeCapsule)`, `fun revealTimeCapsule()`, `fun deleteEntry()`

**JournalScreen:**
- Dark aesthetic (Color(0xFF1A1228) background)
- TIME CAPSULE REVEAL overlay (if due): envelope opens animation → text fades in word-by-word → Keep/Discard buttons
- Write section: large transparent TextField + mood emoji toolbar (horizontal scroll) + phase auto-tag pill
- "Seal for next month 💌" button
- Entry list: LazyColumn with date + phase pill + mood emojis + content preview. Swipe-to-delete + tap to expand

---

### SCREEN 11: Mood Map (`ui/screens/moodmap/`)

**MoodMapViewModel:**
- `moodData: StateFlow<Map<LocalDate, MoodData>>`
- `showCycleOverlay: StateFlow<Boolean>`, `selectedDay: StateFlow<LocalDate?>`
- `fun toggleCycleOverlay()`, `fun selectDay()`

**MoodMapScreen:**
- Custom Canvas heatmap: 7 columns × 14 rows (90 days)
- Each cell: 28dp square, 4dp gap, RoundedCornerShape(4.dp)
- Color per mood (blue/red/gold/green/purple/grey)
- Cycle overlay: drawCircle with phase-colored stroke when enabled
- Tap cell: frosted glass tooltip with mood + journal preview

---

### SCREEN 12: Insights (`ui/screens/insights/`)

**InsightsViewModel:**
- `insights: StateFlow<List<PatternInsight>>`
- `cyclesLogged: StateFlow<Int>`, `minimumCyclesReached: StateFlow<Boolean>`

**InsightsScreen:**
- If <3 cycles: EmptyState with "Still gathering data…" progress bar "N of 3 cycles tracked" + detective mascot
- If ≥3 cycles: insight cards with colored left bar + insight text + animated confidence bar (fills on scroll-into-view via LaunchedEffect)

---

### SCREEN 13: Settings (`ui/screens/settings/`)

**SettingsViewModel:**
- All settings StateFlows + fun for every toggle/update/delete
- `fun deleteAllData()` (with confirmation dialog)
- `fun exportData(context)`, `fun signOut()`

**SettingsScreen:**
9 section cards with headers and custom spring toggles:
1. Profile: name edit, cycle/period length pickers
2. Privacy: stealth mode toggle, biometric lock
3. Reminders: list + swipe-to-delete + add
4. Sound & Haptics: master toggle + individual
5. Account: Google sign-in status, sync status, sign out
6. Premium: current tier + "Upgrade" → Payment
7. Export Data: CSV + PDF (always free)
8. Legal: Privacy policy / Medical disclaimer links
9. Delete all data: destructive button with confirmation AlertDialog

---

### SCREEN 14: Report Export (`ui/screens/report/`)

**ReportViewModel:**
- `reportData: StateFlow<ReportData>`, `exportState: StateFlow<ExportState>`
- `fun generatePdf(context)`, `fun generateCsv(context)`

**ReportExportScreen:**
- Report preview card
- Cycle selector (3 or 6 cycles toggle)
- Included data chips
- "Prep for appointment" bottom sheet
- Export PDF: Loading (document animation) → Done (share sheet via FileProvider)
- PDF via Android `PdfDocument` API (5 pages: cover, cycle table, symptom chart, fertile dates, doctor prep)

---

### SCREEN 15: Payment (`ui/screens/payment/`)

**PaymentViewModel:**
- `products: StateFlow<List<Product>>`, `paymentState: StateFlow<PaymentState>`
- `fun initiatePurchase(product, activity)`, `fun verifyPayment()`, `fun restorePurchases()`

**PaymentScreen:**
- "Go Premium 🌟" heading + current plan card
- 3 product cards with features list + price + Buy button:
  - Premium Themes Pack — ₹99
  - Ad-Free Forever — ₹149
  - Full Premium Bundle — ₹199 (Best Value badge)
- Razorpay: `Checkout().setKeyID(BuildConfig.RAZORPAY_KEY_ID)` → `checkout.open(activity, options)`
- MainActivity implements `PaymentResultWithDataListener`
- "Already purchased? Restore" link

---

### SCREEN 16: Wardrobe (`ui/screens/wardrobe/`)

**WardrobeViewModel:**
- `accessories: StateFlow<List<Accessory>>`, `equippedItems: StateFlow<Set<String>>`, `totalPoints: StateFlow<Int>`
- `fun equipAccessory()`, `fun unlockAccessory()` (deducts points)

**WardrobeScreen:**
- Split layout: live mascot preview (right 50%) + accessories grid (left + below)
- Accessory cards: locked/unlocked/equipped states
- Unlock: 3D flip + ConfettiOverlay
- Apply: mascot preview updates immediately with equipped items

---

### SCREEN 17: Challenges (`ui/screens/challenges/`)

**ChallengesViewModel:**
- `activeChallenges: StateFlow<List<Challenge>>`, `completedChallenges: StateFlow<List<Challenge>>`
- `fun acceptChallenge(id)`, `fun checkDailyProgress(challengeId)`

**ChallengesScreen:**
- Horizontal scroll of active challenge cards with animated progress bar + time remaining
- Accept: "ACCEPTED! 🎉" stamp overlay animation on card
- Completed: trophy animation + reward reveal
- Past challenges: vertical list, greyed with completion date

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Test each screen in emulator via bottom nav and navigation.
Write the report.
