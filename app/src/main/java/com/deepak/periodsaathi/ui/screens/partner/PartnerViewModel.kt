package com.deepak.periodsaathi.ui.screens.partner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.model.PartnerConnectionEntity
import com.deepak.periodsaathi.data.model.QuizAnswerEntity
import com.deepak.periodsaathi.data.repository.PartnerCycleInsights
import com.deepak.periodsaathi.data.repository.PartnerRepository
import com.deepak.periodsaathi.domain.model.CyclePhase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random
import javax.inject.Inject

sealed interface ConnectionUIState {
    object Idle : ConnectionUIState
    data class GeneratingInvite(val partnerName: String) : ConnectionUIState
    data class ActiveInvite(val code: String, val partnerName: String, val status: String) : ConnectionUIState
    object WaitingToConnect : ConnectionUIState
    data class Connected(val partnerName: String, val isPrimary: Boolean) : ConnectionUIState
    object Revoked : ConnectionUIState
}

sealed interface JoinUIState {
    object Idle : JoinUIState
    object Submitting : JoinUIState
    object Success : JoinUIState
    data class Error(val message: String) : JoinUIState
}

// Custom data classes for couples quizzes
data class QuizQuestion(
    val id: String,
    val text: String,
    val options: List<String>
)

data class Quiz(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val questions: List<QuizQuestion>
)

data class HormoneDataPoint(
    val label: String,
    val estrogenLevel: Float, // 0..100
    val progesteroneLevel: Float // 0..100
)

@HiltViewModel
class PartnerViewModel @Inject constructor(
    private val partnerRepository: PartnerRepository
) : ViewModel() {

    private val _joinState = MutableStateFlow<JoinUIState>(JoinUIState.Idle)
    val joinState: StateFlow<JoinUIState> = _joinState.asStateFlow()

    // 1. Connection state flow mapped from Room db
    val connectionState: StateFlow<ConnectionUIState> = partnerRepository.getActiveConnection()
        .combine(joinState) { dbConnection, joinState ->
            if (dbConnection == null) {
                if (joinState is JoinUIState.Success) {
                    ConnectionUIState.Connected("Priya", false)
                } else {
                    ConnectionUIState.Idle
                }
            } else {
                when (dbConnection.status) {
                    "PENDING" -> ConnectionUIState.ActiveInvite(
                        code = dbConnection.inviteCode,
                        partnerName = dbConnection.partnerName,
                        status = "PENDING"
                    )
                    "CONNECTED" -> ConnectionUIState.Connected(
                        partnerName = dbConnection.partnerName,
                        isPrimary = dbConnection.isPrimary
                    )
                    "REVOKED" -> ConnectionUIState.Revoked
                    else -> ConnectionUIState.Idle
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConnectionUIState.Idle
        )

    // 2. Expose secure read-only cycle insights
    val cycleInsights: StateFlow<PartnerCycleInsights?> = partnerRepository.getPartnerCycleInsights()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 3. Quizzes database (Couples Quizzes)
    val quizzesList = listOf(
        Quiz(
            id = "quiz_texting",
            title = "Texting Styles",
            subtitle = "Are your texting styles in sync?",
            emoji = "📱",
            questions = listOf(
                QuizQuestion("q1", "What does a quick response mean to you?", listOf("They care", "They are bored", "Nothing, just free")),
                QuizQuestion("q2", "Do you prefer emojis or plain text?", listOf("Lots of emojis!", "Only pure words", "Gifs and memes only")),
                QuizQuestion("q3", "Your partner double texts, you feel:", listOf("Happy & loved", "A little crowded", "Indifferent"))
            )
        ),
        Quiz(
            id = "quiz_romantic",
            title = "Romantic Style",
            subtitle = "What kind of romantic are you?",
            emoji = "💖",
            questions = listOf(
                QuizQuestion("q1", "Your ideal date night involves:", listOf("Fancy candlelit dinner", "Cozy movie at home", "Adventure/Outdoor activity")),
                QuizQuestion("q2", "Best way to express affection:", listOf("Gifts and surprises", "Words of affirmation", "Acts of service / helping")),
                QuizQuestion("q3", "How do you handle small disputes?", listOf("Talk it out immediately", "Sleep on it to cool down", "Write a text or note"))
            )
        ),
        Quiz(
            id = "quiz_support",
            title = "Mutual Understanding",
            subtitle = "How well do you support each other?",
            emoji = "🍵",
            questions = listOf(
                QuizQuestion("q1", "When she feels physical cramps, you should:", listOf("Offer heating bag immediately", "Give space and silence", "Cook her favorite dinner")),
                QuizQuestion("q2", "Best comforting drink to offer is:", listOf("Warm chamomile tea 🍵", "Dark chocolate milk 🍫", "Hot coffee ☕")),
                QuizQuestion("q3", "When she seems low, she appreciates most:", listOf("A soft foot massage", "Just being listened to", "Funny movies to laugh"))
            )
        )
    )

    // Expose all locally answered quizzes
    val quizAnswers: StateFlow<List<QuizAnswerEntity>> = partnerRepository.getAllQuizAnswers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 4. Generate Daily Support Tips based on current day of the year
    val dailyTip: StateFlow<String> = flow {
        val tips = listOf(
            "She may need extra rest today – offer to make a warm mug of herbal tea 🍵",
            "Energy is high! A perfect day for a walk in the park together or a fun casual date 🌳",
            "Be patient and offer extra comforting hugs today. A hot bag of tea can work wonders 💕",
            "Bring her favorite dark chocolate bar today. Pro-tip: quiet acts of service go a long way 🍫",
            "Offer to take care of minor house chores today so she has plenty of stress-free resting time 🛌",
            "A gentle neck or back massage is highly appreciated right now. Put on some soothing music 🎶",
            "She might feel a bit bloated or sensitive. Warm meals, warm words, and zero questions is the magic recipe 🍲"
        )
        while (true) {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val selectedTip = tips[dayOfYear % tips.size]
            emit(selectedTip)
            kotlinx.coroutines.delay(60000) // update hourly or daily
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Offer to make warm chamomile tea and slide her hot bag nearby 🍵"
    )

    // 5. Expose hormone trends coordinates depending on the current day
    fun getHormoneDataPoints(currentDay: Int): List<HormoneDataPoint> {
        val points = mutableListOf<HormoneDataPoint>()
        for (day in 1..28) {
            // Estrogen peaks at ovulation (day 14), rises again in mid-luteal
            val estrogen = if (day <= 14) {
                10f + (90f * (day / 14f) * (day / 14f))
            } else {
                20f + (50f * kotlin.math.sin((day - 14) * Math.PI / 14).toFloat())
            }

            // Progesterone is very low in follicular, rises to high levels in luteal
            val progesterone = if (day <= 14) {
                5f + (5f * (day / 14f))
            } else {
                10f + (80f * kotlin.math.sin((day - 14) * Math.PI / 14).toFloat())
            }

            // Mark phases for custom charts
            val phaseLabel = when (day) {
                1, 28 -> "Day $day"
                7 -> "🌱"
                14 -> "🥚"
                22 -> "🌙"
                else -> ""
            }
            if (day % 2 == 1 || day == 28) {
                points.add(HormoneDataPoint(phaseLabel, estrogen, progesterone))
            }
        }
        return points
    }

    // Actions
    fun generateInviteCode(partnerName: String) {
        viewModelScope.launch {
            partnerRepository.generateInviteCode(partnerName)
        }
    }

    fun joinWithCode(code: String, partnerName: String) {
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _joinState.value = JoinUIState.Error("Invite code must be exactly 6 digits.")
            return
        }
        _joinState.value = JoinUIState.Submitting
        viewModelScope.launch {
            val success = partnerRepository.connectWithCode(code, partnerName)
            if (success) {
                _joinState.value = JoinUIState.Success
            } else {
                _joinState.value = JoinUIState.Error("Invalid invite code or server sync failed.")
            }
        }
    }

    fun resetJoinState() {
        _joinState.value = JoinUIState.Idle
    }

    fun submitQuizAnswer(quizId: String, questionId: String, answerIndex: Int, isPrimary: Boolean) {
        val userType = if (isPrimary) "PRIMARY" else "PARTNER"
        viewModelScope.launch {
            partnerRepository.submitQuizAnswer(quizId, questionId, answerIndex, userType)
            
            // To make quiz simulation extremely satisfying, we automatically create
            // a mock response from the opposite partner after 1 second so the comparison works instantly!
            launch {
                kotlinx.coroutines.delay(1000)
                val targetUserType = if (isPrimary) "PARTNER" else "PRIMARY"
                // Simulate partner selecting a matching option with high probability or random index
                val mockAnswerIndex = if (Random(System.currentTimeMillis()).nextFloat() > 0.3f) {
                    answerIndex // 70% chance of matching to make the couple happy
                } else {
                    (0..2).random()
                }
                partnerRepository.submitQuizAnswer(quizId, questionId, mockAnswerIndex, targetUserType)
            }
        }
    }

    fun revokeConnection() {
        viewModelScope.launch {
            partnerRepository.revokeConnection()
            _joinState.value = JoinUIState.Idle
        }
    }
}
