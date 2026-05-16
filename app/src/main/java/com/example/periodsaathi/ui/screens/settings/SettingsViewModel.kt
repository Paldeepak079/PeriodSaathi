package com.example.periodsaathi.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _cycleLength = MutableStateFlow(28)
    val cycleLength: StateFlow<Int> = _cycleLength.asStateFlow()
    private val _periodLength = MutableStateFlow(5)
    val periodLength: StateFlow<Int> = _periodLength.asStateFlow()
    private val _notifications = MutableStateFlow(true)
    val notifications: StateFlow<Boolean> = _notifications.asStateFlow()
    private val _haptics = MutableStateFlow(true)
    val haptics: StateFlow<Boolean> = _haptics.asStateFlow()
    private val _biometric = MutableStateFlow(false)
    val biometric: StateFlow<Boolean> = _biometric.asStateFlow()

    fun setCycleLength(length: Int) { _cycleLength.value = length }
    fun setPeriodLength(length: Int) { _periodLength.value = length }
    fun toggleNotifications() { _notifications.value = !_notifications.value }
    fun toggleHaptics() { _haptics.value = !_haptics.value }
    fun toggleBiometric() { _biometric.value = !_biometric.value }
}