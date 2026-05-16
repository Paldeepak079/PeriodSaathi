# Technical Requirements Document (TRD)

## Period Saathi

**Version:** 1.0  
**Date:** May 16, 2026  
**Status:** Production  

---

## 1. PROJECT OVERVIEW

### 1.1 App Purpose and Core Philosophy

Period Saathi is designed as an emotional companion for women navigating their menstrual health journey. The app prioritizes warmth, empathy, and user empowerment over clinical tracking. Every feature is designed to make users feel supported, understood, and in control of their own data.

### 1.2 Target Users

- **Primary:** Women in India aged 15–45
- **Secondary:** Healthcare providers, partners, and caregivers
- **Language Support:** English, Hindi, Marathi, Tamil, Telugu, Bengali

### 1.3 Design Principles

**Offline-First:** All core features function without internet connectivity. Period tracking, predictions, calendar views, wellness logging, reminders, and gamification are fully offline. Network is required only for optional sync and payment features.

**Privacy-by-Design:** No health data leaves the device without explicit user consent. All sensitive data is encrypted at rest using SQLCipher. No third-party analytics without opt-in. Users have complete control over their data export and deletion.

**Account Optional:** Users can access 100% of app functionality without creating an account. Account creation is solely for sync and backup features. No paywall on core features based on account status.

---

## 2. FUNCTIONAL REQUIREMENTS

### FR-01: Period Tracking

**Description:** Manual logging of period start, end, and daily details with empathetic UI feedback.

**User Flow:**
1. User taps any date in calendar or home screen to mark period start
2. Confirmation dialog appears with supportive message
3. User selects bleeding intensity (1-5 scale)
4. Daily log accessible via calendar or quick-add bottom sheet
5. User taps "End period today" button to close current period

**Data Model:**
- `period_start_date`: Date (required)
- `period_end_date`: Date (optional, null until user ends)
- `cycle_id`: UUID (auto-generated)
- `daily_logs[]`:
  - `date`: Date
  - `intensity`: Int (1-5, 1=spotting, 2=light, 3=medium, 4=heavy, 5=very heavy)
  - `has_clot`: Boolean
  - `notes`: String (optional)

**REST DAY Logic:**
- Auto-suggestion triggered when intensity ≥ 4 for 2+ consecutive days
- Badge displayed on home screen with message: "Rest day suggested"
- User can dismiss or snooze (1 day)
- NOT auto-set — requires user confirmation

**Constraints:**
- No auto-detection of period start/end
- Manual entry only, no automatic inference
- No restrictions on bleeding days (1-30+ allowed)

---

### FR-02: Cycle Prediction

**Description:** Intelligent cycle length prediction with confidence scoring and empathetic messaging.

**Prediction Rules:**
1. Minimum 3 complete cycles required before ANY prediction is shown
2. Cycle must have both start and end dates to count as complete
3. Confidence levels:
   - LOW: 3 cycles tracked
   - MEDIUM: 4-5 cycles tracked
   - HIGH: 6+ cycles tracked
4. Accuracy displayed as "±N days" based on standard deviation
5. Algorithm: Weighted moving average (most recent cycles weighted higher)

**Cycle Length Support:**
- Minimum: 1 day (polymenorrhea)
- Maximum: 120 days
- All valid cycles accepted

**Messaging Rules:**
- NEVER use "overdue", "late", "missed", "expected"
- Use supportive language: "Still tracking... your cycle may be taking its time 🌙"
- If prediction uncertain: "Not enough data yet — keep logging!"

**Display:**
- Prediction shown on home screen with confidence indicator
- Calendar shows predicted dates with visual distinction
- PMS window (last 7 days before predicted) highlighted subtly

---

### FR-03: Calendar

**Description:** Full month calendar view with daily logging, symptom tracking, and navigation.

**Features:**
- Full month view with scrollable/pagable day cells
- Swipe left/right to navigate months (animated)
- Visual indicators for: period days, predicted period, fertile window, PMS, rest days
- Fertile window: shown NEUTRAL (no color) by default, can be toggled on in settings

**Per-Day Logging:**
- Long-press opens quick-log bottom sheet
- Loggable: intensity, symptoms (checklist), mood (emoji), notes, water, medicine
- Future dates: disabled, non-interactive (gray out with "Coming soon" tooltip)

**Symbols:**
- Period: Pink dot/underline
- Predicted: Light pink outline
- Fertile: Green outline (user-toggled)
- PMS: Light purple background
- Rest day: Cloud icon with "Rest" label

---

### FR-04: Wellness Tracking

**Description:** Holistic health logging beyond period data — water, habits, medicine, sleep, exercise, diet.

**Water Tracking:**
- Range: 1-16 glasses per day
- Quick add: "+1 glass" button
- Custom ml: "+500ml" shortcut
- Visual: Ring progress with daily goal
- Default goal: 8 glasses

**Habits:**
- Default: 5 customizable items (e.g., "Take vitamins", "Drink warm water", "Stretch 10 min")
- Max: 15 items
- Checkbox toggle per day
- View: Weekly streak indicator

**Medicine:**
- Fields: Name, time, dosage
- Link to reminder: Toggle for notification
- Multiple medicines per day supported

**Sleep:**
- Range: 0-12 hours
- Step: 0.5 hours
- Input: Slider or direct number input

**Exercise:**
- Minutes: Number input
- Type: Dropdown (walk, yoga, gym, cardio, other)
- Visual: Weekly minutes chart

**Diet:**
- Meal emoji tags: 🍎 🥗 🍛 🍜 🥛 ☕ 🍪 🍰
- No calorie counting — pure emoji selection
- Up to 4 tags per day

---

### FR-05: Reminders

**Description:** Notification system for water, medicine, period, yoga, and custom reminders.

**Reminder Types:**
- Water (interval-based)
- Medicine (time-based)
- Period prediction (date-based)
- Yoga (time-based)
- Custom (user-defined message + time)

**Technical Implementation:**
- Exact-time reminders: AlarmManager (NOT WorkManager) for precise timing
- Interval reminders: WorkManager for periodic
- Quiet hours: Default 10pm-7am, configurable
- All reminders opt-in (OFF by default)

**Permissions:**
- Notification permission requested contextually (when user creates first reminder)
- NOT requested on first launch or onboarding
- Clear explanation shown before permission request

**Settings:**
- Toggle per reminder
- Sound: default, silent, custom
- Vibration: on/off
- Do Not Disturb override: per-reminder

---

### FR-06: Gamification

**Description:** Encouragement system with points, streaks, rewards, challenges, and monthly wrapped.

**Points System:**
- Water log: +1 point
- Habits completed: +2 points per habit
- Cycle data logged: +3 points per day
- Period start logged: +10 points
- Period end logged: +15 points

**Streaks:**
- Consecutive days with ANY log (water, habits, period, etc.)
- Display: "N day streak" with flame icon
- Reset: Miss a day (no logs)

**Rewards:**
- Points required for unlock:
  - Themes: 100-500 points
  - Mascot accessories: 50-200 points
  - Wallpapers: 150-300 points

**Monthly Wrapped:**
- Generated automatically after each cycle ends
- Stats: Total days logged, water average, habit streak, top mood
- Shareable (image export)

**Challenges:**
- 3 active challenges per week
- Examples: "Log 7 days water", "Complete 5 habits", "Log mood 3 days"
- Special rewards: Exclusive accessories from challenges only

**Seasonal Drops:**
- Limited-time accessories (e.g., "Holi colors", "Diwali lights")
- Time-limited badge on item
- Available 2-4 weeks per season

---

### FR-07: Partner Mode

**Description:** Share care requests with partners/caregivers without exposing health data.

**Functionality:**
1. User selects care request from preset list OR types custom message
2. Preset requests: "Bring me water", "Make me tea", "Give me a massage", "Be patient with me", "Check on me later", "Surprise me"
3. Opens Android share intent (SMS, WhatsApp, email, any app)
4. Shared content: Care request text ONLY
5. ZERO health data: No dates, no cycle info, no predictions

**Partner Linking:**
- Local code: Generate 6-digit code, share manually
- Partner enters code to see "care requests" from user
- No server involved — purely local storage and intent

**Constraints:**
- No automatic messages
- No health data in any shared content
- User must explicitly choose what to share each time

---

### FR-08: Remedies + Yoga

**Description:** Offline home remedies and yoga flows for period wellness.

**Home Remedies:**
- Format: Flip cards (tap to flip)
- Count: 8+ recipes
- Ingredients: Kitchen items only (ginger, turmeric, cinnamon, jaggery, hot water, etc.)
- Each card: Ingredient list, preparation, benefits, caution notes

**Yoga Flow:**
- 5 poses per flow
- Duration: 4 minutes total
- Visuals: Illustration + step-by-step text
- Breathing circle: Animated guide (inhale/exhale visual)

**Hot Bag Slider:**
- Temperature ranges: Cool → Warm → Hot → Danger zone
- Safety: Warning at danger zone (42°C+), snap-back behavior
- Disclaimer: "Consult doctor for heat therapy safety"

**Content:**
- All embedded in app (offline)
- No API calls
- No external links

---

### FR-09: Journal + Mood Map

**Description:** Private encrypted journal with mood tracking, time capsule, and data export.

**Private Journal:**
- Encrypted storage: SQLCipher
- Entries: Text + mood emoji + timestamp
- Search: Full-text search on encrypted content
- Lock: Optional biometric/PIN protection

**Mood Dial:**
- 7-point scale:
  1. 😢 Very sad
  2. 😔 Sad
  3. 😐 Neutral
  4. 🙂 Fine
  5. 😊 Good
  6. 😄 Great
  7. 🤩 Amazing

**Time Capsule:**
- User writes message + sets reveal date (next cycle day 1)
- Encrypted, auto-unlocks on date
- Notification: "Your time capsule is ready to open!"

**Mood Map:**
- Heatmap: Last 90 days
- Colors: Gradient based on mood (sad→happy)
- Overlay toggle: Show cycle phases on mood data

**Export:**
- PDF export (formatted, includes dates)
- Plain text export (CSV structure)
- All exports available free (no paywall)

---

### FR-10: Payments (Razorpay)

**Description:** One-time purchase system for premium features, with banner ads for non-premium users.

**In-App Purchases (One-Time):**
| Product | Price | Features |
|---------|-------|----------|
| Premium Themes Pack | ₹99 | 10 exclusive app themes |
| Ad-Free Forever | ₹149 | Remove all banner ads |
| Full Premium Bundle | ₹199 | Themes + Ad-Free + Exclusive rewards |

**Payment Processing:**
- Provider: Razorpay SDK
- Indian payment methods: UPI, cards, wallets, net banking
- Secure: PCI-DSS compliant

**Ad Implementation:**
- Banner ads only: AdMob (non-premium users)
- No interstitials, no rewarded ads, no video ads
- Grace period: 7 days ad-free for all new users

**Constraints:**
- NO subscriptions — one-time only
- NO paywall on historical logs
- NO paywall on core features

---

### FR-11: Accounts + Sync (Optional)

**Description:** Optional account system for backup, sync, and cross-device access.

**Authentication:**
- Google Sign-In (Firebase Auth)
- OR Email + Password (Supabase Auth)
- Password reset via email link

**Sync:**
- Synced data: Period logs, wellness data, settings, streaks
- Conflict resolution: Last-write-wins (timestamp comparison)
- Sync frequency: On app open, on changes, manual refresh

**Backup:**
- Opt-in: Google Drive encrypted JSON backup
- Encryption: AES-256 before upload
- Manual export: CSV always available (free)

**Data Access:**
- Account is optional — full app without account
- No features gated by account status
- No data loss if user skips account

---

### FR-12: Privacy + Security

**Description:** Comprehensive security implementation for health data protection.

**Data Encryption:**
- Room Database: SQLCipher with user-derived key
- SharedPreferences: EncryptedSharedPreferences (AES)
- Keys: Stored in Android Keystore

**Screen Security:**
- FLAG_SECURE on all screens (prevents screenshots/recording)
- Screenshot disabled in recent apps

**Network Security:**
- Certificate pinning for Supabase API
- TLS 1.3 enforced

**App Protection:**
- Stealth mode: Disguise app icon/name (e.g., "Calculator" or "Weather")
- PIN protection: 4-6 digit PIN for stealth mode access
- Biometric lock: Optional Face ID / Fingerprint unlock
- Auto-lock: After 30 seconds in background

**Compliance:**
- DPDP Act 2023: Explicit consent for data processing, right to delete
- No analytics without opt-in
- Bug reports: Opt-in only, health data stripped from reports

**Special Modes:**
- Pregnancy pause: Hides period tracking, shows "Paused" status
- Data export: Full data export (JSON/CSV)
- Data wipe: Complete account deletion (irreversible)

---

### FR-13: Widget

**Description:** Home screen widgets for quick access to cycle data.

**2x2 Widget:**
- Shows: Current cycle day number, mascot, water ring progress
- Tap: Opens app to home screen
- Update frequency: Every 4 hours via WorkManager
- Force refresh: Tap on widget triggers immediate update

**4x1 Widget:**
- Shows: Next period countdown ("Period in N days") or "Track today"
- Tap: Opens app to calendar
- Update frequency: Daily

**Technical:**
- Data source: Room database (direct read)
- Update mechanism: WorkManager for scheduled, direct for tap
- Size: Complies with Android widget standards

---

### FR-14: Accessibility

**Description:** Full accessibility support for users with disabilities.

**TalkBack Support:**
- contentDescription on ALL interactive elements
- Logical reading order
- Meaningful labels (not just "Button")

**Touch Targets:**
- Minimum: 48x48dp for all interactive elements
- Spacing: 8dp minimum between touch targets

**Text Scaling:**
- Tested scales: 85%, 100%, 130%, 200%
- No truncation at any scale
- Dynamic layouts

**Color:**
- WCAG AA: 4.5:1 minimum contrast ratio for text
- Non-pink theme option: Teal/Grey/Blue for gender-neutral users

**Animations:**
- No flashing/blinking (triggers seizures)
- Reduced motion support via system setting

**Screen Reader:**
- Announce state changes (e.g., "Cycle day 5, predicted in 12 days")
- Live region updates for dynamic content

---

## 3. NON-FUNCTIONAL REQUIREMENTS

### Performance

- **Cold Start:** <1.5 seconds on mid-range device (Snapdragon 665 equivalent)
- **Frame Rate:** 60fps minimum, 120fps on supported devices
- **ANR Tolerance:** Zero — all database operations on IO dispatcher
- **Memory:** <150MB RAM in normal use
- **APK Size:** <30MB (excluding assets)

### Reliability

- **Crash-Free Rate:** 99.5%+
- **Test Coverage:** ≥70% unit test coverage
- **Recovery:** Graceful degradation on database errors

### Storage

- **Local:** Room database with SQLCipher
- **Backup:** Optional encrypted Google Drive backup
- **Export:** Free CSV and PDF export always available

---

## 4. MUST-NEVER LIST

The following rules are absolute and can never be violated:

1. **No Forced Data Wipe:** Never delete user data without explicit user action
2. **No Paywall on Logs:** Never restrict access to historical data based on payment
3. **No "Late" Language:** Never use overdue/late/missed for periods — only supportive messages
4. **No Subscriptions:** Never implement recurring subscriptions — one-time purchases only
5. **No Health Data in Shares:** Never include period dates, cycle data in shared content
6. **No Auto-Period Detection:** Never auto-infer period start/end without user confirmation
7. **No Unencrypted Health Data:** Never store period/health data without encryption
8. **No Analytics Without Opt-In:** Never send data to analytics without explicit consent
9. **No Interstitial Ads:** Never show full-screen ads — banner only
10. **No Notification Spam:** Never send more than 10 notifications per day
11. **No Discrimination:** Never make assumptions about gender — always use inclusive language
12. **No Medical Advice Claims:** Always include disclaimer that app is not a medical device

---

## 5. LEGAL REQUIREMENTS

### DPDP Act 2023 Compliance

- **Consent:** Explicit opt-in for any data collection
- **Purpose:** Data used only for stated purposes
- **Retention:** Data deleted on user request
- **Portability:** Data export available in machine-readable format
- **Disclosure:** No third-party sharing without consent
- **Grievance:** Contact info for data concerns

### Medical Disclaimer

```
Period Saathi is a personal wellness companion app and is NOT a medical device.
The information provided in this app is for general informational purposes only
and is not intended as a substitute for professional medical advice, diagnosis,
or treatment. Never disregard professional medical advice or delay seeking it
because of something you have read in this app. Always consult your healthcare
provider before making any decisions about your health.
```

### Play Store Data Safety

- **Collected Data:** Period tracking, wellness logs, optional account data
- **Data Use:** App functionality only
- **Sharing:** None (no third parties)
- **Security:** Encryption at rest
- **User Control:** Export and delete available

---

## 6. PRIORITY MATRIX

### P0: Ship-Blockers (Must Fix Before Launch)

| ID | Feature | Issue |
|----|---------|-------|
| P0-1 | Core period tracking | Data persistence not working |
| P0-2 | Cycle prediction | Confidence calculation broken |
| P0-3 | Reminder notifications | Notifications not firing |
| P0-4 | Database encryption | SQLCipher not initializing |
| P0-5 | App crash on launch | Cold start >5 seconds or crash |
| P0-6 | Payment flow | Razorpay integration broken |
| P0-7 | Data export | Export produces corrupt files |
| P0-8 | Widget not updating | Widget shows stale data |

### P1: Launch Quality (Fix Before Public Release)

| ID | Feature | Issue |
|----|---------|-------|
| P1-1 | Calendar rendering | Slow scroll on large datasets |
| P1-2 | Gamification points | Incorrect point calculation |
| P1-3 | Water tracking | Counter not persisting |
| P1-4 | Habit tracking | Custom habits not saving |
| P1-5 | Mood logging | Mood map colors incorrect |
| P1-6 | Journal encryption | Entry search returns no results |
| P1-7 | Partner mode sharing | Share intent crashes |
| P1-8 | Accessibility | TalkBack labels missing |

### P2: Post-Launch Polish (Fix Within 30 Days)

| ID | Feature | Issue |
|----|---------|-------|
| P2-1 | Monthly Wrapped | Not generating for short cycles |
| P2-2 | Challenges | Not rotating weekly |
| P2-3 | Theme switching | Dark mode flicker |
| P2-4 | Sleep tracking | Graph shows incorrect data |
| P2-5 | Exercise logging | Type dropdown shows duplicates |
| P2-6 | Medicine reminders | Snooze not working |
| P2-7 | Time capsule | Not auto-revealing |

### P3: Future Roadmap (Beyond Launch)

| ID | Feature | Issue |
|----|---------|-------|
| P3-1 | Cycle insights | AI-powered health insights |
| P3-2 | Community features | Anonymous community forums |
| P3-3 | Wearable sync | Connect to fitness bands |
| P3-4 | OCR scanning | Scan prescription/medical docs |
| P3-5 | Multiple languages | Add 10+ more Indian languages |
| P3-6 | Family mode | Track for daughters/mothers |
| P3-7 | Doctor reports | Generate PDF for doctor visits |
| P3-8 | Calendar integration | Sync with Google Calendar |

---

## Appendix A: Technology Stack

- **Framework:** Jetpack Compose
- **Language:** Kotlin 2.2.10
- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt
- **Database:** Room + SQLCipher
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Compose BOM:** 2026.02.01
- **Build System:** Gradle with Kotlin DSL

## Appendix B: Package Structure

```
com.periodsaathi.app
├── data/
│   ├── local/
│   ├── remote/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── ui/
│   ├── viewmodel/
│   └── navigation/
└── di/
```

## Appendix C: Color Palette

- **Background:** #FFF8F5 (warm white)
- **Primary:** #FFB5C8 (soft pink)
- **Primary Variant:** #FF8FAB (deeper pink)
- **Secondary:** #FFD1DC (light pink)
- **Text Primary:** #2D2D2D (near black)
- **Text Secondary:** #6B6B6B (gray)
- **Success:** #4CAF50 (green)
- **Warning:** #FF9800 (orange)
- **Error:** #F44336 (red)

## Appendix D: Typography

- **Headings:** Poppins (Bold/SemiBold)
- **Body:** Nunito (Regular/Medium)
- **Accent:** Nunito (Italic for emphasis)

---

*Document Version: 1.0*  
*Last Updated: May 16, 2026*  
*Next Review: Before Beta Release*