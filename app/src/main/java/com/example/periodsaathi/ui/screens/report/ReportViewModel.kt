package com.example.periodsaathi.ui.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periodsaathi.data.repository.CycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExportState {
    data object Idle : ExportState()
    data object Loading : ExportState()
    data object Success : ExportState()
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _selectedCycles = MutableStateFlow(3)
    val selectedCycles: StateFlow<Int> = _selectedCycles.asStateFlow()

    private val _cycleDataLoaded = MutableStateFlow(false)
    val cycleDataAvailable: StateFlow<Boolean> = _cycleDataLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            val cycles = cycleRepository.getLastNCycles(Int.MAX_VALUE).first()
            _cycleDataLoaded.value = cycles.isNotEmpty()
        }
    }

    fun setCycles(count: Int) { _selectedCycles.value = count }

    fun exportPdf() {
        _exportState.value = ExportState.Loading
        viewModelScope.launch {
            val cycles = cycleRepository.getLastNCycles(_selectedCycles.value).first()
            val settings = cycleRepository.getSettings().first()
            delay(2000)
            _exportState.value = ExportState.Success
        }
    }

    fun exportCsv() {
        _exportState.value = ExportState.Loading
        viewModelScope.launch {
            val cycles = cycleRepository.getLastNCycles(_selectedCycles.value).first()
            val settings = cycleRepository.getSettings().first()
            delay(1500)
            _exportState.value = ExportState.Success
        }
    }

    fun reset() { _exportState.value = ExportState.Idle }
}
