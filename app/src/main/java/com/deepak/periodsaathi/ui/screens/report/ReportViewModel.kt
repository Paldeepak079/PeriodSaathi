package com.deepak.periodsaathi.ui.screens.report

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.R
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.model.CycleSettings
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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
    private val headerDateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

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
            val span = Sentry.startTransaction("exportPdf", "task")
            try {
                val cycles = cycleRepository.getLastNCycles(_selectedCycles.value).first()
                val settings = cycleRepository.getSettings().first()
                if (cycles.isEmpty()) {
                    _exportState.value = ExportState.Error("No cycle data available")
                    return@launch
                }
                val file = withContext(Dispatchers.IO) {
                    generatePremiumPdf(cycles, settings)
                }
                span.status = io.sentry.SpanStatus.OK
                _exportState.value = ExportState.Success(file.absolutePath, "application/pdf")
            } catch (e: Exception) {
                span.status = io.sentry.SpanStatus.INTERNAL_ERROR
                span.throwable = e
                _exportState.value = ExportState.Error(e.message ?: "Failed to generate PDF")
            } finally {
                span.finish()
            }
        }
    }

    fun exportCsv() {
        _exportState.value = ExportState.Loading
        viewModelScope.launch {
            val span = Sentry.startTransaction("exportCsv", "task")
            try {
                val cycles = cycleRepository.getLastNCycles(_selectedCycles.value).first()
                val settings = cycleRepository.getSettings().first()
                if (cycles.isEmpty()) {
                    _exportState.value = ExportState.Error("No cycle data available")
                    return@launch
                }
                val file = withContext(Dispatchers.IO) {
                    generateCsv(cycles, settings.userName)
                }
                span.status = io.sentry.SpanStatus.OK
                _exportState.value = ExportState.Success(file.absolutePath, "text/csv")
            } catch (e: Exception) {
                span.status = io.sentry.SpanStatus.INTERNAL_ERROR
                span.throwable = e
                _exportState.value = ExportState.Error(e.message ?: "Failed to generate CSV")
            } finally {
                span.finish()
            }
        }
    }

    fun shareExport(filePath: String, mimeType: String) {
        try {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share Report").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            _exportState.value = ExportState.Error(e.message ?: "Failed to share file")
        }
    }

    fun reset() { _exportState.value = ExportState.Idle }

    // ── Brand Colors ──────────────────────────────────────────────────────

    private val PrimaryRose = DeviceRgb(0x87, 0x4E, 0x58)
    private val SoftRose = DeviceRgb(0xFF, 0xB6, 0xC1)
    private val WarmCream = DeviceRgb(0xFF, 0xF8, 0xF2)
    private val LightPink = DeviceRgb(0xFF, 0xE8, 0xE1)
    private val Lavender = DeviceRgb(0x65, 0x57, 0x81)
    private val DeepText = DeviceRgb(0x1E, 0x1B, 0x18)
    private val SubText = DeviceRgb(0x51, 0x43, 0x45)
    private val White = DeviceRgb(0xFF, 0xFF, 0xFF)
    private val DividerColor = DeviceRgb(0xD6, 0xC2, 0xC3)
    private val BabyBlue = DeviceRgb(0x42, 0x61, 0x7D)
    private val MintGreen = DeviceRgb(0xB8, 0xF0, 0xDC)
    private val ButterYellow = DeviceRgb(0xFF, 0xF3, 0xB0)

    // ── PDF Generation ────────────────────────────────────────────────────

    private fun generatePremiumPdf(cycles: List<List<CycleEntry>>, settings: CycleSettings): File {
        val file = File(context.cacheDir, "PeriodSaathi_Report.pdf")
        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)

        val pageSize = PageSize.A4
        pdf.defaultPageSize = pageSize

        val document = Document(pdf, pageSize)
        document.setMargins(50f, 40f, 60f, 40f)

        // Watermark handler
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, WatermarkHandler())

        // ── HEADER ────────────────────────────────────────────────────────
        addBrandedHeader(document, settings.userName)

        // ── CYCLE OVERVIEW ────────────────────────────────────────────────
        addCycleOverview(document, cycles, settings)

        // ── FLOW SUMMARY ──────────────────────────────────────────────────
        addFlowSummary(document, cycles)

        // ── MOOD & SYMPTOMS ───────────────────────────────────────────────
        addMoodSymptomSummary(document, cycles)

        // ── HYDRATION ─────────────────────────────────────────────────────
        addHydrationSummary(document, cycles)

        // ── CYCLE HISTORY ─────────────────────────────────────────────────
        addCycleHistory(document, cycles)

        // ── NOTES ─────────────────────────────────────────────────────────
        addNotesSection(document, cycles)

        // ── FOOTER ────────────────────────────────────────────────────────
        addFooter(document)

        document.close()
        return file
    }

    // ── HEADER ────────────────────────────────────────────────────────────

    private fun addBrandedHeader(document: Document, userName: String) {
        // Pink accent bar at top
        val accentBar = Table(UnitValue.createPercentArray(floatArrayOf(100f)))
        accentBar.setWidth(UnitValue.createPercentValue(100f))
        accentBar.addCell(
            Cell()
                .setBackgroundColor(PrimaryRose)
                .setHeight(6f)
                .setBorder(Border.NO_BORDER)
        )
        document.add(accentBar)
        document.add(Paragraph(" "))

        // Title
        val title = Paragraph("Period Saathi")
            .setFontSize(28f)
            .setFontColor(PrimaryRose)
            .setBold()
            .setTextAlignment(TextAlignment.LEFT)
        document.add(title)

        val subtitle = Paragraph("Health Report")
            .setFontSize(16f)
            .setFontColor(Lavender)
            .setTextAlignment(TextAlignment.LEFT)
        document.add(subtitle)

        document.add(Paragraph(" "))

        // Summary card
        val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
        summaryTable.setWidth(UnitValue.createPercentValue(100f))

        val nameCell = Cell()
            .setBackgroundColor(WarmCream)
            .setBorder(Border.NO_BORDER)
            .setPadding(12f)
        nameCell.add(Paragraph("Prepared for").setFontSize(9f).setFontColor(SubText))
        nameCell.add(Paragraph(userName).setFontSize(14f).setFontColor(DeepText).setBold())

        val dateCell = Cell()
            .setBackgroundColor(WarmCream)
            .setBorder(Border.NO_BORDER)
            .setPadding(12f)
        dateCell.add(Paragraph("Report Date").setFontSize(9f).setFontColor(SubText))
        dateCell.add(Paragraph(dateFormat.format(Date())).setFontSize(14f).setFontColor(DeepText).setBold())

        summaryTable.addCell(nameCell)
        summaryTable.addCell(dateCell)
        document.add(summaryTable)
        document.add(Paragraph(" "))

        // Divider
        val solidLine = SolidLine(0.5f)
        solidLine.setColor(DividerColor)
        val divider = LineSeparator(solidLine)
        document.add(divider)
        document.add(Paragraph(" "))
    }

    // ── CYCLE OVERVIEW ────────────────────────────────────────────────────

    private fun addCycleOverview(document: Document, cycles: List<List<CycleEntry>>, settings: CycleSettings) {
        addSectionTitle(document, "Cycle Overview")

        val allEntries = cycles.flatten()
        if (allEntries.isEmpty()) {
            document.add(Paragraph("No cycle data recorded yet.")
                .setFontSize(11f).setFontColor(SubText))
            document.add(Paragraph(" "))
            return
        }

        val avgCycleLen = settings.averageCycleLength
        val avgPeriodLen = settings.averagePeriodLength
        val totalDaysTracked = allEntries.size
        val uniqueDates = allEntries.map { entry ->
            Instant.ofEpochMilli(entry.date).atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSet().size

        val table = Table(UnitValue.createPercentArray(floatArrayOf(25f, 25f, 25f, 25f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        val labels = listOf("Avg Cycle", "Avg Period", "Cycles Tracked", "Days Logged")
        val values = listOf("${avgCycleLen}d", "${avgPeriodLen}d", "${cycles.size}", "$uniqueDates")
        val emojis = listOf("🔄", "🩸", "📊", "📅")

        for (i in 0..3) {
            val cell = Cell()
                .setBackgroundColor(WarmCream)
                .setBorder(Border.NO_BORDER)
                .setPadding(10f)
                .setTextAlignment(TextAlignment.CENTER)
            cell.add(Paragraph(emojis[i]).setFontSize(18f).setTextAlignment(TextAlignment.CENTER))
            cell.add(Paragraph(values[i]).setFontSize(16f).setFontColor(PrimaryRose).setBold().setTextAlignment(TextAlignment.CENTER))
            cell.add(Paragraph(labels[i]).setFontSize(8f).setFontColor(SubText).setTextAlignment(TextAlignment.CENTER))
            table.addCell(cell)
        }

        document.add(table)
        document.add(Paragraph(" "))
    }

    // ── FLOW SUMMARY ──────────────────────────────────────────────────────

    private fun addFlowSummary(document: Document, cycles: List<List<CycleEntry>>) {
        addSectionTitle(document, "Flow Summary")

        val allEntries = cycles.flatten()
        val flowEntries = allEntries.filter { it.flowIntensity != null }

        if (flowEntries.isEmpty()) {
            document.add(Paragraph("No flow data recorded.")
                .setFontSize(11f).setFontColor(SubText))
            document.add(Paragraph(" "))
            return
        }

        val lightCount = flowEntries.count { it.flowIntensity == "Light" }
        val mediumCount = flowEntries.count { it.flowIntensity == "Medium" }
        val heavyCount = flowEntries.count { it.flowIntensity == "Heavy" }
        val total = flowEntries.size

        val table = Table(UnitValue.createPercentArray(floatArrayOf(33.33f, 33.33f, 33.34f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        data class FlowData(val label: String, val count: Int, val color: Color)
        val flows = listOf(
            FlowData("Light", lightCount, MintGreen),
            FlowData("Medium", mediumCount, ButterYellow),
            FlowData("Heavy", heavyCount, SoftRose)
        )

        for (flow in flows) {
            val pct = if (total > 0) (flow.count * 100 / total) else 0
            val cell = Cell()
                .setBackgroundColor(flow.color)
                .setBorder(Border.NO_BORDER)
                .setPadding(12f)
                .setTextAlignment(TextAlignment.CENTER)
            cell.add(Paragraph("${flow.count} days").setFontSize(18f).setFontColor(DeepText).setBold().setTextAlignment(TextAlignment.CENTER))
            cell.add(Paragraph("${flow.label} ($pct%)").setFontSize(10f).setFontColor(SubText).setTextAlignment(TextAlignment.CENTER))
            table.addCell(cell)
        }

        document.add(table)
        document.add(Paragraph(" "))
    }

    // ── MOOD & SYMPTOMS ───────────────────────────────────────────────────

    private fun addMoodSymptomSummary(document: Document, cycles: List<List<CycleEntry>>) {
        addSectionTitle(document, "Mood & Symptom Trends")

        val allEntries = cycles.flatten()
        val moodEntries = allEntries.filter { !it.mood.isNullOrBlank() }
        val symptomEntries = allEntries.filter { it.symptoms.isNotBlank() && it.symptoms != "[]" }

        // Mood summary
        if (moodEntries.isNotEmpty()) {
            val moodCounts = moodEntries.groupingBy { it.mood!! }.eachCount()
            val topMoods = moodCounts.entries.sortedByDescending { it.value }.take(5)

            val moodTable = Table(UnitValue.createPercentArray(floatArrayOf(20f, 20f, 20f, 20f, 20f)))
            moodTable.setWidth(UnitValue.createPercentValue(100f))

            val moodEmojis = mapOf(
                "Happy" to "😊", "Sad" to "😢", "Angry" to "😠",
                "Anxious" to "😰", "Calm" to "😌", "Tired" to "😴",
                "Energetic" to "⚡", "Mood Swings" to "🎭",
                "Irritable" to "😤", "Content" to "☺️"
            )

            for (i in 0 until 5) {
                val cell = Cell()
                    .setBackgroundColor(WarmCream)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(8f)
                    .setTextAlignment(TextAlignment.CENTER)
                if (i < topMoods.size) {
                    val mood = topMoods[i]
                    val emoji = moodEmojis[mood.key] ?: "🎭"
                    cell.add(Paragraph(emoji).setFontSize(20f).setTextAlignment(TextAlignment.CENTER))
                    cell.add(Paragraph(mood.key).setFontSize(9f).setFontColor(DeepText).setBold().setTextAlignment(TextAlignment.CENTER))
                    cell.add(Paragraph("${mood.value}x").setFontSize(8f).setFontColor(Lavender).setTextAlignment(TextAlignment.CENTER))
                } else {
                    cell.add(Paragraph("").setHeight(40f))
                }
                moodTable.addCell(cell)
            }
            document.add(moodTable)
        } else {
            document.add(Paragraph("No mood data recorded.")
                .setFontSize(11f).setFontColor(SubText))
        }

        document.add(Paragraph(" "))

        // Symptoms
        if (symptomEntries.isNotEmpty()) {
            val allSymptoms = mutableListOf<String>()
            for (entry in symptomEntries) {
                val symptomList = entry.symptoms
                    .removePrefix("[").removeSuffix("]")
                    .split(",").map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("accuracy_") }
                allSymptoms.addAll(symptomList)
            }
            val symptomCounts = allSymptoms.groupingBy { it }.eachCount()
            val topSymptoms = symptomCounts.entries.sortedByDescending { it.value }.take(8)

            val symptomTable = Table(UnitValue.createPercentArray(floatArrayOf(25f, 25f, 25f, 25f)))
            symptomTable.setWidth(UnitValue.createPercentValue(100f))

            var row = Cell().setBorder(Border.NO_BORDER).setPadding(6f)
            var colCount = 0
            for ((symptom, count) in topSymptoms) {
                val cell = Cell()
                    .setBackgroundColor(LightPink)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(8f)
                    .setTextAlignment(TextAlignment.CENTER)
                cell.add(Paragraph(symptom).setFontSize(9f).setFontColor(DeepText).setBold().setTextAlignment(TextAlignment.CENTER))
                cell.add(Paragraph("($count)").setFontSize(8f).setFontColor(Lavender).setTextAlignment(TextAlignment.CENTER))
                symptomTable.addCell(cell)
                colCount++
                if (colCount == 4) { colCount = 0 }
            }
            while (colCount != 0 && colCount < 4) {
                symptomTable.addCell(Cell().setBorder(Border.NO_BORDER).setHeight(30f))
                colCount++
            }
            document.add(Paragraph("Top Symptoms:").setFontSize(10f).setFontColor(DeepText).setBold())
            document.add(symptomTable)
        } else {
            document.add(Paragraph("No symptom data recorded.")
                .setFontSize(11f).setFontColor(SubText))
        }

        document.add(Paragraph(" "))
    }

    // ── HYDRATION ─────────────────────────────────────────────────────────

    private fun addHydrationSummary(document: Document, cycles: List<List<CycleEntry>>) {
        addSectionTitle(document, "Hydration Tracker")

        val allEntries = cycles.flatten()
        val waterEntries = allEntries.filter { it.waterGlasses > 0 }

        if (waterEntries.isEmpty()) {
            document.add(Paragraph("No water intake data recorded.")
                .setFontSize(11f).setFontColor(SubText))
            document.add(Paragraph(" "))
            return
        }

        val avgWater = waterEntries.map { it.waterGlasses }.average()
        val maxWater = waterEntries.maxOf { it.waterGlasses }
        val daysGoalMet = waterEntries.count { it.waterGlasses >= 8 }

        val table = Table(UnitValue.createPercentArray(floatArrayOf(33.33f, 33.33f, 33.34f)))
        table.setWidth(UnitValue.createPercentValue(100f))

        val metrics = listOf(
            Triple("💧", String.format("%.1f", avgWater), "Avg glasses/day"),
            Triple("🏆", "$maxWater", "Best day"),
            Triple("✅", "$daysGoalMet", "Goal met (8+)")
        )

        for ((emoji, value, label) in metrics) {
            val cell = Cell()
                .setBackgroundColor(WarmCream)
                .setBorder(Border.NO_BORDER)
                .setPadding(12f)
                .setTextAlignment(TextAlignment.CENTER)
            cell.add(Paragraph(emoji).setFontSize(22f).setTextAlignment(TextAlignment.CENTER))
            cell.add(Paragraph(value).setFontSize(18f).setFontColor(BabyBlue).setBold().setTextAlignment(TextAlignment.CENTER))
            cell.add(Paragraph(label).setFontSize(9f).setFontColor(SubText).setTextAlignment(TextAlignment.CENTER))
            table.addCell(cell)
        }

        document.add(table)
        document.add(Paragraph(" "))
    }

    // ── CYCLE HISTORY ─────────────────────────────────────────────────────

    private fun addCycleHistory(document: Document, cycles: List<List<CycleEntry>>) {
        addSectionTitle(document, "Cycle History")

        if (cycles.isEmpty()) {
            document.add(Paragraph("No cycle history available.")
                .setFontSize(11f).setFontColor(SubText))
            document.add(Paragraph(" "))
            return
        }

        var cycleNum = 1
        for (cycle in cycles.reversed()) {
            if (cycle.isEmpty()) continue

            val startDate = Instant.ofEpochMilli(cycle.first().date)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val endDate = Instant.ofEpochMilli(cycle.last().date)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val cycleDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
            val periodDays = cycle.count { it.flowIntensity != null }

            // Cycle header
            val cycleHeader = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            cycleHeader.setWidth(UnitValue.createPercentValue(100f))

            val labelCell = Cell()
                .setBackgroundColor(PrimaryRose)
                .setBorder(Border.NO_BORDER)
                .setPadding(8f)
            labelCell.add(Paragraph("Cycle $cycleNum").setFontSize(12f).setFontColor(White).setBold())
            labelCell.add(Paragraph("${dateFormat.format(Date(cycle.first().date))} — ${dateFormat.format(Date(cycle.last().date))}")
                .setFontSize(8f).setFontColor(DeviceRgb(0xCC, 0xFF, 0xFF)))

            val statsCell = Cell()
                .setBackgroundColor(WarmCream)
                .setBorder(Border.NO_BORDER)
                .setPadding(8f)
                .setTextAlignment(TextAlignment.RIGHT)
            statsCell.add(Paragraph("${cycleDays} days total").setFontSize(10f).setFontColor(DeepText).setBold().setTextAlignment(TextAlignment.RIGHT))
            statsCell.add(Paragraph("${periodDays} period days").setFontSize(9f).setFontColor(Lavender).setTextAlignment(TextAlignment.RIGHT))

            cycleHeader.addCell(labelCell)
            cycleHeader.addCell(statsCell)
            document.add(cycleHeader)

            // Day-by-day visual timeline
            val daysPerRow = 7
            val rows = (cycle.size + daysPerRow - 1) / daysPerRow
            val timelineTable = Table(UnitValue.createPercentArray(FloatArray(daysPerRow) { 100f / daysPerRow }))
            timelineTable.setWidth(UnitValue.createPercentValue(100f))

            for (row in 0 until rows) {
                for (col in 0 until daysPerRow) {
                    val idx = row * daysPerRow + col
                    val cell = Cell()
                        .setBorder(Border.NO_BORDER)
                        .setPadding(3f)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setHeight(40f)

                    if (idx < cycle.size) {
                        val entry = cycle[idx]
                        val entryDate = Instant.ofEpochMilli(entry.date)
                            .atZone(ZoneId.systemDefault()).toLocalDate()

                        val bgColor = when {
                            entry.flowIntensity == "Heavy" -> SoftRose
                            entry.flowIntensity == "Medium" -> ButterYellow
                            entry.flowIntensity == "Light" -> MintGreen
                            entry.mood != null -> WarmCream
                            else -> White
                        }
                        cell.setBackgroundColor(bgColor)

                        val dayNum = entryDate.dayOfMonth.toString()
                        cell.add(Paragraph(dayNum).setFontSize(9f).setFontColor(DeepText).setBold().setTextAlignment(TextAlignment.CENTER))

                        if (entry.flowIntensity != null) {
                            cell.add(Paragraph(entry.flowIntensity!!.first().toString()).setFontSize(7f).setFontColor(Lavender).setTextAlignment(TextAlignment.CENTER))
                        } else if (entry.mood != null) {
                            val moodEmoji = when (entry.mood) {
                                "Happy" -> "😊"
                                "Sad" -> "😢"
                                "Angry" -> "😠"
                                "Anxious" -> "😰"
                                "Calm" -> "😌"
                                "Tired" -> "😴"
                                "Energetic" -> "⚡"
                                "Mood Swings" -> "🎭"
                                "Irritable" -> "😤"
                                "Content" -> "☺️"
                                else -> "🎭"
                            }
                            cell.add(Paragraph(moodEmoji).setFontSize(8f).setTextAlignment(TextAlignment.CENTER))
                        }
                    } else {
                        cell.setBackgroundColor(White)
                    }
                    timelineTable.addCell(cell)
                }
            }
            document.add(timelineTable)
            document.add(Paragraph(" "))
            cycleNum++
        }
    }

    // ── NOTES ─────────────────────────────────────────────────────────────

    private fun addNotesSection(document: Document, cycles: List<List<CycleEntry>>) {
        addSectionTitle(document, "Your Notes")

        val allEntries = cycles.flatten()
        val notesEntries = allEntries.filter { !it.notes.isNullOrBlank() }

        if (notesEntries.isEmpty()) {
            document.add(Paragraph("No notes recorded for this period.")
                .setFontSize(11f).setFontColor(SubText))
            document.add(Paragraph(" "))
            return
        }

        for (entry in notesEntries.take(20)) {
            val noteDate = dateFormat.format(Date(entry.date))

            val noteTable = Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            noteTable.setWidth(UnitValue.createPercentValue(100f))

            val noteCell = Cell()
                .setBackgroundColor(WarmCream)
                .setBorder(Border.NO_BORDER)
                .setPadding(10f)
            noteCell.add(Paragraph(noteDate).setFontSize(8f).setFontColor(Lavender).setBold())
            noteCell.add(Paragraph(entry.notes!!).setFontSize(10f).setFontColor(DeepText))
            noteTable.addCell(noteCell)

            document.add(noteTable)
            document.add(Paragraph(" "))
        }
    }

    // ── FOOTER ────────────────────────────────────────────────────────────

    private fun addFooter(document: Document) {
        document.add(Paragraph(" "))

        val solidLine2 = SolidLine(0.5f)
        solidLine2.setColor(DividerColor)
        val divider = LineSeparator(solidLine2)
        document.add(divider)
        document.add(Paragraph(" "))

        val disclaimer = Paragraph(
            "This report is for personal wellness tracking only and is not a medical diagnosis. " +
            "Please consult a healthcare professional for any health concerns."
        )
            .setFontSize(8f)
            .setFontColor(SubText)
            .setTextAlignment(TextAlignment.CENTER)
        document.add(disclaimer)

        document.add(Paragraph("Generated by Period Saathi")
            .setFontSize(8f)
            .setFontColor(PrimaryRose)
            .setTextAlignment(TextAlignment.CENTER))
    }

    // ── HELPERS ───────────────────────────────────────────────────────────

    private fun addSectionTitle(document: Document, title: String) {
        val titleTable = Table(UnitValue.createPercentArray(floatArrayOf(4f, 96f)))
        titleTable.setWidth(UnitValue.createPercentValue(100f))

        val barCell = Cell()
            .setBackgroundColor(PrimaryRose)
            .setBorder(Border.NO_BORDER)
            .setHeight(20f)
            .setWidth(4f)

        val textCell = Cell()
            .setBorder(Border.NO_BORDER)
            .setPadding(4f)
        textCell.add(Paragraph(title).setFontSize(14f).setFontColor(PrimaryRose).setBold())

        titleTable.addCell(barCell)
        titleTable.addCell(textCell)
        document.add(titleTable)
        document.add(Paragraph(" "))
    }

    // ── WATERMARK ─────────────────────────────────────────────────────────

    private inner class WatermarkHandler : IEventHandler {
        override fun handleEvent(event: Event) {
            val docEvent = event as PdfDocumentEvent
            val page = docEvent.page
            val pdfDoc = docEvent.document
            val pageNumber = pdfDoc.getPageNumber(page)
            val pageSize = page.pageSize

            val canvas = PdfCanvas(page.newContentStreamBefore(), page.resources, pdfDoc)

            // Full-page cream background
            canvas.saveState()
            canvas.setFillColor(WarmCream)
            canvas.rectangle(0.0, 0.0, pageSize.width.toDouble(), pageSize.height.toDouble())
            canvas.fill()
            canvas.restoreState()

            // Subtle rotated watermark text
            canvas.saveState()
            canvas.beginText()
            canvas.setFontAndSize(PdfFontFactory.createFont(), 44f)
            canvas.setColor(SoftRose, true)
            val cx = pageSize.width / 2f
            val cy = pageSize.height / 2f
            val angle = -30.0 * Math.PI / 180.0
            val cos = Math.cos(angle).toFloat()
            val sin = Math.sin(angle).toFloat()
            canvas.setTextMatrix(cos, sin, -sin, cos, cx - 120f, cy)
            canvas.showText("Period Saathi")
            canvas.endText()
            canvas.restoreState()

            // Page number at bottom center
            canvas.saveState()
            canvas.beginText()
            canvas.setFontAndSize(PdfFontFactory.createFont(), 8f)
            canvas.setColor(SubText, true)
            canvas.setTextMatrix(pageSize.width / 2f - 5f, 25f)
            canvas.showText("$pageNumber")
            canvas.endText()
            canvas.restoreState()

            canvas.release()
        }
    }

    // ── CSV ───────────────────────────────────────────────────────────────

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
}
