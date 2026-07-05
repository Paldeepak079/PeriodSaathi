package com.deepak.periodsaathi.ui.screens.phasecoach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.deepak.periodsaathi.domain.model.CyclePhase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyTip(
    val title: String,
    val body: String,
    val emoji: String
)

@HiltViewModel
class PhaseCoachViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _currentPhase = MutableStateFlow(CyclePhase.MENSTRUAL)
    val currentPhase: StateFlow<CyclePhase> = _currentPhase.asStateFlow()

    private val _cycleDay = MutableStateFlow(1)
    val cycleDay: StateFlow<Int> = _cycleDay.asStateFlow()

    private val _currentTipIndex = MutableStateFlow(0)
    val currentTipIndex: StateFlow<Int> = _currentTipIndex.asStateFlow()

    private val _favoritedTips = MutableStateFlow(setOf<Int>())
    val favoritedTips: StateFlow<Set<Int>> = _favoritedTips.asStateFlow()

    private val _dismissedTipIds = MutableStateFlow(setOf<Int>())
    val dismissedTipIds: StateFlow<Set<Int>> = _dismissedTipIds.asStateFlow()

    val menstrualTips = listOf(
        DailyTip("Cramp Relief", "Reach for magnesium-rich foods like dark chocolate or pumpkin seeds to help relax your uterine muscles naturally.", "🥜"),
        DailyTip("Stay Warm", "Apply a heating pad or warm compress to your lower abdomen to ease tension and improve blood flow.", "🔥"),
        DailyTip("Hydrate Wisely", "Drink warm herbal teas like chamomile or ginger — they soothe inflammation and keep you hydrated.", "🍵"),
        DailyTip("Gentle Movement", "Try light walking or restorative yoga poses to release endorphins and reduce discomfort.", "🧘"),
        DailyTip("Rest Deeply", "Your body is shedding its uterine lining — honor it with extra sleep and zero guilt about rest.", "🛌")
    )

    val follicularTips = listOf(
        DailyTip("Rising Energy", "Your energy is building — this is a great time to start new projects or hit the gym.", "⚡"),
        DailyTip("Nourish Growth", "Eat iron-rich foods like spinach and lentils to replenish what your body lost.", "🥬"),
        DailyTip("Social Spark", "Your communication peaks now — schedule meetings or catch up with friends.", "🗣️"),
        DailyTip("Skin Glow", "Estrogen is rising, giving you that natural glow — keep your skincare simple.", "✨"),
        DailyTip("Plan Ahead", "Use this high-energy window to plan your month and set big goals.", "📋")
    )

    val ovulationTips = listOf(
        DailyTip("Peak Confidence", "You're at your most magnetic — trust your voice in negotiations and presentations.", "💫"),
        DailyTip("Bond & Connect", "Oxytocin is high — nurture close relationships and practice self-love.", "💕"),
        DailyTip("Stay Grounded", "With heightened senses, ground yourself with short meditation or deep breathing.", "🌿"),
        DailyTip("Creative Flow", "Your brain is wired for big-picture thinking — capture ideas as they come.", "🎨"),
        DailyTip("Gentle Cardio", "Your joints are more lax — stick to low-impact cardio to avoid injury.", "🏃‍♀️")
    )

    val lutealTips = listOf(
        DailyTip("Tame the Mood", "Your body is sensitive to stress — prioritize calming activities and alone time.", "🌊"),
        DailyTip("Curb Cravings", "Serotonin dips can trigger sugar cravings — opt for complex carbs like oats.", "🥣"),
        DailyTip("Reduce Bloating", "Cut back on salty foods and sip peppermint tea to ease water retention.", "🌱"),
        DailyTip("Slow Down", "Your energy is fading — swap high-intensity workouts for Pilates or stretching.", "🐢"),
        DailyTip("Reflect & Journal", "Your intuition is sharp — journaling now can reveal powerful personal insights.", "📓")
    )

    fun getTipsForPhase(phase: CyclePhase): List<DailyTip> = when (phase) {
        CyclePhase.MENSTRUAL -> menstrualTips
        CyclePhase.FOLLICULAR -> follicularTips
        CyclePhase.OVULATORY, CyclePhase.OVULATION -> ovulationTips
        CyclePhase.LUTEAL, CyclePhase.PMS -> lutealTips
        CyclePhase.UNKNOWN -> menstrualTips
    }

    fun getPhaseHeadline(phase: CyclePhase): String = when (phase) {
        CyclePhase.MENSTRUAL -> "Rest & Restore"
        CyclePhase.FOLLICULAR -> "Rise & Shine"
        CyclePhase.OVULATORY, CyclePhase.OVULATION -> "Peak & Connect"
        CyclePhase.LUTEAL, CyclePhase.PMS -> "Slow & Reflect"
        CyclePhase.UNKNOWN -> "Track & Discover"
    }

    fun getPhaseEmoji(phase: CyclePhase): String = when (phase) {
        CyclePhase.MENSTRUAL -> "\uD83C\uDF39"
        CyclePhase.FOLLICULAR -> "\uD83C\uDF31"
        CyclePhase.OVULATORY, CyclePhase.OVULATION -> "\uD83E\uDD5A"
        CyclePhase.LUTEAL, CyclePhase.PMS -> "\uD83C\uDF19"
        CyclePhase.UNKNOWN -> "\uD83D\uDCCA"
    }

    fun getPhaseDescription(phase: CyclePhase, day: Int): String = when (phase) {
        CyclePhase.MENSTRUAL -> "Day $day of your cycle. Your body is working hard; give it the grace it deserves."
        CyclePhase.FOLLICULAR -> "Day $day of your cycle. Your energy is building — embrace the fresh start."
        CyclePhase.OVULATORY, CyclePhase.OVULATION -> "Day $day of your cycle. You're at your most radiant — shine on."
        CyclePhase.LUTEAL, CyclePhase.PMS -> "Day $day of your cycle. Tune inward and nurture your calm."
        CyclePhase.UNKNOWN -> "Day $day of your cycle. Keep tracking to unlock insights."
    }

    fun getSuperpower(phase: CyclePhase): Pair<String, String> = when (phase) {
        CyclePhase.MENSTRUAL -> "Intuition" to "Quiet the noise and listen to your inner self. This is your most connected week."
        CyclePhase.FOLLICULAR -> "Clarity" to "Your mind is sharp — harness this clarity to map out your goals."
        CyclePhase.OVULATORY, CyclePhase.OVULATION -> "Charisma" to "Your confidence is at its peak — speak up and own the room."
        CyclePhase.LUTEAL, CyclePhase.PMS -> "Resilience" to "You have the power to persevere — be gentle with yourself."
        CyclePhase.UNKNOWN -> "Awareness" to "Every day you track brings you closer to understanding your body."
    }

    fun getPhaseActions(phase: CyclePhase): List<Triple<String, String, String>> = when (phase) {
        CyclePhase.MENSTRUAL -> listOf(
            Triple("Rest", "\uD83D\uDECB", "bedtime"),
            Triple("Warmth", "\uD83D\uDD25", "thermostat"),
            Triple("Nourishment", "\uD83E\uDD57", "restaurant")
        )
        CyclePhase.FOLLICULAR -> listOf(
            Triple("Move", "\uD83C\uDFC3", "directions_run"),
            Triple("Plan", "\uD83D\uDCCB", "edit_note"),
            Triple("Socialize", "\uD83D\uDCAC", "group")
        )
        CyclePhase.OVULATORY, CyclePhase.OVULATION -> listOf(
            Triple("Connect", "\uD83D\uDC8B", "favorite"),
            Triple("Create", "\uD83C\uDFA8", "palette"),
            Triple("Speak Up", "\uD83D\uDCE3", "campaign")
        )
        CyclePhase.LUTEAL, CyclePhase.PMS -> listOf(
            Triple("Reflect", "\uD83D\uDCD6", "book"),
            Triple("Hydrate", "\uD83D\uDCA7", "water_drop"),
            Triple("De-stress", "\uD83E\uDDD8", "self_improvement")
        )
        CyclePhase.UNKNOWN -> listOf(
            Triple("Track", "\uD83D\uDCCA", "monitoring"),
            Triple("Learn", "\uD83D\uDCDA", "school"),
            Triple("Explore", "\uD83D\uDD0D", "search")
        )
    }

    val allPhases = listOf(
        CyclePhase.MENSTRUAL,
        CyclePhase.FOLLICULAR,
        CyclePhase.OVULATORY,
        CyclePhase.LUTEAL
    )

    fun selectPhase(phase: CyclePhase) {
        _currentPhase.value = phase
        _currentTipIndex.value = 0
    }

    fun nextTip() {
        val tips = getTipsForPhase(_currentPhase.value)
        _currentTipIndex.value = (_currentTipIndex.value + 1) % tips.size
    }

    fun previousTip() {
        val tips = getTipsForPhase(_currentPhase.value)
        _currentTipIndex.value = if (_currentTipIndex.value == 0) tips.size - 1 else _currentTipIndex.value - 1
    }

    fun toggleFavorite(index: Int) {
        val current = _favoritedTips.value.toMutableSet()
        if (index in current) current.remove(index) else current.add(index)
        _favoritedTips.value = current
    }

    fun dismissTip(index: Int) {
        _dismissedTipIds.value = _dismissedTipIds.value + index
        nextTip()
    }

    init {
        viewModelScope.launch {
            cycleRepository.getCurrentPhase().collect { phase ->
                _currentPhase.value = phase
            }
        }
        viewModelScope.launch {
            cycleRepository.getCurrentCycleDay().collect { day ->
                _cycleDay.value = day
            }
        }
    }
}
