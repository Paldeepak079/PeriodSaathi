package com.deepak.periodsaathi.wellness.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellness_tips")
data class TipEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val instructions: String,
    val benefits: String,
    val painLevel: String, // mild, moderate, severe
    val category: String, // remedy, desi, ayurveda
    val read: Boolean = false // marked true when read to award coins
)
