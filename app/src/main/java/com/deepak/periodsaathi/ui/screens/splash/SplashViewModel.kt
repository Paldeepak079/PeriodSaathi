package com.deepak.periodsaathi.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.SettingsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashState {
    data object Loading : SplashState()
    data class NavigateTo(val destination: String) : SplashState()
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

    fun determineStartDestination() {
        viewModelScope.launch {
            // DB check runs immediately in parallel with the animation sequence.
            // The SplashScreen composable only acts on NavigateTo AFTER its
            // animation sequence completes, so there is no hard delay needed here.
            val settingsData = try {
                settingsDao.getSettings().first()
            } catch (_: Exception) {
                null
            }
            val destination = when {
                settingsData == null -> "Onboarding"
                settingsData.stealthModeEnabled -> "LockScreen"
                else -> "Home"
            }
            _splashState.value = SplashState.NavigateTo(destination)
        }
    }
}
