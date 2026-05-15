package com.example.periodsaathi.domain.model

import java.time.LocalDate

data class PeriodPrediction(
    val expectedDate: LocalDate,
    val daysUntil: Int,
    val accuracyMinutes: Int = 2,
    val confidence: Float
)
