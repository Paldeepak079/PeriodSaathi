package com.deepak.periodsaathi.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Light colour scheme (Warm Cream / Blush Pink) ─────────────────────────────
private val LightColorScheme = lightColorScheme(
    surface = Surface,
    surfaceDim = SurfaceDim,
    surfaceBright = SurfaceBright,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceVariant = SurfaceVariant,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Background,
    onBackground = OnBackground,
    outline = Outline,
    outlineVariant = OutlineVariant,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    surfaceTint = SurfaceTint,
)

// ── Dark colour scheme (Deep purple / Neon pink) ──────────────────────────────
// Design intent: rich #121212-style depth with neon blush accents for readability
private val DarkColorScheme = darkColorScheme(
    surface = Color(0xFF1A1228),
    surfaceDim = Color(0xFF1A0E2E),
    surfaceBright = Color(0xFF2D1B4E),
    surfaceContainerLowest = Color(0xFF0F0A12),
    surfaceContainerLow = Color(0xFF161020),
    surfaceContainer = Color(0xFF1A1228),
    surfaceContainerHigh = Color(0xFF231A32),
    surfaceContainerHighest = Color(0xFF2D1B4E),
    surfaceVariant = Color(0xFF4A4145),
    onSurface = Color(0xFFF0E6E6),
    onSurfaceVariant = Color(0xFFCAC0C1),
    primary = Color(0xFFFF6B9D),       // neon blush — high-contrast accent
    onPrimary = Color(0xFF1A0010),
    primaryContainer = Color(0xFF5C0030),
    onPrimaryContainer = Color(0xFFFFD9E4),
    inversePrimary = Primary,
    secondary = Color(0xFFD0BFEF),     // soft lavender
    onSecondary = Color(0xFF352B4D),
    secondaryContainer = Color(0xFF4D4068),
    onSecondaryContainer = Color(0xFFEADDFF),
    tertiary = Color(0xFFAAD4F0),      // ice blue
    onTertiary = Color(0xFF0D314D),
    tertiaryContainer = Color(0xFF2A4A64),
    onTertiaryContainer = Color(0xFFCDE5FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A0E2E),
    onBackground = Color(0xFFF0E6E6),
    outline = Color(0xFF948F90),
    outlineVariant = Color(0xFF4A4145),
    inverseSurface = Color(0xFFF0E6E6),
    inverseOnSurface = Color(0xFF33302C),
    surfaceTint = Color(0xFFFF6B9D),
)

/** Composition local so any composable can read the current dark-mode flag. */
val LocalDarkMode = staticCompositionLocalOf { false }

@Composable
fun PeriodSaathiTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalDarkMode provides darkTheme) {
        // 500ms cross-fade when theme switches — smooth, full-screen transition
        Crossfade(
            targetState = darkTheme,
            animationSpec = tween(durationMillis = 500),
            label = "themeSwitch"
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content
            )
        }
    }
}
