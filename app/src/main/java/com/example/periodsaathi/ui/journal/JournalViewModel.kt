package com.example.periodsaathi.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class JournalEntry(
    val id: String,
    val text: String,
    val date: LocalDate,
    val moods: Set<String>,
    val isTimeCapsule: Boolean = false,
    val revealDate: LocalDate? = null
)

data class MoodData(
    val emoji: String,
    val phase: String,
    val hasEntry: Boolean
)

class JournalViewModel : ViewModel() {

    private val _entries = MutableStateFlow<List<JournalEntry>>(createSampleEntries())
    val entries: StateFlow<List<JournalEntry>> = _entries.asStateFlow()

    private val _selectedEntry = MutableStateFlow<JournalEntry?>(null)
    val selectedEntry: StateFlow<JournalEntry?> = _selectedEntry.asStateFlow()

    private val _currentDraft = MutableStateFlow("")
    val currentDraft: StateFlow<String> = _currentDraft.asStateFlow()

    private val _selectedMoods = MutableStateFlow<Set<String>>(emptySet())
    val selectedMoods: StateFlow<Set<String>> = _selectedMoods.asStateFlow()

    private val _timeCapsuleToReveal = MutableStateFlow<JournalEntry?>(null)
    val timeCapsuleToReveal: StateFlow<JournalEntry?> = _timeCapsuleToReveal.asStateFlow()

    private val _moodMapData = MutableStateFlow<Map<LocalDate, MoodData>>(emptyMap())
    val moodMapData: StateFlow<Map<LocalDate, MoodData>> = _moodMapData.asStateFlow()

    private val _currentDay = MutableStateFlow(5)
    val currentDay: StateFlow<Int> = _currentDay.asStateFlow()

    private val _currentPhase = MutableStateFlow("Follicular")
    val currentPhase: StateFlow<String> = _currentPhase.asStateFlow()

    init {
        loadMoodMapData()
    }

    fun updateDraft(text: String) {
        _currentDraft.value = text
    }

    fun toggleMood(emoji: String) {
        _selectedMoods.update { current ->
            if (current.contains(emoji)) {
                current - emoji
            } else {
                current + emoji
            }
        }
    }

    fun saveEntry(isTimeCapsule: Boolean = false) {
        val text = _currentDraft.value
        if (text.isBlank()) return

        viewModelScope.launch {
            val newEntry = JournalEntry(
                id = System.currentTimeMillis().toString(),
                text = text,
                date = LocalDate.now(),
                moods = _selectedMoods.value,
                isTimeCapsule = isTimeCapsule,
                revealDate = if (isTimeCapsule) LocalDate.now().plusDays(30) else null
            )

            _entries.update { current ->
                listOf(newEntry) + current
            }

            _currentDraft.value = ""
            _selectedMoods.value = emptySet()

            loadMoodMapData()
        }
    }

    fun selectEntry(entry: JournalEntry) {
        _selectedEntry.value = entry
    }

    fun checkForTimeCapsuleReveal() {
        val now = LocalDate.now()
        _entries.value.forEach { entry ->
            if (entry.isTimeCapsule && entry.revealDate != null && !entry.revealDate.isAfter(now)) {
                _timeCapsuleToReveal.value = entry
            }
        }
    }

    fun dismissTimeCapsule() {
        _timeCapsuleToReveal.value = null
    }

    fun deleteEntry(entryId: String) {
        _entries.update { current ->
            current.filter { it.id != entryId }
        }
    }

    private fun loadMoodMapData() {
        val data = mutableMapOf<LocalDate, MoodData>()
        val now = LocalDate.now()

        for (i in 90 downTo 0) {
            val date = now.minusDays(i.toLong())
            val hasEntry = _entries.value.any { it.date == date }
            val moods = _entries.value.filter { it.date == date }.flatMap { it.moods }

            val dayOfCycle = (i % 28) + 1
            val phase = when {
                dayOfCycle <= 5 -> "Menstrual"
                dayOfCycle <= 13 -> "Follicular"
                dayOfCycle <= 15 -> "Ovulation"
                else -> "Luteal"
            }

            val emoji = when {
                moods.contains("😊") -> "😊"
                moods.contains("🌟") -> "🌟"
                moods.contains("😰") -> "😰"
                moods.contains("😡") -> "😡"
                moods.contains("😭") -> "😭"
                else -> if (hasEntry) "📝" else ""
            }

            data[date] = MoodData(emoji, phase, hasEntry)
        }

        _moodMapData.value = data
    }

    private fun createSampleEntries(): List<JournalEntry> {
        val now = LocalDate.now()
        return listOf(
            JournalEntry(
                id = "1",
                text = "Had a great day today! Feeling optimistic about the week ahead.",
                date = now.minusDays(1),
                moods = setOf("😊", "🌟"),
                isTimeCapsule = false
            ),
            JournalEntry(
                id = "2",
                text = "Feeling a bit anxious about the upcoming presentation at work.",
                date = now.minusDays(3),
                moods = setOf("😰"),
                isTimeCapsule = false
            ),
            JournalEntry(
                id = "3",
                text = "This too shall pass. Taking it one day at a time.",
                date = now.minusDays(7),
                moods = setOf("😐"),
                isTimeCapsule = true,
                revealDate = now.plusDays(23)
            )
        )
    }
}