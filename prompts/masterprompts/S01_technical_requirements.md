# S01 — TECHNICAL REQUIREMENTS DOCUMENT (TRD)

> **Prerequisites:** S00 must be active in this session.
> **Output file:** `docs/TRD.md`
> **Build check:** No build check for docs. Verify file exists.

---

## REPORT INSTRUCTION
After completing this section, create:
`masterprompts/reports/S01_report.md`

Include:
- 📄 File created: `docs/TRD.md` (yes/no)
- 📏 Word count / sections covered
- ✅ All 14 FR sections written (FR-01 through FR-14)
- ✅ Priority matrix (P0–P3) documented
- ⚠️ Any missing sections or gaps

---

## PROMPT

Write the complete Technical Requirements Document for Period Saathi.

**Create:** `docs/TRD.md`

Include all sections with full detail:

### 1. PROJECT OVERVIEW
- App purpose and core philosophy: emotional companion, not clinical tracker
- Target users: women in India aged 15–45
- Supported languages: English, Hindi, Marathi, Tamil, Telugu, Bengali
- Offline-first design principle — all core features work without internet
- Privacy-by-design: no health data leaves device without user consent
- Account: optional (sync feature only, not required for core use)

### 2. FUNCTIONAL REQUIREMENTS

**FR-01: Period Tracking**
- Manual period start (tap date → confirm dialog)
- Manual period end ("End period today" button only)
- Bleeding intensity scale: 1=spotting, 2=light, 3=medium, 4=heavy, 5=very heavy
- Bleeding days: 1 to 30+ (no upper cap)
- Clot logging: yes/no toggle per day
- REST DAY: auto-suggested (NOT auto-set) when intensity ≥ 4 for 2 consecutive days
- REST DAY badge shown prominently on home screen when active

**FR-02: Cycle Prediction**
- MINIMUM 3 complete cycles before any prediction is shown
- Confidence levels: LOW (3 cycles), MEDIUM (4–5), HIGH (6+)
- Confidence displayed visually on every prediction
- Accuracy shown as "±N days" based on variance
- Support irregular cycles: 1–120 days cycle length
- Support polymenorrhea (two periods in one month)
- Algorithm: weighted moving average (recent cycles weighted higher)
- NEVER use "overdue" or "late" language
- Use: "Still tracking... your cycle may be taking its time 🌙"

**FR-03: Calendar**
- Full month view with day cells
- Log per day: intensity, symptoms, mood, notes, water, medicine
- Fertile window shown NEUTRAL by default (no color without user choice)
- PMS window: last 7 days before predicted period
- Swipe left/right to navigate months
- Long-press day → quick log bottom sheet
- Future dates not loggable (disabled cells)

**FR-04: Wellness Tracking**
- Water: 1–16 glasses per day, +1 and +500ml quick add
- Habits: customizable checklist (default 5, add up to 15)
- Medicine: name + time + dosage, reminder linkable
- Sleep hours: 0–12 in 0.5 step increments
- Exercise: minutes, type (walk/yoga/gym/other)
- Diet: meal emoji tags (no calorie counting)

**FR-05: Reminders**
- Types: Water, Medicine, Period prediction, Yoga, Custom
- Interval options: custom time, repeating every N hours
- Exact time scheduling via AlarmManager (not WorkManager for precise)
- All reminders OFF by default (opt-in only)
- Quiet hours: configurable (default 10pm–7am)
- Notification permission requested contextually, not on first launch

**FR-06: Gamification**
- Points: water log (+1), habits (+2), cycle data (+3)
- Streak: consecutive days with any log
- Rewards: themes, mascot accessories unlocked by points
- Monthly Wrapped generated after each cycle ends
- 3 active challenges per week, special rewards only from challenges
- Seasonal drops: time-limited accessories with limited-time badge

**FR-07: Partner Mode**
- Select care requests from preset list + custom message
- Opens Android share intent (SMS/WhatsApp/any app)
- ZERO health data in shared content — care requests only
- No backend involvement — entirely local + share intent
- Partner linking: local code only, no server pairing

**FR-08: Remedies + Yoga**
- Flip cards for 8+ home remedies (kitchen ingredients only)
- Yoga flow: 5 poses, 4-minute flow, breathing circle
- Hot bag slider: safety warning at danger zone, snap-back behavior
- All content: fully offline, embedded in app

**FR-09: Journal + Mood Map**
- Private journal: encrypted in Room with SQLCipher
- Mood dial: 7-point scale with emoji
- Time capsule: write message, revealed on next cycle day 1
- Mood map: heatmap of last 90 days, cycle overlay toggle
- Export: journal entries as PDF or plain text

**FR-10: Payments (Razorpay)**
- One-time purchases ONLY:
  - "Premium Themes Pack" — ₹99
  - "Ad-Free Forever" — ₹149
  - "Full Premium Bundle" — ₹199
- NEVER: subscriptions, recurring charges, paywalling old logs
- Banner ads only (AdMob) for non-premium users
- No interstitial, rewarded, or video ads
- Ad-free by default in first 7 days (grace period)

**FR-11: Accounts + Sync (Optional)**
- Google Sign-In OR email+password
- Password reset via email (Supabase auth)
- Account is optional: full app without account
- Sync: logs, settings, streak on login
- Conflict resolution: last-write-wins with timestamp comparison
- Auto-backup: opt-in Google Drive backup (encrypted JSON)
- Export: free CSV and PDF always available

**FR-12: Privacy + Security**
- SQLCipher encrypted Room database
- EncryptedSharedPreferences for all sensitive prefs
- FLAG_SECURE on all screens
- Certificate pinning for Supabase API calls
- Stealth mode: disguise app icon/name, PIN-protected
- Biometric lock: optional (Face ID / Fingerprint)
- DPDP Act 2023 compliant
- No analytics without opt-in
- Bug reports: opt-in, health data stripped before sending
- Pregnancy pause mode: hides period tracking

**FR-13: Widget**
- 2×2 widget: cycle day, mascot, water ring
- 4×1 widget: next period countdown
- Updates every 4 hours via WorkManager
- Force refresh: tap widget refreshes immediately
- Widget data: read from Room directly

**FR-14: Accessibility**
- TalkBack: contentDescription on ALL interactive elements
- Minimum touch targets: 48×48dp enforced
- Text scaling: tested at 85%, 100%, 130%, 200% font scale
- Color contrast: WCAG AA minimum (4.5:1 for text)
- Non-pink gender-neutral theme option (teal/grey/blue)
- Screen reader announcements for state changes
- No flashing animations

### 3. NON-FUNCTIONAL REQUIREMENTS
- Cold start: <1.5 seconds on mid-range device
- Frame rate: 60fps min, 120fps where device supports
- Zero ANR tolerance — all DB ops on IO dispatcher
- Memory: <150MB RAM in normal use
- APK size: <30MB
- Crash-free rate target: 99.5%+
- Unit test coverage: ≥70%

### 4. MUST-NEVER LIST
Document all 12 hardcoded rules that can never be violated (forced data wipe, paywall on logs, "late" language, etc.)

### 5. LEGAL REQUIREMENTS
- India DPDP Act 2023 Compliance details
- Medical disclaimer text (full)
- Play Store Data Safety answers (pre-filled)

### 6. PRIORITY MATRIX
- P0: Ship-blockers (list all 8)
- P1: Launch quality (list all 8)
- P2: Post-launch polish (list all 7)
- P3: Future roadmap (list all 8)

---

Write this complete TRD document now. Be thorough — this is the reference for all future development decisions.
