package com.example.periodsaathi.ui.screens.challenges

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Challenge(val id: String, val title: String, val description: String, val progress: Float, val reward: Int, val isCompleted: Boolean)

@HiltViewModel
class ChallengesViewModel @Inject constructor() : ViewModel() {
    private val _activeChallenges = MutableStateFlow(listOf(
        Challenge("1", "7-Day Streak", "Log your period for 7 days in a row", 0.6f, 100, false),
        Challenge("2", "Hydration Hero", "Drink 8 glasses of water for 5 days", 0.4f, 75, false),
        Challenge("3", "Symptom Detective", "Log 10 different symptoms this month", 0.8f, 150, false)
    ))
    val activeChallenges: StateFlow<List<Challenge>> = _activeChallenges.asStateFlow()
}