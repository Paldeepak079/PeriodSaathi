package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cycle_entries", indices = [Index(value = ["date"])])
data class CycleEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val flowIntensity: String? = null,
    val symptoms: String = "[]",
    val mood: String? = null,
    val waterGlasses: Int = 0,
    val notes: String? = null,
    val isRestDay: Boolean = false,
    val cyclePhase: String = "MENSTRUAL"
)

