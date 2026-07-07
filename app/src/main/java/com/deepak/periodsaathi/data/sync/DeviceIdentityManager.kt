package com.deepak.periodsaathi.data.sync

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

    private val deviceUid: String by lazy {
        var uid = prefs.getString(KEY_DEVICE_UID, null)
        if (uid == null) {
            uid = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_UID, uid).apply()
        }
        uid
    }

    private val anonymousAlias: String by lazy {
        var alias = prefs.getString(KEY_ANONYMOUS_ALIAS, null)
        if (alias == null) {
            alias = ALIASES[Math.abs(deviceUid.hashCode()) % ALIASES.size]
            prefs.edit().putString(KEY_ANONYMOUS_ALIAS, alias).apply()
        }
        alias
    }

    fun getDeviceUid(): String = deviceUid
    fun getAnonymousAlias(): String = anonymousAlias
    fun isMyPost(postDeviceId: String): Boolean = postDeviceId == deviceUid

    companion object {
        private const val KEY_DEVICE_UID = "device_uid"
        private const val KEY_ANONYMOUS_ALIAS = "anonymous_alias"

        val ALIASES = listOf(
            "Cosmic Lily", "Gentle Moon", "Wild Poppy", "Silver Fern",
            "Calm Lotus", "Brave Dahlia", "Quiet Willow", "Bright Star",
            "Free Spirit", "Soft Cloud", "Kind Sage", "Bold Iris",
            "Gentle Rose", "Brave Lotus", "Quiet Fern", "Soft Petal",
            "Warm Sun", "Deep Ocean", "Gentle Breeze", "Silent Echo"
        )
    }
}
