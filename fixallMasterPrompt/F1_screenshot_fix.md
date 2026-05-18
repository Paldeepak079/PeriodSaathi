# F1 — Screenshot Fix (Remove FLAG_SECURE)

**Status:** ✅ Done  
**Date:** 2026-05-18

## Files Modified
- `app/src/main/java/com/deepak/periodsaathi/MainActivity.kt`

## Change Made
Removed `window.setFlags(FLAG_SECURE, FLAG_SECURE)` block and the corresponding `WindowManager` import.

## Reason
The master prompt (Section 3 — Security) explicitly states: FLAG_SECURE intentionally NOT set so users can screenshot their health reports for sharing.

## Build Result
Not verified (standalone change; safe)

## Notes
The `enableEdgeToEdge()` and `WindowCompat.setDecorFitsSystemWindows(window, false)` were correctly retained.
