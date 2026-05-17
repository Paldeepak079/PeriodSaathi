package com.example.periodsaathi.data.gamification

import com.example.periodsaathi.data.repository.CycleRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamificationManager @Inject constructor(
    private val cycleRepository: CycleRepository
) {
    companion object {
        const val POINTS_WATER = 1
        const val POINTS_HABIT = 2
        const val POINTS_CYCLE_LOG = 3
        const val POINTS_JOURNAL = 2
        const val POINTS_STREAK_BONUS = 5
        const val POINTS_CHALLENGE = 10
    }

    suspend fun awardPoints(event: PointEvent): GamificationResult {
        val points = when (event) {
            PointEvent.WATER_LOGGED -> POINTS_WATER
            PointEvent.HABIT_COMPLETED -> POINTS_HABIT
            PointEvent.CYCLE_LOGGED -> POINTS_CYCLE_LOG
            PointEvent.JOURNAL_SAVED -> POINTS_JOURNAL
            PointEvent.CHALLENGE_COMPLETED -> POINTS_CHALLENGE
            PointEvent.WEEK_STREAK_BONUS -> POINTS_STREAK_BONUS
        }

        val settings = cycleRepository.getSettings().first()
        val newTotal = settings.totalPoints + points

        cycleRepository.updateSettings(settings.copy(totalPoints = newTotal))

        val streakCount = updateStreak()
        val newRewards = checkNewRewards(newTotal)

        return GamificationResult(
            pointsEarned = points,
            newTotal = newTotal,
            streakCount = streakCount,
            newRewards = newRewards
        )
    }

    suspend fun updateStreak(): Int {
        val settings = cycleRepository.getSettings().first()
        val periodEntries = cycleRepository.getLastNCycles(1).first().flatten()

        if (periodEntries.isEmpty()) {
            return settings.streakCount
        }

        val today = LocalDate.now()
        val lastEntryDate = periodEntries.maxByOrNull { it.date }?.let {
            java.time.Instant.ofEpochMilli(it.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
        } ?: return 0

        val daysSinceLastEntry = ChronoUnit.DAYS.between(lastEntryDate, today)

        val newStreak = when {
            daysSinceLastEntry == 0L -> settings.streakCount
            daysSinceLastEntry == 1L -> settings.streakCount + 1
            else -> 0
        }

        cycleRepository.updateSettings(settings.copy(streakCount = newStreak))
        return newStreak
    }

    suspend fun checkNewRewards(points: Int): List<Reward> {
        return REWARD_CATALOG.filter { reward ->
            points >= reward.pointsRequired && !isRewardUnlocked(reward.id)
        }
    }

    private fun isRewardUnlocked(rewardId: String): Boolean {
        return false
    }
}

enum class PointEvent {
    WATER_LOGGED,
    HABIT_COMPLETED,
    CYCLE_LOGGED,
    JOURNAL_SAVED,
    CHALLENGE_COMPLETED,
    WEEK_STREAK_BONUS
}

data class GamificationResult(
    val pointsEarned: Int,
    val newTotal: Int,
    val streakCount: Int,
    val newRewards: List<Reward> = emptyList()
)

data class Reward(
    val id: String,
    val name: String,
    val type: RewardType,
    val emoji: String,
    val pointsRequired: Int
)

enum class RewardType {
    THEME,
    MASCOT_ACCESSORY,
    BADGE
}

val REWARD_CATALOG: List<Reward> = listOf(
    Reward("theme_lavender", "Lavender Dream", RewardType.THEME, "💜", 50),
    Reward("theme_ocean", "Ocean Breeze", RewardType.THEME, "🌊", 100),
    Reward("acc_crown", "Princess Crown", RewardType.MASCOT_ACCESSORY, "👑", 75),
    Reward("acc_bow", "Pink Bow", RewardType.MASCOT_ACCESSORY, "🎀", 10),
    Reward("acc_star", "Gold Star", RewardType.MASCOT_ACCESSORY, "⭐", 25),
    Reward("acc_heart", "Heart Eyes", RewardType.MASCOT_ACCESSORY, "💖", 30),
    Reward("badge_first_log", "First Step", RewardType.BADGE, "🌟", 5),
    Reward("badge_week_streak", "7-Day Streak", RewardType.BADGE, "🔥", 20),
    Reward("badge_water_pro", "Hydration Pro", RewardType.BADGE, "💧", 40),
    Reward("badge_cycle_master", "Cycle Master", RewardType.BADGE, "👑", 100),
    Reward("theme_sunset", "Sunset Coral", RewardType.THEME, "🌅", 75),
    Reward("theme_midnight", "Midnight Ocean", RewardType.THEME, "🌙", 120),
    Reward("acc_sunglasses", "Cool Shades", RewardType.MASCOT_ACCESSORY, "😎", 50),
    Reward("acc_sparkles", "Sparkles", RewardType.MASCOT_ACCESSORY, "✨", 35),
    Reward("badge_insight", "Pattern Detective", RewardType.BADGE, "🔍", 60),
    Reward("badge_consistent", "Consistency King", RewardType.BADGE, "👑", 80),
    Reward("theme_rose", "Rose Garden", RewardType.THEME, "🌹", 90),
    Reward("theme_forest", "Forest Calm", RewardType.THEME, "🌲", 110),
    Reward("acc_wings", "Angel Wings", RewardType.MASCOT_ACCESSORY, "🪽", 150),
    Reward("badge_community", "Community Hero", RewardType.BADGE, "🏅", 200),
    Reward("theme_premium", "Premium Gold", RewardType.THEME, "✨", 500)
)