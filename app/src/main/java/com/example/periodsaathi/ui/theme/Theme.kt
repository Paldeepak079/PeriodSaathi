package com.example.periodsaathi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BlushPink,
    onPrimary = Color.White,
    secondary = SoftLavender,
    onSecondary = Color(0xFF1A0040),
    background = WarmCream,
    onBackground = Color(0xFF1C1B1B),
    surface = WarmCream,
    onSurface = Color(0xFF1C1B1B),
    error = Error,
    tertiary = Tertiary,
    primaryContainer = PrimaryContainer,
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color(0xFF3A001A),
    secondary = SoftLavender,
    onSecondary = Color(0xFF1A0040),
    tertiary = BabyBlue,
    background = Color(0xFF1A0E2E),
    onBackground = Color(0xFFF0E6E6),
    surface = Color(0xFF1A1228),
    onSurface = Color(0xFFF0E6E6),
    error = Color(0xFFCF6679),
    primaryContainer = Color(0xFF3A0025),
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
