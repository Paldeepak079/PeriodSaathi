package com.example.periodsaathi.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.data.repository.CycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CalendarDayData(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isPeriodDay: Boolean,
    val periodIntensity: Int, // 0 = none, 1-5 = intensity
    val isPredicted: Boolean,
    val isFertile: Boolean,
    val isSelected: Boolean
)

enum class FertilityMode {
    NEUTRAL, PLANNING, AVOIDING
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CycleRepository
) : ViewModel() {

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    private val _calendarDays = MutableStateFlow<List<CalendarDayData>>(emptyList())
    val calendarDays: StateFlow<List<CalendarDayData>> = _calendarDays.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _showLogSheet = MutableStateFlow(false)
    val showLogSheet: StateFlow<Boolean> = _showLogSheet.asStateFlow()

    private val _fertilityMode = MutableStateFlow(FertilityMode.NEUTRAL)
    val fertilityMode: StateFlow<FertilityMode> = _fertilityMode.asStateFlow()

    init {
        loadMonth()
    }

    private fun loadMonth() {
        viewModelScope.launch {
            val yearMonth = _currentYearMonth.value
            repository.getMonthEntries(yearMonth.year, yearMonth.monthValue).collect { entries ->
                val days = generateCalendarDays(yearMonth, entries.map { it.date to it.flowIntensity })
                _calendarDays.value = days
            }
        }
    }

    private fun generateCalendarDays(yearMonth: YearMonth, periodData: List<Pair<Long, String?>): List<CalendarDayData> {
        val days = mutableListOf<CalendarDayData>()
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        val today = LocalDate.now()

        // Get day of week for first day (1 = Monday, 7 = Sunday)
        val firstDayOfWeek = firstDay.dayOfWeek.value

        // Add empty days for padding
        for (i in 1 until firstDayOfWeek) {
            days.add(
                CalendarDayData(
                    date = firstDay.minusDays((firstDayOfWeek - i).toLong()),
                    isCurrentMonth = false,
                    isToday = false,
                    isPeriodDay = false,
                    periodIntensity = 0,
                    isPredicted = false,
                    isFertile = false,
                    isSelected = false
                )
            )
        }

        // Add days of month
        var currentDay = firstDay
        while (!currentDay.isAfter(lastDay)) {
            val epochMillis = currentDay.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val periodDay = periodData.find { it.first == epochMillis }

            days.add(
                CalendarDayData(
                    date = currentDay,
                    isCurrentMonth = true,
                    isToday = currentDay == today,
                    isPeriodDay = periodDay?.second != null,
                    periodIntensity = when (periodDay?.second?.lowercase()) {
                        "light" -> 1
                        "medium", "moderate" -> 2
                        "heavy" -> 3
                        "very_heavy" -> 4
                        else -> 0
                    },
                    isPredicted = false,
                    isFertile = _fertilityMode.value != FertilityMode.NEUTRAL && currentDay.dayOfMonth in 10..16,
                    isSelected = currentDay == _selectedDate.value
                )
            )
            currentDay = currentDay.plusDays(1)
        }

        return days
    }

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        loadMonth()
    }

    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        loadMonth()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _showLogSheet.value = true
    }

    fun toggleFertilityMode() {
        _fertilityMode.value = when (_fertilityMode.value) {
            FertilityMode.NEUTRAL -> FertilityMode.PLANNING
            FertilityMode.PLANNING -> FertilityMode.AVOIDING
            FertilityMode.AVOIDING -> FertilityMode.NEUTRAL
        }
        loadMonth()
    }

    fun dismissLogSheet() {
        _showLogSheet.value = false
    }
}