# Period Saathi — Release Checklist

## PRE-RELEASE: Code Quality
- [ ] versionCode incremented
- [ ] versionName updated (semantic versioning: 1.0.0)
- [ ] All Log.d/v wrapped in if (BuildConfig.DEBUG)
- [ ] No hardcoded API keys in source code
- [ ] google-services.json is production instance
- [ ] No TODO or FIXME comments in shipped code
- [ ] ./gradlew detekt — zero violations
- [ ] ./gradlew lint — zero errors

## PRE-RELEASE: Feature Verification
- [ ] Medical disclaimer shown on first launch
- [ ] Analytics starts DISABLED (requires opt-in)
- [ ] All notifications start DISABLED
- [ ] "Continue without account" works fully offline
- [ ] Period logging works without internet
- [ ] Prediction shows ONLY after 3+ cycles
- [ ] NO "late" or "overdue" text anywhere

## PRE-RELEASE: Device Testing
- [ ] Tested on Android 7.0 (API 24) — minimum SDK
- [ ] Tested on Android 14 (API 34) — target
- [ ] No crash in 30 minutes of normal usage
- [ ] Offline mode: airplane mode → all core features work

## PRE-RELEASE: Play Store Assets
- [ ] Feature graphic created: 1024×500px PNG
- [ ] App icon: 512×512px PNG
- [ ] Screenshots: Home, Calendar, Wellness, Partner Mode
- [ ] Short description: exactly 80 chars, reviewed
- [ ] Privacy policy URL: live and accessible

## PRE-RELEASE: Security
- [ ] FLAG_SECURE on all windows
- [ ] No health data in Logcat
- [ ] ProGuard tested

## STAGED ROLLOUT PLAN

| Week | Action | Gate Condition |
|------|--------|----------------|
| Week 1 | Internal testing | 0 crash reports |
| Week 2 | Closed alpha (50 testers) | Crash-free rate > 99% |
| Week 3 | Open beta | Rating feedback > 3.5 avg |
| Week 4 | Production — 10% rollout | Crash-free rate > 99.5% |
| Week 5 | 25% rollout | ANR rate < 0.1% |
| Week 6 | 50% rollout | Day 1 retention > 40% |
| Week 7 | 100% rollout | Day 7 retention > 25% |

## TARGET METRICS

| Metric | Target |
|--------|--------|
| Crash-free users | > 99.5% |
| ANR rate | < 0.1% |
| Play Store rating | > 4.0 |
| Day 1 retention | > 40% |
| Day 7 retention | > 25% |
| Install size | < 25 MB |