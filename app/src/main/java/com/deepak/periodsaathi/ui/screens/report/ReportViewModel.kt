package com.deepak.periodsaathi.ui.screens.report

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class ExportState {
    data object Idle : ExportState()
    data object Loading : ExportState()
    data class Success(val filePath: String, val mimeType: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _selectedCycles = MutableStateFlow(3)
    val selectedCycles: StateFlow<Int> = _selectedCycles.asStateFlow()

    private val _cycleDataLoaded = MutableStateFlow(false)
    val cycleDataAvailable: StateFlow<Boolean> = _cycleDataLoaded.asStateFlow()

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

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
            try {
                val cycles = cycleRepository.getLastNCycles(_selectedCycles.value).first()
                val settings = cycleRepository.getSettings().first()
                if (cycles.isEmpty()) {
                    _exportState.value = ExportState.Error("No cycle data available")
                    return@launch
                }
                val file = generatePdf(cycles, settings.userName)
                _exportState.value = ExportState.Success(file.absolutePath, "application/pdf")
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "Failed to generate PDF")
            }
        }
    }

    fun exportCsv() {
        _exportState.value = ExportState.Loading
        viewModelScope.launch {
            try {
                val cycles = cycleRepository.getLastNCycles(_selectedCycles.value).first()
                val settings = cycleRepository.getSettings().first()
                if (cycles.isEmpty()) {
                    _exportState.value = ExportState.Error("No cycle data available")
                    return@launch
                }
                val file = generateCsv(cycles, settings.userName)
                _exportState.value = ExportState.Success(file.absolutePath, "text/csv")
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "Failed to generate CSV")
            }
        }
    }

    fun shareExport(filePath: String, mimeType: String) {
        try {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Report"))
        } catch (e: Exception) {
            _exportState.value = ExportState.Error(e.message ?: "Failed to share file")
        }
    }

    fun reset() { _exportState.value = ExportState.Idle }

    private fun generateCsv(cycles: List<List<CycleEntry>>, userName: String): File {
        val file = File(context.cacheDir, "PeriodSaathi_Report.csv")
        val sb = StringBuilder()
        sb.appendLine("Period Saathi Report - $userName")
        sb.appendLine("Generated: ${dateFormat.format(Date())}")
        sb.appendLine()
        sb.appendLine("Date,Flow,Symptoms,Mood,Water (glasses),Notes,Phase")
        for (cycle in cycles) {
            for (entry in cycle) {
                val dateStr = dateFormat.format(Date(entry.date))
                sb.appendLine("$dateStr,${entry.flowIntensity ?: ""},${entry.symptoms},${entry.mood ?: ""},${entry.waterGlasses},${entry.notes ?: ""},${entry.cyclePhase}")
            }
        }
        file.writeText(sb.toString())
        return file
    }

    private fun generatePdf(cycles: List<List<CycleEntry>>, userName: String): File {
        val file = File(context.cacheDir, "PeriodSaathi_Report.pdf")
        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        document.add(Paragraph("Period Saathi Report").setFontSize(24f).setBold())
        document.add(Paragraph("User: $userName"))
        document.add(Paragraph("Generated: ${dateFormat.format(Date())}"))
        document.add(Paragraph(" "))

        var cycleNum = 1
        for (cycle in cycles) {
            document.add(Paragraph("Cycle $cycleNum").setFontSize(18f).setBold())
            document.add(Paragraph(" "))

            if (cycle.isNotEmpty()) {
                val table = Table(floatArrayOf(3f, 2f, 2f, 2f, 1f))
                table.addHeaderCell("Date")
                table.addHeaderCell("Flow")
                table.addHeaderCell("Mood")
                table.addHeaderCell("Phase")
                table.addHeaderCell("Water")

                for (entry in cycle) {
                    table.addCell(dateFormat.format(Date(entry.date)))
                    table.addCell(entry.flowIntensity ?: "-")
                    table.addCell(entry.mood ?: "-")
                    table.addCell(entry.cyclePhase)
                    table.addCell(entry.waterGlasses.toString())
                }
                document.add(table)
            }

            cycleNum++
        }

        document.close()
        return file
    }
}
