package com.example.periodsaathi.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val cycleLength by viewModel.cycleLength.collectAsStateWithLifecycle()
    val periodLength by viewModel.periodLength.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val haptics by viewModel.haptics.collectAsStateWithLifecycle()
    val biometric by viewModel.biometric.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(WarmCream)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text(text = "Settings ⚙️", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary) }

            item { SettingsSection(title = "Cycle", items = listOf(
                SettingsItem("Cycle Length", "$cycleLength days"),
                SettingsItem("Period Length", "$periodLength days")
            ))}

            item { SettingsSection(title = "Notifications", items = listOf(
                SettingsToggleItem("Push Notifications", notifications) { viewModel.toggleNotifications() },
                SettingsToggleItem("Haptic Feedback", haptics) { viewModel.toggleHaptics() }
            ))}

            item { SettingsSection(title = "Security", items = listOf(
                SettingsToggleItem("Biometric Lock", biometric) { viewModel.toggleBiometric() },
                SettingsItem("Privacy Policy", "View"),
                SettingsItem("Terms of Service", "View")
            ))}

            item { SettingsSection(title = "Data", items = listOf(
                SettingsItem("Export Data (CSV)", "Export"),
                SettingsItem("Export Data (PDF)", "Export"),
                SettingsItem("Delete All Data", "Delete", isDangerous = true)
            ))}
        }
    }
}

@Composable
private fun SettingsSection(title: String, items: List<Any>) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(12.dp))
            items.forEach { item ->
                when (item) {
                    is SettingsItem -> { Text(text = "${item.label}: ${item.value}", color = if (item.isDangerous) Error else OnSurface) }
                    is SettingsToggleItem -> { Text(text = item.label, color = OnSurface) }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

data class SettingsItem(val label: String, val value: String, val isDangerous: Boolean = false)
data class SettingsToggleItem(val label: String, val value: Boolean, val onToggle: () -> Unit)