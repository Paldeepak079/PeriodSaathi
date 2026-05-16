package com.example.periodsaathi.ui.screens.journal

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class JournalEntryUi(
    val id: Long,
    val date: String,
    val content: String,
    val mood: String,
    val phase: String
)

@HiltViewModel
class JournalViewModel @Inject constructor() : ViewModel() {
    private val _entries = MutableStateFlow<List<JournalEntryUi>>(emptyList())
    val entries: StateFlow<List<JournalEntryUi>> = _entries.asStateFlow()

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()

    private val _selectedMoods = MutableStateFlow<Set<String>>(emptySet())
    val selectedMoods: StateFlow<Set<String>> = _selectedMoods.asStateFlow()

    private val _showTimeCapsule = MutableStateFlow(false)
    val showTimeCapsule: StateFlow<Boolean> = _showTimeCapsule.asStateFlow()

    val moodOptions = listOf("😊", "😢", "😡", "😴", "🤩", "🥰")

    fun updateDraft(text: String) { _draft.value = text }

    fun toggleMood(mood: String) {
        _selectedMoods.value = if (mood in _selectedMoods.value) {
            _selectedMoods.value - mood
        } else {
            _selectedMoods.value + mood
        }
    }

    fun saveEntry() {
        val newEntry = JournalEntryUi(
            id = System.currentTimeMillis(),
            date = "Today",
            content = _draft.value,
            mood = _selectedMoods.value.joinToString(""),
            phase = "Luteal"
        )
        _entries.value = listOf(newEntry) + _entries.value
        _draft.value = ""
        _selectedMoods.value = emptySet()
    }

    fun revealTimeCapsule() { _showTimeCapsule.value = true }
}