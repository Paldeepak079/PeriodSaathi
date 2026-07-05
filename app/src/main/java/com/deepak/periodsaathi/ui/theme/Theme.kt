package com.deepak.periodsaathi.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LocalDarkMode = staticCompositionLocalOf { false }
val LocalFontOption = staticCompositionLocalOf { FontOption.DEFAULT }
val LocalThemeCategory = staticCompositionLocalOf { ThemeCategory.DEFAULT }
val LocalShapeConfig = staticCompositionLocalOf { ThemeCategory.DEFAULT.shapeConfig }

@Composable
fun PeriodSaathiTheme(
    darkTheme: Boolean = false,
    fontOption: FontOption = FontOption.DEFAULT,
    themeCategory: ThemeCategory = ThemeCategory.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) themeCategory.toDarkColorScheme() else themeCategory.toLightColorScheme()
    
    // Exam Focus Mode Logic:
    val isExamMode = themeCategory == ThemeCategory.EXAM_FOCUS || fontOption == FontOption.LEXEND
    val finalFontOption = if (isExamMode) FontOption.LEXEND else fontOption
    val fontFamily = rememberFontFamily(finalFontOption)
    
    val lineMultiplier = if (isExamMode) 1.25f else 1.0f
    val extraSpacingVal = if (isExamMode) 0.08f else 0.0f
    
    val shapeConfig = themeCategory.shapeConfig

    val typography = Typography(
        displayLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 57.sp, lineHeight = (64.sp * lineMultiplier), letterSpacing = (-0.25f + extraSpacingVal).sp),
        displayMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 45.sp, lineHeight = (52.sp * lineMultiplier), letterSpacing = extraSpacingVal.sp),
        displaySmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = (44.sp * lineMultiplier), letterSpacing = extraSpacingVal.sp),
        headlineLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = (40.sp * lineMultiplier), letterSpacing = (-0.02f + extraSpacingVal).sp),
        headlineMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = (36.sp * lineMultiplier), letterSpacing = (-0.01f + extraSpacingVal).sp),
        headlineSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = (32.sp * lineMultiplier), letterSpacing = extraSpacingVal.sp),
        titleLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = (28.sp * lineMultiplier), letterSpacing = extraSpacingVal.sp),
        titleMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = (24.sp * lineMultiplier), letterSpacing = (0.15f + extraSpacingVal).sp),
        titleSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = (20.sp * lineMultiplier), letterSpacing = (0.1f + extraSpacingVal).sp),
        bodyLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = (28.sp * lineMultiplier), letterSpacing = extraSpacingVal.sp),
        bodyMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = (24.sp * lineMultiplier), letterSpacing = extraSpacingVal.sp),
        bodySmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = (20.sp * lineMultiplier), letterSpacing = extraSpacingVal.sp),
        labelLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = (20.sp * lineMultiplier), letterSpacing = (0.01f + extraSpacingVal).sp),
        labelMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = (16.sp * lineMultiplier), letterSpacing = (0.5f + extraSpacingVal).sp),
        labelSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = (16.sp * lineMultiplier), letterSpacing = (0.5f + extraSpacingVal).sp)
    )

    CompositionLocalProvider(
        LocalDarkMode provides darkTheme,
        LocalFontOption provides finalFontOption,
        LocalThemeCategory provides themeCategory,
        LocalShapeConfig provides shapeConfig
    ) {
        Crossfade(
            targetState = darkTheme to (finalFontOption to themeCategory),
            animationSpec = tween(durationMillis = 400),
            label = "themeSwitch"
        ) { (isDark, _) ->
            MaterialTheme(
                colorScheme = if (isDark) themeCategory.toDarkColorScheme() else themeCategory.toLightColorScheme(),
                typography = typography,
                shapes = androidx.compose.material3.Shapes(
                    small = shapeConfig.chipShape,
                    medium = shapeConfig.cardRadius,
                    large = shapeConfig.bottomSheetShape
                ),
                content = content
            )
        }
    }
}
