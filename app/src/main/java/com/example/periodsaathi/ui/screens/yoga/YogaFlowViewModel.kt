package com.example.periodsaathi.ui.screens.yoga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BreathingPhase { INHALE, HOLD, EXHALE, IDLE }

data class Pose(val name: String, val description: String, val benefits: String, val duration: Int)

@HiltViewModel
class YogaFlowViewModel @Inject constructor() : ViewModel() {

    private val _currentPoseIndex = MutableStateFlow(0)
    val currentPoseIndex: StateFlow<Int> = _currentPoseIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _timeRemaining = MutableStateFlow(240) // 4 minutes
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _breathingPhase = MutableStateFlow(BreathingPhase.IDLE)
    val breathingPhase: StateFlow<BreathingPhase> = _breathingPhase.asStateFlow()

    private var timerJob: Job? = null

    val poses = listOf(
        Pose("Child's Pose", "Kneel and sit back on heels, arms extended forward", "Relieves stress and fatigue", 45),
        Pose("Cat-Cow", "On hands and knees, alternate arching and rounding spine", "Improves spine flexibility", 45),
        Pose("Supine Twist", "Lying on back, bring knees to chest and drop to side", "Aids digestion, releases tension", 45),
        Pose("Bridge Pose", "Lying on back, feet flat, lift hips toward ceiling", "Strengthens back, reduces fatigue", 45),
        Pose("Savasana", "Lying flat, arms at sides, palms up, relax completely", "Deep relaxation, reduces stress", 60)
    )

    fun togglePlay() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            startTimer()
            startBreathingCycle()
        } else {
            timerJob?.cancel()
            _breathingPhase.value = BreathingPhase.IDLE
        }
    }

    fun nextPose() {
        if (_currentPoseIndex.value < poses.size - 1) {
            _currentPoseIndex.value += 1
        }
    }

    fun previousPose() {
        if (_currentPoseIndex.value > 0) {
            _currentPoseIndex.value -= 1
        }
    }

    fun exit() {
        _isPlaying.value = false
        timerJob?.cancel()
        _breathingPhase.value = BreathingPhase.IDLE
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0 && _isPlaying.value) {
                delay(1000)
                _timeRemaining.value -= 1

                // Auto advance pose
                val currentPoseDuration = poses[_currentPoseIndex.value].duration
                val elapsed = 240 - _timeRemaining.value
                val poseElapsed = elapsed % 240
                if (poseElapsed >= currentPoseDuration) {
                    nextPose()
                }
            }
            if (_timeRemaining.value == 0) {
                _isPlaying.value = false
            }
        }
    }

    private fun startBreathingCycle() {
        viewModelScope.launch {
            while (_isPlaying.value) {
                _breathingPhase.value = BreathingPhase.INHALE
                delay(4000)
                _breathingPhase.value = BreathingPhase.HOLD
                delay(2000)
                _breathingPhase.value = BreathingPhase.EXHALE
                delay(6000)
            }
        }
    }
}