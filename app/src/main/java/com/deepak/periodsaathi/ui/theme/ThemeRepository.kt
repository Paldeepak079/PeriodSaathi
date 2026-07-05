package com.deepak.periodsaathi.ui.theme

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepository @Inject constructor(
    private val themeDataStore: ThemeDataStore
) {
    val selectedTheme: Flow<ThemeCategory> = themeDataStore.selectedTheme.map { name ->
        try {
            ThemeCategory.valueOf(name)
        } catch (_: Exception) {
            ThemeCategory.DEFAULT
        }
    }

    suspend fun updateTheme(theme: ThemeCategory) {
        themeDataStore.setTheme(theme.name)
    }
}
