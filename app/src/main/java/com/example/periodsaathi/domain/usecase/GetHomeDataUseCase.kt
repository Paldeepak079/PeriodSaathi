package com.example.periodsaathi.domain.usecase

import com.example.periodsaathi.data.model.CycleEntry
import com.example.periodsaathi.data.repository.CycleRepository
import com.example.periodsaathi.domain.model.CyclePhase
import com.example.periodsaathi.domain.model.PeriodPrediction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HomeData(
    val userName: String,
    val currentCycleDay: Int,
    val currentPhase: CyclePhase,
    val todayEntry: CycleEntry?,
    val prediction: PeriodPrediction?,
    val waterGlasses: Int,
    val streakCount: Int,
    val totalPoints: Int,
    val isRestDay: Boolean,
    val mascotEmotion: MascotEmotion
)

enum class MascotEmotion {
    SLEEPING,
    PAIN,
    SAD,
    EXCITED,
    HAPPY
}

class GetHomeDataUseCase @Inject constructor(
    private val repository: CycleRepository
) {

    operator fun invoke(): Flow<HomeData> {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        val todayEntryFlow = repository.getMonthEntries(
            LocalDate.now().year, LocalDate.now().monthValue
        ).let { flow ->
            combine(flow, repository.getSettings()) { entries, _ ->
                entries.find { entry ->
                    val entryDate = Instant.ofEpochMilli(entry.date)
                        .atZone(ZoneId.systemDefault()).toLocalDate()
                    entryDate == LocalDate.now()
                }
            }
        }

        return combine(
            repository.getSettings(),
            repository.getCurrentCycleDay(),
            repository.getCurrentPhase(),
            todayEntryFlow,
            repository.predictNextPeriod()
        ) { settings, cycleDay, phase, todayEntry, prediction ->
            val waterGlasses = todayEntry?.waterGlasses ?: 0
            val isRestDay = todayEntry?.isRestDay ?: false
            val hasDataToday = todayEntry != null

            val mascotEmotion = if (!hasDataToday) {
                MascotEmotion.SLEEPING
            } else if (isHeavyFlow(todayEntry)) {
                MascotEmotion.PAIN
            } else if (waterGlasses < 3) {
                MascotEmotion.SAD
            } else if (allHabitsComplete(todayEntry)) {
                MascotEmotion.EXCITED
            } else {
                MascotEmotion.HAPPY
            }

            HomeData(
                userName = settings.userName,
                currentCycleDay = cycleDay,
                currentPhase = phase,
                todayEntry = todayEntry,
                prediction = prediction,
                waterGlasses = waterGlasses,
                streakCount = settings.streakCount,
                totalPoints = settings.totalPoints,
                isRestDay = isRestDay,
                mascotEmotion = mascotEmotion
            )
        }
    }

    private fun isHeavyFlow(entry: CycleEntry?): Boolean {
        return entry?.flowIntensity?.uppercase() in listOf("HEAVY", "VERY_HEAVY")
    }

    private fun allHabitsComplete(entry: CycleEntry?): Boolean {
        if (entry == null) return false
        return entry.waterGlasses >= 8 && entry.mood != null
    }
}
