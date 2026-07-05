package com.deepak.periodsaathi.domain.usecase

import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.repository.CycleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatePatternsUseCaseTest {

    private val repository = mockk<CycleRepository>()
    private val useCase = CalculatePatternsUseCase(repository)

    @Test
    fun `returns empty list when fewer than 10 entries`() = runTest {
        val entries = listOf(
            CycleEntry(date = 1000, waterGlasses = 3, symptoms = "[]", mood = "happy"),
            CycleEntry(date = 2000, waterGlasses = 4, symptoms = "[]", mood = "happy"),
        )
        coEvery { repository.getLastNCycles(6) } returns flowOf(listOf(entries))

        val result = useCase()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `detects water-cramp correlation when hydration differs`() = runTest {
        val entries = (1..12).map { i ->
            CycleEntry(
                date = i * 86400000L,
                waterGlasses = if (i % 2 == 0) 2 else 6,
                symptoms = if (i % 2 == 0) "[\"cramps\"]" else "[]",
                mood = "okay"
            )
        }
        coEvery { repository.getLastNCycles(6) } returns flowOf(listOf(entries))

        val result = useCase()
        val waterInsight = result.find { it.type == PatternType.WATER_CORRELATION }
        assertTrue(waterInsight != null)
        assertTrue(waterInsight!!.confidence > 0f)
    }

    @Test
    fun `detects sleep-mood correlation`() = runTest {
        val entries = (1..12).map { i ->
            CycleEntry(
                date = i * 86400000L,
                waterGlasses = 4,
                symptoms = if (i <= 6) "[\"fatigue\"]" else "[]",
                mood = if (i <= 6) "sad" else "happy"
            )
        }
        coEvery { repository.getLastNCycles(6) } returns flowOf(listOf(entries))

        val result = useCase()
        val sleepInsight = result.find { it.type == PatternType.SLEEP_CORRELATION }
        assertTrue(sleepInsight != null)
        assertTrue(sleepInsight!!.confidence > 0f)
    }

    @Test
    fun `returns empty when all entries have cramps`() = runTest {
        val entries = (1..12).map { i ->
            CycleEntry(
                date = i * 86400000L,
                waterGlasses = 4,
                symptoms = "[\"cramps\"]",
                mood = "okay"
            )
        }
        coEvery { repository.getLastNCycles(6) } returns flowOf(listOf(entries))

        val result = useCase()
        val waterInsight = result.find { it.type == PatternType.WATER_CORRELATION }
        assertTrue(waterInsight == null)
    }
}
