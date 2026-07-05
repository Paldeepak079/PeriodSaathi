package com.deepak.periodsaathi.ui.screens.home

import androidx.lifecycle.ViewModel
import java.time.LocalDate
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.gamification.GamificationManager
import com.deepak.periodsaathi.data.gamification.PointEvent
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.deepak.periodsaathi.domain.model.CyclePhase
import com.deepak.periodsaathi.domain.model.PeriodPrediction
import com.deepak.periodsaathi.data.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.deepak.periodsaathi.ui.components.BiologicalState
import com.deepak.periodsaathi.domain.HealthQueryEngine
import com.deepak.periodsaathi.domain.HealthQueryInput
import com.deepak.periodsaathi.domain.HealthQueryResult
import com.deepak.periodsaathi.domain.QueryType
import com.deepak.periodsaathi.data.model.CycleEntry
import java.time.ZoneId

enum class MascotEmotion { HAPPY, EXCITED, SLEEPING, SUPPORTIVE }

data class HomeUiState(
    val userName: String = "Friend",
    val cycleDay: Int = 1,
    val totalCycleDays: Int = 28,
    val phase: CyclePhase = CyclePhase.FOLLICULAR,
    val selectedTabPhase: CyclePhase = CyclePhase.FOLLICULAR,
    val phaseDayInPhase: Int = 2,
    val prediction: PeriodPrediction? = null,
    val waterGlasses: Int = 0,
    val todayFlow: String? = null,
    val todayMood: String? = null,
    val streakCount: Int = 0,
    val points: Int = 0,
    val mascotEmotion: MascotEmotion = MascotEmotion.HAPPY,
    val biologicalState: BiologicalState = BiologicalState.LINING_PHASE,
    val showRestDay: Boolean = false,
    val showConfetti: Boolean = false,
    val isFirstLaunch: Boolean = false,
    val mascotTipIndex: Int = 0,
    val mascotTipText: String = "Remember to stay hydrated! \uD83D\uDCA7",
    val healthQueryResult: HealthQueryResult? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    private val healthQueryEngine: HealthQueryEngine,
    private val userPreferences: UserPreferences,
    private val gamificationManager: GamificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val tomorrowEpoch = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private val mascotTips = listOf(
        "Remember to stay hydrated! \uD83D\uDCA7",
        "Gentle stretching can help with cramps \uD83E\uDDD8",
        "You're doing great! Keep tracking \uD83C\uDF1F",
        "Self-care is important today \uD83D\uDC95",
        "Listen to your body \uD83D\uDCAA"
    )

    private val phaseTips = mapOf(
        CyclePhase.MENSTRUAL to listOf(
            "Rest up — your body is doing important work \uD83D\uDECC",
            "Stay warm and cozy with a heating pad \u2615",
            "Iron-rich foods help replenish your energy \uD83E\uDD69",
            "Gentle walks can ease cramp discomfort \uD83D\uDEB6",
            "You deserve extra self-care today \uD83D\uDC95"
        ),
        CyclePhase.FOLLICULAR to listOf(
            "Your energy is rising — time to move! \uD83C\uDFC3",
            "Nourish your body with fresh greens \uD83E\uDD57",
            "Social plans? Your confidence is peaking \uD83D\uDDE3\uFE0F",
            "Great time to start new projects \uD83D\uDE80",
            "Your skin is glowing — embrace it \u2728"
        ),
        CyclePhase.OVULATORY to listOf(
            "Your communication superpower is active \uD83C\uDFAF",
            "Connection and collaboration feel effortless \uD83E\uDD1D",
            "Trust your intuition today \uD83E\uDDE0",
            "Your energy is magnetic — own it \u26A1",
            "Perfect day for a creative breakthrough \uD83C\uDFA8"
        ),
        CyclePhase.LUTEAL to listOf(
            "Slow down and listen to your body \uD83C\uDF3F",
            "Warm herbal tea can soothe your nerves \uD83C\uDF75",
            "Journaling helps process big emotions \uD83D\uDCDD",
            "Prioritize rest over productivity \uD83D\uDECB\uFE0F",
            "You're almost there — be gentle with yourself \uD83E\uDD17"
        ),
        CyclePhase.PMS to listOf(
            "Hormones are real — be kind to yourself \uD83C\uDF0A",
            "Dark chocolate counts as self-care \uD83C\uDF6B",
            "Give yourself permission to rest \uD83D\uDE34",
            "Deep breaths — this too shall pass \uD83C\uDF2C\uFE0F",
            "You're not alone in how you feel \uD83D\uDC97"
        ),
        CyclePhase.UNKNOWN to listOf(
            "Start tracking to unlock insights \uD83D\uDCCA",
            "Every cycle tells a story \uD83D\uDCD6",
            "Consistency is key — log daily \u2705",
            "You're building valuable self-knowledge \uD83E\uDDE0",
            "Welcome to your journey! \uD83C\uDF1F"
        )
    )

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            val prefsName = userPreferences.userName.first()
            combine(
                cycleRepository.getCurrentCycleDay(),
                cycleRepository.getCurrentPhase(),
                cycleRepository.predictNextPeriod(),
                cycleRepository.getSettings()
            ) { day, phase, prediction, settings ->
                val s = _uiState.value
                val newSelected = if (s.selectedTabPhase == s.phase) phase else s.selectedTabPhase
                val displayName = settings.userName.ifBlank { prefsName.ifBlank { s.userName } }
                HomeUiState(
                    userName = displayName,
                    cycleDay = day,
                    totalCycleDays = settings.averageCycleLength,
                    phase = phase,
                    selectedTabPhase = newSelected,
                    phaseDayInPhase = computePhaseDayInPhase(day, phase, settings.averageCycleLength),
                    prediction = prediction,
                    mascotEmotion = s.mascotEmotion,
                    biologicalState = s.biologicalState,
                    mascotTipIndex = s.mascotTipIndex,
                    mascotTipText = getPhaseTip(newSelected, s.mascotTipIndex),
                    streakCount = s.streakCount,
                    points = s.points,
                    waterGlasses = s.waterGlasses,
                    todayFlow = s.todayFlow,
                    todayMood = s.todayMood,
                    isFirstLaunch = s.isFirstLaunch,
                    showRestDay = s.showRestDay,
                    showConfetti = s.showConfetti,
                    healthQueryResult = s.healthQueryResult
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }

        viewModelScope.launch {
            cycleRepository.observeEntryByDate(todayEpoch).collect { entry ->
                _uiState.value = _uiState.value.copy(
                    waterGlasses = entry?.waterGlasses ?: 0,
                    todayFlow = entry?.flowIntensity,
                    todayMood = entry?.mood,
                    showRestDay = entry?.isRestDay ?: false
                )
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
        viewModelScope.launch {
            val entry = cycleRepository.getEntryByDate(todayEpoch)
            val currentWater = (entry?.waterGlasses ?: 0) + amount
            val newWater = currentWater.coerceIn(0, 8)
            if (entry != null) {
                cycleRepository.updateEntry(entry.copy(waterGlasses = newWater))
            } else {
                cycleRepository.logCycleEntry(
                    CycleEntry(date = todayEpoch, waterGlasses = newWater)
                )
            }
            _uiState.value = _uiState.value.copy(
                waterGlasses = newWater,
                points = if (newWater >= 8) _uiState.value.points + 1 else _uiState.value.points
            )
            if (newWater >= 8) {
                gamificationManager.awardPoints(PointEvent.WATER_LOGGED)
            }
        }
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
        val bioState = when (phase) {
            CyclePhase.MENSTRUAL -> BiologicalState.MENSTRUATION
            CyclePhase.FOLLICULAR -> BiologicalState.FOLLICLE_GROWTH
            CyclePhase.OVULATORY, CyclePhase.OVULATION -> BiologicalState.OVULATION
            CyclePhase.LUTEAL -> BiologicalState.LINING_PHASE
            CyclePhase.PMS -> BiologicalState.LINING_PHASE
            CyclePhase.UNKNOWN -> BiologicalState.DEFAULT
        }
        val s = _uiState.value
        _uiState.value = s.copy(
            selectedTabPhase = phase,
            phaseDayInPhase = computePhaseDayInPhase(s.cycleDay, phase, s.totalCycleDays),
            mascotEmotion = emotion,
            biologicalState = bioState,
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

    fun assessHealth(queryType: QueryType) {
        val lastPeriod = LocalDate.now().minusDays(
            (_uiState.value.cycleDay - 1).toLong()
        )
        val result = healthQueryEngine.assess(
            HealthQueryInput(
                queryType = queryType,
                lastPeriodDate = lastPeriod,
                cycleLength = _uiState.value.totalCycleDays
            )
        )
        _uiState.value = _uiState.value.copy(healthQueryResult = result)
    }

    fun dismissHealthQuery() {
        _uiState.value = _uiState.value.copy(healthQueryResult = null)
    }

    fun rotateTip() {
        val s = _uiState.value
        val newIndex = s.mascotTipIndex + 1
        _uiState.value = s.copy(
            mascotTipIndex = newIndex,
            mascotTipText = getPhaseTip(s.selectedTabPhase, newIndex)
        )
    }
}
