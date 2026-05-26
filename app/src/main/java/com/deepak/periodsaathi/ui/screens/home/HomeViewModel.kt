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
    val selectedTabPhase: CyclePhase = CyclePhase.FOLLICULAR,
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
    val mascotTipIndex: Int = 0,
    val mascotTipText: String = "Remember to stay hydrated! 💧"
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

    private val phaseTips = mapOf(
        CyclePhase.MENSTRUAL to listOf(
            "Rest up — your body is doing important work 🛌",
            "Stay warm and cozy with a heating pad ☕",
            "Iron-rich foods help replenish your energy 🥩",
            "Gentle walks can ease cramp discomfort 🚶",
            "You deserve extra self-care today 💕"
        ),
        CyclePhase.FOLLICULAR to listOf(
            "Your energy is rising — time to move! 🏃",
            "Nourish your body with fresh greens 🥗",
            "Social plans? Your confidence is peaking 🗣️",
            "Great time to start new projects 🚀",
            "Your skin is glowing — embrace it ✨"
        ),
        CyclePhase.OVULATORY to listOf(
            "Your communication superpower is active 🎯",
            "Connection and collaboration feel effortless 🤝",
            "Trust your intuition today 🧠",
            "Your energy is magnetic — own it ⚡",
            "Perfect day for a creative breakthrough 🎨"
        ),
        CyclePhase.LUTEAL to listOf(
            "Slow down and listen to your body 🌿",
            "Warm herbal tea can soothe your nerves 🍵",
            "Journaling helps process big emotions 📝",
            "Prioritize rest over productivity 🛋️",
            "You're almost there — be gentle with yourself 🤗"
        ),
        CyclePhase.PMS to listOf(
            "Hormones are real — be kind to yourself 🌊",
            "Dark chocolate counts as self-care 🍫",
            "Give yourself permission to rest 😴",
            "Deep breaths — this too shall pass 🌬️",
            "You're not alone in how you feel 💗"
        ),
        CyclePhase.UNKNOWN to listOf(
            "Start tracking to unlock insights 📊",
            "Every cycle tells a story 📖",
            "Consistency is key — log daily ✅",
            "You're building valuable self-knowledge 🧠",
            "Welcome to your journey! 🌟"
        )
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
                val newSelected = if (s.selectedTabPhase == s.phase) phase else s.selectedTabPhase
                _uiState.value = s.copy(
                    phase = phase,
                    selectedTabPhase = newSelected,
                    phaseDayInPhase = computePhaseDayInPhase(s.cycleDay, phase, s.totalCycleDays),
                    mascotTipText = getPhaseTip(newSelected, s.mascotTipIndex)
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

    private fun getPhaseTip(phase: CyclePhase, index: Int): String {
        val tips = phaseTips[phase] ?: mascotTips
        return tips[index % tips.size]
    }

    fun onMascotTapped() {
        val s = _uiState.value
        val newIndex = (s.mascotTipIndex + 1)
        _uiState.value = s.copy(
            mascotTipIndex = newIndex,
            mascotTipText = getPhaseTip(s.selectedTabPhase, newIndex)
        )
    }

    fun onPhaseTabSelected(phase: CyclePhase) {
        val emotion = when (phase) {
            CyclePhase.MENSTRUAL -> MascotEmotion.SLEEPING
            CyclePhase.FOLLICULAR -> MascotEmotion.HAPPY
            CyclePhase.OVULATORY, CyclePhase.OVULATION -> MascotEmotion.EXCITED
            CyclePhase.LUTEAL -> MascotEmotion.SUPPORTIVE
            CyclePhase.PMS -> MascotEmotion.SUPPORTIVE
            CyclePhase.UNKNOWN -> MascotEmotion.HAPPY
        }
        val s = _uiState.value
        _uiState.value = s.copy(
            selectedTabPhase = phase,
            phaseDayInPhase = computePhaseDayInPhase(s.cycleDay, phase, s.totalCycleDays),
            mascotEmotion = emotion,
            mascotTipIndex = 0,
            mascotTipText = getPhaseTip(phase, 0)
        )
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
