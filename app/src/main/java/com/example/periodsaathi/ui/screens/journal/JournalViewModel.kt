package com.example.periodsaathi.ui.screens.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.data.dao.JournalDao
import com.example.periodsaathi.data.model.JournalEntry as RoomJournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalEntry(val id: String, val date: Long, val content: String, val moods: List<String>, val phase: String)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalDao: JournalDao
) : ViewModel() {

    private val _entries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val entries: StateFlow<List<JournalEntry>> = _entries.asStateFlow()

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()

    private val _selectedMoods = MutableStateFlow<Set<String>>(emptySet())
    val selectedMoods: StateFlow<Set<String>> = _selectedMoods.asStateFlow()

    private val _showTimeCapsule = MutableStateFlow(false)
    val showTimeCapsule: StateFlow<Boolean> = _showTimeCapsule.asStateFlow()

    init {
        viewModelScope.launch {
            journalDao.getAllEntries().collect { roomEntries ->
                _entries.value = roomEntries.map { it.toUiModel() }
            }
        }
    }

    fun updateDraft(text: String) {
        _draft.value = text
    }

    fun toggleMood(mood: String) {
        _selectedMoods.value = if (mood in _selectedMoods.value) {
            _selectedMoods.value - mood
        } else {
            _selectedMoods.value + mood
        }
    }

    fun saveEntry(isTimeCapsule: Boolean) {
        if (_draft.value.isBlank()) return
        val entry = RoomJournalEntry(
            date = System.currentTimeMillis(),
            content = _draft.value,
            moodEmoji = _selectedMoods.value.firstOrNull(),
            cycleDay = 0,
            cyclePhase = "Follicular",
            isTimeCapsule = isTimeCapsule,
            capsuleRevealDate = if (isTimeCapsule) System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 else null
        )
        viewModelScope.launch {
            journalDao.insertEntry(entry)
        }
        _draft.value = ""
        _selectedMoods.value = emptySet()
    }

    fun deleteEntry(id: String) {
        viewModelScope.launch {
            val roomEntry = journalDao.getAllEntries().first().find { it.id.toString() == id }
            if (roomEntry != null) {
                journalDao.deleteEntry(roomEntry)
            }
        }
    }

    fun revealTimeCapsule() {
        _showTimeCapsule.value = false
    }

    private fun RoomJournalEntry.toUiModel() = JournalEntry(
        id = id.toString(),
        date = date,
        content = content,
        moods = if (moodEmoji != null) listOf(moodEmoji) else emptyList(),
        phase = cyclePhase
    )
}
