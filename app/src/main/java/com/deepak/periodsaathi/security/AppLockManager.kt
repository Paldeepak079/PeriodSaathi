package com.deepak.periodsaathi.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

/**
 * Manages the app-lock fallback PIN that is used when biometric authentication
 * is unavailable or the user chooses to enter a PIN instead.
 *
 * This is intentionally separate from [StealthModeManager] so that the fallback
 * PIN is available to ALL users, even those who have not enabled stealth mode.
 *
 * Storage: private SharedPreferences file "app_lock_prefs", SHA-256 hashed PIN.
 */
class AppLockManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_lock_prefs"
        private const val KEY_PIN_HASH = "lock_pin_hash"

        private fun hashPin(pin: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            return digest.digest(pin.toByteArray())
                .joinToString("") { "%02x".format(it) }
        }
    }

    /** Returns true if a fallback PIN has already been set. */
    fun hasPin(): Boolean = prefs.contains(KEY_PIN_HASH)

    /** Stores a hashed copy of [pin]. Call only during PIN setup. */
    fun setPin(pin: String) {
        prefs.edit().putString(KEY_PIN_HASH, hashPin(pin)).apply()
    }

    /**
     * Returns true if [input] matches the stored PIN hash.
     * Returns false (not throws) if no PIN has been set.
     */
    fun verifyPin(input: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return hashPin(input) == storedHash
    }

    /** Removes the stored PIN — call when the user disables app lock. */
    fun clearPin() {
        prefs.edit().remove(KEY_PIN_HASH).apply()
    }
}
