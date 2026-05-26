package com.deepak.periodsaathi.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalDensity
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    onNavigateToInsights: () -> Unit = {},
    onNavigateToPhaseCoach: () -> Unit = {},
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

            // ── Phase Tabs ───────────────────────────
            item {
                PhaseTabRow(
                    selectedPhase = uiState.selectedTabPhase,
                    onPhaseSelected = { phase -> viewModel.onPhaseTabSelected(phase) },
                    modifier = Modifier.padding(top = 4.dp)
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

            // ── AI Premium Forecast Card ────────────
            item {
                AiForecastCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // ── Pattern Detective Notification Card ──
            item {
                PatternDetectiveCard(
                    onViewInsights = onNavigateToInsights,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // ── Hero glass card (week + mascot) ──────
            item {
                HeroCard(
                    cycleDay = uiState.cycleDay,
                    isRestDay = uiState.showRestDay,
                    mascotTip = uiState.mascotTipText,
                    mascotEmotion = uiState.mascotEmotion.toComponentEmotion(),
                    onMascotTap = { viewModel.onMascotTapped() },
                    onDismissTip = { viewModel.onMascotTapped() },
                    onShareTip = { /* TODO: share sheet */ },
                    onSaveTip = { /* TODO: save tip */ },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                )
            }

            // ── Current Status Dashboard ──────────────
            item {
                StatusDashboard(
                    phaseName = uiState.selectedTabPhase.displayName,
                    phaseDay = uiState.phaseDayInPhase,
                    phaseTotalDays = 5,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                )
            }

            // ── Quick Log Row ────────────────────────
            item {
                QuickLogSection(
                    onLogFlow = onNavigateToCalendar,
                    onLogWater = { viewModel.logWater(1) },
                    onLogMeals = { /* TODO: food log */ },
                    onLogMedicine = { /* TODO: medicine log */ },
                    onPhaseCoach = onNavigateToPhaseCoach
                )
            }

            // ── Daily Insights Section ────────────────
            item {
                DailyInsightsSection(
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // ── Insights Bento ───────────────────────
            item {
                InsightsBento(
                    streakCount = uiState.streakCount,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                )
            }

            // ── Phase Coach Banner ──────────────────
            item {
                PhaseCoachBanner(
                    phaseName = uiState.selectedTabPhase.displayName,
                    phaseDay = uiState.phaseDayInPhase,
                    onViewCoachGuide = onNavigateToPhaseCoach,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp)
                )
            }

            // ── Cycle-Synced Insights Hub ───────────
            item {
                CycleInsightsHub(
                    selectedPhase = uiState.selectedTabPhase,
                    modifier = Modifier.padding(top = 16.dp)
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
            .blur(20.dp)
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
    isRestDay: Boolean = false,
    mascotTip: String,
    mascotEmotion: ComponentMascotEmotion,
    onMascotTap: () -> Unit,
    onDismissTip: () -> Unit = {},
    onShareTip: () -> Unit = {},
    onSaveTip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var swipeOffset by remember { mutableStateOf(0f) }
    var dismissed by remember { mutableStateOf(false) }
    val swipeThreshold = 200f
    val animSwipeOffset = remember { Animatable(0f) }
    val swipeScope = rememberCoroutineScope()

    LaunchedEffect(dismissed) {
        if (dismissed) {
            animSwipeOffset.animateTo(
                targetValue = -swipeThreshold * 2,
                animationSpec = tween(250)
            )
            delay(50)
            swipeOffset = 0f
            dismissed = false
            onDismissTip()
            animSwipeOffset.animateTo(0f, spring())
        }
    }

    GlassCard(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .offset(x = animSwipeOffset.value.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (swipeOffset < -swipeThreshold) {
                            dismissed = true
                        } else if (swipeOffset > swipeThreshold) {
                            swipeOffset = 0f
                            swipeScope.launch { animSwipeOffset.snapTo(0f) }
                            onSaveTip()
                        } else {
                            swipeScope.launch {
                                animSwipeOffset.animateTo(0f, spring(dampingRatio = 0.6f))
                            }
                        }
                        swipeOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        swipeOffset += dragAmount
                        swipeScope.launch {
                            animSwipeOffset.snapTo(
                                swipeOffset.coerceIn(-swipeThreshold * 1.5f, swipeThreshold * 1.5f)
                            )
                        }
                    }
                )
            }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            WeekStrip(cycleDay = cycleDay, isRestDay = isRestDay)

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismissTip,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Dismiss tip",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onShareTip,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share tip",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onSaveTip,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Save tip",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(
                    text = "Insights are for educational purposes and do not replace professional medical advice.",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun WeekStrip(cycleDay: Int, isRestDay: Boolean = false) {
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
                Box {
                    if (isToday && isRestDay) {
                        Surface(
                            color = OnErrorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-22).dp)
                        ) {
                            Text(
                                text = "\uD83D\uDCCF REST DAY",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
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
}

@Composable
private fun QuickLogSection(
    onLogFlow: () -> Unit,
    onLogWater: () -> Unit,
    onLogMeals: () -> Unit,
    onLogMedicine: () -> Unit,
    onPhaseCoach: () -> Unit = {}
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
                QuickLogChip(label = "💧 Water", onClick = onLogWater)
            }
            item {
                QuickLogChip(label = "🍽 Meals", onClick = onLogMeals)
            }
            item {
                QuickLogChip(label = "💊 Medicine", onClick = onLogMedicine)
            }
            item {
                QuickLogChip(label = "🩸 Flow", onClick = onLogFlow)
            }
            item {
                QuickLogChip(label = "🧘 Coach", onClick = onPhaseCoach)
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
    streakCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BentoCard(
            modifier = Modifier.weight(1f),
            iconEmoji = "🌙",
            label = "Sleep",
            value = "7h 20m",
            containerColor = SecondaryContainer.copy(0.5f)
        )
        BentoCard(
            modifier = Modifier.weight(1f),
            iconEmoji = "❤️",
            label = "Streak",
            value = "$streakCount days",
            containerColor = PrimaryContainer.copy(0.5f)
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

// ── AI Premium Forecast Card ───────────────────────────────────────────────────

@Composable
private fun AiForecastCard(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aiPulse")
    val cloudAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudAlpha"
    )

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tint = TertiaryContainer
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 8.dp),
                color = Tertiary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "AI",
                    color = OnTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "☁️",
                    fontSize = 48.sp,
                    modifier = Modifier.graphicsLayer { alpha = cloudAlpha }
                )
                Column {
                    Text(
                        text = "Low Energy Day",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnTertiaryContainer
                    )
                    Text(
                        text = "AI Cycle Whisperer Premium Forecast",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// ── Pattern Detective Notification Card ────────────────────────────────────────

@Composable
private fun PatternDetectiveCard(
    onViewInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    val amber = Color(0xFFFFB74D)

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(),
                color = amber,
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
            ) {}
            Row(
                modifier = Modifier.weight(1f).padding(start = 12.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🔍", fontSize = 24.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "We noticed something interesting",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your sleep patterns seem to affect your cramps. Check Insights for details.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onViewInsights) {
                            Text("View Insights", color = Primary)
                        }
                    }
                }
            }
        }
    }
}

// ── Current Status Dashboard (3-column bento) ──────────────────────────────────

@Composable
private fun StatusDashboard(
    phaseName: String,
    phaseDay: Int,
    phaseTotalDays: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlassCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "$phaseName Phase",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$phaseDay",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Primary
                    )
                    Text(
                        text = "/ $phaseTotalDays Days",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SurfaceContainerHigh)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = phaseDay.toFloat() / phaseTotalDays.coerceAtLeast(1))
                            .clip(RoundedCornerShape(3.dp))
                            .background(Primary)
                    )
                }
            }
        }
        GlassCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "💧", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Flow",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "Medium",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
            }
        }
        GlassCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🥰", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mood",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "Cuddly",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
            }
        }
    }
}

// ── Daily Insights Section ─────────────────────────────────────────────────────

@Composable
private fun DailyInsightsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Daily Insights",
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GlassCard(
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "🧘", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "5-Min Restorative Yoga",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gentle stretches for cramp relief",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
            item {
                GlassCard(
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "🍵", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ginger & Chamomile Blend",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Soothing herbal tea for relaxation",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ── Phase Coach Banner ─────────────────────────────────────────────────────────

@Composable
private fun PhaseCoachBanner(
    phaseName: String,
    phaseDay: Int,
    onViewCoachGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Primary, PrimaryContainer))
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PHASE COACH",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Day $phaseDay of ${phaseName} Phase",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "View Coach Guide",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onViewCoachGuide() }.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhaseTabRow(
    selectedPhase: CyclePhase,
    onPhaseSelected: (CyclePhase) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        CyclePhase.MENSTRUAL,
        CyclePhase.FOLLICULAR,
        CyclePhase.OVULATORY,
        CyclePhase.LUTEAL
    )
    val selectedIndex = tabs.indexOf(selectedPhase).coerceAtLeast(0)
    var containerWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val tabWidth = if (containerWidth > 0) {
        with(density) { (containerWidth / tabs.size).toDp() }
    } else 0.dp

    val indicatorOffset by animateDpAsState(
        targetValue = tabWidth * selectedIndex.toFloat(),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "tabIndicator"
    )

    Surface(
        color = Color.White.copy(alpha = 0.5f),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .onGloballyPositioned { coordinates ->
                    containerWidth = coordinates.size.width
                }
        ) {
            Surface(
                color = Primary,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width((tabWidth - 4.dp).coerceAtLeast(0.dp))
                    .fillMaxHeight()
                    .padding(vertical = 4.dp)
            ) {}

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabs.forEach { phase ->
                    val isSelected = phase == selectedPhase
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onPhaseSelected(phase) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = phase.displayName,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private val phaseMediaContent = mapOf(
    CyclePhase.MENSTRUAL to listOf(
        "5-Min Guided Cramp Relief Meditation" to "Gentle breathing for period comfort",
        "Restorative Yoga for Pelvic Relief" to "Slow stretches to release tension",
        "Breathing Through Discomfort" to "3-minute calming breathwork"
    ),
    CyclePhase.FOLLICULAR to listOf(
        "Understanding Your Energy Spike" to "Harness your follicular superpower",
        "Creative Visualization Exercise" to "5-min visioning for your goals",
        "Morning Vitality Ritual" to "Energize your body & mind"
    ),
    CyclePhase.OVULATORY to listOf(
        "Communication Superpower" to "Speak with clarity & confidence",
        "Confidence Affirmations" to "Boost your self-expression energy",
        "Social Energy Meditation" to "Connect authentically with others"
    ),
    CyclePhase.LUTEAL to listOf(
        "Wind Down Bedtime Practice" to "Gentle yoga nidra for deep rest",
        "Stress Release Breathing" to "4-7-8 breath to calm your nervous system",
        "Self-Compassion Meditation" to "Soften into patience & kindness"
    )
)

@Composable
private fun CycleInsightsHub(
    selectedPhase: CyclePhase,
    modifier: Modifier = Modifier
) {
    val mediaItems = phaseMediaContent[selectedPhase] ?: phaseMediaContent[CyclePhase.FOLLICULAR]!!

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cycle-Synced Insights",
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariant
            )
            Text(
                text = "Audio \uD83C\uDFA7",
                fontSize = 11.sp,
                color = Primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mediaItems.size) { index ->
                val (title, description) = mediaItems[index]
                var isPlaying by remember { mutableStateOf(false) }

                GlassCard(
                    modifier = Modifier.width(220.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPlaying) Primary else Color.White.copy(alpha = 0.6f)
                                )
                                .clickable { isPlaying = !isPlaying },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = if (isPlaying) OnPrimary else Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = OnSurface,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    PeriodSaathiTheme {
        HomeScreen()
    }
}
