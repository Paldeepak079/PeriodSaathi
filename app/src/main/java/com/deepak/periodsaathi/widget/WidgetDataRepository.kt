package com.deepak.periodsaathi.widget

import android.content.Context
import com.deepak.periodsaathi.data.database.PeriodSaathiDatabase
import com.deepak.periodsaathi.domain.model.CyclePhase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class WidgetDataRepository(private val context: Context) {

    suspend fun getCycleWidgetState(): CycleWidgetState = withContext(Dispatchers.IO) {
        try {
            val db = PeriodSaathiDatabase.getInstance(context)
            val today = LocalDate.now()
            val todayEpoch = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val entry = db.cycleDao().getEntryByDate(todayEpoch).first()
            val settings = db.settingsDao().getSettings().first()

            val cycleDay = calculateCycleDay(settings)
            val phase = calculatePhase(settings)

            CycleWidgetState(
                cycleDay = cycleDay,
                phaseName = phase.displayName,
                phaseEmoji = phase.emoji,
                waterCount = entry?.waterGlasses ?: 0,
                totalWater = 8,
                isLoading = false
            )
        } catch (e: Exception) {
            CycleWidgetState(
                cycleDay = 1,
                phaseName = "Tracking",
                phaseEmoji = "📊",
                waterCount = 0,
                totalWater = 8,
                isLoading = false,
                isError = true
            )
        }
    }

    suspend fun getCountdownState(): CountdownState = withContext(Dispatchers.IO) {
        try {
            val db = PeriodSaathiDatabase.getInstance(context)
            val settings = db.settingsDao().getSettings().first()
            val today = LocalDate.now()

            val periodEntries = db.cycleDao().getPeriodEntries().first()
            val cycleStartDates = periodEntries
                .filter { it.flowIntensity != null }
                .sortedBy { it.date }
            val cyclesLogged = cycleStartDates.size

            val lastEntry = cycleStartDates.lastOrNull()
            val avgCycleLength = settings?.averageCycleLength ?: 28

            val daysUntilPeriod: Int? = if (lastEntry != null) {
                val lastStartDate = java.time.Instant.ofEpochMilli(lastEntry.date)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                val daysSince = java.time.temporal.ChronoUnit.DAYS.between(lastStartDate, today).toInt()
                val daysRemaining = avgCycleLength - (daysSince % avgCycleLength)
                daysRemaining.coerceIn(0, avgCycleLength)
            } else null

            val isInPeriod = if (lastEntry != null && settings != null) {
                val lastStartDate = java.time.Instant.ofEpochMilli(lastEntry.date)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                val daysSince = java.time.temporal.ChronoUnit.DAYS.between(lastStartDate, today).toInt()
                daysSince >= 0 && daysSince < (settings.averagePeriodLength)
            } else false

            CountdownState(
                daysUntilPeriod = if (!isInPeriod) daysUntilPeriod else 0,
                isInPeriod = isInPeriod,
                cyclesLogged = cyclesLogged
            )
        } catch (e: Exception) {
            CountdownState(
                daysUntilPeriod = null,
                isInPeriod = false,
                cyclesLogged = 0,
                isError = true
            )
        }
    }

    private fun calculateCycleDay(settings: com.deepak.periodsaathi.data.model.CycleSettings?): Int {
        return settings?.let {
            val lastStart = it.lastPeriodStartDate
            if (lastStart != null) {
                val startDate = java.time.Instant.ofEpochMilli(lastStart)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                val daysSince = java.time.temporal.ChronoUnit.DAYS.between(startDate, LocalDate.now()).toInt() + 1
                daysSince.coerceIn(1, it.averageCycleLength)
            } else 1
        } ?: 1
    }

    private fun calculatePhase(settings: com.deepak.periodsaathi.data.model.CycleSettings?): CyclePhase {
        val cycleDay = calculateCycleDay(settings)
        val cycleLength = settings?.averageCycleLength ?: 28
        val periodLength = settings?.averagePeriodLength ?: 5

        return when {
            cycleDay <= periodLength -> CyclePhase.MENSTRUAL
            cycleDay <= (cycleLength / 2) - 2 -> CyclePhase.FOLLICULAR
            cycleDay <= (cycleLength / 2) + 1 -> CyclePhase.OVULATORY
            else -> CyclePhase.LUTEAL
        }
    }
}

data class CycleWidgetState(
    val cycleDay: Int,
    val phaseName: String,
    val phaseEmoji: String,
    val waterCount: Int,
    val totalWater: Int,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

data class CountdownState(
    val daysUntilPeriod: Int?,
    val isInPeriod: Boolean,
    val cyclesLogged: Int,
    val isError: Boolean = false
)
