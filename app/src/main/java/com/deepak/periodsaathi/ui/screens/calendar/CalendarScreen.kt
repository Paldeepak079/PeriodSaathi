package com.deepak.periodsaathi.ui.screens.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    onNavigateToDayLog: (Long) -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val calendarDays by viewModel.calendarDays.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val showLogSheet by viewModel.showLogSheet.collectAsState()
    val fertilityMode by viewModel.fertilityMode.collectAsState()

    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0E2E), Color(0xFF1A1228))
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = SoftLavender
                )
            }

            Text(
                text = currentYearMonth.format(monthFormatter),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            IconButton(onClick = { viewModel.nextMonth() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = SoftLavender
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Day of week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = SoftLavender
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(calendarDays) { dayData ->
                DayCell(
                    dayData = dayData,
                    isSelected = dayData.date == selectedDate,
                    onTap = {
                        viewModel.selectDate(dayData.date)
                        viewModel.showLogSheet()
                    },
                    onLongPress = {
                        onNavigateToDayLog(dayData.date.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC) * 1000)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Fertility mode selector
        FertilityModeSelector(
            currentMode = fertilityMode,
            onToggle = { viewModel.toggleFertilityMode() }
        )
    }

    // Log entry bottom sheet
    if (showLogSheet) {
        LogEntryBottomSheet(
            selectedDate = selectedDate,
            onDismiss = { viewModel.hideLogSheet() },
            onSave = { flow, symptoms, mood, water, notes ->
                viewModel.logEntry(flow, symptoms, mood, water, notes)
            }
        )
    }
}

@Composable
private fun DayCell(
    dayData: CalendarDayData,
    isSelected: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dayScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> BlushPink
            dayData.isPeriodDay -> BlushPink.copy(alpha = 0.5f)
            else -> Color.Transparent
        },
        label = "dayBg"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (dayData.isToday) Modifier.border(2.dp, BabyBlue, RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayData.date.dayOfMonth.toString(),
            fontSize = 14.sp,
            color = when {
                isSelected -> Color.White
                !dayData.isCurrentMonth -> SoftLavender.copy(alpha = 0.3f)
                else -> Color.White
            }
        )
    }
}

@Composable
private fun FertilityModeSelector(
    currentMode: FertilityMode,
    onToggle: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FertilityMode.entries.forEach { mode ->
                val isActive = mode == currentMode
                TextButton(
                    onClick = { if (mode != currentMode) onToggle() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isActive) BlushPink else SoftLavender
                    )
                ) {
                    Text(
                        text = mode.name,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogEntryBottomSheet(
    selectedDate: LocalDate?,
    onDismiss: () -> Unit,
    onSave: (Int?, List<String>, String?, Int, String?) -> Unit
) {
    var selectedFlow by remember { mutableStateOf<Int?>(null) }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var waterGlasses by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1228)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Log Entry",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Flow intensity
            Text(text = "Flow", color = SoftLavender)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Light", "Medium", "Heavy").forEachIndexed { index, label ->
                    FilterChip(
                        selected = selectedFlow == index + 1,
                        onClick = { selectedFlow = if (selectedFlow == index + 1) null else index + 1 },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlushPink,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mood
            Text(text = "Mood", color = SoftLavender)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("😊" to "Happy", "😐" to "Neutral", "😢" to "Sad", "😤" to "Angry", "😴" to "Tired").forEach { (emoji, _) ->
                    TextButton(
                        onClick = { selectedMood = if (selectedMood == emoji) null else emoji },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (selectedMood == emoji) BlushPink else Color.White
                        )
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Water
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Water: ", color = SoftLavender)
                IconButton(onClick = { if (waterGlasses > 0) waterGlasses-- }) { Text("-", color = Color.White) }
                Text(text = "$waterGlasses glasses", color = Color.White)
                IconButton(onClick = { waterGlasses++ }) { Text("+", color = Color.White) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlushPink,
                    unfocusedBorderColor = SoftLavender
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(selectedFlow, emptyList(), selectedMood, waterGlasses, notes.ifBlank { null }) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink)
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
