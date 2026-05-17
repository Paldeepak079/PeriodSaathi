package com.example.periodsaathi.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppTheme(val displayName: String, val emoji: String) {
    BLUSH_PINK("Blush Pink", "🌸"),
    TEAL_NEUTRAL("Teal Neutral", "🌿"),
    LAVENDER_PURPLE("Lavender Purple", "💜"),
    MIDNIGHT_OCEAN("Midnight Ocean", "🌊"),
    SUNSET_CORAL("Sunset Coral", "🌅")
}

val TealPrimary = Color(0xFF80CBC4)
val TealSecondary = Color(0xFF90A4AE)
val SkyTertiary = Color(0xFF81D4FA)
val NeutralBackground = Color(0xFFF5F7F8)
val TealAccent = Color(0xFF26A69A)
val SlateDark = Color(0xFF37474F)

val TealNeutralColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = SlateDark,
    secondary = TealSecondary,
    onSecondary = Color.White,
    tertiary = SkyTertiary,
    background = NeutralBackground,
    surface = Color(0xFFF0F7F7),
    onBackground = SlateDark,
    onSurface = SlateDark,
    error = Color(0xFFB00020)
)

val LavenderPrimary = Color(0xFF9C27B0)
val LavenderLight = Color(0xFFCE93D8)
val LavenderBackground = Color(0xFFF9F4FF)

val LavenderColorScheme = lightColorScheme(
    primary = LavenderPrimary,
    onPrimary = Color.White,
    primaryContainer = LavenderLight,
    onPrimaryContainer = Color(0xFF1A0032),
    secondary = Color(0xFF6A1B9A),
    background = LavenderBackground,
    surface = Color(0xFFF3E5F5),
    onBackground = Color(0xFF1A0032),
    onSurface = Color(0xFF1A0032),
    error = Color(0xFFB00020)
)

val OceanDark = Color(0xFF0D1B2A)
val OceanMid = Color(0xFF1B2838)
val OceanPrimary = Color(0xFF00BCD4)

val MidnightOceanColorScheme = darkColorScheme(
    primary = OceanPrimary,
    onPrimary = OceanDark,
    primaryContainer = OceanMid,
    secondary = Color(0xFF80DEEA),
    background = OceanDark,
    surface = OceanMid,
    onBackground = Color.White,
    onSurface = Color.White
)

object ThemeHelper {
    fun getColorSchemeForTheme(theme: AppTheme, isDark: Boolean): androidx.compose.material3.ColorScheme {
        return when (theme) {
            AppTheme.BLUSH_PINK -> if (isDark) createDarkColorScheme() else createLightColorScheme()
            AppTheme.TEAL_NEUTRAL -> TealNeutralColorScheme
            AppTheme.LAVENDER_PURPLE -> LavenderColorScheme
            AppTheme.MIDNIGHT_OCEAN -> MidnightOceanColorScheme
            AppTheme.SUNSET_CORAL -> createLightColorScheme()
        }
    }

    private fun createLightColorScheme() = lightColorScheme(
        primary = BlushPink,
        onPrimary = Color.White,
        background = WarmCream,
        onBackground = Color(0xFF1C1B1B),
        surface = WarmCream,
        onSurface = Color(0xFF1C1B1B)
    )

    private fun createDarkColorScheme() = darkColorScheme(
        primary = BlushPink,
        onPrimary = Color(0xFF3A001A),
        background = Color(0xFF1A0E2E),
        onBackground = Color(0xFFF0E6E6),
        surface = Color(0xFF1A1228),
        onSurface = Color(0xFFF0E6E6)
    )
}