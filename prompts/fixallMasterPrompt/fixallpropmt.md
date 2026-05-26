# 🌸 PERIOD SAATHI — FIX-ALL MASTER PROMPT v3.0
## Addresses Every Broken Issue in the Current Build
### Self-contained · Copy-paste ready · OpenCode / DeepSeek / Minimax

---

> **INSTRUCTIONS FOR AI CODING ASSISTANT**
> This is a complete fix directive. Read every section before writing a single line.
> Fix issues in the exact order listed. Do not skip sections.
> Write COMPLETE files — no `// TODO`, no placeholder comments, no partial code.
> After every file: verify it compiles mentally before outputting.

---

# ═══════════════════════════════════════════
# SECTION 0 — PROJECT CONTEXT (READ FIRST)
# ═══════════════════════════════════════════

```
App Name: Period Saathi
Package: com.periodsaathi.app
Platform: Android Native — Kotlin 2.2.10 + Jetpack Compose BOM 2026.02.01
AGP: 9.2.1 | Min SDK: 24 | Target SDK: 36

CURRENT STATE (what exists — do not delete, only fix/extend):
- MainActivity.kt: basic Material3 "Hello Android" screen
- Basic theme with default purple Material colors
- No navigation, no ViewModels, no Room, no features

TARGET STATE (what must exist after fix):
- 18 fully functional screens with premium UI
- All buttons working with click handlers
- Spring animations + haptic feedback everywhere
- Google Sign-In + Supabase backend
- Room database (encrypted) offline-first
- Full navigation graph
- @Preview on every composable
```

---

# ═══════════════════════════════════════════
# FIX 1 — SCREENSHOT CAPABILITY (DO THIS FIRST)
# ═══════════════════════════════════════════

**Problem:** Screenshots are blocked — users can't share cycle reports.
**Fix:** Remove FLAG_SECURE. Period Saathi users NEED screenshot capability for sharing reports and care summaries with partners/doctors.

```kotlin
// MainActivity.kt — REMOVE this line if present:
// window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

// INSTEAD add this to enable screenshots:
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    WindowCompat.setDecorFitsSystemWindows(window, false)
    // FLAG_SECURE deliberately NOT set — users need screenshots for reports
    setContent { ... }
}
```

---

# ═══════════════════════════════════════════
# FIX 2 — DESIGN SYSTEM (REPLACE ENTIRE THEME)
# ═══════════════════════════════════════════

**Problem:** Default purple Material theme — zero resemblance to premium design.
**Fix:** Replace all theme files completely.

## FILE: ui/theme/Color.kt
```kotlin
package com.periodsaathi.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand Palette ──────────────────────────────────────────
val BlushPink      = Color(0xFFFFB5C8)
val DeepRose       = Color(0xFFFF8FAB)
val SoftLavender   = Color(0xFFC9B8FF)
val SoftPurple     = Color(0xFF9B8EC4)
val BabyBlue       = Color(0xFFB8DCFF)
val ButterYellow   = Color(0xFFFFF3B0)
val SoftCoral      = Color(0xFFFFB3A7)
val MintGreen      = Color(0xFFB8F0DC)
val CreamWhite     = Color(0xFFFFF8F5)
val WarmGold       = Color(0xFFFFD700)

// ── Semantic ───────────────────────────────────────────────
val PeriodLight     = Color(0xFFFFE4EC)
val PeriodMedium    = Color(0xFFFFB5C8)
val PeriodHeavy     = Color(0xFFFF8FAB)
val PeriodVeryHeavy = Color(0xFFE05080)
val FertileMint     = Color(0xFFB8F0DC)
val FertileOrange   = Color(0xFFFFDDB3)
val PMSLilac        = Color(0xFFDDD0FF)
val DangerRed       = Color(0xFFFF5252)
val SuccessGreen    = Color(0xFF69F0AE)

// ── Glass ──────────────────────────────────────────────────
val GlassWhite  = Color(0x73FFFFFF)   // 45% white
val GlassBorder = Color(0x99FFFFFF)   // 60% white

// ── Dark backgrounds ──────────────────────────────────────
val DarkNavy  = Color(0xFF1A0E2E)
val WarmNavy  = Color(0xFF1A1228)

// ── Material3 color schemes ───────────────────────────────
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightColorScheme = lightColorScheme(
    primary          = BlushPink,
    onPrimary        = Color(0xFF3D2C35),
    primaryContainer = PeriodLight,
    secondary        = SoftLavender,
    onSecondary      = Color(0xFF2A2040),
    tertiary         = BabyBlue,
    background       = CreamWhite,
    surface          = CreamWhite,
    onBackground     = Color(0xFF2A2040),
    onSurface        = Color(0xFF2A2040),
    error            = DangerRed,
)

val DarkColorScheme = darkColorScheme(
    primary          = DeepRose,
    onPrimary        = Color(0xFFFFF8F5),
    background       = DarkNavy,
    surface          = WarmNavy,
    onBackground     = Color(0xFFFFF8F5),
    onSurface        = Color(0xFFFFF8F5),
)
```

## FILE: ui/theme/Type.kt
```kotlin
package com.periodsaathi.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.periodsaathi.app.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val Nunito   = GoogleFont("Nunito")
private val Poppins  = GoogleFont("Poppins")

private val NunitoFamily = androidx.compose.ui.text.font.FontFamily(
    Font(googleFont = Nunito, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = Nunito, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = Nunito, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = Nunito, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = Nunito, fontProvider = provider, weight = FontWeight.ExtraBold),
)

private val PoppinsFamily = androidx.compose.ui.text.font.FontFamily(
    Font(googleFont = Poppins, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = Poppins, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = Poppins, fontProvider = provider, weight = FontWeight.SemiBold),
)

val AppTypography = Typography(
    displayLarge  = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineLarge = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold,      fontSize = 24.sp, lineHeight = 32.sp),
    headlineMedium= TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold,      fontSize = 20.sp, lineHeight = 28.sp),
    titleLarge    = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold,  fontSize = 18.sp, lineHeight = 26.sp),
    titleMedium   = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold,  fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge     = TextStyle(fontFamily = PoppinsFamily,fontWeight = FontWeight.Normal,    fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium    = TextStyle(fontFamily = PoppinsFamily,fontWeight = FontWeight.Normal,    fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall     = TextStyle(fontFamily = PoppinsFamily,fontWeight = FontWeight.Normal,    fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge    = TextStyle(fontFamily = PoppinsFamily,fontWeight = FontWeight.SemiBold,  fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium   = TextStyle(fontFamily = PoppinsFamily,fontWeight = FontWeight.Medium,    fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall    = TextStyle(fontFamily = PoppinsFamily,fontWeight = FontWeight.Medium,    fontSize = 11.sp, lineHeight = 14.sp),
)
```

## FILE: ui/theme/Shapes.kt
```kotlin
package com.periodsaathi.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small  = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(28.dp),
    large  = RoundedCornerShape(32.dp),
)

// Named shape tokens — use throughout app
val CardShape       = RoundedCornerShape(28.dp)
val ButtonShape     = RoundedCornerShape(50.dp)   // pill
val ChipShape       = RoundedCornerShape(20.dp)
val InputShape      = RoundedCornerShape(16.dp)
val BadgeShape      = RoundedCornerShape(50.dp)
val BottomSheetShape= RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val SmallCardShape  = RoundedCornerShape(16.dp)
```

## FILE: ui/theme/Theme.kt
```kotlin
package com.periodsaathi.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PeriodSaathiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,   // Material You on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = if (darkTheme) DarkNavy else CreamWhite,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        shapes      = AppShapes,
        content     = content
    )
}
```

---

# ═══════════════════════════════════════════
# FIX 3 — GRADLE (ALL REQUIRED DEPENDENCIES)
# ═══════════════════════════════════════════

**Problem:** Missing Hilt, Room, Navigation, Supabase, Razorpay, etc.
**Fix:** Replace app/build.gradle.kts entirely.

## FILE: app/build.gradle.kts
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace   = "com.periodsaathi.app"
    compileSdk  = 36

    defaultConfig {
        applicationId = "com.periodsaathi.app"
        minSdk        = 24
        targetSdk     = 36
        versionCode   = 1
        versionName   = "1.0.0"

        // ── Keys from local.properties (NEVER hardcode) ──────────
        val props = rootProject.file("local.properties")
            .takeIf { it.exists() }
            ?.let { java.util.Properties().also { p -> p.load(it.inputStream()) } }

        buildConfigField("String", "SUPABASE_URL",      "\"${props?.getProperty("supabase.url", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${props?.getProperty("supabase.anon.key", "")}\"")
        buildConfigField("String", "RAZORPAY_KEY_ID",   "\"${props?.getProperty("razorpay.key.id", "")}\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID",  "\"${props?.getProperty("google.client.id", "")}\"")
        buildConfigField("String", "ADMOB_APP_ID",      "\"${props?.getProperty("admob.app.id", "ca-app-pub-3940256099942544~3347511713")}\"")

        ksp { arg("room.schemaLocation", "$projectDir/schemas") }
    }

    buildTypes {
        debug   { applicationIdSuffix = ".debug" }
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true  // needed for java.time on API < 26
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")

    // ── Compose BOM ──────────────────────────────────────────────
    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.animation:animation-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ── Core Android ─────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")

    // ── Navigation (type-safe) ────────────────────────────────────
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // ── Hilt ─────────────────────────────────────────────────────
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // ── Room + SQLCipher ─────────────────────────────────────────
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")

    // ── DataStore ────────────────────────────────────────────────
    implementation("androidx.datastore:datastore-preferences:1.1.2")

    // ── WorkManager ──────────────────────────────────────────────
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // ── Supabase ─────────────────────────────────────────────────
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
    implementation("io.github.jan-tennert.supabase:gotrue-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:2.3.12")

    // ── Google Sign-In (Credential Manager) ───────────────────────
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // ── Razorpay ─────────────────────────────────────────────────
    implementation("com.razorpay:checkout:1.6.41")

    // ── AdMob ────────────────────────────────────────────────────
    implementation("com.google.android.gms:play-services-ads:23.4.0")

    // ── Firebase ─────────────────────────────────────────────────
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // ── Biometrics ───────────────────────────────────────────────
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // ── Security ─────────────────────────────────────────────────
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // ── Accompanist ──────────────────────────────────────────────
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // ── Glance Widgets ───────────────────────────────────────────
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

    // ── Coroutines ───────────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // ── JSON ─────────────────────────────────────────────────────
    implementation("com.google.code.gson:gson:2.11.0")

    // ── Lottie (for complex animations) ──────────────────────────
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    // ── Testing ──────────────────────────────────────────────────
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

---

# ═══════════════════════════════════════════
# FIX 4 — SHARED COMPONENTS (THE UI BUILDING BLOCKS)
# ═══════════════════════════════════════════

**Problem:** No glassmorphism, no premium components, no animations.
**Fix:** Create all shared components before building any screen.

## FILE: ui/components/ScaleButton.kt
```kotlin
package com.periodsaathi.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

/**
 * Wraps content in a scale-bounce press animation.
 * Press → 0.94, Release → 1.02 (overshoot), Settle → 1.0
 * Use this as the base for EVERY tappable element.
 */
@Composable
fun ScaleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .pointerInput(enabled, onClick) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            scale.animateTo(0.94f, spring(stiffness = 600f))
                        }
                        val released = tryAwaitRelease()
                        scope.launch {
                            if (released) {
                                scale.animateTo(1.02f, spring(dampingRatio = 0.4f, stiffness = 400f))
                                scale.animateTo(1.0f, spring(stiffness = 600f))
                            } else {
                                scale.animateTo(1.0f, spring(stiffness = 600f))
                            }
                        }
                    },
                    onTap = { onClick() }
                )
            }
    ) { content() }
}
```

## FILE: ui/components/GlassCard.kt
```kotlin
package com.periodsaathi.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.periodsaathi.app.ui.theme.BlushPink
import com.periodsaathi.app.ui.theme.CardShape
import com.periodsaathi.app.ui.theme.GlassBorder
import com.periodsaathi.app.ui.theme.GlassWhite

/**
 * Premium frosted-glass card used throughout the app.
 * Matches the glassmorphism design from Stitch screenshots.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    tint: Color = Color.Transparent,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val card: @Composable () -> Unit = {
        Surface(
            modifier = modifier.shadow(
                elevation     = elevation,
                shape         = shape,
                ambientColor  = BlushPink.copy(alpha = 0.2f),
                spotColor     = BlushPink.copy(alpha = 0.2f)
            ),
            shape  = shape,
            color  = GlassWhite,
            border = BorderStroke(1.dp, GlassBorder)
        ) {
            Box {
                // Optional colour tint overlay
                if (tint != Color.Transparent) {
                    Box(modifier = Modifier
                        .matchParentSize()
                        .background(tint.copy(alpha = 0.18f)))
                }
                Box(modifier = Modifier.padding(20.dp)) { content() }
            }
        }
    }

    if (onClick != null) {
        ScaleButton(onClick = onClick) { card() }
    } else {
        card()
    }
}
```

## FILE: ui/components/PrimaryButton.kt
```kotlin
package com.periodsaathi.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.periodsaathi.app.ui.theme.*

/**
 * Primary CTA button with gradient background + spring animation.
 * EVERY primary action in the app uses this component.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val haptic = LocalHapticFeedback.current

    ScaleButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled && !isLoading)
                        Brush.horizontalGradient(listOf(BlushPink, DeepRose))
                    else
                        Brush.horizontalGradient(listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f))),
                    shape = ButtonShape
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = isLoading) { loading ->
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        leadingIcon?.invoke()
                        Text(
                            text       = text,
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            style      = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
```

## FILE: ui/components/PastelChip.kt
```kotlin
package com.periodsaathi.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.periodsaathi.app.ui.theme.*

@Composable
fun PastelChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = BlushPink,
    modifier: Modifier = Modifier,
    leadingEmoji: String? = null,
) {
    val haptic = LocalHapticFeedback.current
    val bgColor by animateColorAsState(
        if (selected) accentColor.copy(alpha = 0.25f) else Color.White,
        animationSpec = spring()
    )
    val borderColor by animateColorAsState(
        if (selected) accentColor else accentColor.copy(alpha = 0.3f),
        animationSpec = spring()
    )

    ScaleButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .height(36.dp)
                .background(bgColor, ChipShape)
                .border(1.dp, borderColor, ChipShape)
                .padding(horizontal = 12.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (leadingEmoji != null) {
                Text(leadingEmoji, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text  = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) accentColor else MaterialTheme.colorScheme.onSurface.copy(0.7f)
            )
        }
    }
}
```

## FILE: ui/components/SaathiMascot.kt
```kotlin
package com.periodsaathi.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.periodsaathi.app.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

enum class MascotEmotion { HAPPY, SAD, SLEEPING, EXCITED, PAIN, LISTENING, HUGGING }

private val wellnessTips = listOf(
    "Drinking water reduces cramps by up to 40%! 💧",
    "Ginger tea is nature's ibuprofen 🫚",
    "A 5-minute walk can lift your mood by 15% 🚶‍♀️",
    "Deep breathing helps with period cramps 😮‍💨",
    "Dark chocolate has magnesium — good for PMS! 🍫",
    "Your energy peaks in the follicular phase — use it! ✨",
    "Rest is not laziness — it's recovery 🛌",
    "Fennel seeds in warm water eases bloating 🌿",
    "Turmeric milk helps with inflammation 🥛",
    "Jaggery boosts iron levels during your period 🍬",
    "Your creativity spikes around ovulation 🎨",
    "Warmth (hot bag, warm bath) relaxes uterine muscles 🛁",
    "Magnesium-rich foods reduce PMS mood swings 🥬",
    "Yoga's child's pose gives direct cramp relief 🧘",
    "Staying consistent with sleep helps regulate cycles 😴"
)

@Composable
fun SaathiMascot(
    emotion: MascotEmotion = MascotEmotion.HAPPY,
    size: Dp = 120.dp,
    onClick: (() -> Unit)? = null,
    showTipOnTap: Boolean = true,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var showTip by remember { mutableStateOf(false) }
    var currentTip by remember { mutableStateOf("") }

    // Idle float animation
    val infiniteTransition = rememberInfiniteTransition()
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Zzz animation for sleeping
    val zzzAlpha by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500), repeatMode = RepeatMode.Reverse
        )
    )

    // Scale on tap
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        // Tip bubble above mascot
        if (showTip) {
            TipBubble(
                text = currentTip,
                onDismiss = { showTip = false },
                modifier = Modifier.align(Alignment.TopCenter).offset(y = (-8).dp)
            )
        }

        Canvas(
            modifier = Modifier
                .size(size)
                .offset(y = floatY.dp)
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .then(
                    if (onClick != null || showTipOnTap) Modifier.clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch {
                            scale.animateTo(1.3f, spring(dampingRatio = 0.3f))
                            scale.animateTo(1f,   spring(dampingRatio = 0.6f))
                        }
                        if (showTipOnTap) {
                            currentTip = wellnessTips.random()
                            showTip    = true
                        }
                        onClick?.invoke()
                    } else Modifier
                )
        ) {
            drawMascot(emotion, zzzAlpha)
        }
    }
}

private fun DrawScope.drawMascot(emotion: MascotEmotion, zzzAlpha: Float) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    // Body
    val bodyGradient = Brush.verticalGradient(
        listOf(Color(0xFFFFB5C8), Color(0xFFFFB3A7)),
        startY = cy * 0.3f, endY = cy * 1.4f
    )
    drawRoundRect(
        brush       = bodyGradient,
        topLeft     = Offset(cx * 0.25f, cy * 0.25f),
        size        = androidx.compose.ui.geometry.Size(cx * 1.5f, cy * 1.4f),
        cornerRadius= androidx.compose.ui.geometry.CornerRadius(cx * 0.4f)
    )

    // Cheeks (always visible for HAPPY/EXCITED)
    if (emotion == MascotEmotion.HAPPY || emotion == MascotEmotion.EXCITED) {
        drawCircle(Color(0xFFFFB5C8).copy(alpha = 0.45f), radius = w * 0.08f, center = Offset(cx * 0.55f, cy * 0.92f))
        drawCircle(Color(0xFFFFB5C8).copy(alpha = 0.45f), radius = w * 0.08f, center = Offset(cx * 1.45f, cy * 0.92f))
    }

    when (emotion) {
        MascotEmotion.HAPPY, MascotEmotion.HUGGING -> {
            // Open eyes
            drawCircle(Color(0xFF3D2C35), radius = w * 0.065f, center = Offset(cx * 0.72f, cy * 0.72f))
            drawCircle(Color(0xFF3D2C35), radius = w * 0.065f, center = Offset(cx * 1.28f, cy * 0.72f))
            drawCircle(Color.White, radius = w * 0.025f, center = Offset(cx * 0.75f, cy * 0.68f))
            drawCircle(Color.White, radius = w * 0.025f, center = Offset(cx * 1.31f, cy * 0.68f))
            // Smile arc
            drawArc(
                color    = Color(0xFF3D2C35), startAngle = 10f, sweepAngle = 160f,
                useCenter= false, topLeft = Offset(cx * 0.7f, cy * 0.9f),
                size     = androidx.compose.ui.geometry.Size(cx * 0.6f, cy * 0.25f),
                style    = Stroke(width = w * 0.04f, cap = StrokeCap.Round)
            )
        }
        MascotEmotion.SAD -> {
            // Droopy eyes
            drawArc(Color(0xFF3D2C35), startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(cx * 0.6f, cy * 0.62f), size = androidx.compose.ui.geometry.Size(cx * 0.24f, cy * 0.2f),
                style = Stroke(w * 0.04f, cap = StrokeCap.Round))
            drawArc(Color(0xFF3D2C35), startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(cx * 1.16f, cy * 0.62f), size = androidx.compose.ui.geometry.Size(cx * 0.24f, cy * 0.2f),
                style = Stroke(w * 0.04f, cap = StrokeCap.Round))
            // Frown
            drawArc(Color(0xFF3D2C35), startAngle = 190f, sweepAngle = 160f, useCenter = false,
                topLeft = Offset(cx * 0.7f, cy * 1.0f), size = androidx.compose.ui.geometry.Size(cx * 0.6f, cy * 0.2f),
                style = Stroke(w * 0.04f, cap = StrokeCap.Round))
        }
        MascotEmotion.SLEEPING -> {
            // Closed curved lines
            drawArc(Color(0xFF3D2C35), startAngle = 0f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(cx * 0.6f, cy * 0.62f), size = androidx.compose.ui.geometry.Size(cx * 0.24f, cy * 0.16f),
                style = Stroke(w * 0.04f, cap = StrokeCap.Round))
            drawArc(Color(0xFF3D2C35), startAngle = 0f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(cx * 1.16f, cy * 0.62f), size = androidx.compose.ui.geometry.Size(cx * 0.24f, cy * 0.16f),
                style = Stroke(w * 0.04f, cap = StrokeCap.Round))
            // Gentle smile
            drawArc(Color(0xFF3D2C35), startAngle = 10f, sweepAngle = 160f, useCenter = false,
                topLeft = Offset(cx * 0.75f, cy * 0.88f), size = androidx.compose.ui.geometry.Size(cx * 0.5f, cy * 0.2f),
                style = Stroke(w * 0.035f, cap = StrokeCap.Round))
            // Zzz (floating, animated)
            drawContext.canvas.nativeCanvas.drawText("Z",  cx * 1.5f, cy * 0.45f, android.graphics.Paint().also { p ->
                p.color = android.graphics.Color.argb((zzzAlpha * 180).toInt(), 155, 142, 196)
                p.textSize = w * 0.15f; p.isFakeBoldText = true
            })
        }
        MascotEmotion.EXCITED -> {
            // Large eyes
            drawCircle(Color(0xFF3D2C35), radius = w * 0.08f, center = Offset(cx * 0.72f, cy * 0.70f))
            drawCircle(Color(0xFF3D2C35), radius = w * 0.08f, center = Offset(cx * 1.28f, cy * 0.70f))
            drawCircle(Color.White, radius = w * 0.03f, center = Offset(cx * 0.75f, cy * 0.65f))
            drawCircle(Color.White, radius = w * 0.03f, center = Offset(cx * 1.31f, cy * 0.65f))
            // Big smile
            drawArc(Color(0xFF3D2C35), startAngle = 5f, sweepAngle = 170f, useCenter = false,
                topLeft = Offset(cx * 0.65f, cy * 0.88f), size = androidx.compose.ui.geometry.Size(cx * 0.7f, cy * 0.28f),
                style = Stroke(w * 0.045f, cap = StrokeCap.Round))
        }
        MascotEmotion.PAIN -> {
            // Squinting eyes
            repeat(2) { i ->
                val ex = if (i == 0) cx * 0.65f else cx * 1.15f
                drawLine(Color(0xFF3D2C35), start = Offset(ex, cy * 0.68f), end = Offset(ex + cx * 0.2f, cy * 0.74f),
                    strokeWidth = w * 0.04f, cap = StrokeCap.Round)
                drawLine(Color(0xFF3D2C35), start = Offset(ex + cx * 0.2f, cy * 0.74f), end = Offset(ex, cy * 0.80f),
                    strokeWidth = w * 0.04f, cap = StrokeCap.Round)
            }
            // Concerned mouth
            drawArc(Color(0xFF3D2C35), startAngle = 185f, sweepAngle = 170f, useCenter = false,
                topLeft = Offset(cx * 0.72f, cy * 0.96f), size = androidx.compose.ui.geometry.Size(cx * 0.56f, cy * 0.18f),
                style = Stroke(w * 0.035f, cap = StrokeCap.Round))
        }
        MascotEmotion.LISTENING -> {
            // Normal eyes with ears
            drawCircle(Color(0xFF3D2C35), radius = w * 0.065f, center = Offset(cx * 0.72f, cy * 0.72f))
            drawCircle(Color(0xFF3D2C35), radius = w * 0.065f, center = Offset(cx * 1.28f, cy * 0.72f))
            // Ear bumps
            drawCircle(Color(0xFFFFB5C8).copy(alpha = 0.8f), radius = w * 0.07f, center = Offset(cx * 0.22f, cy * 0.80f))
            drawCircle(Color(0xFFFFB5C8).copy(alpha = 0.8f), radius = w * 0.07f, center = Offset(cx * 1.78f, cy * 0.80f))
        }
    }
}

@Composable
private fun TipBubble(text: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(text) {
        kotlinx.coroutines.delay(4000)
        visible = false
        onDismiss()
    }
    // Simple Surface tip bubble — can be enhanced with custom Path shape
    androidx.compose.material3.Surface(
        modifier = modifier.clickable { visible = false; onDismiss() },
        shape    = com.periodsaathi.app.ui.theme.CardShape,
        color    = Color.White,
        tonalElevation = 4.dp
    ) {
        Text(
            text     = text,
            modifier = Modifier.padding(12.dp),
            style    = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview @Composable
private fun MascotHappyPreview() = PeriodSaathiTheme { SaathiMascot(MascotEmotion.HAPPY) }
@Preview @Composable
private fun MascotSleepingPreview() = PeriodSaathiTheme { SaathiMascot(MascotEmotion.SLEEPING) }
```

---

# ═══════════════════════════════════════════
# FIX 5 — NAVIGATION (ALL 18 SCREENS WIRED)
# ═══════════════════════════════════════════

## FILE: ui/navigation/Screen.kt
```kotlin
package com.periodsaathi.app.ui.navigation

import kotlinx.serialization.Serializable

// ── Every screen as a @Serializable object/class ─────────────
@Serializable object Splash
@Serializable object Onboarding
@Serializable object Login
@Serializable object NameSetup
@Serializable object Home
@Serializable object Calendar
@Serializable object Wellness
@Serializable object PartnerMode
@Serializable object Remedies
@Serializable object YogaFlow
@Serializable object Journal
@Serializable object MoodMap
@Serializable object Insights
@Serializable object Settings
@Serializable object ReportExport
@Serializable object Wardrobe
@Serializable object Challenges
@Serializable object BreathingMode
@Serializable object Payment
@Serializable data class DayLog(val dateEpoch: Long)
```

## FILE: ui/navigation/NavGraph.kt
```kotlin
package com.periodsaathi.app.ui.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.periodsaathi.app.ui.screens.breathing.BreathingModeScreen
import com.periodsaathi.app.ui.screens.calendar.CalendarScreen
import com.periodsaathi.app.ui.screens.challenges.ChallengesScreen
import com.periodsaathi.app.ui.screens.home.HomeScreen
import com.periodsaathi.app.ui.screens.insights.InsightsScreen
import com.periodsaathi.app.ui.screens.journal.JournalScreen
import com.periodsaathi.app.ui.screens.journal.MoodMapScreen
import com.periodsaathi.app.ui.screens.login.LoginScreen
import com.periodsaathi.app.ui.screens.onboarding.OnboardingScreen
import com.periodsaathi.app.ui.screens.onboarding.NameSetupScreen
import com.periodsaathi.app.ui.screens.partner.PartnerModeScreen
import com.periodsaathi.app.ui.screens.payment.PaymentScreen
import com.periodsaathi.app.ui.screens.remedies.RemediesScreen
import com.periodsaathi.app.ui.screens.remedies.YogaFlowScreen
import com.periodsaathi.app.ui.screens.settings.SettingsScreen
import com.periodsaathi.app.ui.screens.settings.ReportExportScreen
import com.periodsaathi.app.ui.screens.settings.WardrobeScreen
import com.periodsaathi.app.ui.screens.splash.SplashScreen
import com.periodsaathi.app.ui.screens.wellness.WellnessScreen
import com.periodsaathi.app.ui.screens.calendar.DayLogScreen

private val enterSpec  = spring<Int>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
private val slideEnter = slideInHorizontally(animationSpec = enterSpec) { it } + fadeIn()
private val slideExit  = slideOutHorizontally { -it / 3 } + fadeOut()
private val popEnter   = slideInHorizontally { -it / 3 } + fadeIn()
private val popExit    = slideOutHorizontally(animationSpec = enterSpec) { it } + fadeOut()

@Composable
fun PeriodSaathiNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: Any = Splash
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
        enterTransition  = { slideEnter },
        exitTransition   = { slideExit  },
        popEnterTransition = { popEnter },
        popExitTransition  = { popExit  }
    ) {
        composable<Splash>        { SplashScreen(navController) }
        composable<Onboarding>    { OnboardingScreen(navController) }
        composable<Login>         { LoginScreen(navController) }
        composable<NameSetup>     { NameSetupScreen(navController) }
        composable<Home>          { HomeScreen(navController) }
        composable<Calendar>      { CalendarScreen(navController) }
        composable<Wellness>      { WellnessScreen(navController) }
        composable<PartnerMode>   { PartnerModeScreen(navController) }
        composable<Remedies>      { RemediesScreen(navController) }
        composable<YogaFlow>      { YogaFlowScreen(navController) }
        composable<Journal>       { JournalScreen(navController) }
        composable<MoodMap>       { MoodMapScreen(navController) }
        composable<Insights>      { InsightsScreen(navController) }
        composable<Settings>      { SettingsScreen(navController) }
        composable<ReportExport>  { ReportExportScreen(navController) }
        composable<Wardrobe>      { WardrobeScreen(navController) }
        composable<Challenges>    { ChallengesScreen(navController) }
        composable<BreathingMode> { BreathingModeScreen(navController) }
        composable<Payment>       { PaymentScreen(navController) }
        composable<DayLog>        { entry ->
            DayLogScreen(dateEpoch = entry.toRoute<DayLog>().dateEpoch, navController = navController)
        }
    }
}
```

## FILE: ui/navigation/BottomNavBar.kt
```kotlin
package com.periodsaathi.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.theme.*

data class BottomNavItem(val route: Any, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    BottomNavItem(Home,     Icons.Rounded.Home,           "Home"),
    BottomNavItem(Calendar, Icons.Rounded.CalendarMonth,  "Calendar"),
    BottomNavItem(Wellness, Icons.Rounded.FavoriteBorder, "Wellness"),
    BottomNavItem(Settings, Icons.Rounded.PersonOutline,  "Profile"),
)

@Composable
fun BottomNavBar(navController: NavController) {
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    Surface(
        color         = CreamWhite.copy(alpha = 0.95f),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        shape         = BottomSheetShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isActive = currentRoute?.contains(item.route::class.simpleName ?: "") == true
                val iconScale by animateFloatAsState(if (isActive) 1.2f else 1f, spring())
                val iconAlpha by animateFloatAsState(if (isActive) 1f else 0.5f, spring())
                val iconColor by animateColorAsState(if (isActive) BlushPink else Color.Gray, spring())

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .alpha(iconAlpha),
                    horizontalAlignment   = Alignment.CenterHorizontally,
                    verticalArrangement   = Arrangement.Center
                ) {
                    ScaleButton(onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isActive) {
                                // Active pill background
                                Box(
                                    modifier = Modifier
                                        .size(48.dp, 32.dp)
                                        .background(BlushPink.copy(alpha = 0.15f), ChipShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(item.icon, item.label,
                                        tint = iconColor,
                                        modifier = Modifier.graphicsLayer { scaleX = iconScale; scaleY = iconScale }
                                    )
                                }
                                Text(item.label, style = MaterialTheme.typography.labelSmall, color = BlushPink)
                            } else {
                                Icon(item.icon, item.label, tint = iconColor,
                                    modifier = Modifier.graphicsLayer { scaleX = iconScale; scaleY = iconScale })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview @Composable
private fun BottomNavPreview() = PeriodSaathiTheme {
    BottomNavBar(rememberNavController())
}
```

---

# ═══════════════════════════════════════════
# FIX 6 — GOOGLE SIGN-IN + SUPABASE
# ═══════════════════════════════════════════

**Problem:** Google button does nothing. Supabase not integrated.

## FILE: data/auth/AuthRepository.kt
```kotlin
package com.periodsaathi.app.data.auth

import android.content.Context
import androidx.credentials.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.periodsaathi.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val userId: String, val email: String?, val displayName: String?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabase: SupabaseClient
) {
    val isLoggedIn: Flow<Boolean> = supabase.auth.sessionStatus.map { status ->
        status is io.github.jan.supabase.gotrue.SessionStatus.Authenticated
    }

    val currentUserId: String?
        get() = supabase.auth.currentUserOrNull()?.id

    /**
     * Sign in with Google using Credential Manager (Android 14+) 
     * or legacy GoogleSignIn for older devices.
     */
    suspend fun signInWithGoogle(activityContext: Context): AuthResult {
        return try {
            val credentialManager = CredentialManager.create(activityContext)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activityContext, request)
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdToken.idToken

                // Exchange Google token with Supabase
                supabase.auth.signInWith(Google) {
                    this.idToken = idToken
                }

                val user = supabase.auth.currentUserOrNull()
                AuthResult.Success(
                    userId      = user?.id ?: "",
                    email       = user?.email,
                    displayName = googleIdToken.displayName
                )
            } else {
                AuthResult.Error("Unsupported credential type")
            }
        } catch (e: GetCredentialException) {
            AuthResult.Error("Sign-in cancelled: ${e.message}")
        } catch (e: Exception) {
            AuthResult.Error("Sign-in failed: ${e.message}")
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            supabase.auth.signInWith(Email) {
                this.email    = email
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull()
            AuthResult.Success(user?.id ?: "", user?.email, null)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email    = email
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull()
            AuthResult.Success(user?.id ?: "", user?.email, null)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign-up failed")
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            supabase.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }
}
```

## FILE: data/di/SupabaseModule.kt
```kotlin
package com.periodsaathi.app.data.di

import com.periodsaathi.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}
```

---

# ═══════════════════════════════════════════
# FIX 7 — ALL 18 SCREENS (COMPLETE IMPLEMENTATIONS)
# ═══════════════════════════════════════════

**Problem:** Most screens don't exist. Buttons don't work.
**Rule for every screen:**
  - Write the FULL ViewModel with `@HiltViewModel`
  - Write the FULL Screen Composable
  - ALL buttons have real `onClick` handlers
  - `@Preview` at the bottom with fake data
  - Handle Loading/Success/Error

## ── SCREEN 1: Home ──────────────────────────────────────────

### FILE: ui/screens/home/HomeViewModel.kt
```kotlin
package com.periodsaathi.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.periodsaathi.app.ui.components.MascotEmotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val userName: String            = "Friend",
    val cycleDay: Int               = 1,
    val totalCycleDays: Int         = 28,
    val phaseName: String           = "Follicular Phase",
    val phaseEmoji: String          = "🌱",
    val nextPeriodDays: Int?        = null,  // null = not enough cycles
    val nextPeriodDate: String?     = null,
    val waterGlasses: Int           = 0,
    val waterGoal: Int              = 8,
    val streakCount: Int            = 0,
    val mascotEmotion: MascotEmotion= MascotEmotion.HAPPY,
    val mascotMoodText: String      = "Feeling good today! 💕",
    val isRestDay: Boolean          = false,
    val showMedicalDisclaimer: Boolean = false,
    val todaySymptomCount: Int      = 0,
    val isLoading: Boolean          = true,
    val errorMessage: String?       = null,
    val cyclePhaseDescription: String = "Great time to start new projects 🌟",
    val aiForecasetText: String     = "Moderate energy day — pace yourself 💕",
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { loadHomeData() }

    private fun loadHomeData() {
        viewModelScope.launch {
            // TODO: inject and use real repositories
            // For now: populate with realistic mock data
            _state.update {
                it.copy(
                    isLoading  = false,
                    userName   = "Priya",
                    cycleDay   = 14,
                    phaseName  = "Ovulation Phase",
                    phaseEmoji = "✨",
                    nextPeriodDays  = 14,
                    nextPeriodDate  = "June 1",
                    waterGlasses    = 3,
                    streakCount     = 7,
                    mascotEmotion   = MascotEmotion.HAPPY,
                    mascotMoodText  = "Your energy is peaking today! ✨",
                    cyclePhaseDescription = "Your most social and energetic days 🌟",
                    aiForecasetText = "High energy expected — great day to connect!"
                )
            }
        }
    }

    fun addWater() {
        viewModelScope.launch {
            val current = _state.value.waterGlasses
            if (current < _state.value.waterGoal) {
                _state.update { it.copy(waterGlasses = current + 1) }
                // TODO: persist to Room
            }
        }
    }

    fun dismissRestDay() { _state.update { it.copy(isRestDay = false) } }

    fun acceptMedicalDisclaimer() {
        _state.update { it.copy(showMedicalDisclaimer = false) }
        // TODO: persist acceptance to Room
    }

    fun onMascotTapped() {
        // Tips are handled in SaathiMascot component directly
    }
}
```

### FILE: ui/screens/home/HomeScreen.kt
```kotlin
package com.periodsaathi.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.components.*
import com.periodsaathi.app.ui.navigation.*
import com.periodsaathi.app.ui.theme.*
import java.util.Calendar

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    // Staggered entry animations
    val sectionAnimations = List(6) { remember { Animatable(0f) } }
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            sectionAnimations.forEachIndexed { i, anim ->
                kotlinx.coroutines.delay(i * 80L)
                anim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
        }
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BlushPink)
        }
        return
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11  -> "Good morning"
        in 12..17 -> "Good afternoon"
        else      -> "Good evening"
    }

    Scaffold(
        containerColor = CreamWhite,
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // ── Medical Disclaimer ────────────────────────────────
            if (state.showMedicalDisclaimer) {
                item {
                    GlassCard(tint = ButterYellow) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("⚕️ Medical Disclaimer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold)
                            Text("Period Saathi is a personal tracking tool, not a medical device. Always consult your doctor for medical concerns.",
                                style = MaterialTheme.typography.bodySmall)
                            PrimaryButton("Got it 💕", onClick = { viewModel.acceptMedicalDisclaimer() })
                        }
                    }
                }
            }

            // ── REST DAY Banner ───────────────────────────────────
            item {
                AnimatedVisibility(
                    visible = state.isRestDay,
                    enter   = slideInVertically { -it } + fadeIn(),
                    exit    = slideOutVertically { -it } + fadeOut()
                ) {
                    GlassCard(tint = SoftCoral) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("🛌 REST DAY",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color(0xFFB84040), fontWeight = FontWeight.ExtraBold)
                                Text("Your flow is heaviest today. You deserve to rest 💕",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            ScaleButton(onClick = { viewModel.dismissRestDay() }) {
                                Icon(Icons.Rounded.Close, "Dismiss", tint = Color.Gray)
                            }
                        }
                    }
                }
            }

            // ── Greeting ──────────────────────────────────────────
            item {
                Box(modifier = Modifier.graphicsLayer { alpha = sectionAnimations[0].value }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("$greeting, ${state.userName} 🌸",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold)
                            Text("Day ${state.cycleDay} of your cycle",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftPurple)
                        }
                        ScaleButton(onClick = { navController.navigate(Settings) }) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(BlushPink.copy(0.2f), BadgeShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Person, "Profile", tint = BlushPink)
                            }
                        }
                    }
                }
            }

            // ── Phase Badge ───────────────────────────────────────
            item {
                Box(modifier = Modifier.graphicsLayer {
                    alpha        = sectionAnimations[0].value
                    translationY = (1f - sectionAnimations[0].value) * 40f
                }) {
                    GlassCard(tint = SoftLavender) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${state.phaseEmoji} ${state.phaseName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold, color = SoftPurple)
                                Text(state.cyclePhaseDescription,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            ScaleButton(onClick = { navController.navigate(Insights) }) {
                                Text("Details →", style = MaterialTheme.typography.labelMedium, color = BlushPink)
                            }
                        }
                    }
                }
            }

            // ── AI Forecast Card ──────────────────────────────────
            item {
                Box(modifier = Modifier.graphicsLayer {
                    alpha        = sectionAnimations[1].value
                    translationY = (1f - sectionAnimations[1].value) * 40f
                }) {
                    GlassCard(tint = ButterYellow) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("🌤", style = MaterialTheme.typography.headlineLarge)
                            Column(Modifier.weight(1f)) {
                                Text("Today's Cycle Forecast ✦",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SoftPurple)
                                Text(state.aiForecasetText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold)
                            }
                            Surface(shape = BadgeShape, color = BlushPink.copy(0.2f)) {
                                Text("AI", modifier = Modifier.padding(6.dp, 2.dp),
                                    style = MaterialTheme.typography.labelSmall, color = BlushPink,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ── Mascot Hero Card ──────────────────────────────────
            item {
                Box(modifier = Modifier.graphicsLayer {
                    alpha        = sectionAnimations[2].value
                    translationY = (1f - sectionAnimations[2].value) * 40f
                }) {
                    GlassCard {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SaathiMascot(
                                emotion = state.mascotEmotion,
                                size    = 140.dp,
                                showTipOnTap = true
                            )
                            Text(state.mascotMoodText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftPurple)
                        }
                    }
                }
            }

            // ── Quick Actions ─────────────────────────────────────
            item {
                Box(modifier = Modifier.graphicsLayer {
                    alpha        = sectionAnimations[3].value
                    translationY = (1f - sectionAnimations[3].value) * 40f
                }) {
                    val actions = listOf(
                        Triple("📝", "Log Today",  { navController.navigate(DayLog(System.currentTimeMillis())) }),
                        Triple("💧", "Water",      { viewModel.addWater() }),
                        Triple("💊", "Medicine",   { navController.navigate(Wellness) }),
                        Triple("🌿", "Remedies",   { navController.navigate(Remedies) }),
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(actions) { _, (emoji, label, action) ->
                            ScaleButton(onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                action()
                            }) {
                                Column(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .background(GlassWhite, CardShape)
                                        .border(1.dp, GlassBorder, CardShape)
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(emoji, style = MaterialTheme.typography.headlineMedium)
                                    Text(label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SoftPurple)
                                }
                            }
                        }
                    }
                }
            }

            // ── Stats Grid ────────────────────────────────────────
            item {
                Box(modifier = Modifier.graphicsLayer {
                    alpha        = sectionAnimations[4].value
                    translationY = (1f - sectionAnimations[4].value) * 40f
                }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Water card
                        GlassCard(modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Wellness) }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💧", style = MaterialTheme.typography.headlineMedium)
                                Text("${state.waterGlasses}/${state.waterGoal}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold, color = BabyBlue)
                                Text("Glasses", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        // Streak card
                        GlassCard(modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Challenges) }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔥", style = MaterialTheme.typography.headlineMedium)
                                Text("${state.streakCount}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold, color = SoftCoral)
                                Text("Day streak", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        // Symptoms card
                        GlassCard(modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Calendar) }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📋", style = MaterialTheme.typography.headlineMedium)
                                Text("${state.todaySymptomCount}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold, color = SoftLavender)
                                Text("Symptoms", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // ── Prediction Banner ─────────────────────────────────
            item {
                Box(modifier = Modifier.graphicsLayer {
                    alpha        = sectionAnimations[5].value
                    translationY = (1f - sectionAnimations[5].value) * 40f
                }) {
                    if (state.nextPeriodDays != null) {
                        GlassCard(tint = SoftCoral, onClick = { navController.navigate(Calendar) }) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("🗓 Next period in ${state.nextPeriodDays} days",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold)
                                    Text(state.nextPeriodDate ?: "",
                                        style = MaterialTheme.typography.bodySmall, color = SoftPurple)
                                }
                                Surface(shape = ChipShape, color = BlushPink.copy(0.2f)) {
                                    Text("±2 days", modifier = Modifier.padding(8.dp, 4.dp),
                                        style = MaterialTheme.typography.labelSmall, color = BlushPink)
                                }
                            }
                        }
                    } else {
                        GlassCard(tint = BabyBlue) {
                            Text("Log 3 complete cycles to unlock predictions 🔮",
                                style = MaterialTheme.typography.bodyMedium, color = SoftPurple)
                        }
                    }
                }
            }

            // ── Quick Nav row ─────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        Triple("💌", "Partner", { navController.navigate(PartnerMode) }),
                        Triple("📓", "Journal",  { navController.navigate(Journal) }),
                        Triple("🏆", "Challenges",{ navController.navigate(Challenges) }),
                        Triple("👗", "Wardrobe", { navController.navigate(Wardrobe) }),
                    ).forEach { (emoji, label, action) ->
                        ScaleButton(onClick = action, modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GlassWhite, SmallCardShape)
                                    .border(1.dp, GlassBorder, SmallCardShape)
                                    .padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(emoji)
                                Text(label, style = MaterialTheme.typography.labelSmall, color = SoftPurple)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF8F5)
@Composable
private fun HomeScreenPreview() {
    PeriodSaathiTheme {
        HomeScreen(navController = rememberNavController())
    }
}
```

---

## ── SCREEN 2: Calendar (with working day log) ───────────────

### FILE: ui/screens/calendar/CalendarViewModel.kt
```kotlin
package com.periodsaathi.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CalendarUiState(
    val yearMonth: YearMonth     = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val showLogSheet: Boolean    = false,
    val loggedDays: Map<LocalDate, Int> = emptyMap(),  // date → intensity 1-5
    val predictedDays: Set<LocalDate>   = emptySet(),
    val fertileDays: Set<LocalDate>     = emptySet(),
    val pmsDays: Set<LocalDate>         = emptySet(),
    val fertilityMode: String           = "NEUTRAL",  // NEUTRAL/PLANNING/AVOIDING
)

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init { loadCalendarData() }

    private fun loadCalendarData() {
        viewModelScope.launch {
            val today = LocalDate.now()
            // Mock data — replace with Room queries
            _state.update { it.copy(
                loggedDays    = mapOf(
                    today.minusDays(5) to 2, today.minusDays(4) to 4,
                    today.minusDays(3) to 3, today.minusDays(2) to 2,
                    today.minusDays(1) to 1
                ),
                predictedDays = setOf(today.plusDays(14), today.plusDays(15), today.plusDays(16)),
                fertileDays   = setOf(today.plusDays(7), today.plusDays(8), today.plusDays(9),
                                      today.plusDays(10), today.plusDays(11)),
                pmsDays       = setOf(today.plusDays(7), today.plusDays(8), today.plusDays(9))
            )}
        }
    }

    fun nextMonth()     { _state.update { it.copy(yearMonth = it.yearMonth.plusMonths(1)) } }
    fun previousMonth() { _state.update { it.copy(yearMonth = it.yearMonth.minusMonths(1)) } }
    fun selectDate(date: LocalDate) { _state.update { it.copy(selectedDate = date, showLogSheet = true) } }
    fun closeLogSheet() { _state.update { it.copy(showLogSheet = false, selectedDate = null) } }
    fun setFertilityMode(mode: String) { _state.update { it.copy(fertilityMode = mode) } }
    fun logEntry(date: LocalDate, intensity: Int, symptoms: List<String>) {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    loggedDays  = state.loggedDays + (date to intensity),
                    showLogSheet = false
                )
            }
            // TODO: persist to Room
        }
    }
}
```

### FILE: ui/screens/calendar/CalendarScreen.kt
```kotlin
package com.periodsaathi.app.ui.screens.calendar

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.components.*
import com.periodsaathi.app.ui.navigation.BottomNavBar
import com.periodsaathi.app.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JTextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = CreamWhite,
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScaleButton(onClick = { viewModel.previousMonth() }) {
                    Icon(Icons.Rounded.ChevronLeft, "Previous month", tint = SoftPurple)
                }
                AnimatedContent(targetState = state.yearMonth) { ym ->
                    Text(
                        text       = ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                ScaleButton(onClick = { viewModel.nextMonth() }) {
                    Icon(Icons.Rounded.ChevronRight, "Next month", tint = SoftPurple)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Day-of-week headers
            Row(Modifier.fillMaxWidth()) {
                listOf("Su","Mo","Tu","We","Th","Fr","Sa").forEach { d ->
                    Text(d, modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium, color = SoftPurple)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Calendar days
            val daysInMonth = generateCalendarDays(state.yearMonth)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement   = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(daysInMonth, key = { it?.toString() ?: "empty_$it" }) { date ->
                    if (date == null) {
                        Box(Modifier.size(40.dp))
                    } else {
                        DayCell(
                            date       = date,
                            intensity  = state.loggedDays[date],
                            isToday    = date == LocalDate.now(),
                            isPredicted= date in state.predictedDays,
                            isFertile  = date in state.fertileDays,
                            isPMS      = date in state.pmsDays,
                            isSelected = state.selectedDate == date,
                            isFuture   = date.isAfter(LocalDate.now()),
                            onClick    = { if (!date.isAfter(LocalDate.now())) viewModel.selectDate(date) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Fertility mode toggle
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Fertility Mode", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("NEUTRAL" to "Neutral 🌙",
                               "PLANNING" to "Planning 👶",
                               "AVOIDING" to "Avoiding 🌿").forEach { (mode, label) ->
                            PastelChip(
                                label    = label,
                                selected = state.fertilityMode == mode,
                                onClick  = { viewModel.setFertilityMode(mode) },
                                accentColor = when(mode) {
                                    "PLANNING" -> MintGreen
                                    "AVOIDING" -> SoftCoral
                                    else       -> SoftLavender
                                }
                            )
                        }
                    }
                }
            }
        }

        // Day log bottom sheet
        if (state.showLogSheet && state.selectedDate != null) {
            DayLogBottomSheet(
                date      = state.selectedDate!!,
                onDismiss = { viewModel.closeLogSheet() },
                onSave    = { intensity, symptoms -> viewModel.logEntry(state.selectedDate!!, intensity, symptoms) }
            )
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate, intensity: Int?, isToday: Boolean, isPredicted: Boolean,
    isFertile: Boolean, isPMS: Boolean, isSelected: Boolean, isFuture: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        intensity == 5 -> PeriodVeryHeavy
        intensity == 4 -> PeriodHeavy
        intensity == 3 -> PeriodMedium
        intensity == 2 -> PeriodLight
        intensity == 1 -> Color(0xFFFFF0F4)
        isFertile       -> FertileMint.copy(alpha = 0.5f)
        isPredicted     -> SoftCoral.copy(alpha = 0.25f)
        else            -> Color.Transparent
    }
    val borderColor = when {
        isSelected  -> BlushPink
        isToday     -> BlushPink.copy(alpha = 0.7f)
        isPredicted -> SoftCoral.copy(alpha = 0.5f)
        isPMS       -> PMSLilac
        else        -> Color.Transparent
    }

    ScaleButton(onClick = onClick, enabled = !isFuture) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(bgColor, BadgeShape)
                .border(
                    width  = if (borderColor != Color.Transparent) 2.dp else 0.dp,
                    color  = borderColor,
                    shape  = BadgeShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isFuture   -> Color.Gray.copy(alpha = 0.4f)
                    intensity != null && intensity >= 3 -> Color.White
                    else       -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayLogBottomSheet(
    date: LocalDate, onDismiss: () -> Unit,
    onSave: (intensity: Int, symptoms: List<String>) -> Unit
) {
    var selectedIntensity by remember { mutableStateOf(0) }
    val selectedSymptoms = remember { mutableStateListOf<String>() }
    val symptoms = listOf("Cramps 😣","Bloating","Headache","Mood swings","Fatigue","Cravings","Backache","Spotting")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = rememberModalBottomSheetState(),
        shape            = BottomSheetShape,
        containerColor   = CreamWhite
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Log ${date.format(DateTimeFormatter.ofPattern("MMM d"))} 📅",
                style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Text("Bleeding intensity", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1 to "Spotting 🩸", 2 to "Light 🌸", 3 to "Medium 💗", 4 to "Heavy ❤️", 5 to "Very Heavy").forEachIndexed { _, (level, label) ->
                    PastelChip(
                        label    = label, selected = selectedIntensity == level,
                        onClick  = { selectedIntensity = level },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text("Symptoms", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                symptoms.forEach { s ->
                    PastelChip(
                        label    = s,
                        selected = s in selectedSymptoms,
                        onClick  = { if (s in selectedSymptoms) selectedSymptoms.remove(s) else selectedSymptoms.add(s) }
                    )
                }
            }

            PrimaryButton(
                text    = "Save Entry ✓",
                onClick = { onSave(selectedIntensity, selectedSymptoms.toList()) },
                enabled = selectedIntensity > 0
            )
        }
    }
}

// Generates nullable LocalDate list for calendar grid (nulls = empty cells before day 1)
private fun generateCalendarDays(yearMonth: YearMonth): List<LocalDate?> {
    val firstDay = yearMonth.atDay(1)
    val offset   = firstDay.dayOfWeek.value % 7  // 0=Sun
    val days     = mutableListOf<LocalDate?>()
    repeat(offset) { days.add(null) }
    for (d in 1..yearMonth.lengthOfMonth()) { days.add(yearMonth.atDay(d)) }
    return days
}

@Preview @Composable
private fun CalendarPreview() = PeriodSaathiTheme { CalendarScreen(rememberNavController()) }
```

---

## ── SCREEN 3: Wellness ──────────────────────────────────────

### FILE: ui/screens/wellness/WellnessScreen.kt
```kotlin
package com.periodsaathi.app.ui.screens.wellness

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.components.*
import com.periodsaathi.app.ui.navigation.BottomNavBar
import com.periodsaathi.app.ui.navigation.BreathingMode
import com.periodsaathi.app.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WellnessScreen(
    navController: NavController
) {
    val haptic = LocalHapticFeedback.current
    var waterGlasses by remember { mutableIntStateOf(3) }
    val waterGoal = 8
    val habits = remember {
        mutableStateListOf(
            "🥗 Ate a healthy meal" to false,
            "😴 Got enough sleep"  to false,
            "🧘 Did some movement" to false,
            "💊 Took my medicine"  to false,
            "🛁 Self-care routine" to false,
        )
    }

    val waterProgress by animateFloatAsState(
        targetValue    = waterGlasses.toFloat() / waterGoal,
        animationSpec  = spring()
    )

    // Wave phase animation
    val infiniteTransition = rememberInfiniteTransition()
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
    )

    Scaffold(
        containerColor = CreamWhite,
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Wellness 💧", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)

            // Water ring card
            GlassCard(tint = BabyBlue) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated water ring
                    Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
                        Canvas(Modifier.fillMaxSize()) {
                            val stroke = 20.dp.toPx()
                            val r = (size.minDimension - stroke) / 2f
                            val c = Offset(size.width / 2, size.height / 2)
                            // Background arc
                            drawArc(Color.Gray.copy(0.2f), -90f, 360f, false, topLeft = Offset(c.x - r, c.y - r),
                                size = Size(r * 2, r * 2), style = Stroke(stroke, cap = StrokeCap.Round))
                            // Progress arc
                            drawArc(Brush.sweepGradient(listOf(BabyBlue, Color(0xFF7EC8E3)), c),
                                -90f, waterProgress * 360f, false,
                                topLeft = Offset(c.x - r, c.y - r), size = Size(r * 2, r * 2),
                                style = Stroke(stroke, cap = StrokeCap.Round))
                            // Wave fill inside
                            val fillY = size.height * (1f - waterProgress) * 0.8f + size.height * 0.1f
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(0f, fillY)
                                val wavelength = size.width / 2f
                                for (x in 0..size.width.toInt()) {
                                    val y = fillY + 8f * sin(2 * PI.toFloat() * x / wavelength + wavePhase)
                                    lineTo(x.toFloat(), y)
                                }
                                lineTo(size.width, size.height); lineTo(0f, size.height); close()
                            }
                            drawPath(path, BabyBlue.copy(alpha = 0.3f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$waterGlasses", style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.ExtraBold, color = Color(0xFF0077B6))
                            Text("of $waterGoal glasses", style = MaterialTheme.typography.bodySmall, color = SoftPurple)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PastelChip("+1 Glass 🥤", waterGlasses < waterGoal,
                            onClick = { if (waterGlasses < waterGoal) { waterGlasses++; haptic.performHapticFeedback(HapticFeedbackType.LongPress) } },
                            accentColor = BabyBlue)
                        PastelChip("+500ml 💧", waterGlasses < waterGoal,
                            onClick = { if (waterGlasses < waterGoal) { waterGlasses = minOf(waterGoal, waterGlasses + 1); haptic.performHapticFeedback(HapticFeedbackType.LongPress) } },
                            accentColor = BabyBlue)
                    }
                }
            }

            // Habits checklist
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Today's Self-Care 🌸",
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    habits.forEachIndexed { i, (label, checked) ->
                        ScaleButton(onClick = {
                            habits[i] = label to !checked
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (checked) MintGreen.copy(0.2f) else Color.Transparent,
                                        SmallCardShape
                                    )
                                    .padding(12.dp, 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(label, style = MaterialTheme.typography.bodyMedium,
                                    color = if (checked) Color(0xFF2A7A5A) else MaterialTheme.colorScheme.onSurface)
                                if (checked) Text("✓",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold, color = MintGreen)
                            }
                        }
                    }
                }
            }

            // Breathing shortcut
            PrimaryButton(
                text = "Start Breathing Exercise 😮‍💨",
                onClick = { navController.navigate(BreathingMode) }
            )
        }
    }
}

@Preview @Composable
private fun WellnessPreview() = PeriodSaathiTheme { WellnessScreen(rememberNavController()) }
```

---

## ── SCREEN 4: Partner Mode ──────────────────────────────────

### FILE: ui/screens/partner/PartnerModeScreen.kt
```kotlin
package com.periodsaathi.app.ui.screens.partner

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.components.*
import com.periodsaathi.app.ui.theme.*

private val careRequests = listOf(
    "🍫" to "Need chocolate & a hug",
    "🎧" to "Just want quiet time",
    "💆" to "Could use a massage",
    "🍵" to "Make me ginger tea",
    "👂" to "Just listen, don't fix",
    "🛌" to "I need to rest today",
    "😭" to "Having a rough time",
    "🌸" to "Send me good vibes",
    "🫂" to "Need a hug right now",
    "🎬" to "Watch something together",
)

@Composable
fun PartnerModeScreen(navController: NavController) {
    val context = LocalContext.current
    val selectedRequests = remember { mutableStateSetOf<String>() }
    var customMessage by remember { mutableStateOf("") }
    var isSent by remember { mutableStateOf(false) }
    var showPrivacyInfo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CreamWhite,
        topBar = {
            Row(Modifier.padding(16.dp, 12.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScaleButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, "Back")
                }
                Text("Partner Mode 💕", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Privacy badge
            GlassCard(
                tint    = MintGreen,
                onClick = { showPrivacyInfo = !showPrivacyInfo }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Shield, "Privacy", tint = Color(0xFF2A7A5A))
                        Text("🛡️ Zero medical data shared",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = Color(0xFF2A7A5A))
                    }
                    AnimatedVisibility(showPrivacyInfo) {
                        Text("Only your selected care requests are shared — no cycle dates, flow data, or health information. This uses your phone's share sheet, nothing goes to our servers.",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text("What do you need right now? 💕",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(360.dp)
            ) {
                items(careRequests) { (emoji, label) ->
                    val isSelected = label in selectedRequests
                    GlassCard(
                        tint    = if (isSelected) BlushPink else Color.Transparent,
                        onClick = {
                            if (isSelected) selectedRequests.remove(label)
                            else selectedRequests.add(label)
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(emoji, style = MaterialTheme.typography.headlineMedium)
                            Text(label, style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            OutlinedTextField(
                value         = customMessage,
                onValueChange = { if (it.length <= 80) customMessage = it },
                label         = { Text("+ Add your own message (optional)") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = InputShape,
                trailingIcon  = { Text("${customMessage.length}/80",
                    style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
            )

            AnimatedContent(targetState = isSent) { sent ->
                if (sent) {
                    GlassCard(tint = MintGreen) {
                        Text("Sent! They'll know you need them 💕",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = Color(0xFF2A7A5A),
                            modifier = Modifier.fillMaxWidth())
                    }
                } else {
                    PrimaryButton(
                        text    = "Send care request 💌",
                        enabled = selectedRequests.isNotEmpty(),
                        onClick = {
                            val message = buildString {
                                append("Hey! I need some care right now 💕\n\n")
                                selectedRequests.forEach { r -> append("• $r\n") }
                                if (customMessage.isNotBlank()) append("\n$customMessage")
                                append("\n\n— sent from Period Saathi")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, message)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share via"))
                            isSent = true
                        }
                    )
                }
            }
        }
    }
}

@Preview @Composable
private fun PartnerModePreview() = PeriodSaathiTheme { PartnerModeScreen(rememberNavController()) }
```

---

## ── SCREEN 5: Remedies + Flip Cards ─────────────────────────

### FILE: ui/screens/remedies/RemediesScreen.kt
```kotlin
package com.periodsaathi.app.ui.screens.remedies

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.components.*
import com.periodsaathi.app.ui.navigation.YogaFlow
import com.periodsaathi.app.ui.theme.*

data class Remedy(
    val id: String, val name: String, val emoji: String,
    val frontColor: Color, val backColor: Color,
    val ingredients: List<String>, val steps: List<String>,
    val helpfulFor: List<String>
)

private val remedies = listOf(
    Remedy("ginger", "Ginger Tea 🫚", "🫚", BlushPink, PeriodLight,
        listOf("1 cup water", "1 tsp grated ginger", "Honey to taste"),
        listOf("Boil water", "Add grated ginger, simmer 5 min", "Strain, add honey", "Drink warm"),
        listOf("Cramps", "Nausea", "Inflammation")),
    Remedy("jaggery", "Jaggery Mix 🍬", "🍬", ButterYellow, Color(0xFFFFF9E0),
        listOf("2 tsp jaggery", "1 tsp sesame seeds", "Warm water"),
        listOf("Mix jaggery and sesame", "Consume with warm water", "Take twice daily during period"),
        listOf("Iron boost", "Fatigue", "Cramps")),
    Remedy("fennel", "Fennel Water 🌿", "🌿", MintGreen, Color(0xFFE8FAF2),
        listOf("1 tsp fennel seeds", "1 cup water"),
        listOf("Boil seeds in water 5 min", "Strain and cool slightly", "Sip slowly"),
        listOf("Bloating", "Digestion", "Cramps")),
    Remedy("turmeric", "Haldi Doodh 🥛", "🥛", ButterYellow, Color(0xFFFFF8E0),
        listOf("1 cup warm milk", "1/4 tsp turmeric", "Pinch of black pepper"),
        listOf("Warm the milk", "Add turmeric and pepper", "Stir well, drink before bed"),
        listOf("Inflammation", "Sleep", "Immunity")),
    Remedy("ajwain", "Ajwain Water 🌾", "🌾", SoftCoral, PeriodLight,
        listOf("1 tsp ajwain", "1 glass warm water"),
        listOf("Add ajwain to warm water", "Let soak 10 min", "Drink on empty stomach"),
        listOf("Cramps", "Bloating", "Digestion")),
)

@Composable
fun RemediesScreen(navController: NavController) {
    val flippedCards = remember { mutableStateSetOf<String>() }
    var hotBagPos by remember { mutableFloatStateOf(0.4f) }

    Scaffold(containerColor = CreamWhite) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScaleButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, "Back")
                }
                Text("Natural Remedies 🌿",
                    style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            }

            // Flip cards
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(remedies, key = { it.id }) { remedy ->
                    val isFlipped = remedy.id in flippedCards
                    val rotation by animateFloatAsState(
                        if (isFlipped) 180f else 0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                    )

                    Box(Modifier.size(160.dp, 200.dp)) {
                        // Front face
                        if (rotation <= 90f) {
                            Surface(
                                shape = CardShape,
                                color = remedy.frontColor.copy(0.3f),
                                modifier = Modifier.fillMaxSize()
                                    .graphicsLayer {
                                        rotationY = rotation
                                        cameraDistance = 12f * density
                                    }
                                    .clickable { flippedCards.add(remedy.id) }
                            ) {
                                Column(Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center) {
                                    Text(remedy.emoji, style = MaterialTheme.typography.displayLarge)
                                    Spacer(Modifier.height(8.dp))
                                    Text(remedy.name, style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Tap to reveal 👆",
                                        style = MaterialTheme.typography.labelSmall, color = SoftPurple)
                                }
                            }
                        }
                        // Back face
                        if (rotation > 90f) {
                            Surface(
                                shape = CardShape,
                                color = remedy.backColor,
                                modifier = Modifier.fillMaxSize()
                                    .graphicsLayer {
                                        rotationY = rotation - 180f
                                        cameraDistance = 12f * density
                                    }
                                    .clickable { flippedCards.remove(remedy.id) }
                            ) {
                                Column(Modifier.padding(12.dp).verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Steps:", style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold)
                                    remedy.steps.forEachIndexed { i, step ->
                                        Text("${i+1}. $step", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text("Helps with:", style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold, color = SoftPurple)
                                    remedy.helpfulFor.forEach { h ->
                                        Text("• $h", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Hot bag safety slider
            GlassCard(tint = if (hotBagPos > 0.8f) SoftCoral else MintGreen.copy(0.3f)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hot Bag Safety Guide 🌡️",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    val safeLabel = when {
                        hotBagPos < 0.6f -> "✅ Safe range — comfortable warmth"
                        hotBagPos < 0.8f -> "⚠️ Getting warm — take care"
                        else             -> "🚫 Too hot! Risk of burns"
                    }
                    Text(safeLabel, style = MaterialTheme.typography.bodySmall,
                        color = if (hotBagPos > 0.8f) Color(0xFFB84040) else MaterialTheme.colorScheme.onSurface)

                    Slider(
                        value         = hotBagPos,
                        onValueChange = { hotBagPos = it },
                        onValueChangeFinished = {
                            if (hotBagPos > 0.8f) { hotBagPos = 0.6f }  // snap back from danger
                        },
                        colors = SliderDefaults.colors(
                            thumbColor      = if (hotBagPos > 0.8f) Color(0xFFE53935) else BlushPink,
                            activeTrackColor= if (hotBagPos > 0.8f) SoftCoral else MintGreen
                        )
                    )

                    if (hotBagPos > 0.8f) {
                        Text("💡 Try a rice bag or microwaveable plushie instead — safer and just as cozy!",
                            style = MaterialTheme.typography.bodySmall, color = Color(0xFFB84040))
                    }
                }
            }

            // Yoga flow CTA
            GlassCard(tint = SoftLavender) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cramp Relief Yoga Flow 🧘", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("5 poses · 4 minutes · with breathing circle",
                        style = MaterialTheme.typography.bodySmall)
                    PrimaryButton("Start Yoga Flow →", onClick = { navController.navigate(YogaFlow) })
                }
            }
        }
    }
}

@Preview @Composable
private fun RemediesPreview() = PeriodSaathiTheme { RemediesScreen(rememberNavController()) }
```

---

## ── SCREEN 6: Yoga + Breathing Circle ──────────────────────

### FILE: ui/screens/remedies/YogaFlowScreen.kt
```kotlin
package com.periodsaathi.app.ui.screens.remedies

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.components.ScaleButton
import com.periodsaathi.app.ui.theme.*
import kotlinx.coroutines.delay

data class YogaPose(val name: String, val emoji: String, val cue: String, val durationSeconds: Int)

private val poses = listOf(
    YogaPose("Child's Pose", "🧎", "Kneel, sit back on heels, arms forward on floor", 60),
    YogaPose("Cat-Cow", "🐱", "On hands & knees: arch back up (cat), drop belly (cow)", 60),
    YogaPose("Supine Twist", "🌀", "Lie on back, bring knee across body, hold each side", 60),
    YogaPose("Legs Up Wall", "🦵", "Lie on back, legs vertical against wall, relax", 90),
    YogaPose("Savasana", "😴", "Lie flat, close eyes, breathe naturally, let go", 60),
)

enum class BreathingPhase { INHALE, HOLD, EXHALE }

@Composable
fun YogaFlowScreen(navController: NavController) {
    var poseIndex    by remember { mutableIntStateOf(0) }
    var isPlaying    by remember { mutableStateOf(true) }
    var breathPhase  by remember { mutableStateOf(BreathingPhase.INHALE) }

    val currentPose = poses[poseIndex.coerceIn(0, poses.lastIndex)]

    // Breathing circle animation
    val radius = remember { Animatable(120f) }
    val glowAlpha = remember { Animatable(0.4f) }

    LaunchedEffect(isPlaying, poseIndex) {
        while (isPlaying) {
            // Inhale 4s
            breathPhase = BreathingPhase.INHALE
            radius.animateTo(170f, tween(4000))
            glowAlpha.animateTo(1f, tween(4000))
            // Hold 2s
            breathPhase = BreathingPhase.HOLD
            delay(2000)
            // Exhale 6s
            breathPhase = BreathingPhase.EXHALE
            radius.animateTo(120f, tween(6000))
            glowAlpha.animateTo(0.4f, tween(6000))
        }
    }

    val breathText = when (breathPhase) {
        BreathingPhase.INHALE -> "Breathe In..."
        BreathingPhase.HOLD   -> "Hold..."
        BreathingPhase.EXHALE -> "Breathe Out..."
    }
    val circleColor = when (breathPhase) {
        BreathingPhase.INHALE -> BlushPink
        BreathingPhase.HOLD   -> ButterYellow
        BreathingPhase.EXHALE -> BabyBlue
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DarkNavy),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: pose name + exit
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column {
                    Text(currentPose.name, style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, color = Color.White)
                    Text(currentPose.cue, style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.6f))
                }
                ScaleButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.Close, "Exit", tint = Color.White.copy(0.6f))
                }
            }

            Text(currentPose.emoji, style = MaterialTheme.typography.displayLarge)

            // Breathing circle
            Box(Modifier.size(280.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    // Glow
                    drawCircle(circleColor.copy(alpha = glowAlpha.value * 0.3f),
                        radius = radius.value + 40f, center = center)
                    // Main circle
                    drawCircle(circleColor.copy(alpha = 0.3f), radius = radius.value, center = center)
                    drawCircle(Color.Transparent, radius = radius.value, center = center,
                        style = Stroke(6.dp.toPx()))
                    drawCircle(circleColor.copy(0.7f), radius = radius.value, center = center,
                        style = Stroke(3.dp.toPx()))
                }
                AnimatedContent(targetState = breathText) { text ->
                    Text(text, style = MaterialTheme.typography.titleMedium,
                        color = Color.White, fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center)
                }
            }

            // Pose dots + next button
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    poses.forEachIndexed { i, _ ->
                        Box(Modifier.size(if (i == poseIndex) 12.dp else 8.dp).background(
                            if (i == poseIndex) Color.White else Color.White.copy(0.3f), BadgeShape))
                    }
                }
                if (poseIndex < poses.lastIndex) {
                    ScaleButton(onClick = { poseIndex++ }) {
                        Box(Modifier.background(BlushPink.copy(0.3f), ButtonShape).padding(24.dp, 12.dp)) {
                            Text("Next Pose →", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    ScaleButton(onClick = { navController.popBackStack() }) {
                        Box(Modifier.background(MintGreen.copy(0.3f), ButtonShape).padding(24.dp, 12.dp)) {
                            Text("Finish Flow ✓", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Preview @Composable
private fun YogaPreview() = PeriodSaathiTheme { YogaFlowScreen(rememberNavController()) }
```

---

## ── SCREEN 7: Full Profile/Settings ────────────────────────

### FILE: ui/screens/settings/SettingsScreen.kt
```kotlin
package com.periodsaathi.app.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.components.*
import com.periodsaathi.app.ui.navigation.*
import com.periodsaathi.app.ui.theme.*

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    var biometricEnabled  by remember { mutableStateOf(false) }
    var stealthEnabled    by remember { mutableStateOf(false) }
    var soundEnabled      by remember { mutableStateOf(true)  }
    var hapticEnabled     by remember { mutableStateOf(true)  }
    var notifEnabled      by remember { mutableStateOf(false) }
    var showDeleteDialog  by remember { mutableStateOf(false) }
    var selectedLanguage  by remember { mutableStateOf("English") }
    val languages = listOf("English", "हिंदी", "मराठी", "தமிழ்", "తెలుగు", "বাংলা")

    Scaffold(
        containerColor = CreamWhite,
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Profile & Settings 👤",
                style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)

            // Account card
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Account", Icons.Rounded.Person)
                    SettingsRow("Sign in with Google 🔑", "Sync across devices",
                        onClick = { navController.navigate(Login) })
                    HorizontalDivider(color = BlushPink.copy(0.2f))
                    SettingsRow("Export Data 📤", "CSV or PDF — always free",
                        onClick = { navController.navigate(ReportExport) })
                }
            }

            // Language
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Language 🌐", Icons.Rounded.Language)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        languages.forEach { lang ->
                            PastelChip(lang, lang == selectedLanguage,
                                onClick = { selectedLanguage = lang })
                        }
                    }
                }
            }

            // Security & Privacy
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Privacy & Security 🔒", Icons.Rounded.Security)
                    ToggleRow("Biometric Lock 👁", "Face ID or fingerprint",
                        biometricEnabled) { biometricEnabled = it }
                    HorizontalDivider(color = BlushPink.copy(0.2f))
                    ToggleRow("Stealth Mode 🥷", "Disguise app on home screen",
                        stealthEnabled) { stealthEnabled = it }
                    HorizontalDivider(color = BlushPink.copy(0.2f))
                    Text("🔒 All health data stays on your device — never sold.",
                        style = MaterialTheme.typography.bodySmall, color = SoftPurple)
                }
            }

            // Preferences
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("Preferences ⚙️", Icons.Rounded.Tune)
                    ToggleRow("Sound Effects 🔊", "Soft chimes on actions", soundEnabled)  { soundEnabled = it }
                    HorizontalDivider(color = BlushPink.copy(0.2f))
                    ToggleRow("Haptic Feedback 📳", "Vibration on taps", hapticEnabled) { hapticEnabled = it }
                    HorizontalDivider(color = BlushPink.copy(0.2f))
                    ToggleRow("Notifications 🔔", "Reminders and predictions", notifEnabled) { notifEnabled = it }
                }
            }

            // Premium & Wardrobe
            GlassCard(tint = ButterYellow) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader("Premium & Wardrobe ✨", Icons.Rounded.Stars)
                    SettingsRow("Go Premium 🌟", "Ad-free + premium themes — ₹199 once",
                        onClick = { navController.navigate(Payment) })
                    HorizontalDivider(color = BlushPink.copy(0.2f))
                    SettingsRow("Saathi's Wardrobe 👗", "Accessories and themes",
                        onClick = { navController.navigate(Wardrobe) })
                }
            }

            // Legal
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader("Legal & About ℹ️", Icons.Rounded.Info)
                    SettingsRow("Medical Disclaimer ⚕️", "Period Saathi is not a medical device", onClick = {})
                    HorizontalDivider(color = BlushPink.copy(0.2f))
                    SettingsRow("Privacy Policy 🛡️", "DPDP Act 2023 compliant", onClick = {})
                    HorizontalDivider(color = BlushPink.copy(0.2f))
                    SettingsRow("Version 1.0.0", "Build 1", onClick = {})
                }
            }

            // Delete account — destructive
            ScaleButton(onClick = { showDeleteDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Color(0xFFFFE8E8), CardShape)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🗑 Delete All Data", color = Color(0xFFB84040),
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete All Data?") },
            text  = { Text("This will permanently delete all your cycle logs, journal entries, and settings. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false /* TODO: delete */ }) {
                    Text("Delete Everything", color = Color(0xFFB84040))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, title, tint = BlushPink, modifier = Modifier.size(20.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingsRow(title: String, subtitle: String, onClick: () -> Unit) {
    ScaleButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp),
            Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title,    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = Color.Gray)
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, value: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title,    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = Color.Gray)
        }
        Switch(value, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = BlushPink, checkedTrackColor = BlushPink.copy(0.3f)))
    }
}

@Preview @Composable
private fun SettingsPreview() = PeriodSaathiTheme { SettingsScreen(rememberNavController()) }
```

---

## ── REMAINING SCREENS (Stub + Preview) ─────────────────────

**For each remaining screen, write a full working stub. A stub is NOT empty — it has:**
- Real UI matching the design (glassmorphism card, correct colors)
- Working navigation (back button, any links to other screens)
- Correct ViewModel (even if data is mocked)
- `@Preview`

### Write complete stubs for:
1. `ui/screens/splash/SplashScreen.kt` — Mascot drop animation + auto-navigate
2. `ui/screens/onboarding/OnboardingScreen.kt` — 3 swipe pages + "Get Started"
3. `ui/screens/login/LoginScreen.kt` — Google + Guest buttons (both wired)
4. `ui/screens/journal/JournalScreen.kt` — Text entry + mood chips + save button
5. `ui/screens/journal/MoodMapScreen.kt` — Grid heatmap placeholder + cycle overlay toggle
6. `ui/screens/insights/InsightsScreen.kt` — Pattern cards + "not enough data" empty state
7. `ui/screens/settings/ReportExportScreen.kt` — PDF + CSV export buttons (wired)
8. `ui/screens/settings/WardrobeScreen.kt` — Accessories grid + mascot preview
9. `ui/screens/challenges/ChallengesScreen.kt` — Challenge cards + accept button
10. `ui/screens/breathing/BreathingModeScreen.kt` — Fullscreen dark + breathing circle (reuse YogaFlow breathing logic)
11. `ui/screens/payment/PaymentScreen.kt` — 3 product cards + Razorpay stub
12. `ui/screens/calendar/DayLogScreen.kt` — Full day detail + edit all fields

---

# ═══════════════════════════════════════════
# FIX 8 — MAINACTIVITY (FINAL COMPLETE VERSION)
# ═══════════════════════════════════════════

## FILE: MainActivity.kt
```kotlin
package com.periodsaathi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.periodsaathi.app.ui.navigation.*
import com.periodsaathi.app.ui.theme.PeriodSaathiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // NOTE: FLAG_SECURE deliberately NOT set — users need screenshot capability
        // for sharing reports, care summaries, and cycle data with partners/doctors

        setContent {
            PeriodSaathiTheme {
                val navController = rememberNavController()
                val currentEntry  by navController.currentBackStackEntryAsState()
                val currentRoute  = currentEntry?.destination?.route ?: ""

                // Bottom nav is shown only on main 4 tabs
                val showBottomNav = listOf(
                    Home::class.qualifiedName,
                    Calendar::class.qualifiedName,
                    Wellness::class.qualifiedName,
                    Settings::class.qualifiedName
                ).any { currentRoute.contains(it?.substringAfterLast(".") ?: "") }

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomNav,
                            enter   = slideInVertically  { it },
                            exit    = slideOutVertically { it }
                        ) {
                            BottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        PeriodSaathiNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
```

---

# ═══════════════════════════════════════════
# FIX 9 — HILT APPLICATION CLASS
# ═══════════════════════════════════════════

## FILE: PeriodSaathiApp.kt
```kotlin
package com.periodsaathi.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PeriodSaathiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(NotificationChannel(
                "REMINDERS", "Reminders", NotificationManager.IMPORTANCE_HIGH
            ).also { it.description = "Period, water, and medicine reminders" })
            manager.createNotificationChannel(NotificationChannel(
                "INSIGHTS", "Insights", NotificationManager.IMPORTANCE_DEFAULT
            ).also { it.description = "Cycle insights and predictions" })
        }
    }
}
```

Update AndroidManifest.xml:
```xml
<application
    android:name=".PeriodSaathiApp"
    android:allowBackup="true"
    ...>
```

---

# ═══════════════════════════════════════════
# FIX 10 — ROOM DATABASE (ENCRYPTED, MINIMAL WORKING)
# ═══════════════════════════════════════════

Write the minimum working Room setup:
- `CycleEntryEntity.kt` — date, flow intensity 0-5, symptoms JSON, notes, water, mood
- `SettingsEntity.kt` — userName, cycleLength, points, streak, premiumTier
- `CycleEntryDao.kt` — insert, update, getByDate: Flow, getPeriodDays: Flow
- `SettingsDao.kt` — getSettings: Flow, upsert
- `PeriodSaathiDatabase.kt` — Room.databaseBuilder (plain for now, SQLCipher upgrade in next sprint)
- `DatabaseModule.kt` — Hilt @Provides for DB and DAOs

Every entity: `updatedAt: Long` field for sync conflict resolution.
Every DAO: use `Flow<>` return types for reactive UI updates.

---

# ═══════════════════════════════════════════
# SECTION 11 — VERIFICATION CHECKLIST
# ═══════════════════════════════════════════

After writing all files, verify each item:

BUTTONS:
  □ Every PrimaryButton has a real onClick (not `{}`)
  □ Every ScaleButton has a real onClick
  □ Every nav action navigates to a real Screen destination
  □ Every list item tap does something (navigate, toggle, update state)

ANIMATIONS:
  □ SaathiMascot has idle float (infiniteTransition animateFloat)
  □ All ScaleButton: press = 0.94, release = 1.02, settle = 1.0
  □ Card entry: alpha + translateY with spring, staggered 80ms
  □ BreathingModeScreen: circle expands/contracts with tween(4000/6000)
  □ Flip cards: graphicsLayer rotationY 0→180 with spring

PREVIEWS:
  □ Every Screen Composable has @Preview
  □ Every shared Component has @Preview
  □ Previews use PeriodSaathiTheme { }
  □ Previews use rememberNavController() or fake nav

NAVIGATION:
  □ ./gradlew assembleDebug shows no "Unresolved reference" errors
  □ All @Serializable objects imported from navigation.Screen
  □ All composable<T> blocks reference real Composable functions
  □ Back navigation: all screens with topBar have back button wired to navController.popBackStack()

SCREENSHOTS:
  □ FLAG_SECURE is NOT set in MainActivity — confirmed
  □ Users can take screenshots from any screen

HILT:
  □ @HiltAndroidApp on Application class
  □ @AndroidEntryPoint on MainActivity
  □ @HiltViewModel on every ViewModel
  □ @Inject constructor() on every ViewModel
  □ All Modules are @InstallIn(SingletonComponent::class)

DESIGN:
  □ Background color = CreamWhite (#FFF8F5) everywhere
  □ No default purple Material3 colors visible
  □ All cards use GlassCard component
  □ All primary buttons use PrimaryButton component
  □ All selectable items use PastelChip or GlassCard with tint
  □ Nunito font visible on headings
  □ Minimum 32dp rounded corners on cards

RUN THIS: ./gradlew assembleDebug
Expected: BUILD SUCCESSFUL in < 3 minutes
If errors: fix every single one before moving on.
```

---

# ═══════════════════════════════════════════
# SECTION 12 — SUPABASE TABLES (FOR BACKEND SETUP)
# ═══════════════════════════════════════════

Run this SQL in the Supabase SQL editor:

```sql
-- Profiles (synced from device settings)
CREATE TABLE profiles (
  id          UUID PRIMARY KEY REFERENCES auth.users(id),
  username    TEXT NOT NULL DEFAULT 'Friend',
  premium_tier TEXT DEFAULT 'FREE',
  cycle_length_avg INT DEFAULT 28,
  streak_count INT DEFAULT 0,
  total_points INT DEFAULT 0,
  language    TEXT DEFAULT 'en',
  updated_at  TIMESTAMPTZ DEFAULT NOW()
);

-- Cycle entries (main health data)
CREATE TABLE cycle_entries (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID REFERENCES profiles(id) ON DELETE CASCADE,
  date          DATE NOT NULL,
  flow_intensity INT DEFAULT 0,          -- 0=none, 1=spot…5=very heavy
  symptoms      TEXT[] DEFAULT '{}',
  mood          TEXT,
  water_glasses INT DEFAULT 0,
  notes         TEXT,
  is_rest_day   BOOLEAN DEFAULT FALSE,
  is_period_start BOOLEAN DEFAULT FALSE,
  is_period_end   BOOLEAN DEFAULT FALSE,
  updated_at    TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(user_id, date)
);

-- RLS: users only see their own data
ALTER TABLE cycle_entries ENABLE ROW LEVEL SECURITY;
CREATE POLICY "own_data" ON cycle_entries FOR ALL USING (auth.uid() = user_id) WITH CHECK (auth.uid() = user_id);

ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
CREATE POLICY "own_profile" ON profiles FOR ALL USING (auth.uid() = id) WITH CHECK (auth.uid() = id);

-- Purchases
CREATE TABLE purchases (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID REFERENCES profiles(id),
  razorpay_payment_id TEXT UNIQUE,
  product_id      TEXT,  -- THEMES_PACK | AD_FREE | PREMIUM_BUNDLE
  amount_paise    INT,
  status          TEXT DEFAULT 'PENDING',
  purchased_at    TIMESTAMPTZ DEFAULT NOW()
);
ALTER TABLE purchases ENABLE ROW LEVEL SECURITY;
CREATE POLICY "own_purchases" ON purchases FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "insert_own"    ON purchases FOR INSERT WITH CHECK (auth.uid() = user_id);
```

---

# FINAL INSTRUCTIONS TO AI ASSISTANT

```
1. Read every section above before writing code.
2. Fix issues in this order:
   Screenshots → Gradle → Theme → Components → Navigation → Screens → MainActivity → Hilt → Room
3. Write COMPLETE files — never truncate. If a file would be very long,
   split it into two messages: "Part 1" and "Part 2".
4. After ALL files are written: run `./gradlew assembleDebug`
   and fix every error before declaring done.
5. Every screen composable must end with a @Preview annotation.
6. Every button click does something — no empty lambdas.
7. FLAG_SECURE must NOT be set — screenshots must work.
8. Google Sign-In button must attempt authentication — not just print a log.
9. All 18 screens must be in the NavGraph — even if some are stubs.
10. The app must launch to a beautiful CreamWhite screen with
    the mascot and gradient — NOT the default purple Material screen.
```