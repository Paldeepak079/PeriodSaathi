package com.deepak.periodsaathi.data.remote

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceIdentityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("device_identity", Context.MODE_PRIVATE)

    companion object {
        private val ANONYMOUS_ALIASES = listOf(
            "Cosmic Lily", "Gentle Moon", "Wild Poppy", "Silver Fern",
            "Calm Lotus", "Brave Dahlia", "Quiet Willow", "Bright Star",
            "Free Spirit", "Soft Cloud", "Kind Sage", "Bold Iris"
        )
    }

    val deviceId: String
        get() {
            var id = prefs.getString("device_id", null)
            if (id == null) {
                id = UUID.randomUUID().toString()
                prefs.edit().putString("device_id", id).apply()
            }
            return id
        }

    val anonymousAlias: String
        get() {
            var alias = prefs.getString("anonymous_alias", null)
            if (alias == null) {
                alias = ANONYMOUS_ALIASES[Math.abs(deviceId.hashCode()) % ANONYMOUS_ALIASES.size]
                prefs.edit().putString("anonymous_alias", alias).apply()
            }
            return alias
        }

    fun resetAlias() {
        val newAlias = ANONYMOUS_ALIASES.random()
        prefs.edit().putString("anonymous_alias", newAlias).apply()
    }
}
