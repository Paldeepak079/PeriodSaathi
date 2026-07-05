package com.deepak.periodsaathi.ui.screens.settings

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
    val selectedFont by viewModel.selectedFont.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Avatar Card
        var showNameEdit by remember { mutableStateOf(false) }
        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "🌸",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (premiumTier == "FREE") "Free Account" else "Premium Member 👑",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    onClick = { showNameEdit = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                ) {
                    Text(
                        "✏️",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }

        if (showNameEdit) {
            var editName by remember { mutableStateOf(userName) }
            AlertDialog(
                onDismissRequest = { showNameEdit = false },
                title = { Text("Edit Name", color = MaterialTheme.colorScheme.onSurface) },
                text = {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Your Name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateUserName(editName)
                        showNameEdit = false
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showNameEdit = false }) { Text("Cancel") }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Font & Theme Personalization ────────────────────────────────
        SettingsSection("Appearance") {
            Text("Font Style", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FontOption.entries.forEach { font ->
                    val isSelected = font == selectedFont
                    Surface(
                        onClick = { viewModel.updateFont(font) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF7C53B3) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.width(60.dp).height(72.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Aa", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontFamily = rememberFontFamily(font))
                            Spacer(Modifier.height(2.dp))
                            Text(font.displayName, fontSize = 9.sp,
                                color = if (isSelected) Color.White.copy(0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Theme", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeCategory.entries.forEach { theme ->
                    val isSelected = theme == selectedTheme
                    Surface(
                        onClick = { viewModel.updateTheme(theme) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) theme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.width(80.dp).height(72.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(theme.emoji, fontSize = 22.sp)
                            Text(theme.displayName, fontSize = 9.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1)
                            Text(theme.suggestedFont.displayName.split(" ")[0], fontSize = 7.sp,
                                color = if (isSelected) Color.White.copy(0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                                maxLines = 1)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            val previewFace = rememberFontFamily(selectedFont)
            val previewColors = listOf(selectedTheme.primary, selectedTheme.secondary, selectedTheme.tertiary)
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = selectedTheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box( modifier = Modifier.size(28.dp).clip(CircleShape).background(selectedTheme.primary) )
                        Spacer(Modifier.width(10.dp))
                        Text("Period Saathi", fontFamily = previewFace, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = selectedTheme.onSurface)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Your personal wellness companion. Track, understand, and embrace your cycle.",
                        fontFamily = previewFace, fontSize = 12.sp, color = selectedTheme.onSurfaceVariant, lineHeight = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        previewColors.take(3).forEach { color ->
                            Surface(shape = RoundedCornerShape(9999.dp), color = color.copy(0.25f)) {
                                Text("Wellness", fontSize = 10.sp, color = color, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile
        SettingsSection("Profile") {
            var showNameDialog by remember { mutableStateOf(false) }
            var showCycleDialog by remember { mutableStateOf(false) }
            var showPeriodDialog by remember { mutableStateOf(false) }
            var editName by remember { mutableStateOf(userName) }

            SettingsItem(title = "Name", value = userName, onClick = { showNameDialog = true })
            SettingsItem(title = "Cycle Length", value = "$cycleLength days", onClick = { showCycleDialog = true })
            SettingsItem(title = "Period Length", value = "$periodLength days", onClick = { showPeriodDialog = true })

            if (showNameDialog) {
                AlertDialog(
                    onDismissRequest = { showNameDialog = false },
                    title = { Text("Edit Name") },
                    text = {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Name") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.updateUserName(editName)
                            showNameDialog = false
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNameDialog = false }) { Text("Cancel") }
                    }
                )
            }

            if (showCycleDialog) {
                var cycleValue by remember { mutableStateOf(cycleLength.toString()) }
                AlertDialog(
                    onDismissRequest = { showCycleDialog = false },
                    title = { Text("Cycle Length (days)") },
                    text = {
                        OutlinedTextField(
                            value = cycleValue,
                            onValueChange = { cycleValue = it.filter { c -> c.isDigit() } },
                            label = { Text("21-45 days") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val days = cycleValue.toIntOrNull() ?: cycleLength
                            viewModel.updateCycleLength(days.coerceIn(21, 45))
                            showCycleDialog = false
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCycleDialog = false }) { Text("Cancel") }
                    }
                )
            }

            if (showPeriodDialog) {
                var periodValue by remember { mutableStateOf(periodLength.toString()) }
                AlertDialog(
                    onDismissRequest = { showPeriodDialog = false },
                    title = { Text("Period Length (days)") },
                    text = {
                        OutlinedTextField(
                            value = periodValue,
                            onValueChange = { periodValue = it.filter { c -> c.isDigit() } },
                            label = { Text("2-10 days") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val days = periodValue.toIntOrNull() ?: periodLength
                            viewModel.updatePeriodLength(days.coerceIn(2, 10))
                            showPeriodDialog = false
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPeriodDialog = false }) { Text("Cancel") }
                    }
                )
            }
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

        // Widgets
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ctx = LocalContext.current
            SettingsSection("Widgets") {
                SettingsItem(title = "Pin Cycle Day Widget", value = "", onClick = {
                    val manager = ctx.getSystemService(AppWidgetManager::class.java)
                    val provider = ComponentName(ctx, com.deepak.periodsaathi.widget.CycleDayWidgetReceiver::class.java)
                    if (manager.isRequestPinAppWidgetSupported) {
                        manager.requestPinAppWidget(provider, null, null)
                    }
                }, showArrow = true)
                SettingsItem(title = "Pin Countdown Widget", value = "", onClick = {
                    val manager = ctx.getSystemService(AppWidgetManager::class.java)
                    val provider = ComponentName(ctx, com.deepak.periodsaathi.widget.PeriodCountdownWidgetReceiver::class.java)
                    if (manager.isRequestPinAppWidgetSupported) {
                        manager.requestPinAppWidget(provider, null, null)
                    }
                }, showArrow = true)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Legal
        SettingsSection("Legal") {
            val ctx = LocalContext.current
            SettingsItem(title = "Privacy Policy", value = "", onClick = {
                android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://periodsaathi.app/privacy")).let {
                    ctx.startActivity(it)
                }
            })
            SettingsItem(title = "Medical Disclaimer", value = "", onClick = {
                androidx.appcompat.app.AlertDialog.Builder(ctx)
                    .setTitle("Medical Disclaimer")
                    .setMessage("This app provides general health information for educational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or qualified health provider with any questions you may have regarding a medical condition.")
                    .setPositiveButton("OK", null)
                    .show()
            })
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
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Out")
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete All Data?", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("This action cannot be undone.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteAllData(); showDeleteDialog = false }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun SettingsSection(title: String, isDanger: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
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
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = MaterialTheme.colorScheme.onSurface)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotEmpty()) Text(text = value, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (showArrow) Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
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
            Text(text = title, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle.isNotEmpty()) Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        )
    }
}
