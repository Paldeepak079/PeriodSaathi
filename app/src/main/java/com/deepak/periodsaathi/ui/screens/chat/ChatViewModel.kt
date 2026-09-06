package com.deepak.periodsaathi.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.datastore.UserPreferences
import com.deepak.periodsaathi.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val geminiService: GeminiService,
    private val healthQueryEngine: HealthQueryEngine,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Conversation history for Gemini context
    private val conversationHistory = mutableListOf<Pair<String, String>>()

    val suggestedPrompts = listOf(
        SuggestedPrompt("When is my next period?", "\uD83D\uDCC5", QueryType.CYCLE_DELAY),
        SuggestedPrompt("Why am I so moody today?", "\uD83D\uDE22", QueryType.ACNE_REASONS),
        SuggestedPrompt("Are my cramps normal?", "\uD83D\uDE14", QueryType.CRAMPS_NORMAL),
        SuggestedPrompt("What should I eat on my period?", "\uD83E\uDD66", QueryType.ACNE_REASONS),
        SuggestedPrompt("Am I in my fertile window?", "\uD83E\uDDB6", QueryType.PREGNANCY_RISK),
        SuggestedPrompt("Why is my period late?", "\u23F3", QueryType.CYCLE_DELAY),
        SuggestedPrompt("Help with PMS symptoms", "\uD83C\uDF38", QueryType.CRAMPS_NORMAL),
        SuggestedPrompt("What exercises are safe during periods?", "\uD83C\uDFC3", QueryType.ACNE_REASONS)
    )

    init {
        viewModelScope.launch {
            val name = userPreferences.userName.first().ifBlank { "there" }
            val cycleDay = try { geminiService.buildChatContext().currentCycleDay } catch (_: Exception) { 0 }
            val phase = try { geminiService.buildChatContext().currentPhase } catch (_: Exception) { "" }

            val greeting = if (cycleDay > 0 && phase.isNotBlank()) {
                "Hey $name! \uD83C\uDF38 You're on day $cycleDay of your cycle ($phase phase). I'm Saathi — ask me anything about your cycle, symptoms, or wellness!"
            } else {
                "Hey $name! \uD83C\uDF38 I'm Saathi, your personal cycle wellness guide. Ask me about cramps, mood, cycle timing, diet, or any period-related question!"
            }

            _uiState.update { it.copy(
                messages = listOf(
                    ChatMessage(sender = MessageSender.SAATHI, text = greeting)
                )
            )}
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage(text: String = _uiState.value.inputText.trim()) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(sender = MessageSender.USER, text = text)
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg,
                inputText = "",
                isTyping = true,
                showSuggestions = false
            )
        }
        conversationHistory.add("user" to text)

        viewModelScope.launch {
            try {
                val response = geminiService.chat(text, conversationHistory)
                conversationHistory.add("model" to response)

                val saathiMsg = ChatMessage(
                    sender = MessageSender.SAATHI,
                    text = response
                )
                _uiState.update { state ->
                    state.copy(messages = state.messages + saathiMsg, isTyping = false)
                }
            } catch (e: Exception) {
                // Fallback to local engine for specific query types
                val queryType = matchQueryType(text)
                if (queryType != null) {
                    processQuery(queryType)
                } else {
                    showErrorResponse()
                }
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
        conversationHistory.add("user" to prompt.label)

        viewModelScope.launch {
            try {
                val response = geminiService.chat(prompt.label, conversationHistory)
                conversationHistory.add("model" to response)

                val saathiMsg = ChatMessage(
                    sender = MessageSender.SAATHI,
                    text = response
                )
                _uiState.update { state ->
                    state.copy(messages = state.messages + saathiMsg, isTyping = false)
                }
            } catch (e: Exception) {
                // Fallback to local engine
                processQuery(prompt.queryType)
            }
        }
    }

    private suspend fun processQuery(queryType: QueryType) {
        try {
            val lastPeriodStr = userPreferences.lastPeriodStart.first()
            val cycleLength = userPreferences.cycleLength.first()
            val lastPeriodDate = runCatching {
                java.time.LocalDate.parse(lastPeriodStr)
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
            conversationHistory.add("model" to responseText)
            _uiState.update { state ->
                state.copy(messages = state.messages + saathiMsg, isTyping = false)
            }
        } catch (e: Exception) {
            showErrorResponse()
        }
    }

    private fun showErrorResponse() {
        val errMsg = ChatMessage(
            sender = MessageSender.SAATHI,
            text = "I'm having trouble connecting right now. Please check your internet connection and try again. \uD83C\uDF38"
        )
        _uiState.update { state ->
            state.copy(messages = state.messages + errMsg, isTyping = false, showSuggestions = true)
        }
    }

    private fun matchQueryType(text: String): QueryType? {
        val lower = text.lowercase()
        return when {
            lower.contains("pregnant") || lower.contains("pregnancy") -> QueryType.PREGNANCY_RISK
            (lower.contains("delayed") || lower.contains("delay") || lower.contains("late") || lower.contains("longer") || lower.contains("taking time")) &&
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
