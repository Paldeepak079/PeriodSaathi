package com.deepak.periodsaathi.domain.usecase

import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.model.CycleSettings
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.deepak.periodsaathi.domain.model.CyclePhase
import com.deepak.periodsaathi.domain.model.PeriodPrediction
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class GetHomeDataUseCaseTest {

    private val repository = mockk<CycleRepository>()
    private lateinit var useCase: GetHomeDataUseCase

    @Before
    fun setup() {
        useCase = GetHomeDataUseCase(repository)
    }

    @Test
    fun `returns home data with default values when no data exists`() = runTest {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val settings = CycleSettings(userName = "Test", streakCount = 0, totalPoints = 0)

        every { repository.getSettings() } returns flowOf(settings)
        every { repository.getCurrentCycleDay() } returns flowOf(1)
        every { repository.getCurrentPhase() } returns flowOf(CyclePhase.MENSTRUAL)
        every { repository.getMonthEntries(any(), any()) } returns flowOf(emptyList())
        every { repository.predictNextPeriod() } returns flowOf(PeriodPrediction(LocalDate.now().plusDays(28), 28, 2, 0f))

        val result = useCase().first()
        assertEquals("Test", result.userName)
        assertEquals(1, result.currentCycleDay)
        assertEquals(CyclePhase.MENSTRUAL, result.currentPhase)
        assertEquals(0, result.waterGlasses)
        assertEquals(0, result.streakCount)
        assertEquals(0, result.totalPoints)
        assertEquals(MascotEmotion.SLEEPING, result.mascotEmotion)
    }

    @Test
    fun `detects heavy flow and shows PAIN mascot`() = runTest {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entry = CycleEntry(date = todayEpoch, flowIntensity = "HEAVY")
        val settings = CycleSettings(userName = "Test")

        every { repository.getSettings() } returns flowOf(settings)
        every { repository.getCurrentCycleDay() } returns flowOf(3)
        every { repository.getCurrentPhase() } returns flowOf(CyclePhase.MENSTRUAL)
        every { repository.getMonthEntries(any(), any()) } returns flowOf(listOf(entry))
        every { repository.predictNextPeriod() } returns flowOf(PeriodPrediction(LocalDate.now().plusDays(25), 25, 2, 0.6f))

        val result = useCase().first()
        assertEquals(MascotEmotion.PAIN, result.mascotEmotion)
        assertNotNull(result.todayEntry)
    }

    @Test
    fun `shows EXCITED mascot when all habits complete`() = runTest {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entry = CycleEntry(date = todayEpoch, waterGlasses = 8, mood = "happy")
        val settings = CycleSettings(userName = "Test")

        every { repository.getSettings() } returns flowOf(settings)
        every { repository.getCurrentCycleDay() } returns flowOf(14)
        every { repository.getCurrentPhase() } returns flowOf(CyclePhase.OVULATORY)
        every { repository.getMonthEntries(any(), any()) } returns flowOf(listOf(entry))
        every { repository.predictNextPeriod() } returns flowOf(PeriodPrediction(LocalDate.now().plusDays(14), 14, 2, 0.8f))

        val result = useCase().first()
        assertEquals(MascotEmotion.EXCITED, result.mascotEmotion)
    }

    @Test
    fun `shows SAD mascot when low water intake`() = runTest {
        val todayEpoch = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entry = CycleEntry(date = todayEpoch, waterGlasses = 2, mood = "okay")
        val settings = CycleSettings(userName = "Test")

        every { repository.getSettings() } returns flowOf(settings)
        every { repository.getCurrentCycleDay() } returns flowOf(7)
        every { repository.getCurrentPhase() } returns flowOf(CyclePhase.FOLLICULAR)
        every { repository.getMonthEntries(any(), any()) } returns flowOf(listOf(entry))
        every { repository.predictNextPeriod() } returns flowOf(PeriodPrediction(LocalDate.now().plusDays(21), 21, 2, 0.5f))

        val result = useCase().first()
        assertEquals(MascotEmotion.SAD, result.mascotEmotion)
    }
}
