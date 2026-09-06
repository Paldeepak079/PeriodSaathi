package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_friends")
data class FriendEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val inviteCode: String,
    val status: String, // SYNCED, PENDING
    val connectedAt: Long,
    val cyclePhase: String = "",
    val cycleDay: Int = 0,
    val totalDays: Int = 28
)
