package com.example.periodsaathi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.data.model.CycleEntry
import com.example.periodsaathi.domain.usecase.GetHomeDataUseCase
import com.example.periodsaathi.domain.usecase.HomeData
import com.example.periodsaathi.domain.usecase.LogCycleEntryUseCase
import com.example.periodsaathi.domain.usecase.MascotEmotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val homeData: HomeData,
        val showRestDayBanner: Boolean = false,
        val mascotTip: String? = null,
        val showMascotTip: Boolean = false
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val logCycleEntryUseCase: LogCycleEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val mascotTips = listOf(
        "Remember to stay hydrated! 💧 Your body will thank you.",
        "Gentle stretching can help with cramps. Try some yoga!",
        "Self-care is important. Take a moment for yourself today.",
        "Tracking your symptoms helps you understand your body better.",
        "Good nutrition supports your cycle. Don't skip meals!",
        "Rest is productive. It's okay to take it easy today.",
        "Your mood is valid. Every feeling is part of the journey.",
        "You've got this! One day at a time. 🌸"
    )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getHomeDataUseCase()
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
                }
                .collect { homeData ->
                    val showRestDay = homeData.isRestDay

                    _uiState.value = HomeUiState.Success(
                        homeData = homeData,
                        showRestDayBanner = showRestDay,
                        mascotTip = null,
                        showMascotTip = false
                    )
                }
        }
    }

    fun logWater(glasses: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                val today = LocalDate.now()
                val epochMilli = today.toEpochDay() * 24 * 60 * 60 * 1000

                val existingEntry = currentState.homeData.todayEntry

                val entry = CycleEntry(
                    id = existingEntry?.id ?: 0,
                    date = epochMilli,
                    flowIntensity = existingEntry?.flowIntensity,
                    symptoms = existingEntry?.symptoms ?: "[]",
                    mood = existingEntry?.mood,
                    waterGlasses = glasses,
                    notes = existingEntry?.notes,
                    isRestDay = existingEntry?.isRestDay ?: false,
                    cyclePhase = existingEntry?.cyclePhase ?: "MENSTRUAL"
                )

                logCycleEntryUseCase(entry)
            }
        }
    }

    fun dismissRestDay() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(showRestDayBanner = false)
        }
    }

    fun onMascotTapped(): String {
        val tip = mascotTips.random()
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(
                mascotTip = tip,
                showMascotTip = true
            )
        }
        return tip
    }

    fun dismissMascotTip() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(showMascotTip = false)
        }
    }

    fun refresh() {
        loadData()
    }
}

private fun LocalDate.toEpochDay(): Long = this.toEpochDay() * 24 * 60 * 60 * 1000