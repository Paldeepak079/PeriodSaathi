# S18 — FINAL INTEGRATION CHECKLIST

> **Prerequisites:** S00–S17 ALL complete · CI green · `bundleRelease` passing
> **This is the final verification pass. Do NOT skip any check.**
> **Output:** Fix any found issues + write final build report.

---

## REPORT INSTRUCTION
After completing this checklist, create:
`masterprompts/reports/S18_FINAL_REPORT.md`

This is the MASTER REPORT. Include:
- 🏁 Overall status: **BUILD READY ✅** or **BLOCKERS FOUND ❌**
- 📊 All checklist sections with PASS/FAIL per item
- 🔨 Build commands run (with output summary)
- 🧪 Test results: N passed / M failed
- 📦 Release AAB: size + SHA256 hash
- 🐛 All issues found and how they were resolved
- ⚠️ Known warnings (not blocking)
- 📋 Remaining work (if any)

---

## PROMPT

Perform the complete final integration verification.
Run EVERY check. Fix ALL failures. Report everything.

---

## ══════════════════════════════════════
## COMPILATION CHECKS
## ══════════════════════════════════════

Run and fix:
```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lint
./gradlew detekt
```

All 4 must pass with:
- `assembleDebug`: BUILD SUCCESSFUL (zero errors)
- `testDebugUnitTest`: zero test failures
- `lint`: zero errors (warnings OK — list them)
- `detekt`: zero violations

If any fail: fix before proceeding to next check.

**Missing files — create if absent:**
- [ ] `PeriodSaathiApp.kt` (`@HiltAndroidApp`, channels, workers init)
- [ ] `res/xml/network_security_config.xml`
- [ ] `res/xml/backup_rules.xml`
- [ ] `res/xml/data_extraction_rules.xml`
- [ ] `res/xml/file_paths.xml` (for FileProvider bug reports)
- [ ] `res/values/strings.xml` (ALL user-visible strings, zero hardcoding)
- [ ] `res/values-hi/strings.xml` (Hindi translations for all strings)
- [ ] `res/drawable/ic_notification.xml` (white, 24dp, single-path vector)
- [ ] `res/drawable/ic_mascot_happy.xml` (mascot vector for widget)
- [ ] `res/drawable/ic_stealth_notes.xml` (notes-style icon for stealth mode)

For any missing file found above, write the complete file immediately before continuing.

---

## ══════════════════════════════════════
## NAVIGATION CHECKS
## ══════════════════════════════════════

- [ ] Every Screen route registered in `NavGraph.kt`
- [ ] Every `composable<T>` has a corresponding `@Serializable` class
- [ ] Back navigation works on every screen (test: system back button)
- [ ] Deep links work (`adb shell am start -W -a android.intent.action.VIEW -d "periodsaathi://home"`)
- [ ] Bottom nav: all 4 tabs navigate correctly
- [ ] `launchSingleTop = true` on all bottom nav navigations
- [ ] `NavGraph` registers: Splash, Onboarding, Login, NameSetup, Home, Calendar, Wellness, PartnerMode, Remedies, YogaFlow, Journal, MoodMap, Insights, Settings, ReportExport, Wardrobe, Challenges, BreathingMode, Payment, DayLog

---

## ══════════════════════════════════════
## BUTTON / INTERACTION CHECKS
## ══════════════════════════════════════

Run grep to find empty lambdas — MUST BE ZERO:
```bash
# Should return zero results:
grep -r "onClick = {}" app/src/main/java/ --include="*.kt" | grep -v "// intentional"
grep -r "onClick = { }" app/src/main/java/ --include="*.kt" | grep -v "// intentional"
```

- [ ] Every Button, IconButton, clickable Modifier has a real `onClick` handler
- [ ] Every tap produces: visual feedback (scale bounce) + haptic
- [ ] Loading buttons show `CircularProgressIndicator` during async ops
- [ ] Disabled buttons are visually distinct (50% opacity) AND not clickable

---

## ══════════════════════════════════════
## STATE MANAGEMENT CHECKS
## ══════════════════════════════════════

Run grep checks:
```bash
# Must return 0 results:
grep -r "GlobalScope" app/src/main/ --include="*.kt"
grep -r "collectAsState()" app/src/main/ --include="*.kt"  # must use collectAsStateWithLifecycle
grep -r "runBlocking" app/src/main/java/ --include="*.kt" | grep -v "// intentional"
```

- [ ] Every ViewModel: `@HiltViewModel + @Inject constructor`
- [ ] All StateFlows initialized with default values (not null)
- [ ] All `collectAsStateWithLifecycle` (never plain `collectAsState`)
- [ ] All Room queries on `Dispatchers.IO`
- [ ] No `GlobalScope` usage anywhere
- [ ] No `runBlocking` on Main thread

---

## ══════════════════════════════════════
## DATABASE CHECKS
## ══════════════════════════════════════

- [ ] SQLCipher properly initialized in `PeriodSaathiDatabase.create()`
- [ ] All Entities have `@Entity` annotation
- [ ] All DAOs have `@Dao` annotation
- [ ] TypeConverters registered in `@Database`
- [ ] Room schema exported to `app/schemas/` folder (`ksp.arg("room.schemaLocation", ...)`)
- [ ] `PendingSyncEntity` used for ALL write operations (verify in `CycleRepositoryImpl`)
- [ ] Database version: 1 (initial release)

---

## ══════════════════════════════════════
## ANIMATION CHECKS
## ══════════════════════════════════════

Run grep for banned animations:
```bash
# Must return 0 results for LinearEasing on user-facing animations:
grep -r "LinearEasing" app/src/main/java/ --include="*.kt"
```

- [ ] No `LinearEasing` on user-facing animations (spring physics everywhere)
- [ ] `animateFloatAsState` with `spring()` for scale animations
- [ ] `animateColorAsState` with `spring()` for color transitions
- [ ] Entry animations: staggered 80ms, alpha + translateY from Animatable
- [ ] `ConfettiOverlay` triggers on: water goal, habit completion, reward unlock
- [ ] Page transitions: `slideInHorizontally` with spring spec

---

## ══════════════════════════════════════
## SECURITY CHECKS
## ══════════════════════════════════════

- [ ] `FLAG_SECURE` set in `MainActivity.onCreate()` BEFORE `setContent {}`
- [ ] SQLCipher key generated on first launch, stored in `EncryptedSharedPreferences`
- [ ] No health data in Logcat: run `adb logcat | grep -i "period\|flow\|cramp\|bleed"` → should be empty
- [ ] No health data in crash reports (review `sanitizeForCrashlytics` logic)
- [ ] Biometric: only enabled if `isBiometricAvailable() == AVAILABLE`
- [ ] Certificate pinning: active in release build, disabled in debug
- [ ] `BuildConfig.DEBUG` gates ALL `Log.d/v` calls

---

## ══════════════════════════════════════
## OFFLINE FIRST CHECKS
## ══════════════════════════════════════

Test in airplane mode:
- [ ] Period logging: works offline ✈️
- [ ] Water tracking: works offline ✈️
- [ ] Habit tracking: works offline ✈️
- [ ] Calendar: loads from Room, no network required ✈️
- [ ] Journal: writes to encrypted Room, no network required ✈️
- [ ] Changes queued in `PendingSyncDao` when offline
- [ ] `SyncWorker` runs when network restored (test: re-enable WiFi)
- [ ] No screen shows error JUST because offline (all show data from Room)

---

## ══════════════════════════════════════
## BUSINESS RULE CHECKS
## ══════════════════════════════════════

Run these grep checks — ALL must return 0 results:
```bash
grep -r "overdue" app/src/main/res/values/ --include="*.xml"
grep -r "late period" app/src/main/res/values/ --include="*.xml"
grep -r "period is late" app/src/main/java/ --include="*.kt"
grep -r "subscription" app/src/main/java/ --include="*.kt" | grep -v "// No subscription"
grep -r "ACCESS_FINE_LOCATION\|ACCESS_COARSE_LOCATION" app/src/main/AndroidManifest.xml
```

- [ ] NO prediction shown until `cyclesLogged >= 3`
- [ ] Prediction always shows confidence badge (LOW/MEDIUM/HIGH)
- [ ] Period start: ONLY manual (tap day → confirm dialog)
- [ ] Period end: ONLY manual ("End period today" button)
- [ ] No "late" or "overdue" language anywhere in `strings.xml` or `values-hi/strings.xml`
- [ ] No location permission requested anywhere in manifest
- [ ] All notifications: require explicit opt-in
- [ ] Premium: one-time purchase ONLY (no `subscribeWith`, no recurring)
- [ ] Ads: banner only, NOT on calendar or logging screens
- [ ] Ads: not shown in first 7 days (grace period logic in AdManager)

---

## ══════════════════════════════════════
## ACCESSIBILITY CHECKS
## ══════════════════════════════════════

- [ ] TalkBack: navigate through Home screen with eyes closed (all elements announced)
- [ ] All interactive elements have non-null `contentDescription`
- [ ] Minimum 48dp touch targets: verify with Layout Inspector (View → Accessibility)
- [ ] Test with font size at 200%: no text clipped, no UI overlap
- [ ] Color contrast: body text is DarkNavy on CreamWhite (14:1 ratio)
- [ ] No color-only information: icons + color used together (not color alone)

---

## ══════════════════════════════════════
## RELEASE BUILD VERIFICATION
## ══════════════════════════════════════

```bash
./gradlew bundleRelease
```

After generating AAB:
- [ ] AAB file exists at `app/build/outputs/bundle/release/app-release.aab`
- [ ] AAB size < 30 MB (check: `dir app\build\outputs\bundle\release\`)
- [ ] Install release APK on device: `./gradlew installRelease` (requires signing configured)
- [ ] All features work in release build (ProGuard doesn't break Room, Hilt, etc.)
- [ ] No Logcat errors from ProGuard (missing keep rules)

---

## ══════════════════════════════════════
## FINAL QUALITY GATE
## ══════════════════════════════════════

After running ALL checks above, output exactly one of:

### If ALL checks pass:
```
BUILD READY ✅
————————————————————————————————
Period Saathi v1.0.0 — RELEASE CANDIDATE
All 8 check categories: PASSED
Unit tests: N passed, 0 failed
Lint: 0 errors, M warnings
Detekt: 0 violations
AAB size: X.X MB

Remaining warnings (non-blocking):
- [List any lint warnings here]

Ready for Play Store internal testing track.
Next step: S17 → Upload AAB to Play Console
```

### If ANY check fails:
```
BLOCKERS FOUND ❌
————————————————————————————————
Failed checks:
- [Category]: [Specific issue] → [How to fix]

Do NOT upload to Play Store until all blockers are resolved.
Fix blockers and re-run this checklist.
```

---

## CONGRATULATIONS 🎉

If you reach BUILD READY ✅, Period Saathi is ready for the world!

Follow the staged rollout plan in `docs/RELEASE_CHECKLIST.md`:
1. Internal testing (team)
2. Closed alpha (50 users)
3. Open beta
4. 10% production rollout
5. Gradual scale to 100%

**Saathi is ready to be everyone's period bestie! 🌸**
