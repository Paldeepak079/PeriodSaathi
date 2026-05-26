package com.deepak.periodsaathi.security

import android.content.Context
import com.deepak.periodsaathi.data.datastore.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the Partner Sync ("Saathi Connect") feature.
 *
 * Generates time-stable 6-character alphanumeric invite codes that partners
 * can enter in their own Period Saathi installation to receive read-only access
 * to cycle summaries, empathy tips, and conversation starters.
 *
 * The code is stored in [UserPreferences] and regenerated only when explicitly
 * requested by the user.
 */
@Singleton
class PartnerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) {
    // Alphanumeric charset — avoids visually ambiguous chars (0, O, 1, I, l)
    private val charset = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    /**
     * Generates a new random 6-character invite code, saves it, and returns it.
     */
    suspend fun generateNewCode(): String {
        val code = (1..6).map { charset.random() }.joinToString("")
        userPreferences.savePartnerCode(code)
        return code
    }

    /**
     * Returns the existing code, or generates one if none exists.
     */
    suspend fun getOrCreateCode(): String {
        val existing = userPreferences.partnerCode.first()
        return if (existing.isNotBlank()) existing else generateNewCode()
    }

    /**
     * Validates and links a partner using the code they entered.
     * Returns true if the code format is valid (6 alphanumeric chars).
     * In a real implementation, this would verify the code against a backend.
     */
    suspend fun linkWithCode(code: String, partnerName: String): Boolean {
        val cleaned = code.trim().uppercase()
        if (cleaned.length != 6 || !cleaned.all { it in charset }) return false
        userPreferences.linkPartner(partnerName.ifBlank { "Your Partner" })
        return true
    }

    /**
     * Unlinks the partner and clears all sync data.
     */
    suspend fun unlinkPartner() {
        userPreferences.unlinkPartner()
    }
}
