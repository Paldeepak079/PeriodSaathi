package com.deepak.periodsaathi.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.datastore.UserPreferences
import com.deepak.periodsaathi.ui.components.MascotEmotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class OnboardingQuestion {
    data class SingleChoice(
        val id: String,
        val title: String,
        val subtitle: String,
        val options: List<String>,
        val mascotEmotion: MascotEmotion
    ) : OnboardingQuestion()

    data class MultiChoice(
        val id: String,
        val title: String,
        val subtitle: String,
        val options: List<String>,
        val mascotEmotion: MascotEmotion
    ) : OnboardingQuestion()
}

fun defaultQuestions(): List<OnboardingQuestion> = listOf(
    OnboardingQuestion.MultiChoice(
        id = "goals",
        title = "Join 500K+ women tracking their wellness journey",
        subtitle = "Select all that apply — Saathi adapts to your needs",
        options = listOf("Track My Cycle", "Get Pregnant", "Track Pregnancy"),
        mascotEmotion = MascotEmotion.HAPPY
    ),
    OnboardingQuestion.SingleChoice(
        id = "birth_control",
        title = "Are you currently using birth control?",
        subtitle = "This helps Saathi give more accurate predictions",
        options = listOf("None / Natural", "Birth Control Pill", "IUD", "Implant", "Condoms", "Other"),
        mascotEmotion = MascotEmotion.LISTENING
    ),
    OnboardingQuestion.SingleChoice(
        id = "cycle_length",
        title = "How long is your typical cycle?",
        subtitle = "From the start of one period to the next",
        options = listOf("21-24 days", "25-28 days", "29-32 days", "33-35 days", "36-45 days", "Not sure"),
        mascotEmotion = MascotEmotion.LISTENING
    ),
    OnboardingQuestion.SingleChoice(
        id = "period_length",
        title = "How many days does your period usually last?",
        subtitle = "The number of days you bleed",
        options = listOf("2-3 days", "4-5 days", "6-7 days", "8-10 days", "Not sure"),
        mascotEmotion = MascotEmotion.LISTENING
    ),
    OnboardingQuestion.SingleChoice(
        id = "last_period",
        title = "When did your last period start?",
        subtitle = "This is used to predict your next cycle",
        options = emptyList(),
        mascotEmotion = MascotEmotion.HAPPY
    )
)

data class OnboardingUiState(
    val currentStep: Int = 0,
    val questions: List<OnboardingQuestion> = defaultQuestions(),
    val selectedGoals: Set<String> = setOf("Track My Cycle"),
    val selectedBirthControl: String? = null,
    val selectedCycleLength: String? = null,
    val selectedPeriodLength: String? = null,
    val lastPeriodDate: String = LocalDate.now().minusDays(14).format(DateTimeFormatter.ISO_LOCAL_DATE),
    val mascotEmotion: MascotEmotion = MascotEmotion.HAPPY,
    val isSaving: Boolean = false
) {
    val totalSteps: Int get() = questions.size + 1
    val isSummaryStep: Boolean get() = currentStep >= questions.size
    val currentQuestion: OnboardingQuestion? get() = questions.getOrNull(currentStep)
    val canGoNext: Boolean get() = when {
        isSummaryStep -> true
        currentQuestion == null -> false
        currentQuestion.id == "goals" -> selectedGoals.isNotEmpty()
        currentQuestion.id == "birth_control" -> selectedBirthControl != null
        currentQuestion.id == "cycle_length" -> selectedCycleLength != null
        currentQuestion.id == "period_length" -> selectedPeriodLength != null
        currentQuestion.id == "last_period" -> lastPeriodDate.isNotBlank()
        else -> false
    }
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectMultiOption(option: String) {
        _uiState.update { state ->
            val goals = state.selectedGoals.toMutableSet()
            if (option in goals) {
                if (goals.size > 1) goals.remove(option)
            } else {
                goals.add(option)
            }
            state.copy(selectedGoals = goals)
        }
    }

    fun selectSingleOption(questionId: String, option: String) {
        _uiState.update { state ->
            when (questionId) {
                "birth_control" -> state.copy(selectedBirthControl = option)
                "cycle_length" -> state.copy(selectedCycleLength = option)
                "period_length" -> state.copy(selectedPeriodLength = option)
                else -> state
            }
        }
    }

    fun setLastPeriodDate(date: String) {
        _uiState.update { it.copy(lastPeriodDate = date) }
    }

    fun goToNextStep() {
        _uiState.update { state ->
            val nextStep = state.currentStep + 1
            val emotion = if (nextStep < state.questions.size) {
                val q = state.questions[nextStep]
                when (q) {
                    is OnboardingQuestion.SingleChoice -> q.mascotEmotion
                    is OnboardingQuestion.MultiChoice -> q.mascotEmotion
                }
            } else MascotEmotion.EXCITED
            state.copy(currentStep = nextStep, mascotEmotion = emotion)
        }
    }

    fun goToPreviousStep() {
        _uiState.update { state ->
            if (state.currentStep <= 0) return@update state
            val prevStep = state.currentStep - 1
            val emotion = if (prevStep < state.questions.size) {
                val q = state.questions[prevStep]
                when (q) {
                    is OnboardingQuestion.SingleChoice -> q.mascotEmotion
                    is OnboardingQuestion.MultiChoice -> q.mascotEmotion
                }
            } else MascotEmotion.HAPPY
            state.copy(currentStep = prevStep, mascotEmotion = emotion)
        }
    }

    fun completeOnboarding(onDone: () -> Unit) {
        val s = _uiState.value
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                userPreferences.saveOnboardingData(
                    goals = s.selectedGoals.map { goalToKey(it) },
                    birthControl = s.selectedBirthControl?.let { bcToKey(it) } ?: "none",
                    cycleLength = parseCycleLength(s.selectedCycleLength),
                    periodLength = parsePeriodLength(s.selectedPeriodLength),
                    lastPeriodStart = s.lastPeriodDate.ifBlank {
                        LocalDate.now().minusDays(14).format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                )
                onDone()
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun goalToKey(goal: String): String = when {
        goal.contains("Cycle", ignoreCase = true) -> "track_cycle"
        goal.contains("Pregnant", ignoreCase = true) -> "get_pregnant"
        goal.contains("Pregnancy", ignoreCase = true) -> "track_pregnancy"
        else -> "track_cycle"
    }

    private fun bcToKey(bc: String): String = when {
        bc.contains("None", ignoreCase = true) -> "none"
        bc.contains("Pill", ignoreCase = true) -> "pill"
        bc.equals("IUD", ignoreCase = true) -> "iud"
        bc.contains("Implant", ignoreCase = true) -> "implant"
        bc.contains("Condom", ignoreCase = true) -> "condom"
        else -> "other"
    }

    private fun parseCycleLength(option: String?): Int = when (option) {
        "21-24 days" -> 23
        "25-28 days" -> 27
        "29-32 days" -> 30
        "33-35 days" -> 34
        "36-45 days" -> 38
        else -> 28
    }

    private fun parsePeriodLength(option: String?): Int = when (option) {
        "2-3 days" -> 3
        "4-5 days" -> 5
        "6-7 days" -> 7
        "8-10 days" -> 9
        else -> 5
    }
}
