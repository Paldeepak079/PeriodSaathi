package com.deepak.periodsaathi.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.deepak.periodsaathi.domain.model.CyclePhase
import com.deepak.periodsaathi.domain.model.PeriodPrediction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MascotEmotion { HAPPY, EXCITED, SLEEPING, SUPPORTIVE }

data class HomeUiState(
    val userName: String = "Friend",
    val cycleDay: Int = 1,
    val totalCycleDays: Int = 28,
    val phase: CyclePhase = CyclePhase.FOLLICULAR,
    val phaseDayInPhase: Int = 2,
    val todayEntry: CycleEntry? = null,
    val prediction: PeriodPrediction? = null,
    val waterGlasses: Int = 0,
    val streakCount: Int = 0,
    val points: Int = 0,
    val mascotEmotion: MascotEmotion = MascotEmotion.HAPPY,
    val showRestDay: Boolean = false,
    val showConfetti: Boolean = false,
    val isFirstLaunch: Boolean = false,
    val mascotTipIndex: Int = 0
)

data class CycleEntry(
    val flowIntensity: Int = 0,
    val symptoms: List<String> = emptyList(),
    val mood: String? = null,
    val waterGlasses: Int = 0,
    val isRestDay: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val mascotTips = listOf(
        "Remember to stay hydrated! 💧",
        "Gentle stretching can help with cramps 🧘",
        "You're doing great! Keep tracking 🌟",
        "Self-care is important today 💕",
        "Listen to your body 💪"
    )

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            cycleRepository.getCurrentCycleDay().collect { day ->
                val s = _uiState.value
                _uiState.value = s.copy(
                    cycleDay = day,
                    phaseDayInPhase = computePhaseDayInPhase(day, s.phase, s.totalCycleDays)
                )
            }
        }
        viewModelScope.launch {
            cycleRepository.getCurrentPhase().collect { phase ->
                val s = _uiState.value
                _uiState.value = s.copy(
                    phase = phase,
                    phaseDayInPhase = computePhaseDayInPhase(s.cycleDay, phase, s.totalCycleDays)
                )
            }
        }
        viewModelScope.launch {
            cycleRepository.predictNextPeriod().collect { prediction ->
                _uiState.value = _uiState.value.copy(prediction = prediction)
            }
        }
    }

    private fun computePhaseDayInPhase(cycleDay: Int, phase: CyclePhase, totalDays: Int): Int {
        val phaseStartDay = when (phase) {
            CyclePhase.MENSTRUAL -> 1
            CyclePhase.FOLLICULAR -> maxOf(1, (totalDays * 0.20f).toInt() + 1)
            CyclePhase.OVULATORY, CyclePhase.OVULATION -> maxOf(1, (totalDays * 0.55f).toInt() + 1)
            CyclePhase.LUTEAL -> maxOf(1, (totalDays * 0.65f).toInt() + 1)
            CyclePhase.PMS -> maxOf(1, (totalDays * 0.90f).toInt() + 1)
            CyclePhase.UNKNOWN -> 1
        }
        return (cycleDay - phaseStartDay + 1).coerceAtLeast(1)
    }

    fun logWater(amount: Int) {
        val newAmount = (_uiState.value.waterGlasses + amount).coerceIn(0, 8)
        _uiState.value = _uiState.value.copy(
            waterGlasses = newAmount,
            points = if (newAmount >= 8) _uiState.value.points + 1 else _uiState.value.points
        )
    }

    fun dismissRestDay() {
        _uiState.value = _uiState.value.copy(showRestDay = false)
    }

    fun onMascotTapped() {
        val newIndex = (_uiState.value.mascotTipIndex + 1) % mascotTips.size
        _uiState.value = _uiState.value.copy(mascotTipIndex = newIndex)
    }

    fun dismissConfetti() {
        _uiState.value = _uiState.value.copy(showConfetti = false)
    }

    fun dismissMedicalDisclaimer() {
        _uiState.value = _uiState.value.copy(isFirstLaunch = false)
    }

    fun refresh() {
        loadHomeData()
    }
}
