package com.example.periodsaathi.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.domain.model.CyclePhase
import com.example.periodsaathi.domain.usecase.MascotEmotion
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToLog: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToRemedies: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var firstSectionHeight by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingSkeleton()
            is HomeUiState.Error -> ErrorState(
                message = state.message,
                onRetry = { viewModel.refresh() }
            )
            is HomeUiState.Success -> {
                val scrollOffset = listState.firstVisibleItemScrollOffset.toFloat()
                val parallaxOffset = (scrollOffset * 0.3f).roundToInt()

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WarmCream),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Rest Day Banner
                    if (state.showRestDayBanner) {
                        item {
                            RestDayBanner(
                                visible = true,
                                onDismiss = { viewModel.dismissRestDay() },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .animateItem()
                            )
                        }
                    }

                    // Greeting Header with parallax
                    item {
                        GreetingHeader(
                            userName = state.homeData.userName,
                            cycleDay = state.homeData.currentCycleDay,
                            predictionDays = state.homeData.prediction?.daysUntil,
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = -parallaxOffset * 0.7f
                                }
                                .onGloballyPositioned { coordinates ->
                                    firstSectionHeight = coordinates.size.height.toFloat()
                                }
                                .animateItem()
                        )
                    }

                    // Mascot Hero Card
                    item {
                        MascotHeroCard(
                            mascotEmotion = state.homeData.mascotEmotion,
                            phase = state.homeData.currentPhase,
                            tip = state.mascotTip,
                            showTip = state.showMascotTip,
                            onMascotTapped = { viewModel.onMascotTapped() },
                            onDismissTip = { viewModel.dismissMascotTip() },
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = -parallaxOffset * 0.5f
                                }
                                .animateItem()
                        )
                    }

                    // Cycle Status Ring
                    item {
                        CycleStatusSection(
                            currentDay = state.homeData.currentCycleDay,
                            totalDays = 28,
                            phase = state.homeData.currentPhase,
                            prediction = state.homeData.prediction,
                            modifier = Modifier.animateItem()
                        )
                    }

                    // Quick Actions
                    item {
                        QuickActionsRow(
                            onLogToday = onNavigateToLog,
                            onWater = { viewModel.logWater(state.homeData.waterGlasses + 1) },
                            onMedicine = { /* TODO */ },
                            onRemedies = onNavigateToRemedies,
                            modifier = Modifier.animateItem()
                        )
                    }

                    // Today's Stats Grid
                    item {
                        TodayStatsGrid(
                            waterGlasses = state.homeData.waterGlasses,
                            waterTotal = 8,
                            mood = state.homeData.todayEntry?.mood ?: "Not logged",
                            symptoms = state.homeData.todayEntry?.symptoms ?: "None",
                            modifier = Modifier.animateItem()
                        )
                    }

                    // Prediction Banner
                    item {
                        PredictionBanner(
                            daysUntil = state.homeData.prediction?.daysUntil ?: 0,
                            expectedDate = state.homeData.prediction?.expectedDate?.toString() ?: "Unknown",
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingSkeleton() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(7) {
            SkeletonCard()
        }
    }
}

@Composable
private fun SkeletonCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Gray.copy(alpha = alpha))
    )
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "😔",
                fontSize = 48.sp
            )
            Text(
                text = message,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            PrimaryButton(
                text = "Retry",
                onClick = onRetry,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Composable
private fun GreetingHeader(
    userName: String,
    cycleDay: Int,
    predictionDays: Int?,
    modifier: Modifier = Modifier
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    val showNotificationDot = predictionDays != null && predictionDays < 3

    Box(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$greeting, $userName 🌸",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Text(
                    text = "Day $cycleDay of your cycle",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌸", fontSize = 20.sp)
                }

                // Notification Bell
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    if (showNotificationDot) {
                        NotificationDot()
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "notificationPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "notificationScale"
    )

    Box(
        modifier = Modifier
            .size(10.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Error)
    )
}

@Composable
private fun MascotHeroCard(
    mascotEmotion: MascotEmotion,
    phase: CyclePhase,
    tip: String?,
    showTip: Boolean,
    onMascotTapped: () -> Unit,
    onDismissTip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (mascotEmotion) {
        MascotEmotion.SLEEPING -> BlushPink.copy(alpha = 0.1f)
        MascotEmotion.PAIN -> SoftCoral.copy(alpha = 0.15f)
        MascotEmotion.SAD -> BabyBlue.copy(alpha = 0.1f)
        MascotEmotion.EXCITED -> ButterYellow.copy(alpha = 0.15f)
        MascotEmotion.HAPPY -> SoftLavender.copy(alpha = 0.1f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clickable { onMascotTapped() },
            onClick = { onMascotTapped() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor,
                                backgroundColor.copy(alpha = 0.5f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mascot emoji based on emotion
                    Text(
                        text = getMascotEmoji(mascotEmotion),
                        fontSize = 80.sp
                    )
                    Text(
                        text = getMascotMoodLabel(mascotEmotion),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface
                    )
                }
            }
        }

        // Tip Bubble
        if (showTip && tip != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = (-20).dp)
            ) {
                MascotTipBubble(
                    tip = tip,
                    visible = true,
                    onDismiss = onDismissTip
                )
            }
        }
    }
}

private fun getMascotEmoji(emotion: MascotEmotion): String = when (emotion) {
    MascotEmotion.SLEEPING -> "😴"
    MascotEmotion.PAIN -> "😣"
    MascotEmotion.SAD -> "😢"
    MascotEmotion.EXCITED -> "🤩"
    MascotEmotion.HAPPY -> "🌸"
}

private fun getMascotMoodLabel(emotion: MascotEmotion): String = when (emotion) {
    MascotEmotion.SLEEPING -> "Resting..."
    MascotEmotion.PAIN -> "Sending hugs 💕"
    MascotEmotion.SAD -> "Cheering you up!"
    MascotEmotion.EXCITED -> "So proud of you!"
    MascotEmotion.HAPPY -> "Feeling great!"
}

@Composable
private fun CycleStatusSection(
    currentDay: Int,
    totalDays: Int,
    phase: CyclePhase,
    prediction: com.example.periodsaathi.domain.model.PeriodPrediction?,
    modifier: Modifier = Modifier
) {
    val isPeriodSoon = prediction?.daysUntil?.let { it <= 1 } ?: false

    val infiniteTransition = rememberInfiniteTransition(label = "ringShake")
    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPeriodSoon) 4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shakeOffset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CycleRing(
            currentDay = currentDay,
            totalDays = totalDays,
            phase = phase,
            modifier = Modifier
                .size(160.dp)
                .offset { IntOffset(shakeOffset.roundToInt(), 0) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isPeriodSoon) {
            Text(
                text = "🎉 Period expected today!",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftCoral
            )
        } else if (prediction != null) {
            Text(
                text = "Next period in ${prediction.daysUntil} days",
                fontSize = 16.sp,
                color = OnSurfaceVariant
            )
            Text(
                text = "Expected: ${prediction.expectedDate}",
                fontSize = 14.sp,
                color = OnSurfaceVariant.copy(alpha = 0.7f)
            )
        } else {
            Text(
                text = "Log your first period to start tracking",
                fontSize = 14.sp,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onLogToday: () -> Unit,
    onWater: () -> Unit,
    onMedicine: () -> Unit,
    onRemedies: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        QuickActionItem("Log Today", "✏️", onLogToday),
        QuickActionItem("Water", "💧", onWater),
        QuickActionItem("Medicine", "💊", onMedicine),
        QuickActionItem("Remedies", "🌿", onRemedies)
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(actions) { action ->
            QuickActionChip(
                label = action.label,
                icon = action.icon,
                onClick = action.onClick
            )
        }
    }
}

private data class QuickActionItem(
    val label: String,
    val icon: String,
    val onClick: () -> Unit
)

@Composable
private fun QuickActionChip(
    label: String,
    icon: String,
    onClick: () -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isSpinning) 360f else 0f,
        animationSpec = tween(400),
        label = "spin",
        finishedListener = { isSpinning = false }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                isSpinning = true
                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BlushPink.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 28.sp,
                modifier = Modifier.rotate(rotation)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun TodayStatsGrid(
    waterGlasses: Int,
    waterTotal: Int,
    mood: String,
    symptoms: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Today's Stats",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Water Card
            StatsCard(
                icon = "💧",
                value = "$waterGlasses/$waterTotal",
                label = "Water",
                modifier = Modifier.weight(1f)
            )

            // Mood Card
            StatsCard(
                icon = "😊",
                value = mood,
                label = "Mood",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Symptoms Card
            StatsCard(
                icon = "📝",
                value = symptoms,
                label = "Symptoms",
                modifier = Modifier.weight(1f)
            )

            // Sleep Card
            StatsCard(
                icon = "🌙",
                value = "--",
                label = "Sleep",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatsCard(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(80.dp),
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = icon, fontSize = 24.sp)
            Column {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PredictionBanner(
    daysUntil: Int,
    expectedDate: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SoftCoral.copy(alpha = 0.1f),
                            BlushPink.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🗓", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "Next period in $daysUntil days",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                        Text(
                            text = expectedDate,
                            fontSize = 14.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Accuracy badge
                Surface(
                    color = MintGreen.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "±2 days",
                        fontSize = 12.sp,
                        color = OnSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Expandable content
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Based on your last 6 cycles",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "Prediction accuracy improves as you log more data",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}