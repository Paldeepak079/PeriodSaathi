package com.deepak.periodsaathi.wellness.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellness_logs")
data class WellnessLogEntity(
    @PrimaryKey
    val date: Long, // Daily epoch timestamp (at start of day)
    val waterIntake: Int = 0,
    val sleepHours: Float = 0f,
    val exerciseMinutes: Int = 0,
    val mood: String = "",
    val painType: String = "",
    val painLevel: String = "",
    val flowLevel: String = "",
    val energyLevel: String = "",
    val mealsLoggedJson: String = "[]", // Stores list of custom food logged as JSON string
    val synced: Boolean = false
)
