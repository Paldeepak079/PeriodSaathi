package com.deepak.periodsaathi.ui.theme

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FontRepository @Inject constructor(
    private val fontDataStore: FontDataStore
) {
    val selectedFont: Flow<FontOption> = fontDataStore.selectedFont.map { name ->
        try {
            FontOption.valueOf(name)
        } catch (_: Exception) {
            FontOption.DEFAULT
        }
    }

    suspend fun updateFont(font: FontOption) {
        fontDataStore.setFont(font.name)
    }
}
