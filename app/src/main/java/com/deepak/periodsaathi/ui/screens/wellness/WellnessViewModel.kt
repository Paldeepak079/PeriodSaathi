package com.deepak.periodsaathi.ui.screens.wellness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.CycleDao
import com.deepak.periodsaathi.data.dao.HabitDao
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.model.HabitCompletion
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.deepak.periodsaathi.domain.model.CyclePhase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HabitItem(val id: String, val name: String, val emoji: String, val isCompleted: Boolean = false)

data class WellnessUiState(
    val waterGlasses: Int = 0,
    val waterGoal: Int = 8,
    val habits: List<HabitItem> = listOf(
        HabitItem("1", "Drink warm water", "☕"),
        HabitItem("2", "Take vitamins", "💊"),
        HabitItem("3", "Stretch 10 min", "🧘"),
        HabitItem("4", "No caffeine", "🚫")
    ),
    val sleepHours: Float = 7f,
    val exerciseMinutes: Int = 0,
    val streakCount: Int = 0,
    val totalPoints: Int = 0,
    val showConfetti: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val cycleDao: CycleDao,
    private val habitDao: HabitDao,
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _wellnessState = MutableStateFlow(WellnessUiState())
    val wellnessState: StateFlow<WellnessUiState> = _wellnessState.asStateFlow()

    private var currentPhase: CyclePhase = CyclePhase.MENSTRUAL

    init {
        viewModelScope.launch {
            val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            cycleDao.getWaterTotalForDate(todayStart).collect { total ->
                _wellnessState.value = _wellnessState.value.copy(waterGlasses = total.coerceAtMost(_wellnessState.value.waterGoal))
            }
        }
        viewModelScope.launch {
            cycleRepository.getCurrentPhase().collect { phase ->
                currentPhase = phase
            }
        }
    }

    fun addWater(amount: Int) {
        val oldCount = _wellnessState.value.waterGlasses
        val newCount = (oldCount + amount).coerceIn(0, _wellnessState.value.waterGoal)
        val goalReached = newCount >= _wellnessState.value.waterGoal && oldCount < _wellnessState.value.waterGoal
        _wellnessState.value = _wellnessState.value.copy(
            waterGlasses = newCount,
            totalPoints = if (goalReached) _wellnessState.value.totalPoints + 10 else _wellnessState.value.totalPoints,
            showConfetti = goalReached
        )
        viewModelScope.launch {
            val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val existing = cycleDao.getEntryByDate(todayStart).first()
            val entry = (existing ?: CycleEntry(
                date = todayStart,
                cyclePhase = currentPhase.name
            )).copy(waterGlasses = newCount, cyclePhase = currentPhase.name)
            cycleDao.insertEntry(entry)
        }
    }

    fun toggleHabit(habitId: String) {
        val updatedHabits = _wellnessState.value.habits.map { habit ->
            if (habit.id == habitId) {
                val newCompleted = !habit.isCompleted
                if (newCompleted) {
                    _wellnessState.value = _wellnessState.value.copy(
                        totalPoints = _wellnessState.value.totalPoints + 5
                    )
                }
                habit.copy(isCompleted = newCompleted)
            } else habit
        }
        _wellnessState.value = _wellnessState.value.copy(habits = updatedHabits)

        viewModelScope.launch {
            val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val habit = updatedHabits.first { it.id == habitId }
            val habitCompletion = HabitCompletion(
                date = todayStart,
                habitId = habitId,
                completed = habit.isCompleted
            )
            habitDao.insertHabitCompletion(habitCompletion)
        }
    }

    fun setSleepHours(hours: Float) {
        _wellnessState.value = _wellnessState.value.copy(sleepHours = hours)
    }

    fun setExerciseMinutes(minutes: Int) {
        _wellnessState.value = _wellnessState.value.copy(exerciseMinutes = minutes)
    }

    fun dismissConfetti() {
        _wellnessState.value = _wellnessState.value.copy(showConfetti = false)
    }
}
