package com.deepak.periodsaathi.domain.usecase

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

class LogCycleEntryUseCaseTest {

    private val repository = mockk<CycleRepository>()
    private lateinit var useCase: LogCycleEntryUseCase

    @Before
    fun setup() {
        useCase = LogCycleEntryUseCase(repository)
    }

    @Test
    fun `rejects future dates`() = runTest {
        val future = LocalDate.now().plusDays(1)
        val entry = CycleEntry(
            date = future.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )

        val result = useCase(entry)
        assertTrue(result.isFailure)
    }

    @Test
    fun `accepts today date`() = runTest {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entry = CycleEntry(date = todayEpoch, flowIntensity = "LIGHT")

        coEvery { repository.getMonthEntries(any(), any()) } returns flowOf(emptyList())
        coEvery { repository.getSettings() } returns flowOf(CycleSettings(userName = "Test"))
        coEvery { repository.logCycleEntry(any()) } returns Unit
        coEvery { repository.getLastNCycles(3) } returns flowOf(emptyList())
        coEvery { repository.updateSettings(any()) } returns Unit

        val result = useCase(entry)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `auto-detects rest day after consecutive heavy flow`() = runTest {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val todayEpoch = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val yesterdayEpoch = yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val yesterdayEntry = CycleEntry(date = yesterdayEpoch, flowIntensity = "HEAVY")
        val todayEntry = CycleEntry(date = todayEpoch, flowIntensity = "HEAVY")

        coEvery { repository.getMonthEntries(any(), any()) } returns flowOf(listOf(yesterdayEntry))
        coEvery { repository.getSettings() } returns flowOf(CycleSettings(userName = "Test"))
        coEvery { repository.logCycleEntry(any()) } returns Unit
        coEvery { repository.getLastNCycles(3) } returns flowOf(emptyList())
        coEvery { repository.updateSettings(any()) } returns Unit

        val result = useCase(todayEntry)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isRestDay == true)
    }

    @Test
    fun `awards points on successful entry`() = runTest {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entry = CycleEntry(date = todayEpoch, flowIntensity = "MODERATE", waterGlasses = 4)

        coEvery { repository.getMonthEntries(any(), any()) } returns flowOf(emptyList())
        coEvery { repository.getSettings() } returns flowOf(CycleSettings(totalPoints = 10, streakCount = 2, userName = "Test"))
        coEvery { repository.logCycleEntry(any()) } returns Unit
        coEvery { repository.getLastNCycles(3) } returns flowOf(emptyList())
        coEvery { repository.updateSettings(any()) } returns Unit

        val result = useCase(entry)
        assertTrue(result.isSuccess)
        coVerify { repository.updateSettings(match { it.totalPoints == 12 && it.streakCount == 3 }) }
    }
}
