package com.deepak.periodsaathi.ui.screens.wellness

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.WaterRingWithWave
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun WellnessScreen(
    viewModel: WellnessViewModel = hiltViewModel()
) {
    val state by viewModel.wellnessState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0E2E), Color(0xFF1A1228))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Wellness",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Water section
            item {
                WaterSection(
                    currentGlasses = state.waterGlasses,
                    goalGlasses = state.waterGoal,
                    onAddGlass = { viewModel.addWater(1) },
                    onAdd500ml = { viewModel.addWater(1) }
                )
            }

            // Habits section
            item {
                Text(text = "Daily Habits", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            items(state.habits) { habit ->
                HabitCheckItem(
                    habit = habit,
                    onToggle = { viewModel.toggleHabit(habit.id) }
                )
            }

            // Sleep section
            item {
                SleepSection(
                    currentHours = state.sleepHours,
                    onHoursChange = { viewModel.setSleepHours(it) }
                )
            }

            // Exercise section
            item {
                ExerciseSection(
                    minutes = state.exerciseMinutes,
                    onMinutesChange = { viewModel.setExerciseMinutes(it) }
                )
            }

            // Rewards section
            item {
                RewardsSection(
                    points = state.totalPoints,
                    streak = state.streakCount
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun WaterSection(
    currentGlasses: Int,
    goalGlasses: Int,
    onAddGlass: () -> Unit,
    onAdd500ml: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WaterRingWithWave(
                currentGlasses = currentGlasses,
                totalGlasses = goalGlasses,
                size = 200.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddGlass,
                    colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+1 Glass")
                }

                OutlinedButton(
                    onClick = onAdd500ml,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+500ml")
                }
            }
        }
    }
}

@Composable
private fun HabitCheckItem(habit: HabitItem, onToggle: () -> Unit) {
    val checkScale by animateFloatAsState(
        targetValue = if (habit.isCompleted) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkScale"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .scale(checkScale)
                    .clip(CircleShape)
                    .background(
                        if (habit.isCompleted) BlushPink else Color.Transparent
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (habit.isCompleted) {
                    Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = habit.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = habit.name,
                color = Color.White,
                fontWeight = if (habit.isCompleted) FontWeight.Normal else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SleepSection(currentHours: Float, onHoursChange: (Float) -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "😴", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Sleep", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${currentHours.toInt()}h", color = BlushPink, fontWeight = FontWeight.Bold)
            }

            Slider(
                value = currentHours,
                onValueChange = onHoursChange,
                valueRange = 0f..12f,
                steps = 23,
                colors = SliderDefaults.colors(
                    thumbColor = BlushPink,
                    activeTrackColor = BlushPink
                )
            )
        }
    }
}

@Composable
private fun ExerciseSection(minutes: Int, onMinutesChange: (Int) -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🏃", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Exercise", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$minutes min", color = BabyBlue, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Walk" to 15, "Yoga" to 20, "Gym" to 30, "Other" to 0).forEach { (label, mins) ->
                    FilterChip(
                        selected = minutes == mins,
                        onClick = { onMinutesChange(if (minutes == mins) 0 else mins) },
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardsSection(points: Int, streak: Int) {
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "⭐", fontSize = 32.sp)
                Text(text = "$points pts", color = WarmGold, fontWeight = FontWeight.Bold)
                Text(text = "Points", color = SoftLavender, fontSize = 12.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🔥", fontSize = 32.sp)
                Text(text = "$streak", color = SoftCoral, fontWeight = FontWeight.Bold)
                Text(text = "Day Streak", color = SoftLavender, fontSize = 12.sp)
            }
        }
    }
}
