package com.example.periodsaathi.ui.screens.yoga

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class BreathingPhase { INHALE, HOLD, EXHALE, REST }

data class YogaPose(
    val name: String,
    val description: String,
    val benefits: String,
    val duration: Int // seconds
)

@HiltViewModel
class YogaFlowViewModel @Inject constructor() : ViewModel() {

    private val _currentPoseIndex = MutableStateFlow(0)
    val currentPoseIndex: StateFlow<Int> = _currentPoseIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _timeRemaining = MutableStateFlow(60)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _breathingPhase = MutableStateFlow(BreathingPhase.REST)
    val breathingPhase: StateFlow<BreathingPhase> = _breathingPhase.asStateFlow()

    val poses = listOf(
        YogaPose(
            "Child's Pose",
            "Kneel on the floor, sit back on heels, stretch arms forward",
            "Relieves back and neck tension, calms the mind",
            60
        ),
        YogaPose(
            "Cat-Cow",
            "On hands and knees, alternate between arching and rounding your back",
            "Improves spine flexibility, relieves menstrual cramps",
            45
        ),
        YogaPose(
            "Supine Twist",
            "Lie on back, bring knees to chest, drop them to one side",
            "Aids digestion, releases lower back tension",
            45
        ),
        YogaPose(
            "Bridge",
            "Lie on back, lift hips while keeping feet planted",
            "Strengthens core, improves blood flow to uterus",
            30
        ),
        YogaPose(
            "Savasana",
            "Lie flat, arms at sides, close eyes, relax completely",
            "Reduces stress, promotes deep relaxation",
            60
        )
    )

    fun togglePlay() {
        _isPlaying.value = !_isPlaying.value
    }

    fun nextPose() {
        if (_currentPoseIndex.value < poses.size - 1) {
            _currentPoseIndex.value++
            _timeRemaining.value = poses[_currentPoseIndex.value].duration
        }
    }

    fun previousPose() {
        if (_currentPoseIndex.value > 0) {
            _currentPoseIndex.value--
            _timeRemaining.value = poses[_currentPoseIndex.value].duration
        }
    }

    fun setBreathingPhase(phase: BreathingPhase) {
        _breathingPhase.value = phase
    }

    fun setTimeRemaining(seconds: Int) {
        _timeRemaining.value = seconds
    }
}