package com.deepak.periodsaathi.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.deepak.periodsaathi.data.datastore.UserPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ThemeEntryPoint {
    fun themeManager(): ThemeManager
    fun fontManager(): FontManager
    fun userPreferences(): UserPreferences
}

@Composable
fun AppThemeProvider(
    content: @Composable () -> Unit
) {
    if (LocalInspectionMode.current) {
        PeriodSaathiTheme(
            darkTheme = false,
            fontOption = FontOption.DEFAULT,
            themeCategory = ThemeCategory.DEFAULT,
            content = content
        )
        return
    }

    val context = LocalContext.current
    val entryPoint = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            ThemeEntryPoint::class.java
        )
    }

    val isDarkMode by entryPoint.userPreferences().isDarkMode.collectAsState(initial = false)
    val currentTheme by entryPoint.themeManager().currentTheme.collectAsState()
    val currentFont by entryPoint.fontManager().currentFont.collectAsState()

    PeriodSaathiTheme(
        darkTheme = isDarkMode,
        fontOption = currentFont,
        themeCategory = currentTheme,
        content = content
    )
}
