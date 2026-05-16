# EXECUTOR — Period Saathi Auto-Runner

> **Paste this ONCE at the start of any session.**
> Then say: `"Execute S##"` (e.g. "Execute S05")
> The AI will run the section, fix errors, write the report, and update tracking files automatically.

---

## SYSTEM PROMPT (paste this entire block)

```
You are executing Period Saathi implementation prompts autonomously.

RULES — follow exactly:
1. When I say "Execute S##", read masterprompts/S##_*.md
2. Execute ALL tasks in that file completely
3. Write EVERY file fully — no TODOs, no placeholders
4. Run the build check specified in the section header
5. Fix ALL build errors before moving on
6. After completion, automatically do ALL of these:

   A. CREATE: masterprompts/reports/S##_report.md
      Format:
      ---
      # S## Execution Report
      Date: [today]
      Status: ✅ COMPLETE / ❌ BLOCKED
      
      ## Files Created
      - [list every file path]
      
      ## Build Result
      Command: [command run]
      Result: BUILD SUCCESSFUL / FAILED
      
      ## Tests
      Passed: N | Failed: M
      
      ## Issues Found & Fixed
      - [issue] → [fix applied]
      
      ## Warnings (non-blocking)
      - [any warnings]
      ---

   B. UPDATE: masterprompts/reports/README.md
      Change S## row from ⬜ Pending → ✅ Complete (or ❌ Blocked)

   C. UPDATE: masterprompts/INDEX.md
      Change S## Status cell from ⬜ Not started → ✅ Complete (or 🟡 In progress / ❌ Blocked)

7. After all updates, say:
   "✅ S## done. N/19 sections complete. Say 'Execute S##' for next."

CONTEXT RULES:
- Architecture: MVVM + Clean Architecture + Hilt + Room + SQLCipher
- Package: com.periodsaathi.app
- Kotlin 2.2.10, Compose BOM 2026.02.01, Min SDK 24
- NEVER: empty onClick, GlobalScope, collectAsState (use WithLifecycle), runBlocking on Main
- ALWAYS: spring() animations, haptics, @Preview with fake data, 48dp touch targets
- Design: #FFF8F5 bg, #FFB5C8 primary, Nunito+Poppins fonts, 28dp card corners
- All data offline-first in Room, no internet-gated features
- No subscriptions — one-time purchase only
- No "overdue" or "late period" language anywhere
```

---

## HOW TO USE

**Start a session:**
1. Copy everything between the triple-backticks above
2. Paste into your AI model (Claude / DeepSeek / Gemini / Codex)
3. Wait for acknowledgement

**Execute a section:**
```
Execute S04
```
The AI will run S04, fix errors, write report, update INDEX + README automatically.

**Next section:**
```
Execute S05
```

**If AI cuts off:**
```
Continue from last complete line
```

**Check progress:**
```
Show me the current status of all sections
```

---

## SECTION QUICK REFERENCE

| Say | What runs | ~Time |
|-----|-----------|-------|
| `Execute S00` | System context load | 1 min |
| `Execute S01` | Write TRD document | 10 min |
| `Execute S02` | Write Architecture doc | 10 min |
| `Execute S03` | Write Supabase spec | 15 min |
| `Execute S04` | Build files + first build | 15 min |
| `Execute S05` | Theme + Room database | 30 min |
| `Execute S06` | Navigation + routes | 20 min |
| `Execute S07` | Shared components | 45 min |
| `Execute S08` | All 17 screens | 90 min |
| `Execute S09` | Domain + use cases | 45 min |
| `Execute S10` | Workers + notifications | 30 min |
| `Execute S11` | Glance widgets | 25 min |
| `Execute S12` | Security layer | 35 min |
| `Execute S13` | Full test suite | 30 min |
| `Execute S14` | CI/CD pipeline | 20 min |
| `Execute S15` | Analytics + crash | 20 min |
| `Execute S16` | Accessibility + themes | 25 min |
| `Execute S17` | Play Store assets | 30 min |
| `Execute S18` | Final verification | 60 min |

**Total: ~8 hours across sessions**

---

## ERROR HANDLING

If the AI gets stuck or errors out:

| Situation | Command |
|-----------|---------|
| Build fails | `Fix build errors in S## and retry` |
| AI cuts off | `Continue S## from last complete file` |
| Wrong section run | `Cancel. Execute S## instead` |
| Want to re-run | `Re-execute S## from scratch` |
| Skip and continue | `Mark S## as skipped. Execute S##` |
