package com.deepak.periodsaathi.ui.screens.moodmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.CycleDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class MoodData(val mood: String, val date: Long)

@HiltViewModel
class MoodMapViewModel @Inject constructor(
    private val cycleDao: CycleDao
) : ViewModel() {

    private val _moodData = MutableStateFlow<Map<LocalDate, MoodData>>(emptyMap())
    val moodData: StateFlow<Map<LocalDate, MoodData>> = _moodData.asStateFlow()

    private val _showCycleOverlay = MutableStateFlow(true)
    val showCycleOverlay: StateFlow<Boolean> = _showCycleOverlay.asStateFlow()

    private val _selectedDay = MutableStateFlow<LocalDate?>(null)
    val selectedDay: StateFlow<LocalDate?> = _selectedDay.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val entries = cycleDao.getAllEntries().first()
            _moodData.value = entries
                .filter { it.mood != null }
                .associate { entry ->
                    val date = Instant.ofEpochMilli(entry.date)
                        .atZone(ZoneId.systemDefault()).toLocalDate()
                    date to MoodData(mood = entry.mood!!, date = entry.date)
                }
            _isLoading.value = false
        }
    }

    fun toggleCycleOverlay() {
        _showCycleOverlay.value = !_showCycleOverlay.value
    }

    fun selectDay(date: LocalDate) {
        _selectedDay.value = if (_selectedDay.value == date) null else date
    }
}

