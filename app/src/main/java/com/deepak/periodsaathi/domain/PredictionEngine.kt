package com.deepak.periodsaathi.domain

import com.deepak.periodsaathi.data.model.CycleEntry
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

data class PredictionResult(
    val nextPeriodDate: LocalDate,
    val confidencePercent: Int,
    val cycleLengthUsed: Int,
    val fertileWindowStart: LocalDate,
    val fertileWindowEnd: LocalDate,
    val ovulationDate: LocalDate,
    val patterns: List<CyclePattern>
)

data class CyclePattern(
    val title: String,
    val description: String,
    val emoji: String
)

@Singleton
class PredictionEngine @Inject constructor() {

    /** Convert epoch-millis Long → LocalDate */
    private fun Long.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

    fun predict(entries: List<CycleEntry>, defaultCycleLength: Int = 28): PredictionResult {
        // Period days = entries where flowIntensity is non-null and non-"none"
        val periodStarts = entries
            .filter { it.flowIntensity != null && it.flowIntensity != "none" }
            .map { it.date.toLocalDate() }
            .sortedDescending()

        val observedLengths = mutableListOf<Long>()
        for (i in 0 until minOf(periodStarts.size - 1, 5)) {
            val diff = ChronoUnit.DAYS.between(periodStarts[i + 1], periodStarts[i])
            if (diff in 18..45) observedLengths.add(diff)
        }

        val cycleLength = when {
            observedLengths.size >= 3 -> observedLengths.average().toInt()
            observedLengths.size >= 1 -> ((observedLengths.average() + defaultCycleLength) / 2).toInt()
            else -> defaultCycleLength
        }

        val confidence = when {
            observedLengths.size >= 5 && cycleVariance(observedLengths) < 3.0 -> 90
            observedLengths.size >= 3 && cycleVariance(observedLengths) < 5.0 -> 78
            observedLengths.size >= 2 -> 62
            observedLengths.size == 1 -> 45
            else -> 30
        }

        val lastPeriodStart = periodStarts.firstOrNull()
            ?: LocalDate.now().minusDays(defaultCycleLength.toLong())

        val nextPeriod = lastPeriodStart.plusDays(cycleLength.toLong())
        val ovulation = lastPeriodStart.plusDays((cycleLength - 14).toLong())
        val fertileStart = ovulation.minusDays(5)
        val fertileEnd = ovulation.plusDays(1)

        return PredictionResult(
            nextPeriodDate = nextPeriod,
            confidencePercent = confidence,
            cycleLengthUsed = cycleLength,
            fertileWindowStart = fertileStart,
            fertileWindowEnd = fertileEnd,
            ovulationDate = ovulation,
            patterns = detectPatterns(entries)
        )
    }

    private fun cycleVariance(lengths: List<Long>): Double {
        if (lengths.isEmpty()) return 100.0
        val mean = lengths.average()
        return sqrt(lengths.map { (it - mean) * (it - mean) }.average())
    }

    private fun detectPatterns(entries: List<CycleEntry>): List<CyclePattern> {
        val patterns = mutableListOf<CyclePattern>()

        // Low water + cramp days
        val lowWaterHighCramp = entries.count { e ->
            (e.waterGlasses) < 4 && e.symptoms.contains("cramp", ignoreCase = true)
        }
        if (lowWaterHighCramp > 2) {
            patterns.add(CyclePattern(
                title = "Hydration and Cramps",
                description = "On days with less than 4 glasses of water, you logged cramps more often. Staying hydrated reduces prostaglandin concentration.",
                emoji = "Water"
            ))
        }

        // Bad mood days (using notes as proxy since no sleepHours field)
        val badMoodDays = entries.count { e ->
            e.mood in listOf("anxious", "irritable", "sad", "angry")
        }
        if (badMoodDays > 3) {
            patterns.add(CyclePattern(
                title = "Mood Fluctuations",
                description = "You have logged negative moods on $badMoodDays days. Hormonal shifts in estrogen and progesterone directly impact serotonin levels.",
                emoji = "Mood"
            ))
        }

        // Bloating pattern
        val bloatingDays = entries.count { it.symptoms.contains("bloat", ignoreCase = true) }
        if (bloatingDays > 3) {
            patterns.add(CyclePattern(
                title = "Bloating Pattern",
                description = "You have logged bloating on $bloatingDays days. This is typically linked to progesterone-driven fluid retention in the luteal phase.",
                emoji = "Leaf"
            ))
        }

        // PMS mood dip
        val moodDips = entries.count { e ->
            e.mood in listOf("sad", "irritable", "anxious") &&
                e.symptoms.contains("pms", ignoreCase = true)
        }
        if (moodDips > 2) {
            patterns.add(CyclePattern(
                title = "Pre-Period Mood Dip",
                description = "You tend to notice mood changes in the days before your period. This is PMS or PMDD — serotonin drops as progesterone rises.",
                emoji = "Moon"
            ))
        }

        // Rest days pattern
        val restDays = entries.count { it.isRestDay }
        if (restDays > 4) {
            patterns.add(CyclePattern(
                title = "Rest Cycle",
                description = "You marked $restDays days as rest days. Adequate rest during menstruation reduces cortisol and helps regulate future cycles.",
                emoji = "Rest"
            ))
        }

        return patterns.take(3)
    }
}
