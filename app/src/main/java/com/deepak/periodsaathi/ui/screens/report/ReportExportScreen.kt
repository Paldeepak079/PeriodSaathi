package com.deepak.periodsaathi.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text(text = "Export Report", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        Spacer(modifier = Modifier.height(24.dp))

        // Report preview
        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "ðŸ“Š Period Saathi Report", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Your health summary for the last $selectedCycles cycles", color = OnSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Includes:", fontWeight = FontWeight.SemiBold, color = OnSurface)
                listOf("ðŸ“… Cycle history", "ðŸ©¸ Period dates", "ðŸ˜Š Mood trends", "ðŸ’§ Water intake", "ðŸ“ˆ Symptom patterns").forEach {
                    Text(text = it, color = OnSurfaceVariant, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cycle selector
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

        // Export buttons
        Button(
            onClick = { viewModel.exportPdf() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
            enabled = exportState !is ExportState.Loading,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = if (exportState is ExportState.Loading) "Generating..." else "Export PDF ðŸ“„")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { viewModel.exportCsv() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceVariant),
            enabled = exportState !is ExportState.Loading,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Export CSV ðŸ“Š")
        }

        // Success state
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
                    Text(text = "âœ…", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Export ready!", color = MintGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

