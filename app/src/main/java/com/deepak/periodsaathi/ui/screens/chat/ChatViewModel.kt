package com.deepak.periodsaathi.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.datastore.UserPreferences
import com.deepak.periodsaathi.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

// ─── Data Models ────────────────────────────────────────────────────────────

enum class MessageSender { USER, SAATHI }

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val riskLevel: RiskLevel? = null,
    val actionItems: List<String> = emptyList(),
    val timestamp: String = LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"))
)

data class SuggestedPrompt(
    val label: String,
    val emoji: String,
    val queryType: QueryType
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isTyping: Boolean = false,
    val error: String? = null,
    val showSuggestions: Boolean = true
)

// ─── ViewModel ───────────────────────────────────────────────────────────────

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val healthQueryEngine: HealthQueryEngine,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val suggestedPrompts = listOf(
        SuggestedPrompt("Am I pregnant?", "\uD83E\uDD31", QueryType.PREGNANCY_RISK),
        SuggestedPrompt("Why is my cycle taking longer?", "\uD83D\uDCC5", QueryType.CYCLE_DELAY),
        SuggestedPrompt("Are my cramps normal?", "\uD83D\uDE14", QueryType.CRAMPS_NORMAL),
        SuggestedPrompt("Why am I breaking out?", "\uD83D\uDCA7", QueryType.ACNE_REASONS)
    )

    init {
        // Greeting message from Saathi
        viewModelScope.launch {
            val name = userPreferences.userName.first().ifBlank { "there" }
            _uiState.update { it.copy(
                messages = listOf(
                    ChatMessage(
                        sender = MessageSender.SAATHI,
                        text = "Hey $name! \uD83C\uDF38 I'm Saathi, your personal cycle wellness guide. " +
                                "I can answer questions about your cycle, symptoms, and hormonal health. " +
                                "What's on your mind today?"
                    )
                )
            )}
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage(text: String = _uiState.value.inputText.trim()) {
        if (text.isBlank()) return

        // Add user message
        val userMsg = ChatMessage(sender = MessageSender.USER, text = text)
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg,
                inputText = "",
                isTyping = true,
                showSuggestions = false
            )
        }

        // Match free-text to a QueryType or give a generic response
        viewModelScope.launch {
            delay(900) // Simulate thinking
            val queryType = matchQueryType(text)
            if (queryType != null) {
                processQuery(queryType)
            } else {
                processGenericQuestion(text)
            }
        }
    }

    fun selectSuggestedPrompt(prompt: SuggestedPrompt) {
        val userMsg = ChatMessage(sender = MessageSender.USER, text = prompt.label)
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg,
                isTyping = true,
                showSuggestions = false
            )
        }
        viewModelScope.launch {
            delay(900)
            processQuery(prompt.queryType)
        }
    }

    private suspend fun processQuery(queryType: QueryType) {
        try {
            val lastPeriodStr = userPreferences.lastPeriodStart.first()
            val cycleLength = userPreferences.cycleLength.first()
            val lastPeriodDate = runCatching {
                LocalDate.parse(lastPeriodStr)
            }.getOrNull()

            val input = HealthQueryInput(
                queryType = queryType,
                lastPeriodDate = lastPeriodDate,
                cycleLength = cycleLength,
                periodLength = userPreferences.periodLength.first()
            )
            val result = healthQueryEngine.assess(input)

            val responseText = buildString {
                append("**${result.headline}**\n\n")
                append(result.explanation)
            }

            val saathiMsg = ChatMessage(
                sender = MessageSender.SAATHI,
                text = responseText,
                riskLevel = result.riskLevel,
                actionItems = result.actionItems
            )
            _uiState.update { state ->
                state.copy(messages = state.messages + saathiMsg, isTyping = false)
            }
        } catch (e: Exception) {
            showErrorResponse()
        }
    }

    private fun processGenericQuestion(text: String) {
        val lowerText = text.lowercase()
        val response = when {
            lowerText.contains("cramp") || lowerText.contains("pain") ->
                "Cramps during your period are caused by prostaglandins that help the uterus shed its lining. \uD83C\uDF38 " +
                        "Most cramps peak on day 1-2 and ease by day 3. Heat therapy, gentle yoga, and staying hydrated can help a lot! " +
                        "If cramps are severe enough to interrupt daily life, it's worth talking to a doctor."

            lowerText.contains("mood") || lowerText.contains("emotion") || lowerText.contains("sad") || lowerText.contains("anxious") ->
                "Mood changes during your cycle are real and valid! \uD83D\uDC9C " +
                        "In the luteal phase, progesterone rises and estrogen falls, which can affect serotonin levels — " +
                        "making you feel more sensitive or anxious. Tracking your moods can reveal patterns and help you plan."

            lowerText.contains("ovulation") || lowerText.contains("ovulate") ->
                "Ovulation typically occurs around day 14 of a 28-day cycle, but this varies! \uD83E\uDDB6 " +
                        "Signs of ovulation include a slight increase in basal body temperature, clearer discharge (egg-white consistency), " +
                        "and sometimes mild one-sided cramping called 'mittelschmerz'. Your fertile window is 5 days before + the ovulation day."

            lowerText.contains("bloat") || lowerText.contains("swelling") ->
                "Bloating is super common in the luteal phase! \uD83C\uDF38 " +
                        "Rising progesterone slows digestion and causes water retention. Tips: reduce sodium intake, " +
                        "stay hydrated (yes, more water helps!), eat smaller meals, and do light exercise like walking or yoga."

            lowerText.contains("discharge") ->
                "Vaginal discharge changes throughout your cycle: \uD83D\uDCA7\n" +
                        "• Period: red/brown (shedding lining)\n• Follicular: minimal, watery\n" +
                        "• Ovulation: clear, stretchy (egg-white texture)\n• Luteal: thicker, white/creamy\n" +
                        "Changes in colour, smell, or unusual itching should be checked by a doctor."

            lowerText.contains("food") || lowerText.contains("eat") || lowerText.contains("diet") ->
                "Cycle-syncing your nutrition can make a big difference! \uD83E\uDD66\n" +
                        "• Period: iron-rich foods (leafy greens, legumes), anti-inflammatory (ginger, turmeric)\n" +
                        "• Follicular: light proteins, fermented foods for gut health\n" +
                        "• Ovulatory: raw veggies, fibre to support estrogen clearance\n" +
                        "• Luteal: complex carbs, magnesium (dark chocolate!), vitamin B6"

            lowerText.contains("exercise") || lowerText.contains("workout") ->
                "Exercise actually helps with period symptoms! \uD83E\uDDD8\n" +
                        "• Period: gentle yoga, walking, stretching\n• Follicular: high intensity works great (energy is rising!)\n" +
                        "• Ovulatory: peak strength — great time for HIIT or heavy lifting\n" +
                        "• Luteal: moderate cardio, pilates, avoid overtraining as fatigue increases"

            lowerText.contains("sleep") ->
                "Sleep quality often dips in the luteal phase due to rising body temperature and progesterone. \uD83C\uDF19 " +
                        "Tips: keep your bedroom cool, avoid screens 1 hour before bed, try magnesium glycinate, " +
                        "and gentle stretching before sleep. Tracking your sleep alongside your cycle reveals useful patterns!"

            else ->
                "Great question! \uD83C\uDF38 I'm designed to help with cycle-related health questions like cramps, mood changes, " +
                        "cycle timing shifts, acne, ovulation, and more. Try asking something specific about your cycle, " +
                        "or tap one of the suggested questions below. \n\n" +
                        "For complex medical concerns, please consult a healthcare provider."
        }

        val saathiMsg = ChatMessage(
            sender = MessageSender.SAATHI,
            text = response
        )
        _uiState.update { state ->
            state.copy(messages = state.messages + saathiMsg, isTyping = false, showSuggestions = true)
        }
    }

    private fun showErrorResponse() {
        val errMsg = ChatMessage(
            sender = MessageSender.SAATHI,
            text = "I ran into an issue retrieving your information. Please make sure your cycle data is set up in Settings, then try again. \uD83C\uDF38"
        )
        _uiState.update { state ->
            state.copy(messages = state.messages + errMsg, isTyping = false)
        }
    }

    private fun matchQueryType(text: String): QueryType? {
        val lower = text.lowercase()
        return when {
            lower.contains("pregnant") || lower.contains("pregnancy") -> QueryType.PREGNANCY_RISK
            (lower.contains("delayed") || lower.contains("delay") || lower.contains("longer") || lower.contains("taking time")) &&
                (lower.contains("period") || lower.contains("cycle")) -> QueryType.CYCLE_DELAY
            lower.contains("cramp") && lower.contains("normal") -> QueryType.CRAMPS_NORMAL
            lower.contains("acne") || lower.contains("break") || lower.contains("pimple") -> QueryType.ACNE_REASONS
            else -> null
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
