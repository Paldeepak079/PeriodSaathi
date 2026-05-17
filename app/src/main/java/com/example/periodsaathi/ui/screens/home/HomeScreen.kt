package com.example.periodsaathi.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.domain.model.CyclePhase
import com.example.periodsaathi.ui.components.CycleRing
import com.example.periodsaathi.ui.components.GlassCard
import com.example.periodsaathi.ui.components.PastelChip
import com.example.periodsaathi.ui.theme.*
import java.time.LocalTime

@Composable
fun HomeScreen(
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToDayLog: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val greeting = getGreeting()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0E2E), Color(0xFF1A1228))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Medical disclaimer
        if (uiState.isFirstLaunch) {
            item {
                MedicalDisclaimerBanner(onDismiss = { viewModel.dismissMedicalDisclaimer() })
            }
        }

        // Greeting
        item {
            GreetingHeader(name = uiState.userName, greeting = greeting)
        }

        // Mascot card
        item {
            MascotCard(
                emotion = uiState.mascotEmotion,
                tip = getMascotTip(uiState.mascotTipIndex),
                onTap = { viewModel.onMascotTapped() }
            )
        }

        // Cycle ring
        item {
            CycleRingCard(
                cycleDay = uiState.cycleDay,
                totalDays = uiState.totalCycleDays,
                phase = uiState.phase,
                prediction = uiState.prediction?.daysUntil
            )
        }

        // Rest day banner
        if (uiState.showRestDay) {
            item {
                RestDayBanner(onDismiss = { viewModel.dismissRestDay() })
            }
        }

        // Quick actions
        item {
            QuickActionsRow(
                onLogPeriod = onNavigateToCalendar,
                onLogWater = { viewModel.logWater(1) },
                onLogMood = { /* TODO */ },
                onLogSymptoms = { /* TODO */ }
            )
        }

        // Stats grid
        item {
            StatsGrid(
                waterGlasses = uiState.waterGlasses,
                streakCount = uiState.streakCount,
                points = uiState.points,
                phase = uiState.phase
            )
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // FAB
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { onNavigateToDayLog(System.currentTimeMillis()) },
            containerColor = BlushPink,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}

@Composable
private fun getGreeting(): String {
    val hour = LocalTime.now().hour
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}

private fun getMascotTip(index: Int): String {
    val tips = listOf(
        "Remember to stay hydrated! 💧",
        "Gentle stretching can help with cramps 🧘",
        "You're doing great! Keep tracking 🌟",
        "Self-care is important today 💕",
        "Listen to your body 💪"
    )
    return tips[index % tips.size]
}

@Composable
private fun MedicalDisclaimerBanner(onDismiss: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚕️ Medical Disclaimer",
                fontWeight = FontWeight.Bold,
                color = DeepRose
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This app provides general information and is not a substitute for professional medical advice.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onDismiss) {
                Text("I understand", color = BlushPink)
            }
        }
    }
}

@Composable
private fun GreetingHeader(name: String, greeting: String) {
    Column {
        Text(
            text = "$greeting!",
            fontSize = 16.sp,
            color = SoftLavender
        )
        Text(
            text = name,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun MascotCard(emotion: MascotEmotion, tip: String, onTap: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = when (emotion) {
                MascotEmotion.HAPPY -> "😊"
                MascotEmotion.EXCITED -> "🤩"
                MascotEmotion.SLEEPING -> "😴"
                MascotEmotion.SUPPORTIVE -> "💕"
            }, fontSize = 48.sp)

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Saathi Tip",
                    fontWeight = FontWeight.Bold,
                    color = BlushPink
                )
                Text(
                    text = tip,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun CycleRingCard(
    cycleDay: Int,
    totalDays: Int,
    phase: CyclePhase,
    prediction: Int?
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
            CycleRing(
                currentDay = cycleDay,
                totalDays = totalDays,
                phase = phase,
                size = 180.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = phase.name.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftLavender
            )

            if (prediction != null && prediction > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Next period in $prediction days",
                    fontSize = 14.sp,
                    color = BabyBlue
                )
            }
        }
    }
}

@Composable
private fun RestDayBanner(onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically() + fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftCoral.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🛌", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Rest Day", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Take it easy today!", fontSize = 14.sp, color = SoftLavender)
                }
                TextButton(onClick = onDismiss) {
                    Text("OK", color = BlushPink)
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onLogPeriod: () -> Unit,
    onLogWater: () -> Unit,
    onLogMood: () -> Unit,
    onLogSymptoms: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { QuickLogChip(label = "🩸 Log Period", onClick = onLogPeriod) }
        item { QuickLogChip(label = "💧 Water", onClick = onLogWater) }
        item { QuickLogChip(label = "😊 Mood", onClick = onLogMood) }
        item { QuickLogChip(label = "🤒 Symptoms", onClick = onLogSymptoms) }
    }
}

@Composable
private fun QuickLogChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BlushPink.copy(alpha = 0.2f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = label, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatsGrid(
    waterGlasses: Int,
    streakCount: Int,
    points: Int,
    phase: CyclePhase
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Water",
            value = "$waterGlasses/8",
            emoji = "💧"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Streak",
            value = "$streakCount days",
            emoji = "🔥"
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Points",
            value = "$points",
            emoji = "⭐"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Phase",
            value = phase.name.take(6),
            emoji = "🌸"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    emoji: String
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 12.sp, color = SoftLavender)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}