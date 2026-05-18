package com.deepak.periodsaathi.ui.screens.challenges

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Challenge(val id: String, val title: String, val description: String, val emoji: String, val duration: String, val pointsReward: Int, val progress: Float, val isActive: Boolean, val isCompleted: Boolean)

@HiltViewModel
class ChallengesViewModel @Inject constructor() : ViewModel() {

    private val _activeChallenges = MutableStateFlow(
        listOf(
            Challenge("1", "Hydration Hero", "Log 8 glasses of water for 7 days", "💧", "7 days", 100, 0.4f, true, false),
            Challenge("2", "Mood Tracker", "Log your mood for 14 days", "😊", "14 days", 150, 0.2f, true, false),
            Challenge("3", "Symptom Detective", "Log symptoms for 5 days", "🔍", "5 days", 75, 0.6f, true, false)
        )
    )
    val activeChallenges: StateFlow<List<Challenge>> = _activeChallenges.asStateFlow()

    private val _completedChallenges = MutableStateFlow(
        listOf(
            Challenge("4", "First Step", "Complete onboarding", "👋", "1 day", 50, 1f, false, true),
            Challenge("5", "Cycle Logger", "Log first period", "🩸", "1 day", 50, 1f, false, true)
        )
    )
    val completedChallenges: StateFlow<List<Challenge>> = _completedChallenges.asStateFlow()

    fun acceptChallenge(id: String) {
        _activeChallenges.value = _activeChallenges.value.map { challenge ->
            if (challenge.id == id) challenge.copy(isActive = true) else challenge
        }
    }

    fun checkDailyProgress(challengeId: String) {
        // Update progress
        _activeChallenges.value = _activeChallenges.value.map { challenge ->
            if (challenge.id == challengeId && challenge.progress < 1f) {
                val newProgress = (challenge.progress + 0.1f).coerceAtMost(1f)
                if (newProgress >= 1f) {
                    challenge.copy(progress = 1f, isCompleted = true)
                } else {
                    challenge.copy(progress = newProgress)
                }
            } else challenge
        }
    }
}
