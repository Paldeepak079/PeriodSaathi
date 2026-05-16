# S03 — BACKEND API SPEC (SUPABASE)

> **Prerequisites:** S00 active · S01–S02 complete
> **Output file:** `docs/BACKEND_SPEC.md`
> **Build check:** No Gradle check. Verify SQL is valid and Edge Function logic is sound.

---

## REPORT INSTRUCTION
After completing this section, create:
`masterprompts/reports/S03_report.md`

Include:
- 📄 File created: `docs/BACKEND_SPEC.md` (yes/no)
- 🗄️ Tables defined: profiles, cycle_entries, wellness_logs, journal_entries, user_settings, purchases (all 6)
- 🔒 RLS policies written for all tables (yes/no)
- ⚡ Edge functions defined: verify-razorpay-payment, batch-sync, export-user-data, delete-account (all 4)
- 📱 Android Supabase client setup documented (yes/no)
- ⚠️ Any security considerations noted

---

## PROMPT

Write the complete Supabase backend specification.

**Create:** `docs/BACKEND_SPEC.md`

---

### SECTION 1: SUPABASE TABLE DEFINITIONS

Write exact SQL `CREATE TABLE` statements for all 6 tables:

**TABLE: `profiles`**
- id: UUID PRIMARY KEY REFERENCES auth.users(id)
- username: TEXT NOT NULL
- created_at / updated_at: TIMESTAMPTZ DEFAULT NOW()
- premium_tier: TEXT DEFAULT 'FREE' — FREE/PREMIUM
- premium_purchased_at: TIMESTAMPTZ
- razorpay_order_id: TEXT
- cycle_length_avg: INT DEFAULT 28
- period_length_avg: INT DEFAULT 5
- selected_theme: TEXT DEFAULT 'DEFAULT'
- language: TEXT DEFAULT 'en'

**TABLE: `cycle_entries`**
- id: UUID PRIMARY KEY DEFAULT gen_random_uuid()
- user_id: UUID REFERENCES profiles(id) ON DELETE CASCADE
- date: DATE NOT NULL
- flow_intensity: INT (0=none, 1=spot, 2=light, 3=medium, 4=heavy, 5=very heavy)
- symptoms: TEXT[] — array of symptom strings
- mood: TEXT
- water_glasses: INT DEFAULT 0
- notes: TEXT
- is_rest_day, is_period_start, is_period_end: BOOLEAN DEFAULT FALSE
- updated_at / created_at: TIMESTAMPTZ
- UNIQUE(user_id, date)

**TABLE: `wellness_logs`**
- id, user_id, date, habits_completed: TEXT[], sleep_hours: DECIMAL(4,1), exercise_minutes: INT, medicine_taken: BOOLEAN, updated_at
- UNIQUE(user_id, date)

**TABLE: `journal_entries`**
- id, user_id, date, content: TEXT (encrypted client-side before upload), mood_emojis: TEXT[], cycle_day: INT, cycle_phase: TEXT, is_time_capsule: BOOLEAN, capsule_reveal_date: DATE, is_revealed: BOOLEAN, updated_at

**TABLE: `user_settings`**
- user_id: UUID PRIMARY KEY, notification_prefs: JSONB, fertility_mode, contraception_mode, stealth_mode_enabled: BOOLEAN, streak_count: INT, total_points: INT, last_sync_at: TIMESTAMPTZ, updated_at

**TABLE: `purchases`**
- id: UUID PRIMARY KEY, user_id, razorpay_payment_id: TEXT UNIQUE, razorpay_order_id: TEXT, product_id: TEXT ('THEMES_PACK'/'AD_FREE'/'PREMIUM_BUNDLE'), amount: INT (paise), currency: TEXT DEFAULT 'INR', status: TEXT ('PENDING'/'COMPLETED'/'FAILED'/'REFUNDED'), purchased_at

---

### SECTION 2: ROW LEVEL SECURITY (RLS) POLICIES

Write complete SQL for:
- `ALTER TABLE ... ENABLE ROW LEVEL SECURITY` for all 5 user tables
- Policy: "Users own their [table]" — FOR ALL USING (auth.uid() = user_id)
- Purchases: separate INSERT policy and SELECT policy
- Purchases: NO UPDATE/DELETE by user (admin only)
- Include explanation of why each policy is structured this way

---

### SECTION 3: SUPABASE EDGE FUNCTIONS

Write TypeScript pseudocode + logic for all 4 functions:

**Function 1: `verify-razorpay-payment`**
- POST /functions/v1/verify-razorpay-payment
- Input: { razorpay_order_id, razorpay_payment_id, razorpay_signature, user_id, product_id }
- Logic: HMAC verify → update purchases table → update profiles.premium_tier
- Auth: Requires valid Supabase JWT
- Returns: { success: true, premium_tier: "PREMIUM" }

**Function 2: `batch-sync`**
- POST /functions/v1/batch-sync
- Input: { operations: [{operation, table, id, payload, updated_at}] }
- Logic: verify JWT → extract user_id → upsert if updated_at > existing → skip if older
- Rate limit: 100 operations per request
- Returns: { synced: N, skipped: M, failed: [] }

**Function 3: `export-user-data`**
- GET /functions/v1/export-user-data
- Logic: Export all tables for user as JSON → sign URL (10 min expiry)
- Returns: { download_url: "..." }

**Function 4: `delete-account`**
- DELETE /functions/v1/delete-account
- Logic: Cascade delete all user data + auth.users record
- Returns: { success: true }

---

### SECTION 4: ANDROID SUPABASE CLIENT SETUP

Document exact Gradle dependencies:
```kotlin
implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
implementation("io.github.jan-tennert.supabase:gotrue-kt")
implementation("io.github.jan-tennert.supabase:postgrest-kt")
implementation("io.github.jan-tennert.supabase:realtime-kt:3.0.0")
implementation("io.ktor:ktor-client-android:2.3.12")
```

Document `SupabaseClient.kt` setup:
- @Singleton Hilt-provided SupabaseClient
- URL from BuildConfig.SUPABASE_URL
- KEY from BuildConfig.SUPABASE_ANON_KEY
- Custom HTTP client with certificate pinning
- Session persisted in EncryptedSharedPreferences

---

### SECTION 5: SYNC IMPLEMENTATION DETAILS

Document:
- SyncWorker trigger conditions (NetworkConstraint + Periodic 15min)
- Batch size: 50 operations
- Rate limit handling: 429 → exponential backoff (1min, 2min, 4min)
- Auth error: 401 → notify user to re-login
- Timeout: 30 seconds per batch
- Pull-on-login sync: fetch all entries updated_at > last_sync_at → merge into Room

Write the complete backend specification document.
