package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cycle_settings")
data class CycleSettings(
    @PrimaryKey
    val id: Int = 1,
    val averageCycleLength: Int = 28,
    val averagePeriodLength: Int = 5,
    val lastPeriodStartDate: Long? = null,
    val partnerName: String? = null,
    val partnerPhone: String? = null,
    val userName: String = "Friend",
    val selectedLanguage: String = "en",
    val contraceptionMode: String = "NONE",
    val fertilityMode: String = "NEUTRAL",
    val stealthModeEnabled: Boolean = false,
    val selectedTheme: String = "DEFAULT",
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val biometricLockEnabled: Boolean = false,
    val streakCount: Int = 0,
    val totalPoints: Int = 0
)

