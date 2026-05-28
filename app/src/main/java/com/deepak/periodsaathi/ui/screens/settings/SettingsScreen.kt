package com.deepak.periodsaathi.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
fun SettingsScreen(
    onNavigateToPayment: () -> Unit = {},
    onNavigateToReport: () -> Unit = {},
    onSignOut: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val cycleLength by viewModel.cycleLength.collectAsState()
    val periodLength by viewModel.periodLength.collectAsState()
    val stealthMode by viewModel.stealthMode.collectAsState()
    val biometricLock by viewModel.biometricLock.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val premiumTier by viewModel.premiumTier.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        Spacer(modifier = Modifier.height(24.dp))

        // Profile
        SettingsSection("Profile") {
            SettingsItem(title = "Name", value = userName, onClick = { })
            SettingsItem(title = "Cycle Length", value = "$cycleLength days", onClick = { })
            SettingsItem(title = "Period Length", value = "$periodLength days", onClick = { })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy
        SettingsSection("Privacy") {
            SettingsToggle(title = "Stealth Mode", subtitle = "Hide app from launcher", checked = stealthMode, onToggle = { viewModel.toggleStealthMode() })
            SettingsToggle(title = "Biometric Lock", subtitle = "Require fingerprint/face", checked = biometricLock, onToggle = { viewModel.toggleBiometricLock() })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sound & Haptics
        SettingsSection("Sound & Haptics") {
            SettingsToggle(title = "Sound", checked = soundEnabled, onToggle = { viewModel.toggleSound() })
            SettingsToggle(title = "Haptic Feedback", checked = hapticEnabled, onToggle = { viewModel.toggleHaptic() })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Premium
        SettingsSection("Premium") {
            SettingsItem(
                title = "Current Plan",
                value = premiumTier,
                onClick = onNavigateToPayment,
                showArrow = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data
        SettingsSection("Data") {
            SettingsItem(title = "Export Data (CSV)", value = "", onClick = onNavigateToReport, showArrow = true)
            SettingsItem(title = "Export PDF Report", value = "", onClick = onNavigateToReport, showArrow = true)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legal
        SettingsSection("Legal") {
            SettingsItem(title = "Privacy Policy", value = "", onClick = { })
            SettingsItem(title = "Medical Disclaimer", value = "", onClick = { })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danger zone
        SettingsSection("Danger Zone", isDanger = true) {
            TextButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Delete All Data", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { viewModel.signOut(onSignOut) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Out")
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete All Data?", color = OnSurface) },
            text = { Text("This action cannot be undone.", color = OnSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteAllData(); showDeleteDialog = false }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Surface
        )
    }
}

@Composable
private fun SettingsSection(title: String, isDanger: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (isDanger) Color.Red else SoftLavender)
        Spacer(modifier = Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(8.dp), content = content)
        }
    }
}

@Composable
private fun SettingsItem(title: String, value: String, onClick: () -> Unit, showArrow: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = OnSurface)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotEmpty()) Text(text = value, color = OnSurfaceVariant)
            if (showArrow) Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = SoftLavender, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun SettingsToggle(title: String, subtitle: String = "", checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, color = OnSurface)
            if (subtitle.isNotEmpty()) Text(text = subtitle, color = OnSurfaceVariant, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = BlushPink, checkedTrackColor = BlushPink.copy(alpha = 0.5f))
        )
    }
}
