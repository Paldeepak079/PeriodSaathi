package com.deepak.periodsaathi.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.domain.model.CyclePhase
import com.deepak.periodsaathi.ui.components.*
import com.deepak.periodsaathi.ui.theme.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

import com.deepak.periodsaathi.ui.components.MascotEmotion as ComponentMascotEmotion

// ─────────────────────────────────────────────
//  Home Screen (Stitch reference: home_dashboard)
//  Background: Warm cream (#FFF8F2)
//  Style: Glassmorphism cards, week strip, mascot speech bubble
// ─────────────────────────────────────────────

/** Map HomeViewModel.MascotEmotion → SaathiMascot component enum */
private fun MascotEmotion.toComponentEmotion(): ComponentMascotEmotion = when (this) {
    MascotEmotion.HAPPY -> ComponentMascotEmotion.HAPPY
    MascotEmotion.EXCITED -> ComponentMascotEmotion.EXCITED
    MascotEmotion.SLEEPING -> ComponentMascotEmotion.SLEEPING
    MascotEmotion.SUPPORTIVE -> ComponentMascotEmotion.HUGGING
}

@Composable
fun HomeScreen(
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToDayLog: (Long) -> Unit = {},
    onNavigateToBreathing: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Background blobs
        HomeMeshBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Top App Bar ──────────────────────────
            item {
                HomeTopBar(
                    userName = uiState.userName,
                    onNotificationsClick = { /* TODO: notifications */ }
                )
            }

            // ── Medical disclaimer (first launch) ────
            if (uiState.isFirstLaunch) {
                item {
                    MedicalDisclaimerBanner(
                        onDismiss = { viewModel.dismissMedicalDisclaimer() },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // ── Greeting + Wellness Score ────────────
            item {
                GreetingRow(
                    name = uiState.userName,
                    cycleDay = uiState.cycleDay,
                    wellnessPercent = 82, // TODO: from uiState
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }

            // ── Hero glass card (week + mascot) ──────
            item {
                HeroCard(
                    cycleDay = uiState.cycleDay,
                    mascotTip = getMascotTip(uiState.mascotTipIndex),
                    mascotEmotion = uiState.mascotEmotion.toComponentEmotion(),
                    onMascotTap = { viewModel.onMascotTapped() },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                )
            }

            // ── Rest Day Banner ──────────────────────
            if (uiState.showRestDay) {
                item {
                    RestDayBanner(
                        onDismiss = { viewModel.dismissRestDay() },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                    )
                }
            }

            // ── Quick Log Row ────────────────────────
            item {
                QuickLogSection(
                    onLogFlow = onNavigateToCalendar,
                    onLogWater = { viewModel.logWater(1) },
                    onLogMeals = { /* TODO: food log */ },
                    onLogMedicine = { /* TODO: medicine log */ }
                )
            }

            // ── Insights Bento ───────────────────────
            item {
                InsightsBento(
                    waterGlasses = uiState.waterGlasses,
                    streakCount = uiState.streakCount,
                    phase = uiState.phase,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                )
            }
        }

        // ── FAB ─────────────────────────────────────
        FloatingActionButton(
            onClick = { onNavigateToDayLog(System.currentTimeMillis()) },
            containerColor = Primary,
            contentColor = OnPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 88.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Log today")
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun HomeMeshBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-60).dp, y = (-20).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(listOf(PrimaryContainer.copy(0.4f), Color.Transparent)),
                    CircleShape
                )
                .blur(50.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = 80.dp)
                .size(250.dp)
                .background(
                    Brush.radialGradient(listOf(SecondaryContainer.copy(0.3f), Color.Transparent)),
                    CircleShape
                )
                .blur(50.dp)
        )
    }
}

@Composable
private fun HomeTopBar(
    userName: String,
    onNotificationsClick: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.dp, color = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.toString() ?: "P",
                        color = OnPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Period Saathi",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
            }
            IconButton(onClick = onNotificationsClick) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Primary)
            }
        }
    }
}

@Composable
private fun GreetingRow(
    name: String,
    cycleDay: Int,
    wellnessPercent: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "Hey ${name.ifBlank { "there" }} 🌸",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                ),
                color = OnSurface
            )
            Text(
                text = if (cycleDay > 0) "Day $cycleDay of your cycle" else "Start tracking today",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }

        // Wellness ring
        WellnessRing(percent = wellnessPercent)
    }
}

@Composable
private fun WellnessRing(percent: Int) {
    Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        val stroke = Stroke(width = 6.dp.value, cap = StrokeCap.Round)
        val sweepAngle = 360f * (percent / 100f)
        androidx.compose.foundation.Canvas(modifier = Modifier.size(64.dp)) {
            // Background ring
            drawArc(
                color = Color.White.copy(0.5f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            // Progress ring
            drawArc(
                color = Primary,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = stroke
            )
        }
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.labelMedium,
            color = Primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HeroCard(
    cycleDay: Int,
    mascotTip: String,
    mascotEmotion: ComponentMascotEmotion,
    onMascotTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Week strip
            WeekStrip(cycleDay = cycleDay)

            Spacer(modifier = Modifier.height(20.dp))

            // Mascot + speech bubble
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                // Speech bubble
                Surface(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = mascotTip,
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(modifier = Modifier.clickable { onMascotTap() }) {
                    SaathiMascot(
                        emotion = mascotEmotion,
                        size = 100.dp,
                        onTap = onMascotTap
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(cycleDay: Int) {
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        (0..6).forEach { dayOffset ->
            val day = startOfWeek.plusDays(dayOffset.toLong())
            val isToday = day == today
            val dayLabel = day.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
            val dayNum = day.dayOfMonth

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isToday -> Error
                                else -> Color.Transparent
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayNum.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = when {
                            isToday -> OnError
                            else -> OnSurface
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickLogSection(
    onLogFlow: () -> Unit,
    onLogWater: () -> Unit,
    onLogMeals: () -> Unit,
    onLogMedicine: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            text = "Quick Log",
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuickLogChip(label = "🩸 Flow", onClick = onLogFlow)
            }
            item {
                QuickLogChip(label = "💧 Water", onClick = onLogWater)
            }
            item {
                QuickLogChip(label = "🍽 Meals", onClick = onLogMeals)
            }
            item {
                QuickLogChip(label = "💊 Medicine", onClick = onLogMedicine)
            }
        }
    }
}

@Composable
private fun QuickLogChip(label: String, onClick: () -> Unit) {
    ScaleButton(onClick = onClick) {
        Surface(
            color = Color.White.copy(alpha = 0.6f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
            shape = CircleShape
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = OnSurface,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun InsightsBento(
    waterGlasses: Int,
    streakCount: Int,
    phase: CyclePhase,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sleep card (placeholder)
        BentoCard(
            modifier = Modifier.weight(1f),
            iconEmoji = "🌙",
            label = "Sleep",
            value = "7h 20m",
            containerColor = SecondaryContainer.copy(0.5f)
        )
        // BPM card (placeholder)
        BentoCard(
            modifier = Modifier.weight(1f),
            iconEmoji = "❤️",
            label = "Streak",
            value = "$streakCount days",
            containerColor = PrimaryContainer.copy(0.5f)
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BentoCard(
            modifier = Modifier.weight(1f),
            iconEmoji = "💧",
            label = "Water",
            value = "$waterGlasses/8 glasses",
            containerColor = TertiaryContainer.copy(0.5f)
        )
        BentoCard(
            modifier = Modifier.weight(1f),
            iconEmoji = "🌸",
            label = "Phase",
            value = phase.name.lowercase().replaceFirstChar { it.uppercase() },
            containerColor = PrimaryFixed.copy(0.5f)
        )
    }
}

@Composable
private fun BentoCard(
    modifier: Modifier = Modifier,
    iconEmoji: String,
    label: String,
    value: String,
    containerColor: Color = Color.White.copy(0.45f)
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Text(iconEmoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = OnSurface
            )
        }
    }
}

@Composable
private fun MedicalDisclaimerBanner(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚕️ Medical Disclaimer",
                fontWeight = FontWeight.Bold,
                color = Primary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "This app provides general information and is not a substitute for professional medical advice.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
            TextButton(onClick = onDismiss) {
                Text("I understand", color = Primary)
            }
        }
    }
}

@Composable
private fun RestDayBanner(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ErrorContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🛌", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🛏 REST DAY",
                    fontWeight = FontWeight.Bold,
                    color = OnErrorContainer,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "Take it easy today!",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            TextButton(onClick = onDismiss) {
                Text("OK", color = Primary)
            }
        }
    }
}

private fun getMascotTip(index: Int): String {
    val tips = listOf(
        "Hydrate yourself today! 💧",
        "Gentle stretching can help with cramps 🧘",
        "You're doing great! Keep tracking 🌟",
        "Self-care is important today 💕",
        "Listen to your body 💪"
    )
    return tips[index % tips.size]
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    PeriodSaathiTheme {
        HomeScreen()
    }
}
