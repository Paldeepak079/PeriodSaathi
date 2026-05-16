package com.example.periodsaathi.ui.screens.insights

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class InsightItem(
    val title: String,
    val description: String,
    val confidence: Float,
    val type: String
)

@HiltViewModel
class InsightsViewModel @Inject constructor() : ViewModel() {
    private val _insights = MutableStateFlow<List<InsightItem>>(emptyList())
    val insights: StateFlow<List<InsightItem>> = _insights.asStateFlow()

    private val _cyclesLogged = MutableStateFlow(3)
    val cyclesLogged: StateFlow<Int> = _cyclesLogged.asStateFlow()

    init {
        _insights.value = listOf(
            InsightItem("Water & Cramps", "On days with low water intake, cramps intensity increases by 40%", 0.85f, "water"),
            InsightItem("Sleep & Mood", "Less than 6 hours sleep correlates with negative mood the next day", 0.72f, "sleep"),
            InsightItem("Symptom Pattern", "Cramps most common on day 2-3 of period", 0.90f, "pattern")
        )
    }
}