package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accessories")
data class AccessoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val emoji: String,
    val pointsCost: Int,
    val unlocked: Boolean = false,
    val equipped: Boolean = false
)
