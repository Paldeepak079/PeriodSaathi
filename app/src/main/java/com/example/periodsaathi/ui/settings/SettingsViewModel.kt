package com.example.periodsaathi.ui.settings

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class ContraceptionMode {
    NONE, PILL, IUD, IMPLANT, RING, PATCH, INJECTION
}

data class CycleSettings(
    val userName: String = "User",
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val contraceptionMode: ContraceptionMode = ContraceptionMode.NONE,
    val stealthModeEnabled: Boolean = false,
    val stealthPin: String? = null,
    val soundsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val partnerNotificationsEnabled: Boolean = true,
    val predictionRemindersEnabled: Boolean = true,
    val customRemindersEnabled: Boolean = true
)

data class Reminder(
    val id: Long = System.currentTimeMillis(),
    val label: String,
    val time: String,
    val enabled: Boolean = true
)

data class CycleData(
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val flowIntensity: String,
    val symptoms: List<String>
)

class SettingsViewModel : ViewModel() {

    private val _settings = MutableStateFlow(CycleSettings())
    val settings: StateFlow<CycleSettings> = _settings.asStateFlow()

    private val _reminders = MutableStateFlow(createDefaultReminders())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _showStealthPinDialog = MutableStateFlow(false)
    val showStealthPinDialog: StateFlow<Boolean> = _showStealthPinDialog.asStateFlow()

    private val _showAddReminderDialog = MutableStateFlow(false)
    val showAddReminderDialog: StateFlow<Boolean> = _showAddReminderDialog.asStateFlow()

    fun updateUserName(name: String) {
        _settings.update { it.copy(userName = name) }
    }

    fun updateCycleLength(days: Int) {
        _settings.update { it.copy(cycleLength = days) }
    }

    fun updatePeriodLength(days: Int) {
        _settings.update { it.copy(periodLength = days) }
    }

    fun updateContraceptionMode(mode: ContraceptionMode) {
        _settings.update { it.copy(contraceptionMode = mode) }
    }

    fun toggleStealthMode(enabled: Boolean, pin: String? = null) {
        _settings.update {
            it.copy(
                stealthModeEnabled = enabled,
                stealthPin = if (enabled) pin else null
            )
        }
    }

    fun showStealthPinDialog(show: Boolean) {
        _showStealthPinDialog.value = show
    }

    fun toggleSounds(enabled: Boolean) {
        _settings.update { it.copy(soundsEnabled = enabled) }
    }

    fun toggleHaptics(enabled: Boolean) {
        _settings.update { it.copy(hapticsEnabled = enabled) }
    }

    fun togglePartnerNotifications(enabled: Boolean) {
        _settings.update { it.copy(partnerNotificationsEnabled = enabled) }
    }

    fun togglePredictionReminders(enabled: Boolean) {
        _settings.update { it.copy(predictionRemindersEnabled = enabled) }
    }

    fun toggleCustomReminders(enabled: Boolean) {
        _settings.update { it.copy(customRemindersEnabled = enabled) }
    }

    fun setReminder(reminder: Reminder) {
        _reminders.update { current ->
            val existing = current.find { it.id == reminder.id }
            if (existing != null) {
                current.map { if (it.id == reminder.id) reminder else it }
            } else {
                current + reminder
            }
        }
    }

    fun deleteReminder(id: Long) {
        _reminders.update { current ->
            current.filter { it.id != id }
        }
    }

    fun toggleReminderEnabled(id: Long) {
        _reminders.update { current ->
            current.map {
                if (it.id == id) it.copy(enabled = !it.enabled) else it
            }
        }
    }

    fun showAddReminderDialog(show: Boolean) {
        _showAddReminderDialog.value = show
    }

    fun exportReport(context: Context, cycles: Int) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading

            try {
                delay(1500)

                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val titlePaint = android.graphics.Paint().apply {
                    textSize = 24f
                    isFakeBoldText = true
                    color = android.graphics.Color.parseColor("#874E58")
                }

                val bodyPaint = android.graphics.Paint().apply {
                    textSize = 14f
                    color = android.graphics.Color.BLACK
                }

                canvas.drawText("Period Saathi Health Report", 50f, 60f, titlePaint)
                canvas.drawText("Generated: ${LocalDate.now()}", 50f, 90f, bodyPaint)

                canvas.drawText("Summary for last $cycles cycles", 50f, 140f, titlePaint)
                canvas.drawText("• Average cycle length: ${_settings.value.cycleLength} days", 50f, 170f, bodyPaint)
                canvas.drawText("• Average period length: ${_settings.value.periodLength} days", 50f, 195f, bodyPaint)
                canvas.drawText("• Current phase: ${getCurrentPhase()}", 50f, 220f, bodyPaint)

                canvas.drawText("Cycle Data", 50f, 280f, titlePaint)
                var yPos = 310f
                repeat(minOf(cycles, 6)) { i ->
                    val date = LocalDate.now().minusDays((cycles - i - 1) * 28L)
                    canvas.drawText(
                        "Cycle ${i + 1}: ${date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                        50f,
                        yPos,
                        bodyPaint
                    )
                    yPos += 25f
                }

                pdfDocument.finishPage(page)

                val fileName = "PeriodSaathi_Report_${System.currentTimeMillis()}.pdf"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }

                pdfDocument.close()

                _exportState.value = ExportState.Success(file.absolutePath)

                delay(3000)
                _exportState.value = ExportState.Idle

            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "Export failed")
                delay(2000)
                _exportState.value = ExportState.Idle
            }
        }
    }

    private fun getCurrentPhase(): String {
        val dayOfCycle = ((LocalDate.now().toEpochDay() % 28) + 28) % 28
        return when (dayOfCycle) {
            in 0..5 -> "Menstrual"
            in 6..13 -> "Follicular"
            in 14..15 -> "Ovulation"
            else -> "Luteal"
        }
    }

    private fun createDefaultReminders(): List<Reminder> = listOf(
        Reminder(id = 1, label = "Period expected", time = "08:00", enabled = true),
        Reminder(id = 2, label = "Log symptoms", time = "21:00", enabled = false)
    )
}

sealed class ExportState {
    data object Idle : ExportState()
    data object Loading : ExportState()
    data class Success(val filePath: String) : ExportState()
    data class Error(val message: String) : ExportState()
}