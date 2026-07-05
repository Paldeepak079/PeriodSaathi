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

data class SymptomPrediction(
    val symptom: String,
    val emoji: String,
    val probability: Float,
    val confidence: String
)

data class CalendarDayData(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isPeriodDay: Boolean,
    val periodIntensity: Int = 0,
    val isPredicted: Boolean = false,
    val isFertile: Boolean = false,
    val isSelected: Boolean = false,
    val mood: String? = null,
    val hasLoggedSymptoms: Boolean = false,
    val hasLoggedWater: Boolean = false,
    val hasLoggedNotes: Boolean = false,
    val hasLoggedExercise: Boolean = false,
    val hasDailyGoalMet: Boolean = false
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

    private val _selectedDate = MutableStateFlow<LocalDate?>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _showLogSheet = MutableStateFlow(false)
    val showLogSheet: StateFlow<Boolean> = _showLogSheet.asStateFlow()

    private val _fertilityMode = MutableStateFlow(FertilityMode.NEUTRAL)
    val fertilityMode: StateFlow<FertilityMode> = _fertilityMode.asStateFlow()

    private val _fertilityGoal = MutableStateFlow<String?>(null)
    val fertilityGoal: StateFlow<String?> = _fertilityGoal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasEntries = MutableStateFlow<Boolean?>(null)
    val hasEntries: StateFlow<Boolean?> = _hasEntries.asStateFlow()

    private val _showPredictorSheet = MutableStateFlow(false)
    val showPredictorSheet: StateFlow<Boolean> = _showPredictorSheet.asStateFlow()

    private val _symptomPredictions = MutableStateFlow<List<SymptomPrediction>>(emptyList())
    val symptomPredictions: StateFlow<List<SymptomPrediction>> = _symptomPredictions.asStateFlow()

    private val _nextPeriodDate = MutableStateFlow<LocalDate?>(null)
    val nextPeriodDate: StateFlow<LocalDate?> = _nextPeriodDate.asStateFlow()

    private val _fertileWindowDate = MutableStateFlow<LocalDate?>(null)
    val fertileWindowDate: StateFlow<LocalDate?> = _fertileWindowDate.asStateFlow()

    private val _cycleDataAvailable = MutableStateFlow(false)
    val cycleDataAvailable: StateFlow<Boolean> = _cycleDataAvailable.asStateFlow()

    init {
        viewModelScope.launch {
            cycleRepository.getLastNCycles(Int.MAX_VALUE).collect { entries ->
                _hasEntries.value = entries.isNotEmpty()
                if (entries.isNotEmpty()) {
                    loadMonth()
                    computeSymptomPredictions()
                }
            }
        }
    }

    private fun computeSymptomPredictions() {
        viewModelScope.launch {
            val cycles = cycleRepository.getLastNCycles(6).first()
            if (cycles.isEmpty()) {
                _cycleDataAvailable.value = false
                _symptomPredictions.value = emptyList()
                return@launch
            }
            _cycleDataAvailable.value = true

            val prediction = cycleRepository.predictNextPeriod().first()
            _nextPeriodDate.value = prediction.expectedDate

            val settings = cycleRepository.getSettings().first()
            val avgCycle = settings?.averageCycleLength ?: 28
            _fertileWindowDate.value = prediction.expectedDate.minusDays(avgCycle.toLong() - 14)

            val allEntries = cycleRepository.getLastNCycles(100).first().flatten()
            val totalCycles = cycles.size

            val symptomCounts = mapOf(
                "Bloating" to 0,
                "Cramps" to 0,
                "Mood changes" to 0,
                "Headache" to 0,
                "Fatigue" to 0
            ).toMutableMap()

            allEntries.forEach { entry ->
                val symptomsText = entry.symptoms.lowercase()
                if (symptomsText.contains("bloat")) symptomCounts["Bloating"] = (symptomCounts["Bloating"] ?: 0) + 1
                if (symptomsText.contains("cramp") || symptomsText.contains("pain")) symptomCounts["Cramps"] = (symptomCounts["Cramps"] ?: 0) + 1
                if (symptomsText.contains("mood") || symptomsText.contains("irritab") || symptomsText.contains("anxious")) symptomCounts["Mood changes"] = (symptomCounts["Mood changes"] ?: 0) + 1
                if (symptomsText.contains("headache")) symptomCounts["Headache"] = (symptomCounts["Headache"] ?: 0) + 1
                if (symptomsText.contains("fatigue") || symptomsText.contains("tired")) symptomCounts["Fatigue"] = (symptomCounts["Fatigue"] ?: 0) + 1
            }

            val total = allEntries.size.coerceAtLeast(1)
            val confidence = when {
                totalCycles >= 6 -> "High confidence"
                totalCycles >= 3 -> "Moderate confidence"
                else -> "Not enough data"
            }

            _symptomPredictions.value = listOf(
                SymptomPrediction("Bloating", "🫧",
                    ((symptomCounts["Bloating"] ?: 0).toFloat() / total).coerceAtLeast(0.35f).coerceAtMost(0.95f),
                    confidence),
                SymptomPrediction("Cramps", "😣",
                    ((symptomCounts["Cramps"] ?: 0).toFloat() / total).coerceAtLeast(0.30f).coerceAtMost(0.90f),
                    confidence),
                SymptomPrediction("Mood changes", "🌊",
                    ((symptomCounts["Mood changes"] ?: 0).toFloat() / total).coerceAtLeast(0.25f).coerceAtMost(0.85f),
                    confidence),
                SymptomPrediction("Headache", "💆",
                    ((symptomCounts["Headache"] ?: 0).toFloat() / total).coerceAtLeast(0.20f).coerceAtMost(0.75f),
                    confidence),
                SymptomPrediction("Fatigue", "😴",
                    ((symptomCounts["Fatigue"] ?: 0).toFloat() / total).coerceAtLeast(0.40f).coerceAtMost(0.95f),
                    confidence)
            )
        }
    }

    fun toggleFertilityMode() {
        _fertilityMode.value = when (_fertilityMode.value) {
            FertilityMode.NEUTRAL -> FertilityMode.PLANNING
            FertilityMode.PLANNING -> FertilityMode.AVOIDING
            FertilityMode.AVOIDING -> FertilityMode.NEUTRAL
        }
    }

    fun setFertilityGoal(goal: String?) {
        _fertilityGoal.value = goal
        loadMonth()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadMonth()
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
            try {
                val date = _selectedDate.value ?: LocalDate.now()
                val epochMillis = date.atStartOfDay(java.time.ZoneId.systemDefault())
                    .toInstant().toEpochMilli()
                cycleRepository.logCycleEntry(com.deepak.periodsaathi.data.model.CycleEntry(
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
                computeSymptomPredictions()
            } catch (_: Exception) {
            } finally {
                hideLogSheet()
            }
        }
    }

    fun togglePredictorSheet() {
        _showPredictorSheet.value = !_showPredictorSheet.value
    }

    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        loadMonth()
    }

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        loadMonth()
    }

    private fun loadMonth() {
        viewModelScope.launch {
            _isLoading.value = true
        }

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

            val entryMap = entries.associateBy { entry ->
                java.time.Instant.ofEpochMilli(entry.date)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            }

            val prediction = cycleRepository.predictNextPeriod().first()
            val predictedDates = mutableSetOf<LocalDate>()
            val fertileDates = mutableSetOf<LocalDate>()
            val ovulationDates = mutableSetOf<LocalDate>()

            if (prediction.confidence > 0.1f && prediction.daysUntil <= 45) {
                var predDate = prediction.expectedDate
                repeat(5) {
                    if (predDate.year == yearMonth.year && predDate.month == yearMonth.month) {
                        predictedDates.add(predDate)
                    }
                    predDate = predDate.plusDays(1)
                }

                val ovulationDay = prediction.expectedDate.minusDays(14)
                if (ovulationDay.year == yearMonth.year && ovulationDay.month == yearMonth.month) {
                    ovulationDates.add(ovulationDay)
                }

                for (d in 5 downTo 0) {
                    val fertileDate = ovulationDay.minusDays(d.toLong())
                    if (fertileDate.year == yearMonth.year && fertileDate.month == yearMonth.month) {
                        fertileDates.add(fertileDate)
                    }
                }
            }

            generateCalendarDays(yearMonth, periodDates, predictedDates, fertileDates, ovulationDates, entryMap)
            
            viewModelScope.launch {
                _isLoading.value = false
            }
        }
    }

    private fun generateCalendarDays(
        yearMonth: YearMonth,
        periodDates: Map<LocalDate, Int>,
        predictedDates: Set<LocalDate>,
        fertileDates: Set<LocalDate>,
        ovulationDates: Set<LocalDate>,
        entryMap: Map<LocalDate, CycleEntry>
    ) {
        val days = mutableListOf<CalendarDayData>()
        val firstDay = yearMonth.atDay(1)
        val today = LocalDate.now()

        val firstDayOfWeek = firstDay.dayOfWeek.value
        for (i in 1 until firstDayOfWeek) {
            val date = firstDay.minusDays((firstDayOfWeek - i).toLong())
            val entry = entryMap[date]
            days.add(CalendarDayData(
                date = date,
                isCurrentMonth = false,
                isToday = date == today,
                isPeriodDay = date in periodDates || date in predictedDates,
                periodIntensity = periodDates[date] ?: 0,
                isPredicted = date in predictedDates && date !in periodDates,
                isFertile = date in fertileDates,
                isSelected = date == _selectedDate.value,
                mood = entry?.mood,
                hasLoggedSymptoms = entry?.symptoms?.let { it.isNotEmpty() && it != "[]" } ?: false,
                hasLoggedWater = entry?.waterGlasses?.let { it > 0 } ?: false,
                hasLoggedNotes = !entry?.notes.isNullOrEmpty(),
                hasLoggedExercise = entry?.isRestDay ?: false,
                hasDailyGoalMet = entry?.let { (it.waterGlasses > 0 && it.symptoms.isNotEmpty() && it.symptoms != "[]") } ?: false
            ))
        }

        for (day in 1..yearMonth.lengthOfMonth()) {
            val date = yearMonth.atDay(day)
            val entry = entryMap[date]
            days.add(CalendarDayData(
                date = date,
                isCurrentMonth = true,
                isToday = date == today,
                isPeriodDay = date in periodDates || date in predictedDates,
                periodIntensity = periodDates[date] ?: 0,
                isPredicted = date in predictedDates && date !in periodDates,
                isFertile = date in fertileDates,
                isSelected = date == _selectedDate.value,
                mood = entry?.mood,
                hasLoggedSymptoms = entry?.symptoms?.let { it.isNotEmpty() && it != "[]" } ?: false,
                hasLoggedWater = entry?.waterGlasses?.let { it > 0 } ?: false,
                hasLoggedNotes = !entry?.notes.isNullOrEmpty(),
                hasLoggedExercise = entry?.isRestDay ?: false,
                hasDailyGoalMet = entry?.let { (it.waterGlasses > 0 && it.symptoms.isNotEmpty() && it.symptoms != "[]") } ?: false
            ))
        }

        val remaining = 42 - days.size
        for (i in 1..remaining) {
            val date = yearMonth.atEndOfMonth().plusDays(i.toLong())
            val entry = entryMap[date]
            days.add(CalendarDayData(
                date = date,
                isCurrentMonth = false,
                isToday = date == today,
                isPeriodDay = date in periodDates || date in predictedDates,
                periodIntensity = periodDates[date] ?: 0,
                isPredicted = date in predictedDates && date !in periodDates,
                isFertile = date in fertileDates,
                isSelected = date == _selectedDate.value,
                mood = entry?.mood,
                hasLoggedSymptoms = entry?.symptoms?.let { it.isNotEmpty() && it != "[]" } ?: false,
                hasLoggedWater = entry?.waterGlasses?.let { it > 0 } ?: false,
                hasLoggedNotes = !entry?.notes.isNullOrEmpty(),
                hasLoggedExercise = entry?.isRestDay ?: false,
                hasDailyGoalMet = entry?.let { (it.waterGlasses > 0 && it.symptoms.isNotEmpty() && it.symptoms != "[]") } ?: false
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
