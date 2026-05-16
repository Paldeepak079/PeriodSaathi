# S00 — SYSTEM CONTEXT (Paste in EVERY new session)

> **Model targets:** DeepSeek V4 Flash · Minimax 2.0 · Claude Sonnet 4.6 · Codex
> **Paste this block FIRST before any other prompt in every session.**
> **Do NOT skip. Do NOT reorder.**

---

## REPORT INSTRUCTION
After completing this section, create a file:
`masterprompts/reports/S00_report.md`
with the following sections:
- ✅ Context loaded successfully (yes/no)
- 📋 Rules acknowledged (list the 13 CRITICAL RULES)
- 🎨 Design tokens confirmed (list primary colors)
- ⚠️ Any clarifications needed

---

## SYSTEM CONTEXT BLOCK

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

## VALIDATION
After pasting and the AI acknowledges this context, type:
> "Context confirmed. Proceed to S01."
