package com.deepak.periodsaathi.domain.usecase

import com.deepak.periodsaathi.data.dao.ChallengeDao
import com.deepak.periodsaathi.data.model.ChallengeProgressEntity
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.model.CycleSettings
import com.deepak.periodsaathi.data.repository.CycleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ChallengeProgressManagerTest {

    private val challengeDao = mockk<ChallengeDao>()
    private val cycleRepository = mockk<CycleRepository>()
    private lateinit var manager: ChallengeProgressManager

    @Before
    fun setup() {
        manager = ChallengeProgressManager(challengeDao, cycleRepository)
    }

    @Test
    fun `hydration hero progresses based on water glasses`() = runTest {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entries = (0..6).map { day ->
            CycleEntry(
                date = todayEpoch - day * 86400000L,
                waterGlasses = if (day < 4) 8 else 2,
                mood = "happy",
                symptoms = "[]"
            )
        }

        coEvery { challengeDao.getAllProgress() } returns flowOf(
            listOf(ChallengeProgressEntity(challengeId = "1", progress = 0f))
        )
        coEvery { cycleRepository.getMonthEntries(any(), any()) } returns flowOf(entries)
        coEvery { challengeDao.updateProgress(any()) } returns Unit

        manager.refreshAllProgress()

        coVerify { challengeDao.updateProgress(match { it.progress > 0f && it.progress <= 1f }) }
    }

    @Test
    fun `mood tracker progresses based on mood entries`() = runTest {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entries = (0..13).map { day ->
            CycleEntry(
                date = todayEpoch - day * 86400000L,
                waterGlasses = 4,
                mood = if (day < 7) "happy" else null,
                symptoms = "[]"
            )
        }

        coEvery { challengeDao.getAllProgress() } returns flowOf(
            listOf(ChallengeProgressEntity(challengeId = "2", progress = 0f))
        )
        coEvery { cycleRepository.getMonthEntries(any(), any()) } returns flowOf(entries)
        coEvery { challengeDao.updateProgress(any()) } returns Unit

        manager.refreshAllProgress()

        coVerify { challengeDao.updateProgress(match { it.progress > 0f }) }
    }

    @Test
    fun `skips completed challenges`() = runTest {
        coEvery { challengeDao.getAllProgress() } returns flowOf(
            listOf(ChallengeProgressEntity(challengeId = "1", progress = 1f, completed = true))
        )
        coEvery { cycleRepository.getMonthEntries(any(), any()) } returns flowOf(emptyList())

        manager.refreshAllProgress()

        coVerify(inverse = true) { challengeDao.updateProgress(any()) }
    }

    @Test
    fun `first step challenge completes when username is set`() = runTest {
        coEvery { challengeDao.getAllProgress() } returns flowOf(
            listOf(ChallengeProgressEntity(challengeId = "4", progress = 0f))
        )
        coEvery { cycleRepository.getMonthEntries(any(), any()) } returns flowOf(emptyList())
        coEvery { cycleRepository.getSettings() } returns flowOf(
            CycleSettings(userName = "TestUser")
        )
        coEvery { challengeDao.updateProgress(any()) } returns Unit

        manager.refreshAllProgress()

        coVerify { challengeDao.updateProgress(match { it.completed && it.progress >= 1f }) }
    }

    @Test
    fun `cycle logger challenge completes when lastPeriodStartDate is set`() = runTest {
        coEvery { challengeDao.getAllProgress() } returns flowOf(
            listOf(ChallengeProgressEntity(challengeId = "5", progress = 0f))
        )
        coEvery { cycleRepository.getMonthEntries(any(), any()) } returns flowOf(emptyList())
        coEvery { cycleRepository.getSettings() } returns flowOf(
            CycleSettings(lastPeriodStartDate = 1000000L)
        )
        coEvery { challengeDao.updateProgress(any()) } returns Unit

        manager.refreshAllProgress()

        coVerify { challengeDao.updateProgress(match { it.completed && it.progress >= 1f }) }
    }
}
