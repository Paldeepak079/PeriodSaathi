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

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "periodsaathi_theme_prefs")

@Singleton
class ThemeDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_THEME = stringPreferencesKey("selected_theme")
    }

    val selectedTheme: Flow<String> = context.themeDataStore.data.map { prefs ->
        prefs[KEY_THEME] ?: ThemeCategory.DEFAULT.name
    }

    suspend fun setTheme(themeName: String) {
        context.themeDataStore.edit { prefs ->
            prefs[KEY_THEME] = themeName
        }
    }
}
