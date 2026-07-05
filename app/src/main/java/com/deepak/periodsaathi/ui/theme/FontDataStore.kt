package com.deepak.periodsaathi.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

private val Context.fontDataStore: DataStore<Preferences> by preferencesDataStore(name = "periodsaathi_font_prefs")

@Singleton
class FontDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_FONT = stringPreferencesKey("selected_font")
    }

    val selectedFont: Flow<String> = context.fontDataStore.data.map { prefs ->
        prefs[KEY_FONT] ?: FontOption.DEFAULT.name
    }

    suspend fun setFont(fontName: String) {
        context.fontDataStore.edit { prefs ->
            prefs[KEY_FONT] = fontName
        }
    }
}
