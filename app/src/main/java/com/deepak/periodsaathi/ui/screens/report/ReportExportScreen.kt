package com.deepak.periodsaathi.ui.screens.report

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun ReportExportScreen(
    viewModel: ReportViewModel = hiltViewModel()
) {
    val selectedCycles by viewModel.selectedCycles.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val cycleDataAvailable by viewModel.cycleDataAvailable.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(exportState) {
        if (exportState is ExportState.Success) {
            val state = exportState as ExportState.Success
            viewModel.shareExport(state.filePath, state.mimeType)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text(text = "Export Report", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        if (!cycleDataAvailable) {
            ReportExportEmptyState()
        } else {
            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Period Saathi Report", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Your health summary for the last $selectedCycles cycles", color = OnSurfaceVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Includes:", fontWeight = FontWeight.SemiBold, color = OnSurface)
                    listOf("Cycle history", "Period dates", "Mood trends", "Water intake", "Symptom patterns").forEach {
                        Text(text = it, color = OnSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Select cycles to include", color = OnSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(3, 6).forEach { cycles ->
                    FilterChip(
                        selected = selectedCycles == cycles,
                        onClick = { viewModel.setCycles(cycles) },
                        label = { Text("$cycles cycles") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlushPink,
                            selectedLabelColor = OnSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.exportPdf() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                enabled = exportState !is ExportState.Loading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (exportState is ExportState.Loading) "Generating..." else "Export PDF")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.exportCsv() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceVariant),
                enabled = exportState !is ExportState.Loading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Export CSV")
            }

            if (exportState is ExportState.Success) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MintGreen.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Export ready! File shared.", color = MintGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (exportState is ExportState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = (exportState as ExportState.Error).message, color = Error, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { viewModel.reset() }) {
                    Text("Dismiss", color = OnSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ReportExportEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "📄", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No data to export",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Track your cycles first to generate\nand export your health report",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
