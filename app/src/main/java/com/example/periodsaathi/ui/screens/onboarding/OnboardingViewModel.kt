package com.example.periodsaathi.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    fun nextPage() {
        if (_currentPage.value < 2) {
            _currentPage.value++
        }
    }

    fun skip() {
        _currentPage.value = 2
    }

    fun completeOnboarding() {
        _showConfetti.value = true
    }

    fun onConfettiComplete() {
        _showConfetti.value = false
    }
}