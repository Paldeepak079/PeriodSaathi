package com.example.periodsaathi.data.repository

import com.example.periodsaathi.data.dao.CycleDao
import com.example.periodsaathi.data.dao.SettingsDao
import com.example.periodsaathi.data.model.CycleEntry
import com.example.periodsaathi.data.model.CycleSettings
import com.example.periodsaathi.domain.model.CyclePhase
import com.example.periodsaathi.domain.model.PeriodPrediction
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

interface CycleRepository {
    suspend fun logCycleEntry(entry: CycleEntry)
    fun getMonthEntries(year: Int, month: Int): Flow<List<CycleEntry>>
    fun predictNextPeriod(): Flow<PeriodPrediction>
    fun getCurrentCycleDay(): Flow<Int>
    fun getCurrentPhase(): Flow<CyclePhase>
    fun getLastNCycles(n: Int): Flow<List<List<CycleEntry>>>
    fun getSettings(): Flow<CycleSettings>
    suspend fun updateSettings(settings: CycleSettings)
}

@Singleton
class CycleRepositoryImpl @Inject constructor(
    private val cycleDao: CycleDao,
    private val settingsDao: SettingsDao
) : CycleRepository {

    override suspend fun logCycleEntry(entry: CycleEntry) {
        cycleDao.insertEntry(entry)
    }

    override fun getMonthEntries(year: Int, month: Int): Flow<List<CycleEntry>> {
        val start = LocalDate.of(year, month, 1)
        val end = start.withDayOfMonth(start.lengthOfMonth())
        return cycleDao.getEntriesBetweenDates(
            start = start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            end = end.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    override fun predictNextPeriod(): Flow<PeriodPrediction> {
        return combine(
            settingsDao.getSettings(),
            cycleDao.getPeriodEntries()
        ) { settings, periodEntries ->
            val today = LocalDate.now()
            val periodStarts = detectCycleStarts(periodEntries)
            val lastPeriodStart = periodStarts.lastOrNull()
                ?: settings?.lastPeriodStartDate?.let { epochToDate(it) }

            if (lastPeriodStart == null) {
                return@combine PeriodPrediction(
                    expectedDate = today.plusDays(28),
                    daysUntil = 28,
                    accuracyMinutes = 2,
                    confidence = 0f
                )
            }

            val intervals = mutableListOf<Long>()
            for (i in 1 until periodStarts.size) {
                val days = ChronoUnit.DAYS.between(periodStarts[i - 1], periodStarts[i])
                intervals.add(days)
            }

            val avgCycleLength = if (intervals.isNotEmpty()) {
                (intervals.sum().toDouble() / intervals.size).toLong()
            } else {
                (settings?.averageCycleLength ?: 28).toLong()
            }

            val expectedDate = lastPeriodStart.plusDays(avgCycleLength)
            val daysUntil = ChronoUnit.DAYS.between(today, expectedDate).toInt().coerceAtLeast(0)
            val confidence = (intervals.size.toFloat() / 6f).coerceIn(0f, 1f)

            PeriodPrediction(
                expectedDate = expectedDate,
                daysUntil = daysUntil,
                accuracyMinutes = 2,
                confidence = confidence
            )
        }
    }

    override fun getCurrentCycleDay(): Flow<Int> {
        return combine(
            settingsDao.getSettings(),
            cycleDao.getPeriodEntries()
        ) { settings, periodEntries ->
            val today = LocalDate.now()
            val periodStarts = detectCycleStarts(periodEntries)

            val lastStart = periodStarts.lastOrNull()
                ?: settings?.lastPeriodStartDate?.let { epochToDate(it) }

            if (lastStart == null) return@combine 1

            val daysSince = ChronoUnit.DAYS.between(lastStart, today).toInt() + 1
            daysSince.coerceIn(1, settings?.averageCycleLength ?: 28)
        }
    }

    override fun getCurrentPhase(): Flow<CyclePhase> {
        return combine(
            getCurrentCycleDay(),
            settingsDao.getSettings()
        ) { cycleDay, settings ->
            val cycleLength = settings?.averageCycleLength ?: 28
            val periodLength = settings?.averagePeriodLength ?: 5

            when {
                cycleDay <= periodLength -> CyclePhase.MENSTRUAL
                cycleDay <= (cycleLength / 2) - 2 -> CyclePhase.FOLLICULAR
                cycleDay <= (cycleLength / 2) + 1 -> CyclePhase.OVULATORY
                else -> CyclePhase.LUTEAL
            }
        }
    }

    override fun getLastNCycles(n: Int): Flow<List<List<CycleEntry>>> {
        return flow {
            val allEntries = cycleDao.getAllEntries().first()
            val periodStarts = detectCycleStarts(
                allEntries.filter { it.flowIntensity != null }
            )

            if (periodStarts.isEmpty()) {
                emit(emptyList())
                return@flow
            }

            val relevantStarts = periodStarts.takeLast(n)
            val cycles = mutableListOf<List<CycleEntry>>()

            for (i in relevantStarts.indices) {
                val cycleStart = relevantStarts[i].atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()
                val cycleEnd = if (i + 1 < relevantStarts.size) {
                    relevantStarts[i + 1].minusDays(1).atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } else {
                    relevantStarts[i].plusDays(40).atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }

                val cycleEntries = allEntries.filter { entry ->
                    entry.date in cycleStart..cycleEnd
                }.sortedBy { it.date }

                cycles.add(cycleEntries)
            }

            emit(cycles)
        }
    }

    override fun getSettings(): Flow<CycleSettings> {
        return settingsDao.getSettings().map { it ?: CycleSettings() }
    }

    override suspend fun updateSettings(settings: CycleSettings) {
        settingsDao.upsertSettings(settings)
    }

    private fun detectCycleStarts(periodEntries: List<CycleEntry>): List<LocalDate> {
        val sorted = periodEntries
            .filter { it.flowIntensity != null }
            .sortedBy { it.date }

        if (sorted.isEmpty()) return emptyList()

        val starts = mutableListOf<LocalDate>()
        var lastEpoch = 0L

        for (entry in sorted) {
            val daysDiff = if (lastEpoch == 0L) 0L
            else ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(lastEpoch).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(entry.date).atZone(ZoneId.systemDefault()).toLocalDate()
            )

            if (daysDiff >= 14 || lastEpoch == 0L) {
                starts.add(epochToDate(entry.date))
            }
            if (entry.date > lastEpoch) lastEpoch = entry.date
        }

        return starts
    }

    private fun epochToDate(epoch: Long): LocalDate {
        return Instant.ofEpochMilli(epoch)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCycleRepository(impl: CycleRepositoryImpl): CycleRepository
}
