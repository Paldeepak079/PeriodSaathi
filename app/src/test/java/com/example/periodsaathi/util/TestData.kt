package com.example.periodsaathi.util

import com.example.periodsaathi.data.model.CycleEntry
import com.example.periodsaathi.data.model.CycleSettings
import com.example.periodsaathi.domain.model.CyclePhase
import com.example.periodsaathi.domain.usecase.HomeData
import com.example.periodsaathi.domain.usecase.MascotEmotion
import java.time.LocalDate

object TestData {

    fun fakeCycleEntry(
        date: LocalDate = LocalDate.now(),
        flowIntensity: String? = "MODERATE",
        mood: String? = "GOOD",
        waterGlasses: Int = 4,
        isRestDay: Boolean = false
    ): CycleEntry = CycleEntry(
        id = 0,
        date = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
        flowIntensity = flowIntensity,
        symptoms = "[]",
        mood = mood,
        waterGlasses = waterGlasses,
        notes = "Test note",
        isRestDay = isRestDay,
        cyclePhase = CyclePhase.MENSTRUAL.name
    )

    fun fakePeriodHistory(cycleLengths: List<Int>): List<LocalDate> {
        var date = LocalDate.of(2024, 1, 1)
        return buildList {
            add(date)
            for (length in cycleLengths) {
                date = date.plusDays(length.toLong())
                add(date)
            }
        }.reversed()
    }

    fun fakeSettings(
        userName: String = "Test User",
        cyclesLogged: Int = 5,
        averageCycleLength: Int = 28,
        streakCount: Int = 3,
        totalPoints: Int = 50
    ): CycleSettings = CycleSettings(
        id = 1,
        userName = userName,
        averageCycleLength = averageCycleLength,
        averagePeriodLength = 5,
        lastPeriodStartDate = null,
        streakCount = streakCount,
        totalPoints = totalPoints
    )

    fun fakeHomeUiState(
        mascotEmotion: MascotEmotion = MascotEmotion.HAPPY,
        waterGlasses: Int = 4,
        cycleDay: Int = 14
    ): HomeData = HomeData(
        userName = "Test User",
        currentCycleDay = cycleDay,
        currentPhase = CyclePhase.FOLLICULAR,
        todayEntry = fakeCycleEntry(),
        prediction = null,
        waterGlasses = waterGlasses,
        streakCount = 3,
        totalPoints = 50,
        isRestDay = false,
        mascotEmotion = mascotEmotion
    )
}