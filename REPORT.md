# Period Saathi Master Prompt Analysis Report

**Date:** 2026-07-05  
**Analyzed file:** `PeriodSaathi_MasterPrompt.md`  
**Comparison scope:** Generated docs, Gradle config, Android source, manifest, payment/sync/security code, and a debug build attempt.

## Executive Summary

`PeriodSaathi_MasterPrompt.md` is comprehensive, but it is not a reliable production source of truth in its current form. It mixes product requirements, architecture, code-generation prompts, compliance rules, release checklists, and aspirational implementation claims. The result is useful as a vision document, but unsafe as an execution contract without consolidation.

The largest gaps are:

- The prompt says the app is `com.periodsaathi.app`, Razorpay-based, SQLCipher-encrypted, offline-sync capable, and `collectAsStateWithLifecycle` compliant. The current app is `com.deepak.periodsaathi`, uses Stripe, does not wire SQLCipher into Room, has no `PendingSync` implementation, and uses `collectAsState()` widely.
- Several "never violate" rules are currently violated: "late" language exists in health/chat flows, notification permission is requested during onboarding, empty click handlers remain, `FLAG_SECURE` is not enabled, and most composables do not have previews.
- The generated docs and implementation have drifted from the master prompt: TRD/architecture still mention Razorpay/Supabase/PendingSync while code implements Stripe and local-only/no-op sync.
- The debug build did not complete. `./gradlew.bat assembleDebug` failed because the Gradle daemon ran out of native memory while compiling.

## Critical Findings

### 1. Source-of-Truth Drift

| Area | Master prompt says | Current repo evidence | Impact |
|---|---|---|---|
| Package | `com.periodsaathi.app` | `app/build.gradle.kts` uses `com.deepak.periodsaathi` | Backend, OAuth, notifications, intent actions, docs, and Play Store setup can be misconfigured. |
| Target SDK | Target SDK 36 | App uses compile/target SDK 35 | Release checklist and build instructions are inconsistent. |
| Payment | Razorpay one-time purchase | Stripe SDK, `StripePaymentService`, Stripe UI copy | Payment spec, backend spec, release checklist, and actual app disagree. |
| Database encryption | Room + SQLCipher encrypted at rest | Room builder does not use `SupportFactory`; `PeriodSaathiDatabase` is plain Room | Health/journal data encryption claim is inaccurate. |
| Offline sync | `PendingSyncDao` + batch Supabase sync | No `PendingSyncEntity`/DAO in app; `SyncWorker` only logs prediction info | Optional account/sync claims are incomplete. |
| Navigation | Navigation3/type-safe | Uses Navigation Compose typed routes, not Navigation3 | Prompt should describe actual stack or migration plan. |
| State collection | Never use `collectAsState` | Many UI screens use `collectAsState()` | Violates lifecycle safety rule. |

### 2. Security and Privacy Gaps

- **SQLCipher is not active.** `PeriodSaathiDatabase.getInstance()` builds Room without a SQLCipher `SupportFactory`; `WellnessDatabase` is also a plain Room database.
- **`FLAG_SECURE` is missing.** `MainActivity` sets `FLAG_KEEP_SCREEN_ON`, but not `FLAG_SECURE`.
- **Certificate pinning is a stub.** `CertificatePinner.getPinnerBuilder()` returns `null`, and hashes are placeholders.
- **Tracked credential files exist.** Two Google OAuth client secret JSON files under `docs/` are tracked by Git. `local.properties` is ignored but contains sensitive local keys/passwords and should not be copied into reports, screenshots, or commits.
- **Privacy/legal flow is incomplete.** Docs exist, but first-launch DPDP consent and first-launch medical disclaimer are not implemented as required by the prompt.
- **Notification permission is requested in onboarding.** The prompt says permission must be contextual and not requested on first launch.

### 3. Supabase Spec Issues

The Supabase backend spec is useful but incomplete and risky:

- `batch-sync` accepts a client-provided table name; this must be whitelisted server-side.
- Payment verification in `docs/BACKEND_SPEC.md` records `amount: 0`, so it is not auditable.
- The function examples do not show replay protection/idempotency for payment verification.
- RLS policies should explicitly target `TO authenticated` and retain ownership predicates.
- `SECURITY DEFINER` trigger functions should have explicit `REVOKE EXECUTE FROM PUBLIC` where applicable and must be reviewed with Supabase advisors.
- The current Android code calls `create-payment-intent`, while the backend spec defines Razorpay `verify-razorpay-payment`.

### 4. Functional Gaps Against Requirements

- Prediction rules are inconsistent. The prompt requires no predictions before 3 full cycles and support for 1-120 day cycles. `CyclePredictionEngine` clamps cycles to 21-45 days and can return predictions with insufficient data; `CycleRepository.predictNextPeriod()` also returns a default future prediction with 0 confidence.
- "Late" language appears in `HealthQueryEngine`, `ChatViewModel`, `ChatScreen`, and Home health chips, violating the prompt's copy rule.
- Calendar still contains hardcoded prediction values such as `"28 Mar"` and `"13 Days"`, plus empty predictor/rest-day/accuracy click handlers.
- Partner mode has implemented screens, but much of the partner flow is simulated/local and not aligned with the prompt's strict "share intent only, no health data" positioning.
- Widgets now read Room, but countdown logic ignores the 3-cycle prediction gate and can show period countdown from settings/default averages.
- Settings constrains cycle length to 21-45 and period length to 2-10, while the TRD says cycle length can be 1-120 and bleeding days should not have an enforced upper cap.

### 5. UI/Architecture Compliance Gaps

- Only 5 `@Preview` annotations were found across screens/components, far below the prompt's "every composable" requirement.
- Empty click handlers remain in Calendar, Time Capsule, and `RecipeCard`.
- Most user-visible strings are hardcoded in Compose files instead of Android string resources.
- Animations use many `tween` specs even though the prompt says spring physics for user-facing animations.
- `collectAsState()` is used broadly instead of `collectAsStateWithLifecycle()`.
- Several docs still reference generated paths like `com.periodsaathi.app/...`, while actual code uses `com.deepak.periodsaathi/...`.

### 6. Build Verification

`./gradlew.bat assembleDebug` was run twice:

- First run timed out after 2 minutes.
- Second run failed after the Gradle daemon disappeared.
- The JVM crash log reports insufficient native memory: `Native memory allocation (mmap) failed` with `-Xmx4096m`.

This means the current report cannot certify compile success. A follow-up build should run with reduced Gradle memory pressure, fewer daemon processes, and possibly `--no-daemon`.

## Prompt-Level Gaps and Incompleteness

1. **The file needs a canonical priority model.** P0/P1/P2 appears in the TRD, then the generated implementation asks for many P2/P3 items as if they are launch blockers.
2. **It does not define acceptance tests per feature.** The final checklist is broad, but requirements like "no prediction before 3 cycles" need specific tests and expected fixture data.
3. **It over-requires previews and KDoc without enforcement.** If these are mandatory, add Detekt/custom lint rules or CI checks.
4. **It lacks a migration strategy for generated code drift.** Existing code is Stripe-based and `com.deepak.periodsaathi`; the prompt still regenerates Razorpay and `com.periodsaathi.app`.
5. **It says "complete files" but omits exact integration boundaries.** Many sections produce independent files without saying how to delete/replace old duplicates.
6. **It treats security controls as complete by file creation.** SQLCipher, certificate pinning, DPDP consent, and export/delete rights need runtime verification steps, not just files.
7. **Backend and mobile contracts are incomplete.** DTOs, auth token handling, sync conflict payloads, Edge Function deployment, and local/remote schema mapping are underspecified.

## Recommendations

1. Split `PeriodSaathi_MasterPrompt.md` into four source-controlled documents:
   - `docs/PRODUCT_REQUIREMENTS.md`
   - `docs/TECHNICAL_SPEC.md`
   - `docs/IMPLEMENTATION_PLAN.md`
   - `docs/RELEASE_READINESS.md`
2. Choose one payment provider and delete the other path from prompt, docs, code, release checklist, and local properties. Current code is Stripe; current prompt is Razorpay.
3. Choose one package/application ID and update OAuth, notification actions, backend docs, manifest, and Play Store docs.
4. Implement SQLCipher in the actual Room builders or remove encryption-at-rest claims until implemented.
5. Replace `collectAsState()` with `collectAsStateWithLifecycle()` across app screens.
6. Remove "late/overdue/missed" UX copy and rename internal user-facing query labels.
7. Add `PendingSyncEntity`, `PendingSyncDao`, and real Supabase sync only if optional sync remains in scope.
8. Add a CI check for forbidden strings, empty click handlers, location permission, subscriptions, and `collectAsState(`.
9. Move all user-visible copy to string resources before localization work.
10. Rotate any exposed OAuth/secret material and remove tracked credential JSON files from Git history if they are real credentials.

## Phased Implementation Plan

### Phase 0: Stabilize Build and Source of Truth

- Reduce Gradle memory pressure and get `assembleDebug` passing.
- Decide canonical package ID, target SDK, payment provider, and backend provider.
- Update the master prompt and generated docs to match those decisions.
- Remove or quarantine tracked credential files.

### Phase 1: Ship-Blocker Privacy and Safety

- Wire SQLCipher into `PeriodSaathiDatabase`, `WellnessDatabase`, and `PartnerDatabase`.
- Enable `FLAG_SECURE` where required.
- Replace certificate pinning stub with a real implementation or remove the claim.
- Add first-launch DPDP consent and medical disclaimer.
- Remove onboarding notification permission request; request it only from reminder creation.

### Phase 2: Core Product Rule Compliance

- Enforce no predictions before 3 full manually ended cycles.
- Support the documented cycle range or update requirements to the actual supported range.
- Remove all "late/overdue/missed" user-facing language.
- Fix Calendar hardcoded predictions and empty click handlers.
- Replace broad `collectAsState()` usage with lifecycle-aware collection.

### Phase 3: Data and Sync Integrity

- Implement `PendingSyncEntity`/DAO and a real `SyncWorker`, or explicitly mark Supabase sync as not implemented.
- Align Supabase Edge Functions with the chosen payment provider.
- Whitelist sync tables, add idempotency, and verify RLS with advisors.
- Add import/export/delete-all tests for local and account-backed users.

### Phase 4: UX Completion and Accessibility

- Convert hardcoded UI text to resources and add missing Hindi/localized strings.
- Add missing previews or relax the "every composable" requirement to "public screen/component previews."
- Add TalkBack labels, semantic roles, live regions, and font-scale checks.
- Replace remaining empty click handlers and simulated partner/payment flows.

### Phase 5: Release Readiness

- Run `assembleDebug`, unit tests, lint, Detekt, and release build.
- Verify Play Store Data Safety against actual runtime behavior.
- Validate purchases end to end with production-mode provider sandbox/live test keys.
- Run offline-mode, restore, widget update, and data deletion acceptance tests.

## Updated Status

The app is not build-ready or release-ready. The master prompt is a strong starting artifact, but it must be corrected into an executable source of truth before more code generation, because the current prompt would keep regenerating features that conflict with the codebase.
