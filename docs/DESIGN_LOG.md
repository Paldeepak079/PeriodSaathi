# Design Log

## Period Saathi — Design Decisions

### FINAL DECISIONS

- **S06-2:** Screen sealed class uses `@Serializable` via kotlinx.serialization
- **S06-3:** Bottom bar = 5 tabs directly in NavGraph, no separate composable wrapper
- **S06-4:** Drawer removed, deep link stubs at bottom
- **S07-1:** All 15 components accept `Modifier` as first param
- **S07-2:** Mascot: Canvas-based `drawCircle` + `drawPath` (no Lottie)
- **S07-4:** GlassCard (rounded-corner wrapper ONLY) and `GlassCard` (clickable) coexist via named imports
- **S07-8:** FlipCard requires `isFlipped` + `onFlip` (controlled from parent)
- **S07-9:** BreathingCircle = 48dp circle, scale 1.0↔1.15, 4s cycle
- **S08:** All lazy layouts require `key` param
- **S08:** All LazyColumn items ARE wrapped in `GlassCard(shape = RoundedCornerShape(16.dp))` and NON-clickable
- **S08-7:** YogaScreen shows a list of yoga poses
- **S08-17:** No more OnboardingScreen — users can skip
- **S09:** Prediction algorithm uses weighted moving average (4-cycles)
- **S10-1:** Challenge model has `pointsReward: Int`
- **S10-2:** MonthlyWrappedWorker aggregates stats from Room DB
- **S11:** Glance widgets read Room DB via `RoomDatabaseBuilder.build()` in `backgroundTask()`
- **S12-2:** Stealth mode detection = `PackageManager.getLaunchIntentForPackage` check
- **S15:** Crashes go to Firebase Crashlytics (not sentry)
- **S17:** App Privacy section written to docs/privacy.md
