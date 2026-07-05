package com.deepak.periodsaathi.ui.theme

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    private val _currentTheme = MutableStateFlow(ThemeCategory.DEFAULT)
    val currentTheme: StateFlow<ThemeCategory> = _currentTheme.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    init {
        scope.launch {
            themeRepository.selectedTheme.collect { theme ->
                _currentTheme.value = theme
            }
        }
    }

    fun setTheme(theme: ThemeCategory) {
        _currentTheme.value = theme
        scope.launch {
            themeRepository.updateTheme(theme)
        }
    }
}
