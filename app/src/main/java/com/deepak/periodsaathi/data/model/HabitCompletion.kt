package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "habit_completions",
    indices = [Index(value = ["date", "habitId"], unique = true)]
)
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val habitId: String,
    val completed: Boolean
)