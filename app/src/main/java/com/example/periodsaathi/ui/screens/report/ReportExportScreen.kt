package com.example.periodsaathi.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun ReportExportScreen(viewModel: ReportViewModel = hiltViewModel()) {
    val exportState by viewModel.exportState.collectAsStateWithLifecycle()
    val selectedCycles by viewModel.selectedCycles.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(WarmCream)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Export Report 📄", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
            Spacer(modifier = Modifier.height(24.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📋 Period Saathi Report", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Last $selectedCycles cycles", color = OnSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Include in report:", fontWeight = FontWeight.Bold, color = OnSurface)
            Spacer(modifier = Modifier.height(8.dp))
            listOf("Cycle dates", "Symptom log", "Mood tracking", "Fertile window").forEach { item ->
                Text(text = "✓ $item", color = OnSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(24.dp))
            when (exportState) {
                ExportState.Idle -> {
                    SpringBounceButton(text = "Export as PDF", onClick = { viewModel.exportPdf() }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    SpringBounceButton(text = "Export as CSV", onClick = { viewModel.exportCsv() }, modifier = Modifier.fillMaxWidth(), backgroundColor = Secondary)
                }
                ExportState.Loading -> CircularProgressIndicator(color = Primary)
                ExportState.Success -> Text(text = "✅ Export Complete!", fontWeight = FontWeight.Bold, color = MintGreen)
            }
        }
    }
}