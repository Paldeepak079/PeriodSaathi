package com.deepak.periodsaathi.domain.usecase

import com.deepak.periodsaathi.data.dao.ChallengeDao
import com.deepak.periodsaathi.data.model.ChallengeProgressEntity
import com.deepak.periodsaathi.data.repository.CycleRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class ChallengeDefinition(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val duration: String,
    val pointsReward: Int,
    val targetProgress: Float = 1f
)

val CHALLENGE_DEFINITIONS = listOf(
    ChallengeDefinition("1", "Hydration Hero", "Log 8 glasses of water for 7 days", "\uD83D\uDCA7", "7 days", 100),
    ChallengeDefinition("2", "Mood Tracker", "Log your mood for 14 days", "\uD83D\uDE0A", "14 days", 150),
    ChallengeDefinition("3", "Symptom Detective", "Log symptoms for 5 days", "\uD83D\uDD0D", "5 days", 75),
    ChallengeDefinition("4", "First Step", "Complete onboarding", "\uD83D\uDC4B", "1 day", 50),
    ChallengeDefinition("5", "Cycle Logger", "Log first period", "\uD83E\uDE78", "1 day", 50)
)

class ChallengeProgressManager @Inject constructor(
    private val challengeDao: ChallengeDao,
    private val cycleRepository: CycleRepository
) {
    suspend fun refreshAllProgress() {
        val progressList = challengeDao.getAllProgress().first()
        if (progressList.isEmpty()) return

        val today = LocalDate.now()
        val todayEpoch = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val weekAgoEpoch = today.minusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val monthEntries = cycleRepository.getMonthEntries(today.year, today.monthValue).first()
        val recentEntries = monthEntries.filter {
            it.date >= weekAgoEpoch && it.date <= todayEpoch + 86400000
        }

        for (progress in progressList) {
            if (progress.completed) continue
            val updated = computeProgress(progress.challengeId, recentEntries, progress)
            if (updated != progress) {
                challengeDao.updateProgress(updated)
            }
        }
    }

    private suspend fun computeProgress(
        challengeId: String,
        recentEntries: List<com.deepak.periodsaathi.data.model.CycleEntry>,
        current: ChallengeProgressEntity
    ): ChallengeProgressEntity {
        return when (challengeId) {
            "1" -> computeHydrationHero(recentEntries, current)
            "2" -> computeMoodTracker(recentEntries, current)
            "3" -> computeSymptomDetective(recentEntries, current)
            "4" -> computeFirstStep(current)
            "5" -> computeCycleLogger(current)
            else -> current
        }
    }

    private fun computeHydrationHero(
        entries: List<com.deepak.periodsaathi.data.model.CycleEntry>,
        current: ChallengeProgressEntity
    ): ChallengeProgressEntity {
        val daysWithEnoughWater = entries.count { it.waterGlasses >= 8 }
        val progress = (daysWithEnoughWater.toFloat() / 7f).coerceAtMost(1f)
        return current.copy(progress = progress, completed = progress >= 1f, lastUpdated = System.currentTimeMillis())
    }

    private fun computeMoodTracker(
        entries: List<com.deepak.periodsaathi.data.model.CycleEntry>,
        current: ChallengeProgressEntity
    ): ChallengeProgressEntity {
        val daysWithMood = entries.count { it.mood != null && it.mood.isNotBlank() }
        val progress = (daysWithMood.toFloat() / 14f).coerceAtMost(1f)
        return current.copy(progress = progress, completed = progress >= 1f, lastUpdated = System.currentTimeMillis())
    }

    private fun computeSymptomDetective(
        entries: List<com.deepak.periodsaathi.data.model.CycleEntry>,
        current: ChallengeProgressEntity
    ): ChallengeProgressEntity {
        val daysWithSymptoms = entries.count { it.symptoms != "[]" && it.symptoms.isNotBlank() }
        val progress = (daysWithSymptoms.toFloat() / 5f).coerceAtMost(1f)
        return current.copy(progress = progress, completed = progress >= 1f, lastUpdated = System.currentTimeMillis())
    }

    private suspend fun computeFirstStep(
        current: ChallengeProgressEntity
    ): ChallengeProgressEntity {
        val settings = cycleRepository.getSettings().first()
        val hasName = settings.userName.isNotBlank() && settings.userName != "Friend"
        return if (hasName) {
            current.copy(progress = 1f, completed = true, lastUpdated = System.currentTimeMillis())
        } else current
    }

    private suspend fun computeCycleLogger(
        current: ChallengeProgressEntity
    ): ChallengeProgressEntity {
        val settings = cycleRepository.getSettings().first()
        val hasPeriod = settings.lastPeriodStartDate != null
        return if (hasPeriod) {
            current.copy(progress = 1f, completed = true, lastUpdated = System.currentTimeMillis())
        } else current
    }
}
