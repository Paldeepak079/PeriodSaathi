package com.example.periodsaathi.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.theme.BlushPink

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToExport: () -> Unit = {}
) {
    val settings by viewModel.settings.collectAsState()
    val reminders by viewModel.reminders.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F0E6)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings ⚙️",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )
        }

        item {
            ProfileSection(
                userName = settings.userName,
                cycleLength = settings.cycleLength,
                periodLength = settings.periodLength,
                onNameChange = { viewModel.updateUserName(it) },
                onCycleLengthChange = { viewModel.updateCycleLength(it) },
                onPeriodLengthChange = { viewModel.updatePeriodLength(it) }
            )
        }

        item {
            PrivacySection(
                stealthModeEnabled = settings.stealthModeEnabled,
                onStealthToggle = { viewModel.showStealthPinDialog(true) }
            )
        }

        item {
            RemindersSection(
                reminders = reminders,
                onToggleReminder = { viewModel.toggleReminderEnabled(it) },
                onDeleteReminder = { viewModel.deleteReminder(it) },
                onAddReminder = { viewModel.showAddReminderDialog(true) }
            )
        }

        item {
            SoundHapticsSection(
                soundsEnabled = settings.soundsEnabled,
                hapticsEnabled = settings.hapticsEnabled,
                onSoundsToggle = { viewModel.toggleSounds(it) },
                onHapticsToggle = { viewModel.toggleHaptics(it) }
            )
        }

        item {
            NotificationsSection(
                partnerEnabled = settings.partnerNotificationsEnabled,
                predictionEnabled = settings.predictionRemindersEnabled,
                customEnabled = settings.customRemindersEnabled,
                onPartnerToggle = { viewModel.togglePartnerNotifications(it) },
                onPredictionToggle = { viewModel.togglePredictionReminders(it) },
                onCustomToggle = { viewModel.toggleCustomReminders(it) }
            )
        }

        item {
            ExportSection(onExportClick = onNavigateToExport)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (viewModel.showStealthPinDialog.value) {
        StealthPinDialog(
            onDismiss = { viewModel.showStealthPinDialog(false) },
            onConfirm = { pin ->
                viewModel.toggleStealthMode(true, pin)
                viewModel.showStealthPinDialog(false)
            }
        )
    }
}

@Composable
private fun ProfileSection(
    userName: String,
    cycleLength: Int,
    periodLength: Int,
    onNameChange: (String) -> Unit,
    onCycleLengthChange: (Int) -> Unit,
    onPeriodLengthChange: (Int) -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BlushPink.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.first().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = BlushPink
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (isEditingName) {
                    OutlinedTextField(
                        value = userName,
                        onValueChange = onNameChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                } else {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isEditingName = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cycle Length",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            NumberPicker(
                value = cycleLength,
                onValueChange = onCycleLengthChange,
                minValue = 21,
                maxValue = 35
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Period Length",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            NumberPicker(
                value = periodLength,
                onValueChange = onPeriodLengthChange,
                minValue = 2,
                maxValue = 10
            )
        }
    }
}

@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int,
    maxValue: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { if (value > minValue) onValueChange(value - 1) },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BlushPink.copy(alpha = 0.1f))
        ) {
            Text(text = "−", fontSize = 20.sp, color = BlushPink)
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        IconButton(
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BlushPink.copy(alpha = 0.1f))
        ) {
            Text(text = "+", fontSize = 20.sp, color = BlushPink)
        }
    }
}

@Composable
private fun PrivacySection(
    stealthModeEnabled: Boolean,
    onStealthToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Privacy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Keep data on device",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Your health data never leaves your phone",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = true,
                    onCheckedChange = {},
                    enabled = false,
                    colors = SwitchDefaults.colors(
                        disabledCheckedThumbColor = Color(0xFF4CAF50),
                        disabledCheckedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔒",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Stealth Mode",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = stealthModeEnabled,
                    onCheckedChange = { onStealthToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BlushPink,
                        checkedTrackColor = BlushPink.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
private fun RemindersSection(
    reminders: List<Reminder>,
    onToggleReminder: (Long) -> Unit,
    onDeleteReminder: (Long) -> Unit,
    onAddReminder: () -> Unit
) {
    var swipeOffset by remember { mutableFloatStateOf(0f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5D4037)
                )

                IconButton(onClick = onAddReminder) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add reminder",
                        tint = BlushPink
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            reminders.forEach { reminder ->
                SwipeableReminderRow(
                    reminder = reminder,
                    onToggle = { onToggleReminder(reminder.id) },
                    onDelete = { onDeleteReminder(reminder.id) }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SwipeableReminderRow(
    reminder: Reminder,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var offset by remember { mutableFloatStateOf(0f) }
    val threshold = 100f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFCDD2))
            .padding(start = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offset < -threshold) {
                                onDelete()
                            }
                            offset = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offset += dragAmount
                            offset = offset.coerceIn(-150f, 0f)
                        }
                    )
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reminder.label,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BlushPink.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = reminder.time,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = BlushPink
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = reminder.enabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BlushPink,
                        checkedTrackColor = BlushPink.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
private fun SoundHapticsSection(
    soundsEnabled: Boolean,
    hapticsEnabled: Boolean,
    onSoundsToggle: (Boolean) -> Unit,
    onHapticsToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sound & Haptics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = BlushPink
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Haptic Soundtrack",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = soundsEnabled,
                    onCheckedChange = onSoundsToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BlushPink,
                        checkedTrackColor = BlushPink.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = null,
                    tint = BlushPink
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Haptic Feedback",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = hapticsEnabled,
                    onCheckedChange = onHapticsToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BlushPink,
                        checkedTrackColor = BlushPink.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
private fun NotificationsSection(
    partnerEnabled: Boolean,
    predictionEnabled: Boolean,
    customEnabled: Boolean,
    onPartnerToggle: (Boolean) -> Unit,
    onPredictionToggle: (Boolean) -> Unit,
    onCustomToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ToggleRow(
                label = "Partner notifications",
                checked = partnerEnabled,
                onToggle = onPartnerToggle
            )

            ToggleRow(
                label = "Prediction reminders",
                checked = predictionEnabled,
                onToggle = onPredictionToggle
            )

            ToggleRow(
                label = "Custom reminder alerts",
                checked = customEnabled,
                onToggle = onCustomToggle
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BlushPink,
                checkedTrackColor = BlushPink.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun ExportSection(onExportClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExportClick() },
        colors = CardDefaults.cardColors(containerColor = BlushPink.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = BlushPink
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Export Health Report",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BlushPink
                )
                Text(
                    text = "Generate PDF for doctor visits",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun StealthPinDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Stealth Mode PIN") },
        text = {
            Column {
                Text(
                    text = "Create a 4-digit PIN to access Stealth Mode",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it },
                    label = { Text("PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(pin) },
                enabled = pin.length == 4,
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}