package com.deepak.periodsaathi.wellness.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solutions")
data class SolutionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String = "",
    val emoji: String = "",
    val category: String, // yoga, exercise, remedy, breathing, diet, hydration, mood, sleep
    val symptomType: String = "general", // cramps, bloating, headache, fatigue, general
    val severity: String = "all", // mild, moderate, severe, all
    val ingredients: String = "[]", // JSON array
    val steps: String = "[]", // JSON array
    val benefits: String = "[]", // JSON array
    val isRecipe: Boolean = false,
    val durationMinutes: Int = 0,
    val difficulty: String = "beginner", // beginner, intermediate, advanced
    val isFavorite: Boolean = false,
    val energyLevel: String = "all" // low, normal, high, all
)
