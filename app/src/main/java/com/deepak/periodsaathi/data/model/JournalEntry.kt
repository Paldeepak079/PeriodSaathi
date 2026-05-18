package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val content: String,
    val moodEmoji: String? = null,
    val cycleDay: Int,
    val cyclePhase: String,
    val isTimeCapsule: Boolean = false,
    val capsuleRevealDate: Long? = null,
    val isRevealed: Boolean = false
)

