# Period Saathi — Master Prompts Index

> **How to use:** Paste one section at a time into your AI model.
> Always start with **S00** in every new session.
> Never skip a section. Never reorder.

---

## 🗺️ EXECUTION ORDER

| # | File | Title | Files Created | Build Check | Status |
|---|------|-------|--------------|-------------|--------|
| S00 | `S00_system_context.md` | System Context | — | None | ⬜ Not started |
| S01 | `S01_technical_requirements.md` | Technical Requirements (TRD) | `docs/TRD.md` | None | ✅ Complete |
| S02 | `S02_architecture_plan.md` | Architecture Plan | `docs/ARCHITECTURE.md` | None | ✅ Complete |
| S03 | `S03_backend_spec.md` | Supabase Backend Spec | `docs/BACKEND_SPEC.md` | None | ✅ Complete |
| S04 | `S04_gradle_build_files.md` | Gradle Build Files | 5 build files | `assembleDebug` | ✅ Complete |
| S05 | `S05_foundation_db_theme.md` | Foundation: DB + Theme | 19 Kotlin files | `assembleDebug` | ✅ Complete |
| S06 | `S06_navigation.md` | Navigation (All Routes) | 4 Kotlin files | `assembleDebug` | ✅ Complete |
| S07 | `S07_shared_components.md` | Shared Components | 10 Kotlin files | `assembleDebug` | ✅ Complete |
| S08 | `S08_all_screens.md` | All 17 Screens | 34 Kotlin files | `assembleDebug` | ✅ Complete |
| S09 | `S09_domain_layer.md` | Domain Layer | 20 Kotlin files | `assembleDebug` + tests | ⬜ |
| S10 | `S10_gamification_workers.md` | Gamification + Workers | 7 Kotlin files | `assembleDebug` | ✅ Complete |
| S11 | `S11_widgets.md` | Glance Widgets | 3 Kotlin + 2 XML | `assembleDebug` | ✅ Complete |
| S12 | `S12_security.md` | Security Layer | 5 Kotlin + 2 XML | `assembleDebug` | ✅ Complete |
| S13 | `S13_testing.md` | Testing Suite | 7 test files | `testDebugUnitTest` | ✅ Complete |
| S14 | `S14_cicd_quality.md` | CI/CD + Quality | 5 config files | GitHub Actions | ✅ Complete |
| S15 | `S15_monitoring_analytics.md` | Monitoring + Analytics | 3 Kotlin files | `assembleDebug` | ✅ Complete |
| S16 | `S16_accessibility_themes.md` | Accessibility + Themes | 3 Kotlin files | `assembleDebug` | ✅ Complete |
| S17 | `S17_play_store_deployment.md` | Play Store Deployment | 6 docs files | `bundleRelease` | ✅ Complete |
| S18 | `S18_final_integration_checklist.md` | Final Integration | Fixes only | All checks | ✅ Complete |

Update status: ⬜ Not started → 🟡 In progress → ✅ Complete → ❌ Blocked

---

## 📁 REPORTS DIRECTORY

Each section generates an execution report at:
`masterprompts/reports/S##_report.md`

Final report: `masterprompts/reports/S18_FINAL_REPORT.md`

---

## 🤖 COMPATIBLE AI MODELS

These prompts are tested and designed for:
- **DeepSeek V4 Flash** — best for S04–S09 (code generation)
- **Minimax 2.0** — best for S07–S08 (UI components)
- **Claude Sonnet 4.6** — best for S01–S03, S13, S14 (planning + tests)
- **OpenAI Codex** — best for S05–S06 (structured code)
- **Gemini 2.5 Pro** — best for S08 (large screen generation)

---

## ⚡ QUICK START

### New Session (any AI model):
```
1. Open S00_system_context.md
2. Copy ENTIRE contents
3. Paste into AI model
4. Wait for "context acknowledged"
5. Open the target section (e.g. S05)
6. Copy ENTIRE contents
7. Paste into AI model
8. Let it generate → review → run build
9. Fix errors → write report → proceed to next section
```

### Resuming a Session:
```
1. Always re-paste S00 first (every new session/window)
2. Check which section you're on (look at reports/ folder)
3. Paste the next section
4. Continue from there
```

### If AI Output is Cut Off:
```
Say exactly: "Continue from the last complete code line in [FileName].kt"
The AI will resume from where it stopped.
```

---

## 📊 ESTIMATED TIME PER SECTION

| Section | Estimated Time | Notes |
|---------|---------------|-------|
| S00 | 1 min | Just paste, no generation |
| S01 | 10 min | Documentation writing |
| S02 | 10 min | Documentation writing |
| S03 | 15 min | SQL + TypeScript |
| S04 | 15 min | Gradle config |
| S05 | 30 min | 14 files, complex DB setup |
| S06 | 20 min | Navigation graph |
| S07 | 45 min | 10 components with Canvas |
| S08 | 90 min | **LARGEST** — 34 files, all screens |
| S09 | 45 min | Domain layer + prediction algorithm |
| S10 | 30 min | Workers + gamification |
| S11 | 25 min | Glance widgets |
| S12 | 35 min | Security + lock screens |
| S13 | 30 min | Test suite |
| S14 | 20 min | CI/CD config |
| S15 | 20 min | Analytics |
| S16 | 25 min | Accessibility |
| S17 | 30 min | Play Store docs |
| S18 | 60 min | Final verification + fixes |
| **Total** | **~8 hours** | Across multiple sessions |

---

## 🚨 CRITICAL REMINDERS

> **NEVER** use empty `onClick = {}` lambdas
> **NEVER** use `GlobalScope` or `runBlocking` on Main thread
> **NEVER** use `collectAsState()` — always `collectAsStateWithLifecycle()`
> **NEVER** show predictions before 3 cycles
> **NEVER** use "overdue" or "late period" language
> **NEVER** auto-end a period — manual only
> **NEVER** gated features (offline-first always)
> **NEVER** subscriptions — one-time purchase only

---

## 📞 SUPPORT

Project: Period Saathi
Repo: github.com/Paldeepak079/PeriodSaathi
Support: support@periodsaathi.app
