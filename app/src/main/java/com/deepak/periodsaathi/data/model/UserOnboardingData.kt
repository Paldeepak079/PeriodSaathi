package com.deepak.periodsaathi.data.model

data class OnboardingResponse(
    val goals: List<String> = emptyList(),
    val birthControl: String? = null,
    val cycleLength: Int? = null,
    val periodLength: Int? = null,
    val lastPeriodStart: String? = null
)
