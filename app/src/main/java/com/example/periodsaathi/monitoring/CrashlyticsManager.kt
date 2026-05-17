package com.example.periodsaathi.monitoring

import android.content.Context
import com.example.periodsaathi.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashlyticsManager @Inject constructor(
    private val context: Context
) {

    fun initialize(analyticsOptedIn: Boolean) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(analyticsOptedIn && !BuildConfig.DEBUG)
    }

    fun logEvent(event: String, params: Map<String, String> = emptyMap()) {
        if (BuildConfig.DEBUG) return
        try {
            val analytics = FirebaseAnalytics.getInstance(context)
            val bundle = android.os.Bundle().apply {
                params.forEach { (k, v) -> putString(k, v) }
            }
            analytics.logEvent(event, bundle)
        } catch (e: Exception) {
        }
    }

    fun setUserId(userId: String) {
        val hashed = userId.toByteArray().let {
            MessageDigest.getInstance("SHA-256").digest(it)
        }.joinToString("") { "%02x".format(it) }.take(16)
        FirebaseCrashlytics.getInstance().setUserId(hashed)
    }

    fun recordException(e: Throwable, context: String = "") {
        val sanitizedMessage = sanitizeForCrashlytics(e.message ?: "")
        val sanitizedException = RuntimeException("[$context] $sanitizedMessage", e.cause)
        FirebaseCrashlytics.getInstance().recordException(sanitizedException)
    }

    fun logScreenView(screenName: String) {
        if (BuildConfig.DEBUG) return
        logEvent("screen_view", mapOf("screen" to screenName))
    }

    fun logWaterGoalReached() = logEvent("water_goal_reached")
    fun logCycleLogged() = logEvent("cycle_logged")
    fun logPurchaseCompleted(productId: String) = logEvent("purchase_completed", mapOf("product" to productId))

    private fun sanitizeForCrashlytics(message: String): String {
        val healthKeywords = listOf("period", "bleeding", "flow", "cramps", "symptoms", "mood", "intensity", "menstrual", "cycle")
        var sanitized = message
        healthKeywords.forEach { keyword ->
            sanitized = sanitized.replace(keyword, "[redacted]", ignoreCase = true)
        }
        return sanitized
    }
}