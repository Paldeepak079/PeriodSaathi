package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remedies")
data class RemedyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val emoji: String,
    val ingredients: String,
    val steps: String,
    val helpsWith: String,
    val isFavorited: Boolean = false
)
