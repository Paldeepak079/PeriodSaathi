package com.deepak.periodsaathi.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.CycleDao
import com.deepak.periodsaathi.data.dao.JournalDao
import com.deepak.periodsaathi.data.dao.ReminderDao
import com.deepak.periodsaathi.data.dao.SettingsDao
import com.deepak.periodsaathi.data.datastore.UserPreferences
import com.deepak.periodsaathi.data.model.CycleSettings
import com.deepak.periodsaathi.security.StealthModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val cycleDao: CycleDao,
    private val journalDao: JournalDao,
    private val reminderDao: ReminderDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    /** Dark mode preference — backed by DataStore, drives global 500ms theme crossfade. */
    val isDarkMode: StateFlow<Boolean> = userPreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleDarkMode() {
        viewModelScope.launch {
            userPreferences.setDarkMode(!isDarkMode.value)
        }
    }

private val _userName = MutableStateFlow("Friend")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _cycleLength = MutableStateFlow(28)
    val cycleLength: StateFlow<Int> = _cycleLength.asStateFlow()

    private val _periodLength = MutableStateFlow(5)
    val periodLength: StateFlow<Int> = _periodLength.asStateFlow()

    private val _stealthMode = MutableStateFlow(false)
    val stealthMode: StateFlow<Boolean> = _stealthMode.asStateFlow()

    private val _biometricLock = MutableStateFlow(false)
    val biometricLock: StateFlow<Boolean> = _biometricLock.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _premiumTier = MutableStateFlow("FREE")
    val premiumTier: StateFlow<String> = _premiumTier.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settings = settingsDao.getSettings().first() ?: CycleSettings()
            _userName.value = settings.userName
            _cycleLength.value = settings.averageCycleLength
            _periodLength.value = settings.averagePeriodLength
            _stealthMode.value = settings.stealthModeEnabled
            _soundEnabled.value = settings.soundEnabled
            _hapticEnabled.value = settings.hapticEnabled
        }
    }

private fun saveSettings() {
        viewModelScope.launch {
            try {
                val current = settingsDao.getSettings().first() ?: CycleSettings()
                settingsDao.upsertSettings(current.copy(
                    userName = _userName.value,
                    averageCycleLength = _cycleLength.value,
                    averagePeriodLength = _periodLength.value,
                    stealthModeEnabled = _stealthMode.value,
                    soundEnabled = _soundEnabled.value,
                    hapticEnabled = _hapticEnabled.value
                ))
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to save settings"
            }
        }
    }

    fun updateUserName(name: String) {
        _userName.value = name
        saveSettings()
    }

    fun updateCycleLength(length: Int) {
        _cycleLength.value = length.coerceIn(21, 45)
        saveSettings()
    }

    fun updatePeriodLength(length: Int) {
        _periodLength.value = length.coerceIn(2, 10)
        saveSettings()
    }

    fun toggleStealthMode() {
        _stealthMode.value = !_stealthMode.value
        saveSettings()
    }

    fun toggleBiometricLock() {
        _biometricLock.value = !_biometricLock.value
        saveSettings()
    }

    fun toggleSound() {
        _soundEnabled.value = !_soundEnabled.value
        saveSettings()
    }

    fun toggleHaptic() {
        _hapticEnabled.value = !_hapticEnabled.value
        saveSettings()
    }

    fun deleteAllData() {
        viewModelScope.launch {
            cycleDao.deleteAll()
            journalDao.deleteAll()
            reminderDao.deleteAll()
            settingsDao.deleteAll()
            loadSettings()
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            cycleDao.deleteAll()
            journalDao.deleteAll()
            reminderDao.deleteAll()
            settingsDao.deleteAll()
            onComplete()
        }
    }
}
