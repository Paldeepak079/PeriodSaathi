package com.deepak.periodsaathi.domain

import com.deepak.periodsaathi.BuildConfig
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

data class ChatContext(
    val userName: String = "",
    val currentCycleDay: Int = 0,
    val currentPhase: String = "",
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val lastPeriodDate: String = "",
    val nextPeriodDate: String = "",
    val daysUntilNextPeriod: Int = 0,
    val predictionConfidence: Float = 0f,
    val recentSymptoms: List<String> = emptyList(),
    val recentMoods: List<String> = emptyList(),
    val recentFlowLevels: List<String> = emptyList(),
    val recentNotes: List<String> = emptyList(),
    val birthControl: String = "none",
    val recentCycleLengths: List<Int> = emptyList()
)

@Singleton
class GeminiService @Inject constructor(
    private val cycleRepository: CycleRepository
) {
    private var generativeModel: GenerativeModel? = null

    private fun getModel(): GenerativeModel {
        if (generativeModel == null) {
            generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = BuildConfig.GEMINI_API_KEY,
                systemInstruction = content { text(SYSTEM_PROMPT) }
            )
        }
        return generativeModel!!
    }

    suspend fun buildChatContext(): ChatContext {
        val settings = cycleRepository.getSettings().first()
        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()

        val lastPeriodDate = settings.lastPeriodStartDate?.let {
            Instant.ofEpochMilli(it).atZone(zone).toLocalDate()
        }

        val currentCycleDay = lastPeriodDate?.let {
            ChronoUnit.DAYS.between(it, today).toInt() + 1
        } ?: 1

        val cycleLength = settings.averageCycleLength
        val periodLength = settings.averagePeriodLength

        val currentPhase = when {
            currentCycleDay <= periodLength -> "Menstrual"
            currentCycleDay <= (cycleLength / 2) - 2 -> "Follicular"
            currentCycleDay <= (cycleLength / 2) + 1 -> "Ovulatory"
            currentCycleDay > cycleLength - 5 -> "PMS/Late Luteal"
            else -> "Luteal"
        }

        val nextPeriodDate = lastPeriodDate?.plusDays(cycleLength.toLong())
        val daysUntilNextPeriod = nextPeriodDate?.let {
            ChronoUnit.DAYS.between(today, it).toInt().coerceAtLeast(0)
        } ?: cycleLength

        val prediction = cycleRepository.predictNextPeriod().first()

        // Gather recent entries (last 7 days)
        val recentEntries = mutableListOf<String>()
        val recentSymptoms = mutableListOf<String>()
        val recentMoods = mutableListOf<String>()
        val recentFlowLevels = mutableListOf<String>()
        val recentNotes = mutableListOf<String>()

        for (i in 0..6) {
            val date = today.minusDays(i.toLong())
            val epoch = date.atStartOfDay(zone).toInstant().toEpochMilli()
            val entry = cycleRepository.getEntryByDate(epoch)
            if (entry != null) {
                if (entry.symptoms != "[]") {
                    recentSymptoms.addAll(
                        entry.symptoms.removeSurrounding("[", "]")
                            .split(",").map { it.trim().removeSurrounding("\"") }
                            .filter { it.isNotBlank() }
                    )
                }
                if (entry.mood != null) recentMoods.add(entry.mood!!)
                if (entry.flowIntensity != null) recentFlowLevels.add(entry.flowIntensity!!)
                if (entry.notes.isNullOrBlank().not()) recentNotes.add(entry.notes!!)
            }
        }

        // Get last 3 cycle lengths for pattern analysis
        val lastCycles = cycleRepository.getLastNCycles(4).first()
        val recentCycleLengths = lastCycles.map { it.size }

        return ChatContext(
            userName = settings.userName.ifBlank { "there" },
            currentCycleDay = currentCycleDay,
            currentPhase = currentPhase,
            cycleLength = cycleLength,
            periodLength = periodLength,
            lastPeriodDate = lastPeriodDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "not set",
            nextPeriodDate = nextPeriodDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "calculating",
            daysUntilNextPeriod = daysUntilNextPeriod,
            predictionConfidence = prediction.confidence,
            recentSymptoms = recentSymptoms.distinct(),
            recentMoods = recentMoods.distinct(),
            recentFlowLevels = recentFlowLevels,
            recentNotes = recentNotes.take(3),
            birthControl = settings.contraceptionMode,
            recentCycleLengths = recentCycleLengths
        )
    }

    suspend fun chat(
        userMessage: String,
        conversationHistory: List<Pair<String, String>>
    ): String {
        val context = buildChatContext()
        val model = getModel()

        val history = mutableListOf<com.google.ai.client.generativeai.type.Content>()

        // Add conversation history
        for ((role, text) in conversationHistory) {
            val roleStr = if (role == "user") "user" else "model"
            history.add(content(role = roleStr) { text(text) })
        }

        val chat = model.startChat(history = history)
        val response = chat.sendMessage(buildPrompt(userMessage, context))
        return response.text ?: "I couldn't generate a response. Please try again."
    }

    private fun buildPrompt(userMessage: String, ctx: ChatContext): String {
        return """
USER'S CURRENT CYCLE DATA:
- Name: ${ctx.userName}
- Current cycle day: ${ctx.currentCycleDay} of ${ctx.cycleLength}
- Current phase: ${ctx.currentPhase}
- Period length: ${ctx.periodLength} days
- Last period started: ${ctx.lastPeriodDate}
- Next period expected: ${ctx.nextPeriodDate} (in ${ctx.daysUntilNextPeriod} days)
- Prediction confidence: ${(ctx.predictionConfidence * 100).toInt()}%
- Birth control: ${ctx.birthControl}
${if (ctx.recentCycleLengths.isNotEmpty()) "- Recent cycle lengths: ${ctx.recentCycleLengths.joinToString(", ")} days" else ""}
${if (ctx.recentSymptoms.isNotEmpty()) "- Recent symptoms (last 7 days): ${ctx.recentSymptoms.joinToString(", ")}" else ""}
${if (ctx.recentMoods.isNotEmpty()) "- Recent moods (last 7 days): ${ctx.recentMoods.joinToString(", ")}" else ""}
${if (ctx.recentFlowLevels.isNotEmpty()) "- Recent flow levels: ${ctx.recentFlowLevels.joinToString(", ")}" else ""}
${if (ctx.recentNotes.isNotEmpty()) "- Recent notes: ${ctx.recentNotes.joinToString(" | ")}" else ""}

USER'S QUESTION: $userMessage

Respond using the user's actual data above. Be specific and personal. Reference their actual cycle day, phase, symptoms, and predictions. Keep it concise and conversational.
        """.trimIndent()
    }

    companion object {
        private const val SYSTEM_PROMPT = """You are Saathi, a warm, knowledgeable, and empathetic period and menstrual health companion inside the Period Saathi app.

CORE RULES:
- ONLY answer questions about: periods, menstrual cycles, symptoms, PMS, cramps, mood changes, flow, ovulation, cycle phases, fertility, pregnancy risk, diet/nutrition for periods, exercise for periods, sleep during cycles, wellness tips related to menstruation, hormonal health, birth control effects on cycles, and general reproductive health education.
- ALWAYS use the user's real cycle data provided in the prompt. Reference their actual cycle day, phase, symptoms, and predictions.
- NEVER fabricate cycle dates, symptoms, or medical history. If data is missing, say so clearly.
- NEVER diagnose medical conditions. Provide general educational information and recommend consulting a healthcare professional when appropriate.
- For out-of-scope questions (not related to periods/wellness), politely say you're focused on period and cycle help and redirect.
- Keep responses natural, concise (2-4 short paragraphs max), friendly, and personalized.
- Use simple language, avoid excessive medical jargon.
- Include a brief medical disclaimer when giving health-related advice.
- Use emojis sparingly (1-2 per response max).
- If a question seems urgent or describes severe symptoms, always recommend seeing a doctor.
- Remember context from the conversation to handle follow-up questions well.
- Do NOT start responses with "Great question!" or generic filler. Jump straight to the answer.
- Do NOT repeat the same intro phrase across responses. Be varied and natural."""
    }
}
