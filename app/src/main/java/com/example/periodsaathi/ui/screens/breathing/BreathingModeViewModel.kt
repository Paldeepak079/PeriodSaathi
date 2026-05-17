package com.example.periodsaathi.ui.screens.breathing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BreathingPhase { INHALE, HOLD_IN, EXHALE, HOLD_OUT }

@HiltViewModel
class BreathingModeViewModel @Inject constructor() : ViewModel() {

    private val _phase = MutableStateFlow(BreathingPhase.INHALE)
    val phase: StateFlow<BreathingPhase> = _phase.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _cycleCount = MutableStateFlow(0)
    val cycleCount: StateFlow<Int> = _cycleCount.asStateFlow()

    private var breathingJob: Job? = null

    private val phaseDurations = listOf(
        BreathingPhase.INHALE to 4000L,
        BreathingPhase.HOLD_IN to 4000L,
        BreathingPhase.EXHALE to 4000L,
        BreathingPhase.HOLD_OUT to 2000L
    )

    private val phaseOrder = listOf(
        BreathingPhase.INHALE,
        BreathingPhase.HOLD_IN,
        BreathingPhase.EXHALE,
        BreathingPhase.HOLD_OUT
    )

    fun startBreathing() {
        if (_isRunning.value) return
        _isRunning.value = true
        _cycleCount.value = 0

        breathingJob = viewModelScope.launch {
            while (isActive) {
                for (currentPhase in phaseOrder) {
                    val duration = phaseDurations.first { it.first == currentPhase }.second
                    _phase.value = currentPhase
                    val startTime = System.currentTimeMillis()

                    while (isActive && System.currentTimeMillis() - startTime < duration) {
                        val elapsed = System.currentTimeMillis() - startTime
                        _progress.value = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
                        delay(16)
                    }
                    _progress.value = 1f
                }
                _cycleCount.value = _cycleCount.value + 1
                if (_cycleCount.value >= 10) {
                    _isRunning.value = false
                    break
                }
            }
        }
    }

    fun stopBreathing() {
        breathingJob?.cancel()
        _isRunning.value = false
        _phase.value = BreathingPhase.INHALE
        _progress.value = 0f
    }
}
