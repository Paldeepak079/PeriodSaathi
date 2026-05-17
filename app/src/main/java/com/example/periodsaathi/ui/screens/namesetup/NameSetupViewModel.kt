package com.example.periodsaathi.ui.screens.namesetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.data.dao.SettingsDao
import com.example.periodsaathi.data.model.CycleSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameSetupViewModel @Inject constructor(
    private val settingsDao: SettingsDao
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveComplete = MutableSharedFlow<Unit>()
    val saveComplete: SharedFlow<Unit> = _saveComplete.asSharedFlow()

    fun updateName(value: String) {
        if (value.length <= 30) {
            _name.value = value
        }
    }

    fun saveName(fromGoogle: Boolean) {
        val userName = _name.value.trim()
        if (userName.isEmpty()) return

        viewModelScope.launch {
            _isSaving.value = true
            val currentSettings = settingsDao.getSettings().first() ?: CycleSettings()
            settingsDao.upsertSettings(currentSettings.copy(userName = userName))
            _isSaving.value = false
            _saveComplete.emit(Unit)
        }
    }
}
