package com.deepak.periodsaathi.wellness.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long,
    val amount: Int, // e.g. +5 or -50
    val description: String, // e.g. "Completed Breathing Meditate Session" or "Unlocked Peach Sunset theme"
    val type: String // EARN or SPEND
)
