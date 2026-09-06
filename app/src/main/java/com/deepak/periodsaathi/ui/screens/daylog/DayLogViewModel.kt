package com.deepak.periodsaathi.ui.screens.daylog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.repository.CycleRepository
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DayLogUiState(
    val dateEpoch: Long = 0L,
    val flowIntensity: String? = null,
    val symptoms: List<String> = emptyList(),
    val mood: String? = null,
    val waterGlasses: Int = 0,
    val notes: String = "",
    val isRestDay: Boolean = false,
    val isSaving: Boolean = false,
    val showDatePicker: Boolean = false
)

@HiltViewModel
class DayLogViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(DayLogUiState())
    val state: StateFlow<DayLogUiState> = _state.asStateFlow()

    private val _saveComplete = MutableSharedFlow<Unit>()
    val saveComplete: SharedFlow<Unit> = _saveComplete.asSharedFlow()

    private val _saveError = MutableSharedFlow<String>()
    val saveError: SharedFlow<String> = _saveError.asSharedFlow()

    private val flowLevels = listOf("Light", "Medium", "Heavy")
    private val symptomOptions = listOf("Cramps", "Headache", "Bloating", "Fatigue", "Nausea", "Backache", "Mood Swings")
    private val moodOptions = listOf("Happy", "Calm", "Sad", "Anxious", "Irritable", "Energetic", "Tired")

    val availableSymptoms: List<String> get() = symptomOptions
    val availableMoods: List<String> get() = moodOptions
    val availableFlowLevels: List<String> get() = flowLevels

    fun setDateEpoch(epoch: Long) {
        _state.value = _state.value.copy(dateEpoch = epoch)
    }

    fun setFlowIntensity(intensity: String?) {
        _state.value = _state.value.copy(flowIntensity = intensity)
    }

    fun toggleSymptom(symptom: String) {
        val current = _state.value.symptoms.toMutableList()
        if (symptom in current) current.remove(symptom)
        else current.add(symptom)
        _state.value = _state.value.copy(symptoms = current)
    }

    fun setMood(mood: String?) {
        _state.value = _state.value.copy(mood = mood)
    }

    fun setWaterGlasses(glasses: Int) {
        _state.value = _state.value.copy(
            waterGlasses = glasses.coerceIn(0, 20)
        )
    }

    fun setNotes(notes: String) {
        _state.value = _state.value.copy(notes = notes)
    }

    fun toggleRestDay() {
        _state.value = _state.value.copy(isRestDay = !_state.value.isRestDay)
    }

    fun saveEntry() {
        val s = _state.value
        if (s.flowIntensity == null && !s.isRestDay) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            try {
                val existing = cycleRepository.getEntryByDate(s.dateEpoch)
                val entry = if (existing != null) {
                    existing.copy(
                        flowIntensity = s.flowIntensity,
                        symptoms = s.symptoms.joinToString(","),
                        mood = s.mood,
                        waterGlasses = s.waterGlasses,
                        notes = s.notes.ifEmpty { null },
                        isRestDay = s.isRestDay
                    )
                } else {
                    CycleEntry(
                        date = s.dateEpoch,
                        flowIntensity = s.flowIntensity,
                        symptoms = s.symptoms.joinToString(","),
                        mood = s.mood,
                        waterGlasses = s.waterGlasses,
                        notes = s.notes.ifEmpty { null },
                        isRestDay = s.isRestDay
                    )
                }
                if (existing != null) {
                    cycleRepository.updateEntry(entry)
                } else {
                    cycleRepository.logCycleEntry(entry)
                }
                _state.value = _state.value.copy(isSaving = false)
                _saveComplete.emit(Unit)
                com.deepak.periodsaathi.worker.WidgetRefreshWorker.refreshAllWidgets(context)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false)
                _saveError.emit("Failed to save: ${e.message}")
            }
        }
    }
}
