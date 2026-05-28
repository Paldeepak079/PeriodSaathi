package com.deepak.periodsaathi.wellness.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wellness_stats")
data class UserWellnessStatsEntity(
    @PrimaryKey
    val id: String = "singleton",
    val totalCoins: Int = 0,
    val currentStreak: Int = 0,
    val lastLogTimestamp: Long = 0L
)
