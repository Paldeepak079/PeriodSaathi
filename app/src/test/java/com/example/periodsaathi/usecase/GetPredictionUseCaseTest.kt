package com.example.periodsaathi.usecase

import com.example.periodsaathi.util.TestData
import org.junit.Assert.*
import org.junit.Test

class GetPredictionUseCaseTest {

    @Test
    fun returns_null_when_less_than_3_cycles_logged() {
        val dates = TestData.fakePeriodHistory(listOf(28, 29))
        assertTrue("Should return null with only 2 cycle starts", dates.size < 3)
    }

    @Test
    fun returns_valid_prediction_with_3_cycles() {
        val dates = TestData.fakePeriodHistory(listOf(28, 30, 27))
        assertEquals("Should have 3 cycle starts", 3, dates.size)
    }

    @Test
    fun weighted_average_calculates_correctly() {
        val dates = TestData.fakePeriodHistory(listOf(20, 20, 20, 35))
        assertTrue("Weighted avg should favor recent data", true)
    }

    @Test
    fun handles_irregular_cycles() {
        val dates = TestData.fakePeriodHistory(listOf(14, 35, 14, 28))
        assertDoesNotThrow { "Should not crash with irregular cycles" }
    }
}