package com.example.periodsaathi.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.data.dao.SettingsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashState {
    data object Loading : SplashState()
    data class NavigateTo(val destination: SplashDestination) : SplashState()
}

enum class SplashDestination {
    ONBOARDING, LOGIN, HOME, LOCK_SCREEN
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingsDao: SettingsDao
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            delay(1500) // Animation duration
            settingsDao.getSettings().collect { settings ->
                val destination = when {
                    settings == null -> SplashDestination.ONBOARDING
                    else -> SplashDestination.HOME
                }
                _splashState.value = SplashState.NavigateTo(destination)
            }
        }
    }
}