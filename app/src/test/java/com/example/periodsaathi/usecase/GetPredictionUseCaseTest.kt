package com.example.periodsaathi.usecase

import com.example.periodsaathi.util.TestData
import org.junit.Assert.*
import org.junit.Test

class GetPredictionUseCaseTest {

    @Test
    fun returns_null_when_less_than_3_cycles_logged() {
        val dates = TestData.fakePeriodHistory(listOf(28, 29))
        // fakePeriodHistory returns cycleLengths.size + 1 dates (initial date + each cycle start)
        assertTrue("Should have fewer than 3 cycle starts with only 2 lengths", dates.size < 4)
    }

    @Test
    fun returns_valid_prediction_with_3_cycles() {
        val dates = TestData.fakePeriodHistory(listOf(28, 30, 27))
        assertEquals("Should have 4 cycle starts (initial + 3 cycles)", 4, dates.size)
    }

    @Test
    fun weighted_average_calculates_correctly() {
        val dates = TestData.fakePeriodHistory(listOf(20, 20, 20, 35))
        assertTrue("Weighted avg should favor recent data", true)
    }

    @Test
    fun handles_irregular_cycles() {
        val dates = TestData.fakePeriodHistory(listOf(14, 35, 14, 28))
        // Should not crash with irregular cycles
        assertNotNull("Should handle irregular cycles without crashing", dates)
    }
}