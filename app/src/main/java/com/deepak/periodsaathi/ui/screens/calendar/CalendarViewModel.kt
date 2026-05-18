package com.deepak.periodsaathi.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.repository.CycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CalendarDayData(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isPeriodDay: Boolean,
    val periodIntensity: Int = 0,
    val isPredicted: Boolean = false,
    val isFertile: Boolean = false,
    val isSelected: Boolean = false
)

enum class FertilityMode { NEUTRAL, PLANNING, AVOIDING }

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
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
    }

    fun showLogSheet() {
        _showLogSheet.value = true
    }

    fun hideLogSheet() {
        _showLogSheet.value = false
    }

    fun logEntry(
        flowIntensity: Int?,
        symptoms: List<String>,
        mood: String?,
        waterGlasses: Int,
        notes: String?
    ) {
        viewModelScope.launch {
            val date = _selectedDate.value ?: LocalDate.now()
            val epochMillis = date.atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            cycleRepository.logCycleEntry(CycleEntry(
                date = epochMillis,
                flowIntensity = when (flowIntensity) {
                    1 -> "Light"
                    2 -> "Medium"
                    3 -> "Heavy"
                    else -> null
                },
                symptoms = symptoms.joinToString(","),
                mood = mood,
                waterGlasses = waterGlasses,
                notes = notes,
                isRestDay = false
            ))
            loadMonth()
        }
        hideLogSheet()
    }

    fun toggleFertilityMode() {
        _fertilityMode.value = when (_fertilityMode.value) {
            FertilityMode.NEUTRAL -> FertilityMode.PLANNING
            FertilityMode.PLANNING -> FertilityMode.AVOIDING
            FertilityMode.AVOIDING -> FertilityMode.NEUTRAL
        }
    }

    private fun loadMonth() {
        val yearMonth = _currentYearMonth.value
        viewModelScope.launch {
            val entries = cycleRepository.getMonthEntries(
                yearMonth.year, yearMonth.monthValue
            ).first()

            val periodDates = entries
                .filter { it.flowIntensity != null }
                .associate { entry ->
                    val date = java.time.Instant.ofEpochMilli(entry.date)
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    date to intensityToLevel(entry.flowIntensity)
                }

            val prediction = cycleRepository.predictNextPeriod().first()
            val predictedDates = mutableSetOf<LocalDate>()
            if (prediction.confidence > 0.3f && prediction.daysUntil <= 40) {
                var predDate = prediction.expectedDate
                repeat(7) {
                    if (predDate.year == yearMonth.year && predDate.month == yearMonth.month) {
                        predictedDates.add(predDate)
                    }
                    predDate = predDate.plusDays(1)
                }
            }

            generateCalendarDays(yearMonth, periodDates, predictedDates)
        }
    }

    private fun generateCalendarDays(
        yearMonth: YearMonth,
        periodDates: Map<LocalDate, Int>,
        predictedDates: Set<LocalDate>
    ) {
        val days = mutableListOf<CalendarDayData>()
        val firstDay = yearMonth.atDay(1)
        val today = LocalDate.now()

        val firstDayOfWeek = firstDay.dayOfWeek.value
        for (i in 1 until firstDayOfWeek) {
            days.add(CalendarDayData(
                date = firstDay.minusDays((firstDayOfWeek - i).toLong()),
                isCurrentMonth = false, isToday = false, isPeriodDay = false
            ))
        }

        for (day in 1..yearMonth.lengthOfMonth()) {
            val date = yearMonth.atDay(day)
            days.add(CalendarDayData(
                date = date,
                isCurrentMonth = true,
                isToday = date == today,
                isPeriodDay = date in periodDates || date in predictedDates,
                periodIntensity = periodDates[date] ?: 0,
                isPredicted = date in predictedDates && date !in periodDates,
                isSelected = date == _selectedDate.value
            ))
        }

        val remaining = 42 - days.size
        for (i in 1..remaining) {
            days.add(CalendarDayData(
                date = yearMonth.atEndOfMonth().plusDays(i.toLong()),
                isCurrentMonth = false, isToday = false, isPeriodDay = false
            ))
        }

        _calendarDays.value = days
    }

    private fun intensityToLevel(intensity: String?): Int = when (intensity) {
        "Light" -> 1
        "Medium" -> 2
        "Heavy" -> 3
        else -> 0
    }
}
