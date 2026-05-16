package com.example.periodsaathi.ui.screens.report

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class ExportState { data object Idle, data object Loading, data object Success }

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()
    private val _selectedCycles = MutableStateFlow(3)
    val selectedCycles: StateFlow<Int> = _selectedCycles.asStateFlow()

    fun setCycles(count: Int) { _selectedCycles.value = count }
    fun exportPdf() { _exportState.value = ExportState.Loading; kotlinx.coroutines.GlobalScope.launch { kotlinx.coroutines.delay(2000); _exportState.value = ExportState.Success } }
    fun exportCsv() { _exportState.value = ExportState.Loading; kotlinx.coroutines.GlobalScope.launch { kotlinx.coroutines.delay(1500); _exportState.value = ExportState.Success } }
}

private object kotlinx { val GlobalScope = kotlinx.coroutines.GlobalScope; fun delay(timeMs: Long) = kotlinx.coroutines.delay(timeMs) }