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
class FontManager @Inject constructor(
    private val fontRepository: FontRepository
) {
    private val _currentFont = MutableStateFlow(FontOption.DEFAULT)
    val currentFont: StateFlow<FontOption> = _currentFont.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    init {
        scope.launch {
            fontRepository.selectedFont.collect { font ->
                _currentFont.value = font
            }
        }
    }

    fun setFont(font: FontOption) {
        _currentFont.value = font
        scope.launch {
            fontRepository.updateFont(font)
        }
    }
}
