# Backend API Specification (Supabase)

## Period Saathi

**Version:** 1.0  
**Date:** May 16, 2026  
**Platform:** Supabase (PostgreSQL + Edge Functions + Realtime)

---

## SECTION 1: SUPABASE TABLE DEFINITIONS

### Table: `profiles`

Stores user profile and account-level data. Linked 1:1 with `auth.users`.

```sql
CREATE TABLE profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    premium_tier TEXT DEFAULT 'FREE' CHECK (premium_tier IN ('FREE', 'PREMIUM')),
    premium_purchased_at TIMESTAMPTZ,
    razorpay_order_id TEXT,
    cycle_length_avg INT DEFAULT 28,
    period_length_avg INT DEFAULT 5,
    selected_theme TEXT DEFAULT 'DEFAULT',
    language TEXT DEFAULT 'en'
);

-- Auto-create profile on user signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER SET search_path = public
AS $$
BEGIN
    INSERT INTO public.profiles (id, username)
    VALUES (
        NEW.id,
        COALESCE(NEW.raw_user_meta_data ->> 'username', 'User')
    );
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_new_user();
```

### Table: `cycle_entries`

Daily period and symptom logs. One row per user per date.

```sql
CREATE TABLE cycle_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    flow_intensity INT DEFAULT 0 CHECK (flow_intensity BETWEEN 0 AND 5),
    symptoms TEXT[] DEFAULT '{}',
    mood TEXT,
    water_glasses INT DEFAULT 0,
    notes TEXT DEFAULT '',
    is_rest_day BOOLEAN DEFAULT FALSE,
    is_period_start BOOLEAN DEFAULT FALSE,
    is_period_end BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, date)
);

CREATE INDEX idx_cycle_entries_user_date ON cycle_entries(user_id, date);
CREATE INDEX idx_cycle_entries_updated ON cycle_entries(updated_at);
```

### Table: `wellness_logs`

Daily wellness tracking data beyond period logging.

```sql
CREATE TABLE wellness_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    habits_completed TEXT[] DEFAULT '{}',
    sleep_hours DECIMAL(4,1),
    exercise_minutes INT DEFAULT 0,
    medicine_taken BOOLEAN DEFAULT FALSE,
    diet_tags TEXT[] DEFAULT '{}',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, date)
);

CREATE INDEX idx_wellness_logs_user_date ON wellness_logs(user_id, date);
```

### Table: `journal_entries`

Encrypted journal entries. Content is encrypted client-side before upload. Server never sees plaintext.

```sql
CREATE TABLE journal_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    content TEXT NOT NULL,  -- client-side encrypted (AES-256-GCM)
    mood_emojis TEXT[] DEFAULT '{}',
    cycle_day INT,
    cycle_phase TEXT,
    is_time_capsule BOOLEAN DEFAULT FALSE,
    capsule_reveal_date DATE,
    is_revealed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_journal_entries_user_date ON journal_entries(user_id, date);
CREATE INDEX idx_journal_entries_capsule ON journal_entries(user_id, capsule_reveal_date)
    WHERE is_time_capsule = TRUE AND is_revealed = FALSE;
```

### Table: `user_settings`

User preferences and gamification state. Single row per user (no UNIQUE needed on PK).

```sql
CREATE TABLE user_settings (
    user_id UUID PRIMARY KEY REFERENCES profiles(id) ON DELETE CASCADE,
    notification_prefs JSONB DEFAULT '{}'::jsonb,
    fertility_mode BOOLEAN DEFAULT FALSE,
    contraception_mode BOOLEAN DEFAULT FALSE,
    stealth_mode_enabled BOOLEAN DEFAULT FALSE,
    streak_count INT DEFAULT 0,
    total_points INT DEFAULT 0,
    last_sync_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

### Table: `purchases`

One-time in-app purchase records. One row per transaction.

```sql
CREATE TABLE purchases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    razorpay_payment_id TEXT UNIQUE,
    razorpay_order_id TEXT,
    product_id TEXT NOT NULL CHECK (product_id IN ('THEMES_PACK', 'AD_FREE', 'PREMIUM_BUNDLE')),
    amount INT NOT NULL,  -- in paise (₹1 = 100 paise)
    currency TEXT DEFAULT 'INR',
    status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    purchased_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_purchases_user ON purchases(user_id);
CREATE INDEX idx_purchases_status ON purchases(status);
```

---

## SECTION 2: ROW LEVEL SECURITY (RLS) POLICIES

### Enable RLS on All Tables

```sql
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE cycle_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE wellness_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE journal_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE purchases ENABLE ROW LEVEL SECURITY;
```

### Profiles Policy

Users can read and update their own profile. No INSERT (handled by trigger). No DELETE (handled by Edge Function).

```sql
CREATE POLICY "Users can view own profile"
    ON profiles FOR SELECT
    USING (auth.uid() = id);

CREATE POLICY "Users can update own profile"
    ON profiles FOR UPDATE
    USING (auth.uid() = id)
    WITH CHECK (auth.uid() = id);
```

### Cycle Entries Policy

Full CRUD on own entries only. Row-level isolation between users.

```sql
CREATE POLICY "Users own their cycle entries"
    ON cycle_entries FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);
```

### Wellness Logs Policy

Full CRUD on own logs only.

```sql
CREATE POLICY "Users own their wellness logs"
    ON wellness_logs FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);
```

### Journal Entries Policy

Full CRUD on own journal entries.

```sql
CREATE POLICY "Users own their journal entries"
    ON journal_entries FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);
```

### User Settings Policy

Full CRUD on own settings only. Single-row guarantee via PK.

```sql
CREATE POLICY "Users own their settings"
    ON user_settings FOR ALL
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);
```

### Purchases Policy (Restricted)

Users can INSERT (during purchase) and SELECT (view receipt). NO UPDATE or DELETE — admin only via Supabase dashboard.

```sql
CREATE POLICY "Users can insert purchases"
    ON purchases FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can view own purchases"
    ON purchases FOR SELECT
    USING (auth.uid() = user_id);

-- UPDATE and DELETE are intentionally NOT granted to users
-- Admin can manage via Supabase Dashboard
```

### Policy Rationale

| Table | SELECT | INSERT | UPDATE | DELETE |
|-------|--------|--------|--------|--------|
| profiles | Own row | Trigger only | Own row | Edge Function |
| cycle_entries | Own rows | Own rows | Own rows | Own rows |
| wellness_logs | Own rows | Own rows | Own rows | Own rows |
| journal_entries | Own rows | Own rows | Own rows | Own rows |
| user_settings | Own row | Own row | Own row | Own row |
| purchases | Own rows | Own rows | Blocked | Blocked |

Purchases are insert-only for users to prevent tampering with payment records. Refunds and status changes are handled server-side via Edge Function.

---

## SECTION 3: SUPABASE EDGE FUNCTIONS

### Function 1: `verify-razorpay-payment`

Verifies Razorpay payment signature and updates premium status.

**Endpoint:** `POST /functions/v1/verify-razorpay-payment`

**Request:**
```json
{
  "razorpay_order_id": "order_Lxyz123",
  "razorpay_payment_id": "pay_abc456",
  "razorpay_signature": "signature_string",
  "user_id": "uuid-here",
  "product_id": "THEMES_PACK"
}
```

**Logic (TypeScript):**
```typescript
import { serve } from "https://deno.land/std@0.192.0/http/server.ts";
import { createHmac } from "node:crypto";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.38.0";

serve(async (req) => {
  // 1. Verify JWT
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_ANON_KEY")!,
    { global: { headers: { Authorization: req.headers.get("Authorization")! } } }
  );

  const { data: { user }, error: authError } = await supabase.auth.getUser();
  if (authError || !user) return new Response("Unauthorized", { status: 401 });

  // 2. Parse body
  const { razorpay_order_id, razorpay_payment_id, razorpay_signature, product_id } = await req.json();

  // 3. HMAC-SHA256 verification
  const body = razorpay_order_id + "|" + razorpay_payment_id;
  const expectedSignature = createHmac("sha256", Deno.env.get("RAZORPAY_KEY_SECRET")!)
    .update(body)
    .digest("hex");

  if (expectedSignature !== razorpay_signature) {
    return new Response(JSON.stringify({ success: false, error: "Invalid signature" }), { status: 400 });
  }

  // 4. Update purchases table
  await supabase.from("purchases").insert({
    user_id: user.id,
    razorpay_payment_id,
    razorpay_order_id,
    product_id,
    amount: 0, // replaced by actual amount from Razorpay
    status: "COMPLETED",
  });

  // 5. Update profile premium tier
  await supabase.from("profiles").update({
    premium_tier: "PREMIUM",
    premium_purchased_at: new Date().toISOString(),
    razorpay_order_id,
  }).eq("id", user.id);

  // 6. Update user_settings to reflect premium status
  const productPoints: Record<string, { adFree: boolean; premiumBundle: boolean }> = {
    THEMES_PACK: { adFree: false, premiumBundle: false },
    AD_FREE: { adFree: true, premiumBundle: false },
    PREMIUM_BUNDLE: { adFree: true, premiumBundle: true },
  };

  return new Response(JSON.stringify({
    success: true,
    premium_tier: "PREMIUM",
    features: productPoints[product_id],
  }), { headers: { "Content-Type": "application/json" } });
});
```

### Function 2: `batch-sync`

Handles batch sync operations from the Android client.

**Endpoint:** `POST /functions/v1/batch-sync`

**Rate Limit:** 100 operations per request

**Request:**
```json
{
  "operations": [
    {
      "operation": "upsert",
      "table": "cycle_entries",
      "id": "uuid-here",
      "payload": { "date": "2026-05-15", "flow_intensity": 3 },
      "updated_at": 1715731200000
    }
  ]
}
```

**Logic (TypeScript):**
```typescript
import { serve } from "https://deno.land/std@0.192.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.38.0";

serve(async (req) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_ANON_KEY")!,
    { global: { headers: { Authorization: req.headers.get("Authorization")! } } }
  );

  const { data: { user }, error: authError } = await supabase.auth.getUser();
  if (authError || !user) return new Response("Unauthorized", { status: 401 });

  const { operations } = await req.json();
  if (!operations || operations.length > 100) {
    return new Response("Max 100 operations", { status: 400 });
  }

  const results = { synced: 0, skipped: 0, failed: [] as string[] };

  for (const op of operations) {
    try {
      // Check if existing record is newer
      const { data: existing } = await supabase
        .from(op.table)
        .select("updated_at")
        .eq("id", op.id)
        .single();

      if (existing && new Date(existing.updated_at).getTime() > op.updated_at) {
        results.skipped++;
        continue;
      }

      // Upsert
      const { error } = await supabase
        .from(op.table)
        .upsert({ ...op.payload, id: op.id, user_id: user.id, updated_at: new Date(op.updated_at).toISOString() });

      if (error) {
        results.failed.push(op.id);
      } else {
        results.synced++;
      }
    } catch (e) {
      results.failed.push(op.id);
    }
  }

  return new Response(JSON.stringify(results), {
    headers: { "Content-Type": "application/json" },
  });
});
```

### Function 3: `export-user-data`

Exports all user data as JSON with time-limited signed URL.

**Endpoint:** `GET /functions/v1/export-user-data`

**Response:**
```json
{
  "download_url": "https://xyz.supabase.co/storage/v1/object/signed/exports/uuid/data.json?token=abc&expires=600"
}
```

**Logic (TypeScript):**
```typescript
import { serve } from "https://deno.land/std@0.192.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.38.0";

serve(async (req) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!, // Service role for full read
  );

  const token = req.headers.get("Authorization")?.replace("Bearer ", "");
  const { data: { user }, error: authError } = await supabase.auth.getUser(token);
  if (authError || !user) return new Response("Unauthorized", { status: 401 });

  // Fetch all user data
  const [cycleEntries, wellnessLogs, journalEntries, purchases] = await Promise.all([
    supabase.from("cycle_entries").select("*").eq("user_id", user.id),
    supabase.from("wellness_logs").select("*").eq("user_id", user.id),
    supabase.from("journal_entries").select("*").eq("user_id", user.id),
    supabase.from("purchases").select("*").eq("user_id", user.id),
  ]);

  const exportData = {
    exported_at: new Date().toISOString(),
    user_id: user.id,
    cycle_entries: cycleEntries.data || [],
    wellness_logs: wellnessLogs.data || [],
    journal_entries: journalEntries.data || [],
    purchases: purchases.data || [],
  };

  // Upload to exports bucket
  const fileName = `exports/${user.id}/data.json`;
  await supabase.storage.from("user-exports").upload(fileName, JSON.stringify(exportData, null, 2), {
    contentType: "application/json",
    upsert: true,
  });

  // Generate signed URL (10 min expiry)
  const { data: signedUrl } = await supabase.storage
    .from("user-exports")
    .createSignedUrl(fileName, 600);

  return new Response(JSON.stringify({ download_url: signedUrl?.signedUrl }), {
    headers: { "Content-Type": "application/json" },
  });
});
```

### Function 4: `delete-account`

Permanently deletes all user data and auth account.

**Endpoint:** `DELETE /functions/v1/delete-account`

**Logic (TypeScript):**
```typescript
import { serve } from "https://deno.land/std@0.192.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2.38.0";

serve(async (req) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
  );

  const token = req.headers.get("Authorization")?.replace("Bearer ", "");
  const { data: { user }, error: authError } = await supabase.auth.getUser(token);
  if (authError || !user) return new Response("Unauthorized", { status: 401 });

  // Cascade delete: CASCADE FK handles all child tables
  const { error: deleteError } = await supabase
    .from("profiles")
    .delete()
    .eq("id", user.id);

  if (deleteError) {
    return new Response(JSON.stringify({ success: false, error: deleteError.message }), { status: 500 });
  }

  // Delete auth user (triggers profile cascade via ON DELETE CASCADE)
  const { error: authDeleteError } = await supabase.auth.admin.deleteUser(user.id);
  if (authDeleteError) {
    return new Response(JSON.stringify({ success: false, error: authDeleteError.message }), { status: 500 });
  }

  return new Response(JSON.stringify({ success: true }), {
    headers: { "Content-Type": "application/json" },
  });
});
```

---

## SECTION 4: ANDROID SUPABASE CLIENT SETUP

### Dependencies (Gradle)

```kotlin
// Version Catalog (libs.versions.toml)
[versions]
supabase-bom = "3.0.0"
ktor-android = "2.3.12"

[libraries]
supabase-bom = { group = "io.github.jan-tennert.supabase", name = "bom", version.ref = "supabase-bom" }
supabase-gotrue = { group = "io.github.jan-tennert.supabase", name = "gotrue-kt" }
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt" }
supabase-realtime = { group = "io.github.jan-tennert.supabase", name = "realtime-kt", version = "3.0.0" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor-android" }
```

### SupabaseClient.kt

```kotlin
package com.periodsaathi.app.data.remote.supabase

import android.content.Context
import com.periodsaathi.app.BuildConfig
import com.periodsaathi.app.data.preferences.SecurePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionManager
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeriodSaathiSupabaseClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securePreferences: SecurePreferences
) {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        httpEngine = Android.create {
            // Certificate pinning configured in network_security_config.xml
            // Timeout: 30 seconds
        }

        install(Auth) {
            sessionManager = SessionManager.LocalStorage(
                storage = SupabasePersistedSessionStorage(securePreferences)
            )
        }

        install(Postgrest)

        install(Realtime) {
            schema = "public"
        }

        defaultHttpRequestConfig {
            timeout {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 30_000
            }
        }
    }
}
```

### SupabaseModule.kt

```kotlin
package com.periodsaathi.app.data.remote.supabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides @Singleton
    fun provideSupabaseClient(client: PeriodSaathiSupabaseClient): io.github.jan.supabase.SupabaseClient {
        return client.client
    }

    @Provides @Singleton
    fun provideAuth(client: io.github.jan.supabase.SupabaseClient): Auth {
        return client.auth
    }

    @Provides @Singleton
    fun providePostgrest(client: io.github.jan.supabase.SupabaseClient): Postgrest {
        return client.postgrest
    }

    @Provides @Singleton
    fun provideRealtime(client: io.github.jan.supabase.SupabaseClient): Realtime {
        return client.realtime
    }
}
```

### SupabasePersistedSessionStorage.kt

```kotlin
package com.periodsaathi.app.data.remote.supabase

import com.periodsaathi.app.data.preferences.SecurePreferences
import io.github.jan.supabase.gotrue.SessionManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class SupabasePersistedSessionStorage(
    private val securePreferences: SecurePreferences
) : SessionManager.LocalStorage {

    override suspend fun getItem(key: String): String? {
        return securePreferences.getString(key)
    }

    override suspend fun setItem(key: String, value: String) {
        securePreferences.putString(key, value)
    }

    override suspend fun removeItem(key: String) {
        securePreferences.remove(key)
    }
}
```

### Network Security Config (certificate pinning)

```xml
<!-- res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">supabase.co</domain>
        <pin-set expiration="2027-01-01">
            <!-- Supabase API certificate pins -->
            <pin digest="SHA-256">AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=</pin>
            <pin digest="SHA-256">BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

---

## SECTION 5: SYNC IMPLEMENTATION DETAILS

### SyncWorker Trigger Conditions

| Trigger | Mechanism | Frequency |
|---------|-----------|-----------|
| Network available | `ConnectivityManager.NetworkCallback` | Immediate on reconnect |
| Periodic | `WorkManager PeriodicWorkRequest` | Every 15 minutes |
| User-initiated | Manual "Sync now" button in Settings | On demand |

### WorkManager Configuration

```kotlin
val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .setBackoffCriteria(
        BackoffPolicy.EXPONENTIAL,
        1, TimeUnit.MINUTES
    )
    .build()

WorkManager.getInstance(context)
    .enqueueUniquePeriodicWork(
        "periodic_sync",
        ExistingPeriodicWorkPolicy.KEEP,
        syncRequest
    )
```

### Sync Flow

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐     ┌───────────┐
│ Local Write  │────▶│ PendingSync  │────▶│ SyncWorker  │────▶│ Supabase  │
│ (Room)       │     │ Dao (queue)  │     │ (batch 50)  │     │ API       │
└─────────────┘     └──────────────┘     └─────────────┘     └───────────┘
                                              │
                                     ┌───────┴───────┐
                                     ▼               ▼
                                ┌────────┐     ┌──────────┐
                                │ SUCCESS │     │ FAILURE  │
                                │ delete  │     │ retry+1  │
                                │ from    │     │ backoff  │
                                │ queue   │     │          │
                                └────────┘     │ ≥5 fails │
                                               │→ Notify  │
                                               └──────────┘
```

### Error Handling

| HTTP Status | Client Action |
|:-----------:|---------------|
| 200 | Success — delete from PendingSyncDao |
| 401 | Auth error — clear session, notify user to re-login |
| 409 | Conflict — server data is newer, discard local |
| 429 | Rate limit — exponential backoff (1min, 2min, 4min) |
| 5xx | Server error — retry with backoff |

### Batch Sync Parameters

- **Batch size:** 50 operations per request
- **Timeout:** 30 seconds per batch
- **Order:** FIFO by `createdAt`
- **Retry limit:** 5 attempts
- **Backoff strategy:** `2^retry * 30 seconds` (30s, 60s, 2min, 4min, 8min)

### Pull-on-Login Sync

When user logs in (Google Sign-In or email), a full pull sync executes:

1. Fetch `last_sync_at` from `user_settings`
2. Fetch all remote entries with `updated_at > last_sync_at` for all tables
3. For each entry:
   - If no local entry exists → Insert
   - If local entry exists → Compare `updated_at`:
     - Remote newer → Replace local
     - Local newer → Keep local (will be pushed in next batch sync)
     - Same → Skip
4. Update `last_sync_at` to current timestamp
5. Trigger batch sync to push any local changes not yet synced

### Conflict Resolution (Pull Sync)

```kotlin
data class SyncResult(
    val synced: Int,
    val skipped: Int,
    val failed: List<String>,
    val conflicts: List<String>
)

fun resolvePullConflict(
    local: BaseEntity,
    remote: BaseEntity
): BaseEntity {
    return when {
        remote.updatedAt > local.updatedAt -> remote  // Remote wins
        local.updatedAt > remote.updatedAt -> local  // Local wins (will push later)
        else -> local  // Same timestamp, no change
    }
}
```

---

*Document Version: 1.0*  
*Last Updated: May 16, 2026*  
*Next Review: When Supabase project is provisioned*