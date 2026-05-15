package com.example.periodsaathi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_NAME = stringPreferencesKey("user_name")
        val CYCLE_LENGTH = intPreferencesKey("cycle_length")
        val PERIOD_LENGTH = intPreferencesKey("period_length")
        val LAST_PERIOD_START = stringPreferencesKey("last_period_start")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME] ?: "Priya"
    }

    suspend fun setLoggedIn(name: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_NAME] = name
        }
    }

    suspend fun setGuestMode() {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_NAME] = "Guest"
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
            prefs[USER_NAME] = ""
        }
    }

    suspend fun setCycleData(cycleLength: Int, periodLength: Int, lastPeriodStart: String) {
        context.dataStore.edit { prefs ->
            prefs[CYCLE_LENGTH] = cycleLength
            prefs[PERIOD_LENGTH] = periodLength
            prefs[LAST_PERIOD_START] = lastPeriodStart
        }
    }
}
