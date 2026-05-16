package com.example.periodsaathi.ui.screens.moodmap

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MoodData(val date: String, val mood: Int) // 1-5

@HiltViewModel
class MoodMapViewModel @Inject constructor() : ViewModel() {
    private val _moodData = MutableStateFlow(List(90) { MoodData("Day ${it+1}", (1..5).random()) })
    val moodData: StateFlow<List<MoodData>> = _moodData.asStateFlow()
    private val _showCycleOverlay = MutableStateFlow(true)
    val showCycleOverlay: StateFlow<Boolean> = _showCycleOverlay.asStateFlow()

    fun toggleOverlay() { _showCycleOverlay.value = !_showCycleOverlay.value }
}