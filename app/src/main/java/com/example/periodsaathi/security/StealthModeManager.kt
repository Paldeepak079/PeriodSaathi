package com.example.periodsaathi.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class StealthModeManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "stealth_mode"
        private const val KEY_ENABLED = "stealth_enabled"
        private const val KEY_PIN_HASH = "stealth_pin_hash"
        private const val KEY_DISGUISE_NAME = "stealth_disguise_name"
        private const val KEY_AUTHENTICATED = "stealth_authenticated"

        private fun hashPin(pin: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(pin.toByteArray())
            return hash.joinToString("") { "%02x".format(it) }
        }
    }

    fun isStealthModeEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, false)

    fun isAuthenticated(): Boolean = prefs.getBoolean(KEY_AUTHENTICATED, false)

    fun setAuthenticated(authenticated: Boolean) {
        prefs.edit().putBoolean(KEY_AUTHENTICATED, authenticated).apply()
    }

    fun enableStealthMode(pin: String) {
        prefs.edit()
            .putBoolean(KEY_ENABLED, true)
            .putString(KEY_PIN_HASH, hashPin(pin))
            .putString(KEY_DISGUISE_NAME, "Weather")
            .apply()
    }

    fun disableStealthMode() {
        prefs.edit()
            .putBoolean(KEY_ENABLED, false)
            .remove(KEY_PIN_HASH)
            .remove(KEY_DISGUISE_NAME)
            .putBoolean(KEY_AUTHENTICATED, false)
            .apply()
    }

    fun verifyPin(input: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return hashPin(input) == storedHash
    }

    fun getDisguiseName(): String = prefs.getString(KEY_DISGUISE_NAME, "Weather") ?: "Weather"

    fun hasPin(): Boolean = prefs.contains(KEY_PIN_HASH)
}
