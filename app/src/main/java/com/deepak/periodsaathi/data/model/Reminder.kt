package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val timeHour: Int,
    val timeMinute: Int,
    val isEnabled: Boolean = true,
    val label: String,
    val intervalHours: Int? = null
)

