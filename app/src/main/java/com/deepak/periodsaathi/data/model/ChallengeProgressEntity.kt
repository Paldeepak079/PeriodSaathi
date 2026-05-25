package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenge_progress")
data class ChallengeProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val challengeId: String,
    val progress: Float = 0f,
    val completed: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
