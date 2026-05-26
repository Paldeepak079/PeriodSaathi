# S17 — PLAY STORE DEPLOYMENT

> **Prerequisites:** S00 active · S16 complete · all tests passing · CI green
> **Output:** 6 documentation files in `docs/`
> **Build check:** `./gradlew bundleRelease` — must produce a valid AAB.

---

## REPORT INSTRUCTION
After completing this section AND generating the AAB, create:
`masterprompts/reports/S17_report.md`

Include:
- 📄 All 6 files created (checklist)
- ✅ `./gradlew bundleRelease` result (SUCCESS / FAILED)
- 📦 AAB file size (should be <30MB)
- 🔑 Keystore generated and stored securely (yes/no)
- 📋 Play Store listing complete in English + Hindi (yes/no)
- 🔒 Privacy Policy: DPDP Act compliant (verified all sections, yes/no)
- 📊 Data Safety section: all questions answered (yes/no)
- ❌ Any build signing errors

---

## PROMPT

Create all Play Store deployment assets and documentation.
Write all 6 files completely.

---

### FILE 1: `docs/PLAY_STORE_LISTING.md`

**SHORT DESCRIPTION (80 chars max):**
```
Your cute period bestie 🌸 Track, rest & earn rewards. Offline & private.
```

**FULL DESCRIPTION (write complete 4000-char English description):**

Opening hook:
> Meet Saathi — your adorable, empathetic period companion who understands that every cycle is unique. Period Saathi isn't just a tracker; it's a friend that celebrates your body's rhythm without judgment.

Key features (write 15 bullet points including):
- Manual period tracking (start and end on your terms)
- Beautiful cycle visualization with Saathi mascot
- Smart predictions after 3 cycles (with confidence ratings)
- Offline-first: 100% of features work without internet
- Water and wellness tracking with rewards
- Customizable reminders (water, medicine, yoga)
- Partner mode: send care requests (zero health data shared)
- Home remedies and yoga flow for cycle comfort
- Private encrypted journal with mood tracking
- Time capsule: write to your future self
- Gamification: earn points, unlock themes and accessories
- Biometric lock and optional stealth mode
- Ad-free premium (one-time purchase, no subscriptions)
- Multiple language support (English, Hindi, and more)
- Privacy-first: your health data never leaves your device

Privacy statement paragraph:
> Your intimate health data belongs to you — and only you. Period Saathi stores everything on your device, encrypted. We do not collect, sell, or access your period data, journal entries, or any health information. An account is optional and only used for syncing between your own devices.

What makes Period Saathi different paragraph (emotional companion, not clinical tracker):
> Unlike clinical period trackers, Saathi speaks to you with warmth and care. We never say your period is "overdue" or "late" — because every body is different. We never push predictions on you before we've earned the data to make them. And we never pressure you to log more than you want to.

Permissions explanation:
> - Internet: Required only for optional account sync (not needed for core features)
> - Notifications: Required for opt-in reminders you configure (never sent without your permission)
> - Vibration: For haptic feedback when interacting with the app (improves the experience)
> - Biometric: Only if you enable the biometric lock feature
> - Receive boot completed: To restart your opt-in reminders after phone restart

Contact/support info:
> Support: support@periodsaathi.app (response within 48 hours)
> Report bugs: In-app Settings → Report a Bug

Medical disclaimer (brief):
> Period Saathi is a personal tracking tool. It is not a medical device and does not provide medical diagnoses. Always consult a healthcare professional for medical concerns.

**HINDI DESCRIPTION (write equivalent 4000-char Hindi translation):**
Translate the full description above to Hindi. Maintain the warm, friendly tone. Use:
- "साथी" for Saathi
- "पीरियड" for period (common usage in India)
- "गोपनीयता" for privacy
- Friendly, accessible Hindi (not formal/clinical)

---

### FILE 2: `docs/PRIVACY_POLICY.html`

Complete DPDP Act 2023 compliant HTML privacy policy.

Write a full HTML file with:
- Proper `<!DOCTYPE html>` + CSS for readability
- Responsive mobile layout (single-column, max-width 800px)
- All 10 required sections:

**Section 1: Who We Are**
Individual developer / company name, India location, contact email.

**Section 2: What Data We Collect**
```
Without account (default):
- NOTHING leaves your device
- All data stored locally, encrypted with SQLCipher

With account (optional):
- Email address (for account recovery only)
- Display name (you choose this)
- Sync tokens (for secure data sync)

Health data (ALWAYS LOCAL):
- Period dates, symptoms, mood, journal entries
- NEVER transmitted to our servers
- NEVER accessible to us
- NEVER shared with any third party
```

**Section 3: Why We Collect It**
Email/name: Account management and password recovery only.

**Section 4: How We Protect It**
- SQLCipher 4.5.4 encryption (AES-256) for all health data
- EncryptedSharedPreferences for app settings
- TLS 1.3 for all network communication
- Certificate pinning prevents man-in-middle attacks
- Biometric/PIN app lock (optional, user-controlled)

**Section 5: Your Rights Under DPDP Act 2023**
```
Right to Access: Request a full export of your account data (in-app: Settings → Export Data)
Right to Correction: Update your name/email anytime in app settings
Right to Erasure: Delete ALL data in-app (Settings → Delete All Data) or email us
Right to Nominate: Designate a nominee via email to our grievance officer
Right to Withdraw Consent: Disable analytics anytime in Settings → Privacy
```

**Section 6: Data Retention**
- Account data: deleted within 30 days of account deletion request
- Health data: deleted immediately when you use in-app delete
- Exports: you keep what you exported — we don't

**Section 7: Third Parties**
| Service | Purpose | Data Shared | Optional |
|---------|---------|-------------|----------|
| AdMob (Google) | Banner ads | Device identifiers | Yes (disable in Premium) |
| Razorpay | Payment processing | Email, payment amount | Only if purchasing |
| Firebase Crashlytics | Crash reports | App crash data (sanitized) | Yes (opt-in) |
| Supabase | Account sync | Email, display name | Yes (only with account) |

**Section 8: Contact**
support@periodsaathi.app — response within 48 hours

**Section 9: Grievance Officer**
Name: [Developer Name]
Email: grievance@periodsaathi.app
Response time: Within 72 hours as per DPDP Act 2023

**Section 10: Effective Date**
Version 1.0, effective from [LAUNCH_DATE]. Will be updated with 7 days notice via in-app notification.

---

### FILE 3: `docs/PLAY_STORE_DATA_SAFETY.md`

Complete Play Store Data Safety questionnaire answers:

```markdown
# Play Store Data Safety Answers

## Does your app collect or share any of the required user data types?
Yes.

## Is all of the user data collected by your app encrypted in transit?
Yes — TLS 1.3 for all network requests.

## Do you provide a way for users to request that their data is deleted?
Yes — In-app: Settings → Delete All Data
      Via email: support@periodsaathi.app

---

## DATA COLLECTED AND SHARED

### App activity
- Collected: Yes (if analytics opted in)
- Shared: No
- Required for app to function: No
- Encrypted in transit: Yes
- User can request deletion: Yes
- Purpose: App functionality analytics (opt-in only)

### App info and performance (crash logs)
- Collected: Yes (if crash reporting opted in)
- Shared: Yes — with Firebase Crashlytics
- Required for app to function: No
- Encrypted in transit: Yes
- User can request deletion: Yes
- Purpose: Crash analysis, app stability improvement

---

## DATA NOT COLLECTED (even if it appears app could)

### Health and fitness data
Status: NOT collected by us
Note: Period dates, symptoms, flow data stored ONLY on device.
We have no access to this data.

### Personal communications (journal entries)
Status: NOT collected by us
Note: Journal entries encrypted on device, never synced.

### Location
Status: NOT collected
Note: We never request location permission.

### Photos and videos
Status: NOT collected
Note: We never access camera or photo library.

---

## DATA COLLECTED WITH ACCOUNT (optional feature)

### Name
- Why: Display name in app
- Shared: No
- Optional: Yes

### Email address
- Why: Account creation, password reset
- Shared: No (except Razorpay for purchase receipt)
- Optional: Yes
```

---

### FILE 4: `docs/MEDICAL_DISCLAIMER.md`

```markdown
# Medical Disclaimer

## Period Saathi — Health Tool Disclaimer

**Effective Date:** [LAUNCH_DATE]

Period Saathi is a personal menstrual tracking tool designed to help you log and understand your cycle. It is **NOT a medical device**, does **NOT provide medical diagnoses**, and is **NOT a substitute for professional medical advice, diagnosis, or treatment**.

### What Period Saathi Does
- Helps you log period dates, symptoms, and wellness data
- Shows patterns in your data over time
- Provides estimates of future cycle dates based on your history
- Offers wellness tips and comfort suggestions

### What Period Saathi Does NOT Do
- Provide medical diagnoses
- Replace consultation with a qualified healthcare provider
- Detect pregnancy, STIs, or medical conditions
- Serve as a contraception method (cycle tracking is NOT reliable contraception)
- Guarantee accuracy of predictions

### When to See a Doctor
Consult a qualified healthcare professional if you experience:
- Periods lasting more than 7 days
- Severe pain that disrupts daily life
- Irregular cycles with significant variation
- Absence of period for 3+ months (when not pregnant)
- Unusual symptoms not previously experienced
- Any concerns about your reproductive health

### Emergency
In case of a medical emergency, contact your local emergency services immediately (India: 112 or 108).

### Fertility and Contraception
Period Saathi may show fertile window estimates. This information is:
- Based only on your logged cycle data
- **NOT reliable for contraception purposes**
- Educational only — not medical guidance

Please consult a healthcare provider for contraception and fertility advice.

---

*Period Saathi is built with love and care, but it is not a doctor. When in doubt, please reach out to a healthcare professional.*

*Support: support@periodsaathi.app*
```

---

### FILE 5: `docs/RELEASE_CHECKLIST.md`

```markdown
# Period Saathi — Release Checklist

## PRE-RELEASE: Code Quality
- [ ] versionCode incremented (current: _____)
- [ ] versionName updated (semantic versioning: 1.0.0)
- [ ] All `Log.d()` and `Log.v()` wrapped in `if (BuildConfig.DEBUG)` guards
- [ ] No hardcoded API keys in source code
- [ ] `google-services.json` is production instance (not test)
- [ ] Supabase URL is production project
- [ ] Razorpay key starts with `rzp_live_` (not `rzp_test_`)
- [ ] AdMob test device IDs removed from release build
- [ ] No `TODO` or `FIXME` comments in shipped code
- [ ] `./gradlew detekt` — zero violations
- [ ] `./gradlew lint` — zero errors

## PRE-RELEASE: Feature Verification
- [ ] Medical disclaimer shown on first launch
- [ ] DPDP consent screen shown on first launch
- [ ] Analytics starts DISABLED (requires opt-in)
- [ ] All notifications start DISABLED (requires opt-in per type)
- [ ] "Continue without account" works fully offline
- [ ] Period logging works without internet
- [ ] Water tracking persists across app restart
- [ ] Calendar loads correctly for past 6 months
- [ ] Prediction shows ONLY after 3+ cycles (verified fresh install)
- [ ] NO "late" or "overdue" text anywhere (grep: `grep -r "overdue\|late period\|period late" app/src/main/res/`)
- [ ] One-time payment only — NO subscription wording

## PRE-RELEASE: Device Testing
- [ ] Tested on Android 7.0 (API 24) — minimum SDK
- [ ] Tested on Android 10 (API 29) — mid target
- [ ] Tested on Android 15 (API 35) — latest
- [ ] Tested on low-end device (2GB RAM — Redmi 9A or similar)
- [ ] Tested on tablet (7-inch or larger)
- [ ] No crash in 30 minutes of normal usage (0 crashes)
- [ ] Offline mode: airplane mode → all core features work
- [ ] Cold start time < 1.5 seconds (measured with Systrace)
- [ ] No ANR in 30-minute test session
- [ ] Biometric lock: tested on enrolled device
- [ ] Stealth mode: icon switches correctly (if tested)

## PRE-RELEASE: Play Store Assets
- [ ] Feature graphic created: 1024×500px PNG
- [ ] App icon: 512×512px PNG (for Play Store listing)
- [ ] Screenshots (minimum 2, max 8 per device type):
  - [ ] Phone (16:9 or 9:16): Home, Calendar, Wellness, Partner Mode
  - [ ] Tablet: at least 1 screenshot
- [ ] Short description: exactly 80 chars, reviewed
- [ ] Full description: reviewed for spelling, tone, accuracy
- [ ] Hindi description: reviewed by Hindi speaker
- [ ] Privacy policy URL: live and accessible
- [ ] Medical disclaimer: reviewed by team

## PRE-RELEASE: Security
- [ ] FLAG_SECURE on all windows (screenshot blocked)
- [ ] SQLCipher key not derivable from device ID alone
- [ ] Certificate pinning active in release build
- [ ] No health data in Logcat (test with Logcat filtered by "HealthData")
- [ ] ProGuard: tested release APK all features work after obfuscation

## STAGED ROLLOUT PLAN

| Week | Action | Gate Condition |
|------|--------|----------------|
| Week 1 | Internal testing (team + family) | 0 crash reports |
| Week 2 | Closed alpha — 50 testers via Play Console | Crash-free rate > 99% |
| Week 3 | Open beta — Google Play Early Access | Rating feedback > 3.5 avg |
| Week 4 | Production — **10%** rollout | Crash-free rate > 99.5% |
| Week 5 | **25%** rollout | ANR rate < 0.1% |
| Week 6 | **50%** rollout | Day 1 retention > 40% |
| Week 7 | **100%** rollout | Day 7 retention > 25% |

## TARGET LAUNCH METRICS

| Metric | Target |
|--------|--------|
| Crash-free users | > 99.5% |
| ANR rate | < 0.1% |
| Play Store rating | > 4.0 after 50 reviews |
| Day 1 retention | > 40% |
| Day 7 retention | > 25% |
| Install size | < 25 MB |
| Cold start | < 1.5 seconds |
```

---

### FILE 6: `docs/KEYSTORE_SETUP.md`

```markdown
# Keystore Generation Guide

## CRITICAL WARNING
> If you lose your keystore file or password, you **cannot update your app on the Play Store**.
> There is NO way to recover it. Google will reject updates signed with a different key.
> **Back up your keystore in at least 3 separate secure locations.**

## Generate Keystore (run once, keep forever)

```bash
keytool -genkey -v \
  -keystore period-saathi-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias period-saathi-key \
  -dname "CN=Period Saathi, OU=Dev, O=YourName, L=City, S=State, C=IN"
```

When prompted:
- Keystore password: use a strong, unique password (20+ chars)
- Key password: can be the same as keystore password
- Save both passwords in a password manager (Bitwarden, 1Password)

## Verify Keystore
```bash
keytool -list -v -keystore period-saathi-release.jks
```

## Configure `local.properties`
```properties
keystore.path=../period-saathi-release.jks
keystore.password=YOUR_KEYSTORE_PASSWORD
key.alias=period-saathi-key
key.password=YOUR_KEY_PASSWORD
```

## Backup Checklist
- [ ] Keystore saved to encrypted USB drive
- [ ] Keystore uploaded to personal Google Drive (encrypted ZIP)
- [ ] Passwords saved in password manager
- [ ] `local.properties` NEVER committed to git (verify in `.gitignore`)

## For Play App Signing (Recommended)
Upload your keystore to Google Play App Signing.
Google will manage signing for you after initial upload.
This protects against keystore loss going forward.

Instructions: Play Console → Setup → App integrity → App signing

## Build Release AAB
```bash
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

## Upload to Play Console
1. Play Console → Your App → Production → Create new release
2. Upload AAB file
3. Add release notes (English + Hindi minimum)
4. Review and roll out
```
```

---

## AFTER COMPLETION
Run: `./gradlew bundleRelease`
Verify AAB size: `dir app\build\outputs\bundle\release\`
Write the report.
