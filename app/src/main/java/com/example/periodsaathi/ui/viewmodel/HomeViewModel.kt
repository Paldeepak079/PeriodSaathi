package com.example.periodsaathi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.data.model.CycleEntry
import com.example.periodsaathi.domain.model.CyclePhase
import com.example.periodsaathi.domain.usecase.GetHomeDataUseCase
import com.example.periodsaathi.domain.usecase.HomeData
import com.example.periodsaathi.domain.usecase.LogCycleEntryUseCase
import com.example.periodsaathi.domain.usecase.MascotEmotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val homeData: HomeData,
        val showConfetti: Boolean = false,
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

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            getHomeDataUseCase().collect { homeData ->
                val showRestDay = homeData.isRestDay
                val mascotTip = getMascotTip(homeData.mascotEmotion, homeData.currentPhase)

                _uiState.value = HomeUiState.Success(
                    homeData = homeData,
                    showRestDayBanner = showRestDay,
                    mascotTip = mascotTip,
                    showMascotTip = mascotTip != null
                )
            }
        }
    }

    fun logCycleEntry(
        flowIntensity: String?,
        symptoms: List<String>,
        mood: String?,
        waterGlasses: Int,
        notes: String?
    ) {
        viewModelScope.launch {
            val today = java.time.LocalDate.now()
            val epochMilli = today.toEpochDay() * 24 * 60 * 60 * 1000

            val entry = CycleEntry(
                date = epochMilli,
                flowIntensity = flowIntensity,
                symptoms = symptoms.toString(),
                mood = mood,
                waterGlasses = waterGlasses,
                notes = notes,
                isRestDay = false
            )

            when (val result = logCycleEntryUseCase(entry)) {
                is LogCycleEntryUseCase.Result.Success -> {
                    // Reload data after logging
                    loadHomeData()
                }
                is LogCycleEntryUseCase.Result.Error -> {
                    _uiState.value = HomeUiState.Error(result.message)
                }
            }
        }
    }

    fun updateWaterGlasses(glasses: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                logCycleEntry(
                    flowIntensity = currentState.homeData.todayEntry?.flowIntensity,
                    symptoms = emptyList(),
                    mood = currentState.homeData.todayEntry?.mood,
                    waterGlasses = glasses,
                    notes = currentState.homeData.todayEntry?.notes
                )
            }
        }
    }

    fun dismissMascotTip() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(showMascotTip = false)
        }
    }

    fun dismissRestDayBanner() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(showRestDayBanner = false)
        }
    }

    fun dismissConfetti() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(showConfetti = false)
        }
    }

    fun triggerConfetti() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(showConfetti = true)
        }
    }

    private fun getMascotTip(emotion: MascotEmotion, phase: CyclePhase): String? {
        return when (emotion) {
            MascotEmotion.SLEEPING -> "Good morning! Let's start tracking your day. Tap to log how you're feeling!"
            MascotEmotion.PAIN -> "I'm here for you. Would you like some tips to feel better? Try a warm compress or gentle stretching."
            MascotEmotion.SAD -> "Don't forget to drink water! Staying hydrated helps with mood and energy. You've got this!"
            MascotEmotion.EXCITED -> "Amazing progress! You're doing great keeping track of your cycle. Keep it up!"
            MascotEmotion.HAPPY -> when (phase) {
                CyclePhase.MENSTRUAL -> "Remember to be kind to yourself today. Rest is productive too!"
                CyclePhase.FOLLICULAR -> "You're in your energetic phase! Great time to start new projects."
                CyclePhase.OVULATION -> "You're feeling amazing! This is a great day for social activities."
                CyclePhase.LUTEAL -> "Take things at your own pace. Self-care is important during this phase."
            }
        }
    }
}