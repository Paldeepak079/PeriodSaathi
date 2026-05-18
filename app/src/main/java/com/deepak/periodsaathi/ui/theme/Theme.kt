package com.deepak.periodsaathi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    // Surface colors
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
    // Primary
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,
    // Secondary
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    // Tertiary
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    // Error
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    // Background
    background = Background,
    onBackground = OnBackground,
    // Outline
    outline = Outline,
    outlineVariant = OutlineVariant,
    // Inverse
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    // Surface tint
    surfaceTint = SurfaceTint,
)

private val DarkColorScheme = darkColorScheme(
    // Surface colors - Dark theme versions
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
    // Primary - Dark
    primary = Color(0xFFFCB3BE),
    onPrimary = Color(0xFF5C1223),
    primaryContainer = Color(0xFF7B444E),
    onPrimaryContainer = Color(0xFFFFD9DE),
    inversePrimary = Primary,
    // Secondary - Dark
    secondary = Color(0xFFD0BFEF),
    onSecondary = Color(0xFF352B4D),
    secondaryContainer = Color(0xFF4D4068),
    onSecondaryContainer = Color(0xFFEADDFF),
    // Tertiary - Dark
    tertiary = Color(0xFFAAD4F0),
    onTertiary = Color(0xFF0D314D),
    tertiaryContainer = Color(0xFF2A4A64),
    onTertiaryContainer = Color(0xFFCDE5FF),
    // Error - Dark
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    // Background - Dark
    background = Color(0xFF1A0E2E),
    onBackground = Color(0xFFF0E6E6),
    // Outline - Dark
    outline = Color(0xFF948F90),
    outlineVariant = Color(0xFF4A4145),
    // Inverse - Dark
    inverseSurface = Color(0xFFF0E6E6),
    inverseOnSurface = Color(0xFF33302C),
    // Surface tint - Dark
    surfaceTint = Color(0xFFFCB3BE),
)

@Composable
fun PeriodSaathiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

