package com.deepak.periodsaathi.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pure, offline-first, fully testable cycle prediction engine.
 *
 * Design goals:
 *  - No Android dependencies (pure Kotlin / java.time)
 *  - Weighted average: most recent cycles have higher weight
 *  - Fertile window: days 10–17 (adjustable by cycle length)
 *  - Ovulation estimate: cycle_length - 14 (± 2 days)
 *  - PMS window: ovulation_day + 3 → period_start - 1
 *  - Handles irregular cycles (±5 days) gracefully
 *  - Edge cases: no data, single cycle, very short/long cycles
 */
@Singleton
class CyclePredictionEngine @Inject constructor() {

    companion object {
        const val MIN_CYCLE_LENGTH = 21
        const val MAX_CYCLE_LENGTH = 45
        const val DEFAULT_CYCLE_LENGTH = 28
        const val DEFAULT_PERIOD_LENGTH = 5
        /** How many recent cycles to include in the weighted average */
        const val MAX_CYCLES_FOR_PREDICTION = 6
    }

    data class CyclePrediction(
        /** Predicted start date of the next period */
        val nextPeriodStart: LocalDate,
        /** Predicted end date of the next period */
        val nextPeriodEnd: LocalDate,
        /** Start of the fertile window (inclusive) */
        val fertileWindowStart: LocalDate,
        /** End of the fertile window (inclusive) */
        val fertileWindowEnd: LocalDate,
        /** Most likely ovulation day */
        val ovulationDay: LocalDate,
        /** Day PMS symptoms are expected to start */
        val pmsStart: LocalDate,
        /** The predicted cycle length used (weighted average) */
        val predictedCycleLength: Int,
        /** Number of days late/early (positive = late, negative = early) */
        val daysFromExpected: Int,
        /** Confidence level 0-100 based on data quality */
        val confidencePercent: Int,
        /** True if current cycle is longer than average (irregular flag) */
        val isIrregular: Boolean
    )

    data class CurrentCycleStatus(
        /** Day number within the current cycle (1-indexed) */
        val dayOfCycle: Int,
        /** Days until the next expected period */
        val daysUntilNextPeriod: Int,
        /** The current phase */
        val currentPhase: PredictedPhase
    )

    enum class PredictedPhase(val displayName: String, val emoji: String) {
        MENSTRUAL("Period", "\uD83E\uDE78"),
        FOLLICULAR("Follicular", "\uD83C\uDF31"),
        FERTILE("Fertile", "\uD83C\uDF3A"),
        OVULATION("Ovulation", "\uD83E\uDDB6"),
        LUTEAL("Luteal", "\uD83C\uDF19"),
        PMS("PMS", "\uD83D\uDE14"),
        UNKNOWN("Tracking", "\uD83D\uDCCA")
    }

    /**
     * Predict the next cycle given a list of past period start dates (most recent first).
     *
     * @param pastPeriodStarts      List of past period start dates. Most recent FIRST.
     * @param lastPeriodStartDate   The most recent confirmed period start. May match [pastPeriodStarts][0].
     * @param avgCycleLength        User-provided default (used when there's insufficient data)
     * @param avgPeriodLength       User-provided period duration in days
     */
    fun predictNextCycle(
        pastPeriodStarts: List<LocalDate>,
        lastPeriodStartDate: LocalDate,
        avgCycleLength: Int = DEFAULT_CYCLE_LENGTH,
        avgPeriodLength: Int = DEFAULT_PERIOD_LENGTH
    ): CyclePrediction {
        val predictedLength = weightedAverageCycleLength(pastPeriodStarts, avgCycleLength)
        val safeLength = predictedLength.coerceIn(MIN_CYCLE_LENGTH, MAX_CYCLE_LENGTH)
        val safePeriodLen = avgPeriodLength.coerceIn(2, 10)

        val nextStart = lastPeriodStartDate.plusDays(safeLength.toLong())
        val nextEnd = nextStart.plusDays(safePeriodLen.toLong() - 1)

        // Ovulation: cycle_length - 14 days from period start (luteal phase is ~14 days fixed)
        val ovulationDayFromStart = (safeLength - 14).coerceAtLeast(10)
        val ovulation = lastPeriodStartDate.plusDays(ovulationDayFromStart.toLong())

        // Fertile window: 5 days before ovulation + ovulation day + 1 day after
        val fertileStart = ovulation.minusDays(5)
        val fertileEnd = ovulation.plusDays(1)

        // PMS: ovulation + 3 days
        val pmsStart = ovulation.plusDays(3)

        // Confidence based on data points
        val dataPoints = pastPeriodStarts.size
        val confidence = when {
            dataPoints >= 6 -> 90
            dataPoints >= 3 -> 75
            dataPoints >= 1 -> 55
            else -> 30
        }

        // Irregular if last cycle deviated from average by more than 5 days
        val isIrregular = if (pastPeriodStarts.size >= 2) {
            val lastActualLength = ChronoUnit.DAYS.between(
                pastPeriodStarts[1], pastPeriodStarts[0]
            ).toInt()
            kotlin.math.abs(lastActualLength - safeLength) > 5
        } else false

        val today = LocalDate.now()
        val daysFromExpected = ChronoUnit.DAYS.between(nextStart, today).toInt()

        return CyclePrediction(
            nextPeriodStart = nextStart,
            nextPeriodEnd = nextEnd,
            fertileWindowStart = fertileStart,
            fertileWindowEnd = fertileEnd,
            ovulationDay = ovulation,
            pmsStart = pmsStart,
            predictedCycleLength = safeLength,
            daysFromExpected = daysFromExpected,
            confidencePercent = confidence,
            isIrregular = isIrregular
        )
    }

    /**
     * Get the current day-of-cycle and phase based on the last period start.
     */
    fun getCurrentStatus(
        lastPeriodStartDate: LocalDate,
        cycleLength: Int = DEFAULT_CYCLE_LENGTH,
        periodLength: Int = DEFAULT_PERIOD_LENGTH,
        pastPeriodStarts: List<LocalDate> = emptyList()
    ): CurrentCycleStatus {
        val today = LocalDate.now()
        val dayOfCycle = (ChronoUnit.DAYS.between(lastPeriodStartDate, today) + 1)
            .toInt().coerceAtLeast(1)
        val safeLength = cycleLength.coerceIn(MIN_CYCLE_LENGTH, MAX_CYCLE_LENGTH)
        val daysLeft = safeLength - dayOfCycle

        val prediction = predictNextCycle(pastPeriodStarts, lastPeriodStartDate, safeLength, periodLength)
        val phase = determinePhase(today, lastPeriodStartDate, prediction, periodLength)

        return CurrentCycleStatus(
            dayOfCycle = dayOfCycle,
            daysUntilNextPeriod = daysLeft.coerceAtLeast(0),
            currentPhase = phase
        )
    }

    /**
     * Determine which phase of the cycle today falls in.
     */
    fun determinePhase(
        today: LocalDate,
        lastPeriodStart: LocalDate,
        prediction: CyclePrediction,
        periodLength: Int = DEFAULT_PERIOD_LENGTH
    ): PredictedPhase {
        val periodEnd = lastPeriodStart.plusDays(periodLength.toLong() - 1)
        return when {
            !today.isBefore(lastPeriodStart) && !today.isAfter(periodEnd) -> PredictedPhase.MENSTRUAL
            !today.isBefore(prediction.fertileWindowStart) && !today.isAfter(prediction.fertileWindowEnd) -> PredictedPhase.FERTILE
            today == prediction.ovulationDay -> PredictedPhase.OVULATION
            !today.isBefore(prediction.pmsStart) && today.isBefore(prediction.nextPeriodStart) -> PredictedPhase.PMS
            today.isBefore(prediction.fertileWindowStart) -> PredictedPhase.FOLLICULAR
            else -> PredictedPhase.LUTEAL
        }
    }

    /**
     * Compute a weighted average cycle length.
     *
     * Weight scheme (exponential decay, most-recent cycles weighted heaviest):
     *  - 1st (most recent): weight 6
     *  - 2nd: weight 4
     *  - 3rd: weight 3
     *  - 4th: weight 2
     *  - 5th+: weight 1
     */
    fun weightedAverageCycleLength(
        pastPeriodStarts: List<LocalDate>,
        fallbackLength: Int = DEFAULT_CYCLE_LENGTH
    ): Int {
        if (pastPeriodStarts.size < 2) return fallbackLength

        // Sort descending (most recent first) and take up to MAX_CYCLES_FOR_PREDICTION
        val sorted = pastPeriodStarts.sortedDescending().take(MAX_CYCLES_FOR_PREDICTION + 1)

        val weights = listOf(6, 4, 3, 2, 1, 1, 1) // Index 0 = most recent interval
        var weightedSum = 0.0
        var totalWeight = 0.0

        for (i in 0 until sorted.size - 1) {
            val intervalDays = ChronoUnit.DAYS.between(sorted[i + 1], sorted[i]).toInt()
            // Filter out obviously invalid intervals
            if (intervalDays < MIN_CYCLE_LENGTH || intervalDays > MAX_CYCLE_LENGTH) continue
            val w = weights.getOrElse(i) { 1 }.toDouble()
            weightedSum += intervalDays * w
            totalWeight += w
        }

        return if (totalWeight == 0.0) fallbackLength
        else (weightedSum / totalWeight).toInt().coerceIn(MIN_CYCLE_LENGTH, MAX_CYCLE_LENGTH)
    }

    /**
     * Returns the human-readable description of days until next period.
     */
    fun daysUntilPeriodLabel(daysLeft: Int): String = when {
        daysLeft <= 0 -> "Your period may start today"
        daysLeft == 1 -> "Period expected tomorrow"
        daysLeft <= 3 -> "Period in $daysLeft days"
        else -> "$daysLeft days until your period"
    }
}
