package com.deepak.periodsaathi.ui.screens.daylog

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun DayLogScreen(
    dateEpoch: Long,
    onBack: () -> Unit = {},
    viewModel: DayLogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(dateEpoch) {
        viewModel.setDateEpoch(dateEpoch)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.saveComplete.collect { onBack() }
    }

    LaunchedEffect(Unit) {
        viewModel.saveError.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack) {
                        Text("Cancel", color = OnSurfaceVariant)
                    }
                    Text(
                        text = "Log Day",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    TextButton(
                        onClick = { viewModel.saveEntry() },
                        enabled = !state.isSaving && (state.flowIntensity != null || state.isRestDay)
                    ) {
                        Text("Save", color = BlushPink)
                    }
                }
            }

            // Flow intensity
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Flow Intensity", color = OnSurface, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.availableFlowLevels.forEach { level ->
                                val isSelected = state.flowIntensity == level
                                val bgColor by animateColorAsState(
                                    if (isSelected) BlushPink else Color.Transparent,
                                    label = "flowBg"
                                )
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setFlowIntensity(if (isSelected) null else level) },
                                    label = { Text(level) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BlushPink
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Symptoms
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Symptoms", color = OnSurface, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.availableSymptoms.take(4).forEach { symptom ->
                                val isSelected = symptom in state.symptoms
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.toggleSymptom(symptom) },
                                    label = { Text(symptom, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SoftCoral
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.availableSymptoms.drop(4).forEach { symptom ->
                                val isSelected = symptom in state.symptoms
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.toggleSymptom(symptom) },
                                    label = { Text(symptom, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SoftCoral
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Mood
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mood", color = OnSurface, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.availableMoods.take(4).forEach { mood ->
                                val isSelected = state.mood == mood
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setMood(if (isSelected) null else mood) },
                                    label = { Text(mood, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BabyBlue
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.availableMoods.drop(4).forEach { mood ->
                                val isSelected = state.mood == mood
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setMood(if (isSelected) null else mood) },
                                    label = { Text(mood, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BabyBlue
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Water
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Water", color = OnSurface, fontWeight = FontWeight.SemiBold)
                            Text("${state.waterGlasses} glasses", color = BabyBlue, fontSize = 14.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(
                                onClick = { viewModel.setWaterGlasses(state.waterGlasses - 1) },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(40.dp)
                            ) { Text("-", fontSize = 18.sp) }
                            FilledTonalButton(
                                onClick = { viewModel.setWaterGlasses(state.waterGlasses + 1) },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(40.dp)
                            ) { Text("+", fontSize = 18.sp) }
                        }
                    }
                }
            }

            // Rest day toggle
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Rest Day", color = OnSurface, fontWeight = FontWeight.SemiBold)
                            Text("Take it easy", color = OnSurfaceVariant, fontSize = 14.sp)
                        }
                        Switch(
                            checked = state.isRestDay,
                            onCheckedChange = { viewModel.toggleRestDay() },
                            colors = SwitchDefaults.colors(checkedTrackColor = BlushPink)
                        )
                    }
                }
            }

            // Notes
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Notes", color = OnSurface, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.notes,
                            onValueChange = { viewModel.setNotes(it) },
                            placeholder = { Text("How are you feeling?", color = OnSurfaceVariant) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BlushPink,
                                unfocusedBorderColor = OnSurfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Save button
            item {
                Button(
                    onClick = { viewModel.saveEntry() },
                    enabled = !state.isSaving && (state.flowIntensity != null || state.isRestDay),
                    colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = OnSurface,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Entry", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}




