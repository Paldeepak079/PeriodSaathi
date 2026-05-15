package com.example.periodsaathi.ui.wellness

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.SoftLavender
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HabitItem(
    val id: String,
    val label: String,
    val emoji: String,
    val isCompleted: Boolean = false,
    val pointValue: Int,
    val color: Color = BlushPink
)

data class Reward(
    val id: String,
    val name: String,
    val description: String,
    val requiredPoints: Int,
    val isUnlocked: Boolean = false,
    val themeColors: List<Color>? = null,
    val mascotAccessory: String? = null
)

class WellnessViewModel : ViewModel() {

    private val _todayWater = MutableStateFlow(0)
    val todayWater: StateFlow<Int> = _todayWater.asStateFlow()

    private val _totalPoints = MutableStateFlow(0)
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    private val _streakCount = MutableStateFlow(0)
    val streakCount: StateFlow<Int> = _streakCount.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    private val _activeTheme = MutableStateFlow<String?>(null)
    val activeTheme: StateFlow<String?> = _activeTheme.asStateFlow()

    private val _habits = MutableStateFlow(createDefaultHabits())
    val habits: StateFlow<List<HabitItem>> = _habits.asStateFlow()

    private val _rewards = MutableStateFlow(createDefaultRewards())
    val rewards: StateFlow<List<Reward>> = _rewards.asStateFlow()

    private val waterGoal = 8

    fun addWater(glasses: Int) {
        _todayWater.update { it + glasses }
        val newTotal = _todayWater.value

        if (newTotal >= waterGoal && _todayWater.value - glasses < waterGoal) {
            _totalPoints.update { it + 50 }
            viewModelScope.launch {
                _showConfetti.value = true
                delay(3000)
                _showConfetti.value = false
            }
        }
    }

    fun toggleHabit(habitId: String) {
        _habits.update { habits ->
            habits.map { habit ->
                if (habit.id == habitId && !habit.isCompleted) {
                    _totalPoints.update { it + habit.pointValue }
                    habit.copy(isCompleted = true)
                } else {
                    habit
                }
            }
        }
        checkRewardUnlocks()
    }

    fun unlockReward(rewardId: String) {
        _rewards.update { rewards ->
            rewards.map { reward ->
                if (reward.id == rewardId && !reward.isUnlocked && _totalPoints.value >= reward.requiredPoints) {
                    reward.copy(isUnlocked = true)
                } else {
                    reward
                }
            }
        }
    }

    fun applyTheme(themeId: String) {
        _activeTheme.value = themeId
    }

    private fun checkRewardUnlocks() {
        _rewards.update { rewards ->
            rewards.map { reward ->
                if (!reward.isUnlocked && _totalPoints.value >= reward.requiredPoints) {
                    reward.copy(isUnlocked = true)
                } else {
                    reward
                }
            }
        }
    }

    private fun createDefaultHabits(): List<HabitItem> = listOf(
        HabitItem("1", "Drink 8 glasses of water", "💧", pointValue = 20, color = BabyBlue),
        HabitItem("2", "Take vitamins", "💊", pointValue = 15, color = BlushPink),
        HabitItem("3", "Light stretching", "🧘", pointValue = 25, color = SoftLavender),
        HabitItem("4", "Eat iron-rich meal", "🍎", pointValue = 20, color = Color(0xFFFF6B6B)),
        HabitItem("5", "Track mood", "📓", pointValue = 10, color = Color(0xFF98D8C8)),
        HabitItem("6", "Rest & relax", "😴", pointValue = 15, color = Color(0xFFF7DC6F))
    )

    private fun createDefaultRewards(): List<Reward> = listOf(
        Reward(
            id = "theme_pastel",
            name = "Pastel Dreams",
            description = "Soft pastel theme with gentle colors",
            requiredPoints = 50,
            themeColors = listOf(BlushPink, SoftLavender, BabyBlue)
        ),
        Reward(
            id = "mascot_crown",
            name = "Crown Accessory",
            description = "Add a cute crown to your Saathi",
            requiredPoints = 100,
            mascotAccessory = "crown"
        ),
        Reward(
            id = "theme_sunset",
            name = "Sunset Glow",
            description = "Warm sunset gradient theme",
            requiredPoints = 150,
            themeColors = listOf(Color(0xFFFF9A8B), Color(0xFFFF6B95))
        ),
        Reward(
            id = "mascot_wings",
            name = "Angel Wings",
            description = "Beautiful wings for your mascot",
            requiredPoints = 200,
            mascotAccessory = "wings"
        ),
        Reward(
            id = "theme_galaxy",
            name = "Galaxy Night",
            description = "Deep purple and starry theme",
            requiredPoints = 300,
            themeColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
        ),
        Reward(
            id = "mascot_aura",
            name = "Golden Aura",
            description = "Special glowing aura effect",
            requiredPoints = 400,
            mascotAccessory = "aura"
        )
    )

    companion object {
        const val WATER_GOAL = 8
    }
}