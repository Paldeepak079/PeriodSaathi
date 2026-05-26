package com.deepak.periodsaathi.data.datastore

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
        // ── Auth ──────────────────────────────────────────────────────────
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_NAME = stringPreferencesKey("user_name")

        // ── Cycle data ────────────────────────────────────────────────────
        val CYCLE_LENGTH = intPreferencesKey("cycle_length")
        val PERIOD_LENGTH = intPreferencesKey("period_length")
        val LAST_PERIOD_START = stringPreferencesKey("last_period_start")

        // ── Onboarding questionnaire ──────────────────────────────────────
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        /** Comma-separated goal keys: "track_cycle", "get_pregnant", "track_pregnancy" */
        val USER_GOALS = stringPreferencesKey("user_goals")
        /** E.g. "none", "pill", "iud", "condom", "other" */
        val BIRTH_CONTROL = stringPreferencesKey("birth_control")

        // ── Onboarding resume ─────────────────────────────────────────────
        val ONBOARDING_STEP = intPreferencesKey("onboarding_step")

        // ── Appearance ────────────────────────────────────────────────────
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

        // ── Partner sync ──────────────────────────────────────────────────
        val PARTNER_CODE = stringPreferencesKey("partner_code")
        val PARTNER_LINKED = booleanPreferencesKey("partner_linked")
        val PARTNER_NAME = stringPreferencesKey("partner_name")
    }

    // ── Auth ─────────────────────────────────────────────────────────────────
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME] ?: "Priya" }

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

    // ── Cycle data ────────────────────────────────────────────────────────────
    val cycleLength: Flow<Int> = context.dataStore.data.map { it[CYCLE_LENGTH] ?: 28 }
    val periodLength: Flow<Int> = context.dataStore.data.map { it[PERIOD_LENGTH] ?: 5 }
    val lastPeriodStart: Flow<String> = context.dataStore.data.map { it[LAST_PERIOD_START] ?: "" }

    suspend fun setCycleData(cycleLength: Int, periodLength: Int, lastPeriodStart: String) {
        context.dataStore.edit { prefs ->
            prefs[CYCLE_LENGTH] = cycleLength
            prefs[PERIOD_LENGTH] = periodLength
            prefs[LAST_PERIOD_START] = lastPeriodStart
        }
    }

    // ── Onboarding questionnaire ──────────────────────────────────────────────
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    val userGoals: Flow<String> = context.dataStore.data.map { it[USER_GOALS] ?: "" }
    val birthControl: Flow<String> = context.dataStore.data.map { it[BIRTH_CONTROL] ?: "none" }

    /**
     * Persists all onboarding questionnaire answers atomically.
     * Goals are stored as a comma-separated string for easy parsing.
     */
    suspend fun saveOnboardingData(
        goals: List<String>,
        birthControl: String,
        cycleLength: Int,
        periodLength: Int,
        lastPeriodStart: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[USER_GOALS] = goals.joinToString(",")
            prefs[BIRTH_CONTROL] = birthControl
            prefs[CYCLE_LENGTH] = cycleLength
            prefs[PERIOD_LENGTH] = periodLength
            prefs[LAST_PERIOD_START] = lastPeriodStart
            prefs[ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = true }
    }

    val onboardingStep: Flow<Int> = context.dataStore.data.map { it[ONBOARDING_STEP] ?: 0 }

    suspend fun saveOnboardingStep(step: Int) {
        context.dataStore.edit { it[ONBOARDING_STEP] = step }
    }

    // ── Appearance ────────────────────────────────────────────────────────────
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[IS_DARK_MODE] = enabled }
    }

    // ── Partner sync ──────────────────────────────────────────────────────────
    val partnerCode: Flow<String> = context.dataStore.data.map { it[PARTNER_CODE] ?: "" }
    val partnerLinked: Flow<Boolean> = context.dataStore.data.map { it[PARTNER_LINKED] ?: false }
    val partnerName: Flow<String> = context.dataStore.data.map { it[PARTNER_NAME] ?: "" }

    suspend fun savePartnerCode(code: String) {
        context.dataStore.edit { it[PARTNER_CODE] = code }
    }

    suspend fun linkPartner(partnerName: String) {
        context.dataStore.edit { prefs ->
            prefs[PARTNER_LINKED] = true
            prefs[PARTNER_NAME] = partnerName
        }
    }

    suspend fun unlinkPartner() {
        context.dataStore.edit { prefs ->
            prefs[PARTNER_LINKED] = false
            prefs[PARTNER_NAME] = ""
            prefs[PARTNER_CODE] = ""
        }
    }
}
