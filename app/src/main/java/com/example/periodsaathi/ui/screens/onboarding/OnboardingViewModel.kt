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

    private val _isComplete = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete.asStateFlow()

    fun nextPage() {
        if (_currentPage.value < 2) {
            _currentPage.value += 1
        }
    }

    fun skip() {
        _isComplete.value = true
    }

    fun completeOnboarding() {
        _isComplete.value = true
    }

    fun onPageChanged(page: Int) {
        _currentPage.value = page
    }
}