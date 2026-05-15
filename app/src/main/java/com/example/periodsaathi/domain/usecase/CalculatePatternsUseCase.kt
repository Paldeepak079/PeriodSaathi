package com.example.periodsaathi.domain.usecase

import com.example.periodsaathi.data.model.CycleEntry
import com.example.periodsaathi.data.repository.CycleRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class PatternInsight(
    val description: String,
    val confidence: Float,
    val type: PatternType
)

enum class PatternType {
    WATER_CORRELATION,
    SLEEP_CORRELATION,
    SYMPTOM_PATTERN,
    MOOD_PATTERN
}

class CalculatePatternsUseCase @Inject constructor(
    private val repository: CycleRepository
) {

    private val gson = Gson()

    suspend operator fun invoke(): List<PatternInsight> {
        val insights = mutableListOf<PatternInsight>()
        val cycles = repository.getLastNCycles(6).first()
        val allEntries = cycles.flatten()

        if (allEntries.size < 10) return insights

        insights.addAll(analyzeWaterCrampCorrelation(allEntries))
        insights.addAll(analyzeSleepMoodCorrelation(allEntries))
        insights.addAll(analyzeSymptomPatterns(cycles))

        return insights
    }

    private fun analyzeWaterCrampCorrelation(entries: List<CycleEntry>): List<PatternInsight> {
        val results = mutableListOf<PatternInsight>()

        val daysWithCramps = entries.filter { hasSymptom(it, "cramps") }
        val daysWithoutCramps = entries.filter { !hasSymptom(it, "cramps") }

        if (daysWithCramps.isEmpty() || daysWithoutCramps.isEmpty()) return results

        val avgWaterWithCramps = daysWithCramps.map { it.waterGlasses }.average()
        val avgWaterWithoutCramps = daysWithoutCramps.map { it.waterGlasses }.average()
        val diff = avgWaterWithoutCramps - avgWaterWithCramps

        if (diff > 1.5) {
            results.add(
                PatternInsight(
                    description = "On days with cramps, you drink %.0f fewer glasses of water on average. Staying hydrated may help reduce discomfort.".format(diff),
                    confidence = (entries.count { hasSymptom(it, "cramps") }.toFloat() / entries.size).coerceIn(0f, 1f),
                    type = PatternType.WATER_CORRELATION
                )
            )
        }

        return results
    }

    private fun analyzeSleepMoodCorrelation(entries: List<CycleEntry>): List<PatternInsight> {
        val results = mutableListOf<PatternInsight>()

        val fatigueDays = entries.filter { hasSymptom(it, "fatigue") || hasSymptom(it, "insomnia") }
        val normalDays = entries.filter { !hasSymptom(it, "fatigue") && !hasSymptom(it, "insomnia") }

        if (fatigueDays.isEmpty() || normalDays.isEmpty()) return results

        val positiveMoods = setOf("happy", "energetic", "great", "calm", "good")
        val fatigueMoodRatio = fatigueDays.count { it.mood != null && it.mood!!.lowercase() in positiveMoods }
            .toFloat() / fatigueDays.size.coerceAtLeast(1)
        val normalMoodRatio = normalDays.count { it.mood != null && it.mood!!.lowercase() in positiveMoods }
            .toFloat() / normalDays.size.coerceAtLeast(1)

        if (normalMoodRatio - fatigueMoodRatio > 0.2f) {
            results.add(
                PatternInsight(
                    description = "Sleep quality strongly affects your mood. On days with fatigue or insomnia, positive moods drop by %d%%.".format(((normalMoodRatio - fatigueMoodRatio) * 100).toInt()),
                    confidence = (fatigueDays.size.toFloat() / entries.size).coerceIn(0f, 1f),
                    type = PatternType.SLEEP_CORRELATION
                )
            )
        }

        return results
    }

    private fun analyzeSymptomPatterns(cycles: List<List<CycleEntry>>): List<PatternInsight> {
        val results = mutableListOf<PatternInsight>()

        if (cycles.isEmpty()) return results

        val symptomByCycleDay = mutableMapOf<Int, MutableMap<String, Int>>()

        for ((cycleIndex, cycle) in cycles.withIndex()) {
            val cycleStart = cycle.minOfOrNull { it.date } ?: continue

            for (entry in cycle) {
                val cycleDay = ChronoUnit.DAYS.between(
                    Instant.ofEpochMilli(cycleStart).atZone(ZoneId.systemDefault()).toLocalDate(),
                    Instant.ofEpochMilli(entry.date).atZone(ZoneId.systemDefault()).toLocalDate()
                ).toInt().coerceIn(0, 40)

                val symptoms = parseSymptoms(entry.symptoms)
                for (symptom in symptoms) {
                    symptomByCycleDay.getOrPut(cycleDay) { mutableMapOf() }
                        .merge(symptom.lowercase(), 1, Int::plus)
                }
            }
        }

        val topSymptoms = symptomByCycleDay.entries
            .sortedBy { it.key }
            .take(5)
            .filter { (_, symptomCounts) ->
                symptomCounts.values.maxOrNull() ?: 0 >= 2
            }

        for ((day, symptomCounts) in topSymptoms) {
            val mostFrequent = symptomCounts.maxByOrNull { it.value } ?: continue
            val phaseLabel = phaseForCycleDay(day)

            results.add(
                PatternInsight(
                    description = "During $phaseLabel (day ${day + 1}), you most frequently report \"${mostFrequent.key}\" (${mostFrequent.value}x across cycles).",
                    confidence = (mostFrequent.value.toFloat() / cycles.size).coerceIn(0f, 1f),
                    type = PatternType.SYMPTOM_PATTERN
                )
            )
        }

        return results
    }

    private fun phaseForCycleDay(day: Int): String = when {
        day <= 4 -> "Menstrual"
        day <= 12 -> "Follicular"
        day <= 15 -> "Ovulation"
        else -> "Luteal"
    }

    private fun hasSymptom(entry: CycleEntry, symptom: String): Boolean {
        return parseSymptoms(entry.symptoms).any { it.equals(symptom, ignoreCase = true) }
    }

    private fun parseSymptoms(json: String): List<String> {
        return try {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, listType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
