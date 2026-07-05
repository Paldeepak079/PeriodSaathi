package com.deepak.periodsaathi.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ButtonStyle {
    PILL, ROUNDED_SQUARE, STANDARD, SOFT
}

data class ShapeConfig(
    val cardRadius: RoundedCornerShape,
    val buttonShape: RoundedCornerShape,
    val bottomSheetShape: RoundedCornerShape,
    val chipShape: RoundedCornerShape,
    val inputShape: RoundedCornerShape
)

enum class ThemeCategory(
    val displayName: String,
    val emoji: String,
    val description: String,
    val suggestedFont: FontOption,
    val buttonStyle: ButtonStyle,
    val shapeConfig: ShapeConfig,
    val isLight: Boolean,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val error: Color = Color(0xFFBA1A1A),
    val onError: Color = Color.White,
    val errorContainer: Color = Color(0xFFFFDAD6),
    val onErrorContainer: Color = Color(0xFF93000A)
) {
    PERIOD_SAATHI_CLASSIC(
        displayName = "Classic", emoji = "🌸",
        description = "Cute feminine, warm cream & pink",
        suggestedFont = FontOption.QUICKSAND,
        buttonStyle = ButtonStyle.PILL,
        shapeConfig = ShapeConfig(RoundedCornerShape(24.dp), RoundedCornerShape(9999.dp), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), RoundedCornerShape(9999.dp), RoundedCornerShape(16.dp)),
        isLight = true,
        primary = Color(0xFFE890A6), onPrimary = Color.White,
        primaryContainer = Color(0xFFFFE5EB), onPrimaryContainer = Color(0xFF631527),
        secondary = Color(0xFFD46A84), onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFD1DC), onSecondaryContainer = Color(0xFF4C0E1E),
        tertiary = Color(0xFFB5A4DB), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFEBDCFF), onTertiaryContainer = Color(0xFF38205A),
        background = Color(0xFFFCF6F0), onBackground = Color(0xFF2C1E21),
        surface = Color(0xFFFFF7F5), onSurface = Color(0xFF2C1E21),
        surfaceVariant = Color(0xFFF7E6EB), onSurfaceVariant = Color(0xFF6B4D55),
        outline = Color(0xFF857276), outlineVariant = Color(0xFFD7C2C5)
    ),
    FLO_STYLE_PREMIUM(
        displayName = "Flo Premium", emoji = "✨",
        description = "Luxury wellness, pearl & rose",
        suggestedFont = FontOption.PLAYFAIR_DISPLAY,
        buttonStyle = ButtonStyle.ROUNDED_SQUARE,
        shapeConfig = ShapeConfig(RoundedCornerShape(20.dp), RoundedCornerShape(12.dp), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), RoundedCornerShape(8.dp), RoundedCornerShape(12.dp)),
        isLight = true,
        primary = Color(0xFFE57C82), onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDADC), onPrimaryContainer = Color(0xFF5D1E21),
        secondary = Color(0xFF9E8CD8), onSecondary = Color.White,
        secondaryContainer = Color(0xFFEDE8FF), onSecondaryContainer = Color(0xFF3B1E63),
        tertiary = Color(0xFF8E8CD8), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE8D3FF), onTertiaryContainer = Color(0xFF2E1A47),
        background = Color(0xFFFAF9F6), onBackground = Color(0xFF252424),
        surface = Color(0xFFFFF9FA), onSurface = Color(0xFF252424),
        surfaceVariant = Color(0xFFF7ECEE), onSurfaceVariant = Color(0xFF634D50),
        outline = Color(0xFF8A7375), outlineVariant = Color(0xFFDCC2C4)
    ),
    DARK_NIGHT(
        displayName = "Dark Night", emoji = "🌙",
        description = "Chic dark mode with pink glow",
        suggestedFont = FontOption.OUTFIT,
        buttonStyle = ButtonStyle.ROUNDED_SQUARE,
        shapeConfig = ShapeConfig(RoundedCornerShape(16.dp), RoundedCornerShape(10.dp), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), RoundedCornerShape(8.dp), RoundedCornerShape(10.dp)),
        isLight = false,
        primary = Color(0xFFFF79B0), onPrimary = Color.Black,
        primaryContainer = Color(0xFF860A44), onPrimaryContainer = Color(0xFFFFD9E4),
        secondary = Color(0xFFB388FF), onSecondary = Color.Black,
        secondaryContainer = Color(0xFF5400B8), onSecondaryContainer = Color(0xFFE6DEFF),
        tertiary = Color(0xFF26C6DA), onTertiary = Color.Black,
        tertiaryContainer = Color(0xFF004D56), onTertiaryContainer = Color(0xFFB2EBF2),
        background = Color(0xFF121212), onBackground = Color(0xFFE1E1E1),
        surface = Color(0xFF1E1E1E), onSurface = Color(0xFFE1E1E1),
        surfaceVariant = Color(0xFF332D30), onSurfaceVariant = Color(0xFFCAC0C4),
        outline = Color(0xFF9B8E92), outlineVariant = Color(0xFF4E4346)
    ),
    LAVENDER_DREAM(
        displayName = "Lavender", emoji = "🔮",
        description = "Calming lavender dream mist",
        suggestedFont = FontOption.MERRIWEATHER,
        buttonStyle = ButtonStyle.SOFT,
        shapeConfig = ShapeConfig(RoundedCornerShape(16.dp), RoundedCornerShape(8.dp), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), RoundedCornerShape(8.dp), RoundedCornerShape(10.dp)),
        isLight = true,
        primary = Color(0xFF8C73C7), onPrimary = Color.White,
        primaryContainer = Color(0xFFEDE8FF), onPrimaryContainer = Color(0xFF2D1663),
        secondary = Color(0xFFB5A4DB), onSecondary = Color.White,
        secondaryContainer = Color(0xFFF3EEFF), onSecondaryContainer = Color(0xFF321A58),
        tertiary = Color(0xFF7A8CD0), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE1E6FF), onTertiaryContainer = Color(0xFF15225C),
        background = Color(0xFFF1EEF7), onBackground = Color(0xFF1E1C24),
        surface = Color(0xFFF7F5FC), onSurface = Color(0xFF1E1C24),
        surfaceVariant = Color(0xFFE8E2F2), onSurfaceVariant = Color(0xFF565063),
        outline = Color(0xFF8C869C), outlineVariant = Color(0xFFD5CFE5)
    ),
    SAKURA_BLOSSOM(
        displayName = "Sakura", emoji = "🌸",
        description = "Cherry blossom Japanese aesthetic",
        suggestedFont = FontOption.NUNITO,
        buttonStyle = ButtonStyle.PILL,
        shapeConfig = ShapeConfig(RoundedCornerShape(24.dp), RoundedCornerShape(9999.dp), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), RoundedCornerShape(9999.dp), RoundedCornerShape(16.dp)),
        isLight = true,
        primary = Color(0xFFFA8072), onPrimary = Color.White,
        primaryContainer = Color(0xFFFFEBEE), onPrimaryContainer = Color(0xFF6B1116),
        secondary = Color(0xFFFF8FA3), onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFD1DC), onSecondaryContainer = Color(0xFF6B0E23),
        tertiary = Color(0xFFFFB3C1), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFFF0F3), onTertiaryContainer = Color(0xFF6B0B1D),
        background = Color(0xFFFFF0F3), onBackground = Color(0xFF2B1C1D),
        surface = Color(0xFFFFF7F8), onSurface = Color(0xFF2B1C1D),
        surfaceVariant = Color(0xFFFCE1E5), onSurfaceVariant = Color(0xFF6B4D51),
        outline = Color(0xFF857274), outlineVariant = Color(0xFFD7C2C4)
    ),
    MOONLIGHT(
        displayName = "Moonlight", emoji = "🌑",
        description = "Elegant deep navy & silver glow",
        suggestedFont = FontOption.MONTSERRAT,
        buttonStyle = ButtonStyle.ROUNDED_SQUARE,
        shapeConfig = ShapeConfig(RoundedCornerShape(16.dp), RoundedCornerShape(12.dp), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), RoundedCornerShape(6.dp), RoundedCornerShape(12.dp)),
        isLight = false,
        primary = Color(0xFFBDC3C7), onPrimary = Color.Black,
        primaryContainer = Color(0xFF2C3E50), onPrimaryContainer = Color(0xFFECF0F1),
        secondary = Color(0xFF5A6B7C), onSecondary = Color.White,
        secondaryContainer = Color(0xFF253444), onSecondaryContainer = Color(0xFFDCE2E6),
        tertiary = Color(0xFF34495E), onTertiary = Color.White,
        tertiaryContainer = Color(0xFF1B2631), onTertiaryContainer = Color(0xFFBDC3C7),
        background = Color(0xFF0B132B), onBackground = Color(0xFFECF0F1),
        surface = Color(0xFF1C2541), onSurface = Color(0xFFECF0F1),
        surfaceVariant = Color(0xFF2B3A5A), onSurfaceVariant = Color(0xFFE2E8F0),
        outline = Color(0xFF6A7F9D), outlineVariant = Color(0xFF2F3C59)
    ),
    MINT_CARE(
        displayName = "Mint Care", emoji = "🌱",
        description = "Fresh mint cream wellness",
        suggestedFont = FontOption.QUICKSAND,
        buttonStyle = ButtonStyle.SOFT,
        shapeConfig = ShapeConfig(RoundedCornerShape(20.dp), RoundedCornerShape(9999.dp), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), RoundedCornerShape(9999.dp), RoundedCornerShape(12.dp)),
        isLight = true,
        primary = Color(0xFF2E8B57), onPrimary = Color.White,
        primaryContainer = Color(0xFFE2FAF0), onPrimaryContainer = Color(0xFF0C4E2D),
        secondary = Color(0xFF4DB6AC), onSecondary = Color.White,
        secondaryContainer = Color(0xFFE0F2F1), onSecondaryContainer = Color(0xFF004D40),
        tertiary = Color(0xFF26A69A), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFB2DFDB), onTertiaryContainer = Color(0xFF00332D),
        background = Color(0xFFF2FAF6), onBackground = Color(0xFF152A20),
        surface = Color(0xFFF8FCFA), onSurface = Color(0xFF152A20),
        surfaceVariant = Color(0xFFDEEFE7), onSurfaceVariant = Color(0xFF455A4F),
        outline = Color(0xFF758A7E), outlineVariant = Color(0xFFC0D5CB)
    ),
    EXAM_FOCUS(
        displayName = "Exam Focus", emoji = "📚",
        description = "Off-white & deep blue study mode",
        suggestedFont = FontOption.LEXEND,
        buttonStyle = ButtonStyle.STANDARD,
        shapeConfig = ShapeConfig(RoundedCornerShape(12.dp), RoundedCornerShape(8.dp), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), RoundedCornerShape(6.dp), RoundedCornerShape(8.dp)),
        isLight = true,
        primary = Color(0xFF1A365D), onPrimary = Color.White,
        primaryContainer = Color(0xFFEBF4FF), onPrimaryContainer = Color(0xFF1E3A8A),
        secondary = Color(0xFF4A90E2), onSecondary = Color.White,
        secondaryContainer = Color(0xFFDCEBFF), onSecondaryContainer = Color(0xFF10407A),
        tertiary = Color(0xFF5C6AC4), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE0E3F9), onTertiaryContainer = Color(0xFF222B69),
        background = Color(0xFFFAF9F6), onBackground = Color(0xFF1C2D42),
        surface = Color(0xFFFFFFFF), onSurface = Color(0xFF1C2D42),
        surfaceVariant = Color(0xFFEDF2F7), onSurfaceVariant = Color(0xFF4A5568),
        outline = Color(0xFF718096), outlineVariant = Color(0xFFCBD5E0)
    ),
    GOLD_PREMIUM(
        displayName = "Gold Premium", emoji = "👑",
        description = "Champagne & gold luxury theme",
        suggestedFont = FontOption.DM_SANS,
        buttonStyle = ButtonStyle.ROUNDED_SQUARE,
        shapeConfig = ShapeConfig(RoundedCornerShape(22.dp), RoundedCornerShape(14.dp), RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp), RoundedCornerShape(8.dp), RoundedCornerShape(14.dp)),
        isLight = true,
        primary = Color(0xFFC5A059), onPrimary = Color.White,
        primaryContainer = Color(0xFFFFF6E5), onPrimaryContainer = Color(0xFF5A4315),
        secondary = Color(0xFFE5C173), onSecondary = Color.Black,
        secondaryContainer = Color(0xFFFFF2D4), onSecondaryContainer = Color(0xFF4D3800),
        tertiary = Color(0xFF9E7E45), onTertiary = Color.White,
        tertiaryContainer = Color(0xFFEAD8BA), onTertiaryContainer = Color(0xFF3F3014),
        background = Color(0xFFFAF6EE), onBackground = Color(0xFF29241B),
        surface = Color(0xFFFFFAF2), onSurface = Color(0xFF29241B),
        surfaceVariant = Color(0xFFF2EAD9), onSurfaceVariant = Color(0xFF6E6047),
        outline = Color(0xFF8F8065), outlineVariant = Color(0xFFE0D5BE)
    ),
    AMOLED_BLACK(
        displayName = "Amoled Black", emoji = "🖤",
        description = "Pure black battery-optimized theme",
        suggestedFont = FontOption.POPPINS,
        buttonStyle = ButtonStyle.STANDARD,
        shapeConfig = ShapeConfig(RoundedCornerShape(12.dp), RoundedCornerShape(8.dp), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), RoundedCornerShape(6.dp), RoundedCornerShape(8.dp)),
        isLight = false,
        primary = Color(0xFFFF1493), onPrimary = Color.White,
        primaryContainer = Color(0xFF4A0027), onPrimaryContainer = Color(0xFFFFD6E7),
        secondary = Color(0xFF00FFFF), onSecondary = Color.Black,
        secondaryContainer = Color(0xFF004747), onSecondaryContainer = Color(0xFFE0FFFF),
        tertiary = Color(0xFFFFD700), onTertiary = Color.Black,
        tertiaryContainer = Color(0xFF4D3D00), onTertiaryContainer = Color(0xFFFFF0B3),
        background = Color(0xFF000000), onBackground = Color(0xFFF3F3F3),
        surface = Color(0xFF101010), onSurface = Color(0xFFF3F3F3),
        surfaceVariant = Color(0xFF252525), onSurfaceVariant = Color(0xFFCCCCCC),
        outline = Color(0xFF888888), outlineVariant = Color(0xFF333333)
    );

    companion object {
        val DEFAULT = PERIOD_SAATHI_CLASSIC
    }

    fun toLightColorScheme() = lightColorScheme(
        primary = primary, onPrimary = onPrimary,
        primaryContainer = primaryContainer, onPrimaryContainer = onPrimaryContainer,
        secondary = secondary, onSecondary = onSecondary,
        secondaryContainer = secondaryContainer, onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary, onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer, onTertiaryContainer = onTertiaryContainer,
        background = background, onBackground = onBackground,
        surface = surface, onSurface = onSurface,
        surfaceVariant = surfaceVariant, onSurfaceVariant = onSurfaceVariant,
        outline = outline, outlineVariant = outlineVariant,
        error = error, onError = onError,
        errorContainer = errorContainer, onErrorContainer = onErrorContainer
    )

    fun toDarkColorScheme() = darkColorScheme(
        primary = primaryContainer, onPrimary = onPrimaryContainer,
        primaryContainer = primary, onPrimaryContainer = onPrimary,
        secondary = secondaryContainer, onSecondary = onSecondaryContainer,
        secondaryContainer = secondary, onSecondaryContainer = onSecondary,
        tertiary = tertiaryContainer, onTertiary = onTertiaryContainer,
        tertiaryContainer = tertiary, onTertiaryContainer = onTertiary,
        background = if (this == AMOLED_BLACK) Color.Black else Color(0xFF1A1228),
        onBackground = Color(0xFFF0E6E6),
        surface = if (this == AMOLED_BLACK) Color(0xFF121212) else Color(0xFF1A1228),
        onSurface = Color(0xFFF0E6E6),
        surfaceVariant = Color(0xFF4A4145), onSurfaceVariant = Color(0xFFCAC0C1),
        outline = Color(0xFF948F90), outlineVariant = Color(0xFF4A4145),
        error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6)
    )

    @Composable
    fun resolveShapeConfig(): ShapeConfig = shapeConfig
}
