package com.example.periodsaathi.domain.usecase

import com.example.periodsaathi.data.model.CycleEntry
import com.example.periodsaathi.data.repository.CycleRepository
import com.example.periodsaathi.domain.model.CyclePhase
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class LogCycleEntryUseCase @Inject constructor(
    private val repository: CycleRepository
) {

    suspend operator fun invoke(entry: CycleEntry): Result<CycleEntry> {
        return try {
            validateEntry(entry)
            val enriched = autoDetectFields(entry)
            repository.logCycleEntry(enriched)
            awardPoints(entry)
            Result.success(enriched)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateEntry(entry: CycleEntry) {
        val entryDate = Instant.ofEpochMilli(entry.date)
            .atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now()

        if (entryDate.isAfter(today)) {
            throw IllegalArgumentException("Cannot log entries for future dates")
        }
    }

    private suspend fun autoDetectFields(entry: CycleEntry): CycleEntry {
        val today = LocalDate.now()
        val weekAgo = today.minusDays(7)

        val weekStart = weekAgo.atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
        val todayEnd = today.atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val recentEntries = repository.getMonthEntries(
            today.year, today.monthValue
        ).first().filter { e ->
            e.date in weekStart..todayEnd && e.flowIntensity != null
        }

        val yesterdayEpoch = today.minusDays(1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val yesterdayEntry = recentEntries.find {
            ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate(),
                today
            ) == 1L
        }
        val previousWasHeavy = yesterdayEntry?.flowIntensity?.let {
            flowLevelToInt(it) >= flowLevelToInt("HEAVY")
        } ?: false

        val autoRestDay = entry.flowIntensity?.let {
            flowLevelToInt(it) >= flowLevelToInt("HEAVY") && previousWasHeavy
        } ?: entry.isRestDay

        val phase = determinePhase(entry.date)

        return entry.copy(
            isRestDay = entry.isRestDay || autoRestDay,
            cyclePhase = phase.name
        )
    }

    private suspend fun awardPoints(entry: CycleEntry) {
        val settings = repository.getSettings().first()
        repository.updateSettings(
            settings.copy(
                totalPoints = settings.totalPoints + 2,
                streakCount = settings.streakCount + 1
            )
        )
    }

    private suspend fun determinePhase(dateEpoch: Long): CyclePhase {
        val date = Instant.ofEpochMilli(dateEpoch)
            .atZone(ZoneId.systemDefault()).toLocalDate()
        val settings = repository.getSettings().first()

        var lastStart = settings.lastPeriodStartDate?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        if (lastStart == null || date.isBefore(lastStart)) {
            val cycles = repository.getLastNCycles(3).first()
            val periodEntries = cycles.flatten()
                .filter { it.flowIntensity != null }
                .sortedByDescending { it.date }

            for (entry in periodEntries) {
                val entryDate = Instant.ofEpochMilli(entry.date)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                if (!entryDate.isAfter(date)) {
                    lastStart = entryDate
                    break
                }
            }
        }

        if (lastStart == null) return CyclePhase.MENSTRUAL

        val cycleDay = ChronoUnit.DAYS.between(lastStart, date).toInt().coerceIn(1, 40)
        val cycleLength = settings.averageCycleLength
        val periodLength = settings.averagePeriodLength

        return when {
            cycleDay <= periodLength -> CyclePhase.MENSTRUAL
            cycleDay <= cycleLength / 2 - 1 -> CyclePhase.FOLLICULAR
            cycleDay <= cycleLength / 2 + 1 -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }
    }

    private fun flowLevelToInt(level: String): Int = when (level.uppercase()) {
        "NONE" -> 0
        "SPOTTING" -> 1
        "LIGHT" -> 2
        "MODERATE" -> 3
        "HEAVY" -> 4
        "VERY_HEAVY" -> 5
        else -> 0
    }
}
