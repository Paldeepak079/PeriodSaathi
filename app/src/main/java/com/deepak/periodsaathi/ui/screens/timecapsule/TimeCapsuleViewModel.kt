package com.deepak.periodsaathi.ui.screens.timecapsule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.JournalDao
import com.deepak.periodsaathi.data.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleViewModel @Inject constructor(
    private val journalDao: JournalDao
) : ViewModel() {

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveComplete = MutableSharedFlow<Unit>()
    val saveComplete: SharedFlow<Unit> = _saveComplete.asSharedFlow()

    fun updateText(text: String) {
        if (text.length <= 200) {
            _text.value = text
        }
    }

    fun saveCapsule() {
        val content = _text.value
        if (content.isBlank() || _isSaving.value) return

        viewModelScope.launch {
            _isSaving.value = true
            try {
                val entry = JournalEntry(
                    date = System.currentTimeMillis(),
                    content = content,
                    cycleDay = 0,
                    cyclePhase = "Time Capsule",
                    isTimeCapsule = true,
                    capsuleRevealDate = System.currentTimeMillis() + 28L * 24 * 60 * 60 * 1000
                )
                journalDao.insertEntry(entry)
                _text.value = ""
                _saveComplete.emit(Unit)
            } catch (_: Exception) {
            } finally {
                _isSaving.value = false
            }
        }
    }
}
