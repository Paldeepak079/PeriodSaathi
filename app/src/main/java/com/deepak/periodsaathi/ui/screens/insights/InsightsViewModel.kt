package com.deepak.periodsaathi.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.repository.CycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class PatternInsight(val title: String, val description: String, val confidence: Float, val color: Long)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _cyclesLogged = MutableStateFlow(0)
    val cyclesLogged: StateFlow<Int> = _cyclesLogged.asStateFlow()

    private val _minimumCyclesReached = MutableStateFlow(false)
    val minimumCyclesReached: StateFlow<Boolean> = _minimumCyclesReached.asStateFlow()

    private val _insights = MutableStateFlow<List<PatternInsight>>(emptyList())
    val insights: StateFlow<List<PatternInsight>> = _insights.asStateFlow()

    init {
        viewModelScope.launch {
            val cycles = cycleRepository.getLastNCycles(Int.MAX_VALUE).first()
            val cycleCount = cycles.size
            _cyclesLogged.value = cycleCount
            _minimumCyclesReached.value = cycleCount >= 3

            if (cycleCount >= 3) {
                computeInsights(cycles)
            }
        }
    }

    private fun computeInsights(cycles: List<List<com.deepak.periodsaathi.data.model.CycleEntry>>) {
        val computed = mutableListOf<PatternInsight>()

        if (cycles.size >= 2) {
            val lengths = mutableListOf<Long>()
            for (i in 1 until cycles.size) {
                val prevStart = cycles[i - 1].firstOrNull()?.date ?: continue
                val currStart = cycles[i].firstOrNull()?.date ?: continue
                val days = ChronoUnit.DAYS.between(
                    Instant.ofEpochMilli(prevStart).atZone(ZoneId.systemDefault()).toLocalDate(),
                    Instant.ofEpochMilli(currStart).atZone(ZoneId.systemDefault()).toLocalDate()
                )
                if (days in 20..45) lengths.add(days)
            }
            if (lengths.isNotEmpty()) {
                val avg = lengths.average()
                val consistency = 1f - (lengths.map { kotlin.math.abs(it - avg) }.average().toFloat() / avg.toFloat()).coerceIn(0f, 1f)
                computed.add(
                    PatternInsight(
                        title = "Cycle Length",
                        description = "Average cycle is ${avg.toInt()} days (${lengths.size} intervals)",
                        confidence = (lengths.size.toFloat() / 6f).coerceIn(0f, 1f) * consistency,
                        color = 0xFF4CAF50
                    )
                )
            }
        }

        val periodDurations = cycles.map { cycle ->
            cycle.filter { it.flowIntensity != null }.size
        }.filter { it > 0 }
        if (periodDurations.isNotEmpty()) {
            val avgDuration = periodDurations.average().toInt()
            computed.add(
                PatternInsight(
                    title = "Period Duration",
                    description = "Average period lasts $avgDuration days",
                    confidence = (periodDurations.size.toFloat() / 6f).coerceIn(0f, 1f),
                    color = 0xFFFFB5C8
                )
            )
        }

        val entriesWithSymptoms = cycles.flatten().count { it.symptoms != null && it.symptoms != "[]" }
        if (entriesWithSymptoms > 0) {
            computed.add(
                PatternInsight(
                    title = "Symptom Tracking",
                    description = "Symptoms logged on $entriesWithSymptoms days",
                    confidence = (entriesWithSymptoms.toFloat() / 60f).coerceIn(0f, 1f),
                    color = 0xFFFFB5C8
                )
            )
        }

        val moodEntries = cycles.flatten().filter { it.mood != null }
        if (moodEntries.isNotEmpty()) {
            computed.add(
                PatternInsight(
                    title = "Mood Trend",
                    description = "${moodEntries.size} mood entries across ${cycles.size} cycles",
                    confidence = (moodEntries.size.toFloat() / 30f).coerceIn(0f, 1f),
                    color = 0xFFB8DCFF
                )
            )
        }

        _insights.value = computed
    }
}

