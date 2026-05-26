# S04 — GRADLE BUILD FILES

> **Prerequisites:** S00 active · S01–S03 complete
> **Output files:** `settings.gradle.kts`, `build.gradle.kts` (root), `app/build.gradle.kts`, `local.properties` (template), `.gitignore` update
> **Build check:** `./gradlew assembleDebug` — must succeed with zero errors before S05.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S04_report.md`

Include:
- 📄 Files created (list all 5)
- ✅ `./gradlew assembleDebug` result (SUCCESS / FAILED)
- ❌ Build errors encountered (paste exact error messages)
- 🔧 Fixes applied to resolve errors
- ⚠️ Warnings found (list, with fix status)
- 📦 All dependencies added (count)

---

## PROMPT

Write ALL Gradle and build configuration files for Period Saathi.

---

### FILE 1: `settings.gradle.kts`

```kotlin
pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}
rootProject.name = "PeriodSaathi"
include(":app")
```

---

### FILE 2: `build.gradle.kts` (root)

Plugins block (`apply false`):
- com.android.application: 9.2.1
- com.android.library: 9.2.1
- org.jetbrains.kotlin.android: 2.2.10
- org.jetbrains.kotlin.plugin.compose: 2.2.10
- com.google.dagger.hilt.android: 2.51.1
- com.google.devtools.ksp: 2.2.10-1.0.31
- org.jetbrains.kotlin.plugin.serialization: 2.2.10
- com.google.gms.google-services: 4.4.2
- com.google.firebase.crashlytics: 3.0.2
- io.gitlab.arturbosch.detekt: 1.23.6

---

### FILE 3: `app/build.gradle.kts` (complete)

Apply all plugins listed in File 2.

`android {}` block:
- namespace: "com.periodsaathi.app"
- compileSdk: 36
- defaultConfig: minSdk=24, targetSdk=36, versionCode=1, versionName="1.0.0"
- BuildConfig fields from local.properties: SUPABASE_URL, SUPABASE_ANON_KEY, RAZORPAY_KEY_ID, ADMOB_APP_ID
- KSP: room.schemaLocation
- buildTypes: debug (applicationIdSuffix=".debug", DEBUG_MODE=true) + release (minify=true, shrinkResources=true, proguard, DEBUG_MODE=false)
- compileOptions: JavaVersion.VERSION_17
- kotlinOptions: jvmTarget="17"
- buildFeatures: compose=true, buildConfig=true
- testOptions: unitTests.isReturnDefaultValues=true
- bundle: enableSplit for language, density, abi

**COMPLETE DEPENDENCIES (exact versions):**

```kotlin
// Compose BOM 2026.02.01
implementation(platform("androidx.compose:compose-bom:2026.02.01"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")
implementation("androidx.compose.animation:animation")
implementation("androidx.compose.foundation:foundation")
debugImplementation("androidx.compose.ui:ui-tooling")
debugImplementation("androidx.compose.ui:ui-test-manifest")

// Navigation
implementation("androidx.navigation:navigation-compose:2.9.0")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")

// Hilt
implementation("com.google.dagger:hilt-android:2.51.1")
ksp("com.google.dagger:hilt-compiler:2.51.1")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
implementation("androidx.hilt:hilt-work:1.2.0")
ksp("androidx.hilt:hilt-compiler:1.2.0")

// Room + SQLCipher
implementation("androidx.room:room-runtime:2.7.0")
implementation("androidx.room:room-ktx:2.7.0")
ksp("androidx.room:room-compiler:2.7.0")
implementation("net.zetetic:android-database-sqlcipher:4.5.4")
implementation("androidx.sqlite:sqlite-ktx:2.4.0")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.1.2")

// WorkManager
implementation("androidx.work:work-runtime-ktx:2.10.0")

// Supabase BOM
implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
implementation("io.github.jan-tennert.supabase:gotrue-kt")
implementation("io.github.jan-tennert.supabase:postgrest-kt")
implementation("io.ktor:ktor-client-android:2.3.12")

// Google Sign-In
implementation("com.google.android.gms:play-services-auth:21.2.0")

// Razorpay
implementation("com.razorpay:checkout:1.6.41")

// AdMob
implementation("com.google.android.gms:play-services-ads:23.4.0")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
implementation("com.google.firebase:firebase-crashlytics-ktx")
implementation("com.google.firebase:firebase-analytics-ktx")

// Biometrics
implementation("androidx.biometric:biometric:1.2.0-alpha05")

// Security
implementation("androidx.security:security-crypto:1.1.0-alpha06")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

// JSON
implementation("com.google.code.gson:gson:2.11.0")

// Google Fonts
implementation("androidx.compose.ui:ui-text-google-fonts")

// Accompanist
implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
implementation("com.google.accompanist:accompanist-permissions:0.36.0")

// Glance (Widgets)
implementation("androidx.glance:glance-appwidget:1.1.1")
implementation("androidx.glance:glance-material3:1.1.1")

// PDF generation
implementation("com.itextpdf:itext7-core:8.0.5")

// Testing
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("app.cash.turbine:turbine:1.1.0")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
testImplementation("androidx.arch.core:core-testing:2.2.0")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
androidTestImplementation("androidx.test.ext:junit:1.2.1")
androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
kspAndroidTest("com.google.dagger:hilt-compiler:2.51.1")
```

---

### FILE 4: `local.properties` (TEMPLATE — never commit to git)

```properties
# Android SDK path (auto-generated by Android Studio)
sdk.dir=/Users/you/Library/Android/sdk

# Supabase
supabase.url=https://YOUR_PROJECT.supabase.co
supabase.anon.key=YOUR_ANON_KEY

# Razorpay
razorpay.key.id=rzp_test_XXXXXX

# AdMob
admob.app.id=ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX

# Signing (release builds only)
keystore.path=../period-saathi-key.jks
keystore.password=CHANGE_ME
key.alias=period-saathi
key.password=CHANGE_ME
```

---

### FILE 5: `.gitignore` additions

Add these lines:
```
local.properties
*.jks
google-services.json
.env
/app/schemas/
```

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Fix ALL errors. Then write the report.
