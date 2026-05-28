package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.PrimaryButton
import com.deepak.periodsaathi.ui.theme.OnSurface
import com.deepak.periodsaathi.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizedCarePopup(
    currentPainType: String,
    currentPainLevel: String,
    currentFlowLevel: String,
    currentEnergyLevel: String,
    onDismiss: () -> Unit,
    onSave: (painType: String, painLevel: String, flowLevel: String, energyLevel: String) -> Unit
) {
    var painType by remember { mutableStateOf(currentPainType) }
    var painLevel by remember { mutableStateOf(currentPainLevel) }
    var flowLevel by remember { mutableStateOf(currentFlowLevel) }
    var energyLevel by remember { mutableStateOf(currentEnergyLevel) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personalized Care Plan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "Help Saathi personalize your ayurvedic tips, diet plans, and daily recovery routines.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // 1. Pain Type Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pain Area", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("None", "Cramps", "Headache", "Backache", "Nausea").forEach { type ->
                        FilterChip(
                            selected = painType == type,
                            onClick = { painType = type },
                            label = { Text(type) }
                        )
                    }
                }
            }

            // 2. Pain Intensity Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pain Intensity", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Mild", "Moderate", "Severe").forEach { level ->
                        FilterChip(
                            selected = painLevel == level,
                            onClick = { painLevel = level },
                            label = { Text(level) }
                        )
                    }
                }
            }

            // 3. Flow Level Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Flow Intensity", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Light", "Medium", "Heavy").forEach { level ->
                        FilterChip(
                            selected = flowLevel == level,
                            onClick = { flowLevel = level },
                            label = { Text(level) }
                        )
                    }
                }
            }

            // 4. Energy Level Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Energy Level", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Low", "Normal", "High").forEach { level ->
                        FilterChip(
                            selected = energyLevel == level,
                            onClick = { energyLevel = level },
                            label = { Text(level) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Apply Care Suggestions",
                onClick = { onSave(painType, painLevel, flowLevel, energyLevel) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
