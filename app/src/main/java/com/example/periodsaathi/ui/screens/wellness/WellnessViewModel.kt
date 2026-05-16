package com.example.periodsaathi.ui.screens.wellness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.data.model.CycleSettings
import com.example.periodsaathi.data.repository.CycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WellnessUiState(
    val waterGlasses: Int = 0,
    val waterGoal: Int = 8,
    val habits: List<Habit> = emptyList(),
    val sleepHours: Float = 7f,
    val exerciseMinutes: Int = 30,
    val streakCount: Int = 0,
    val totalPoints: Int = 0,
    val showConfetti: Boolean = false
)

data class Habit(
    val id: String,
    val name: String,
    val emoji: String,
    val isCompleted: Boolean
)

@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val repository: CycleRepository
) : ViewModel() {

    private val _wellnessState = MutableStateFlow(WellnessUiState())
    val wellnessState: StateFlow<WellnessUiState> = _wellnessState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                val defaultHabits = listOf(
                    Habit("1", "Drink 8 glasses", "💧", false),
                    Habit("2", "Log symptoms", "📝", false),
                    Habit("3", "Take vitamins", "💊", false),
                    Habit("4", "Light exercise", "🚶", false),
                    Habit("5", "Meditate", "🧘", false)
                )
                _wellnessState.value = _wellnessState.value.copy(
                    habits = defaultHabits,
                    totalPoints = settings?.totalPoints ?: 0,
                    streakCount = settings?.streakCount ?: 0
                )
            }
        }
    }

    fun addWater(glasses: Int) {
        val newGlasses = (_wellnessState.value.waterGlasses + glasses).coerceAtMost(_wellnessState.value.waterGoal)
        val showConfetti = newGlasses >= _wellnessState.value.waterGoal && _wellnessState.value.waterGlasses < _wellnessState.value.waterGoal

        _wellnessState.value = _wellnessState.value.copy(
            waterGlasses = newGlasses,
            showConfetti = showConfetti,
            totalPoints = _wellnessState.value.totalPoints + glasses
        )
    }

    fun toggleHabit(habitId: String) {
        val habits = _wellnessState.value.habits.map { habit ->
            if (habit.id == habitId) {
                habit.copy(isCompleted = !habit.isCompleted)
            } else habit
        }
        val completedCount = habits.count { it.isCompleted }
        val pointsEarned = if (completedCount == habits.size) 10 else 1

        _wellnessState.value = _wellnessState.value.copy(
            habits = habits,
            totalPoints = _wellnessState.value.totalPoints + pointsEarned
        )
    }

    fun dismissConfetti() {
        _wellnessState.value = _wellnessState.value.copy(showConfetti = false)
    }
}