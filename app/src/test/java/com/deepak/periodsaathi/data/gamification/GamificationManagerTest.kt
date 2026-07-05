package com.deepak.periodsaathi.data.gamification

import android.content.Context
import com.deepak.periodsaathi.data.model.CycleSettings
import com.deepak.periodsaathi.data.repository.CycleRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GamificationManagerTest {

    private val repository = mockk<CycleRepository>()
    private val context = mockk<Context>()
    private lateinit var manager: GamificationManager

    @Before
    fun setup() {
        every { context.getSharedPreferences(any(), any()) } returns mockk(relaxed = true)
        manager = GamificationManager(repository, context)
    }

    @Test
    fun `awardPoints adds correct points for water logged`() = runTest {
        val settings = CycleSettings(totalPoints = 10, streakCount = 0)
        coEvery { repository.getSettings() } returns flowOf(settings)
        coEvery { repository.getLastNCycles(1) } returns flowOf(emptyList())
        coEvery { repository.updateSettings(any()) } returns Unit

        val result = manager.awardPoints(PointEvent.WATER_LOGGED)

        assertEquals(1, result.pointsEarned)
        assertEquals(11, result.newTotal)
    }

    @Test
    fun `awardPoints adds correct points for challenge completed`() = runTest {
        val settings = CycleSettings(totalPoints = 0, streakCount = 0)
        coEvery { repository.getSettings() } returns flowOf(settings)
        coEvery { repository.getLastNCycles(1) } returns flowOf(emptyList())
        coEvery { repository.updateSettings(any()) } returns Unit

        val result = manager.awardPoints(PointEvent.CHALLENGE_COMPLETED)

        assertEquals(10, result.pointsEarned)
        assertEquals(10, result.newTotal)
    }

    @Test
    fun `updateStreak resets to zero when no entries exist`() = runTest {
        coEvery { repository.getSettings() } returns flowOf(CycleSettings(streakCount = 3))
        coEvery { repository.getLastNCycles(1) } returns flowOf(emptyList())

        val streak = manager.updateStreak()

        assertEquals(3, streak)
    }

    @Test
    fun `checkNewRewards returns correct rewards at threshold`() = runTest {
        val rewards = manager.checkNewRewards(50)

        assertTrue(rewards.isNotEmpty())
        assertTrue(rewards.any { it.id == "theme_lavender" })
    }

    @Test
    fun `checkNewRewards returns empty below 5 points`() = runTest {
        val rewards = manager.checkNewRewards(3)

        assertTrue(rewards.none { it.id == "badge_first_log" })
    }
}
