package com.deepak.periodsaathi.ui.screens.calendar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.ShimmerCalendarGrid
import com.deepak.periodsaathi.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.combinedClickable
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen(
    onNavigateToDayLog: (Long) -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val calendarDays by viewModel.calendarDays.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val showLogSheet by viewModel.showLogSheet.collectAsState()
    val fertilityGoal by viewModel.fertilityGoal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasEntries by viewModel.hasEntries.collectAsState()
    val showPredictorSheet by viewModel.showPredictorSheet.collectAsState()

    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(20.dp),
                color = Color.White.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BlushPink.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("U", color = BlushPink, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Period Saathi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlushPink
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = BlushPink
                        )
                    }
                }
            }

            GoalToggleChips(
                selectedGoal = fertilityGoal,
                onGoalSelected = { viewModel.setFertilityGoal(it) }
            )

            if (isLoading) {
                ShimmerCalendarGrid()
            } else if (hasEntries == false) {
                CalendarEmptyState(onStartTracking = { viewModel.showLogSheet() })
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous",
                            tint = OnSurfaceVariant
                        )
                    }

                    Text(
                        text = currentYearMonth.format(monthFormatter),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )

                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = currentYearMonth,
                    transitionSpec = {
                        if (targetState.isAfter(initialState)) {
                            (slideInHorizontally { width -> width } + fadeIn(animationSpec = tween(350)))
                                .togetherWith(slideOutHorizontally { width -> -width } + fadeOut(animationSpec = tween(350)))
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn(animationSpec = tween(350)))
                                .togetherWith(slideOutHorizontally { width -> width } + fadeOut(animationSpec = tween(350)))
                        }
                    },
                    label = "calendarTransition"
                ) { targetMonth ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                CycleHealthCard(
                    nextPeriodDate = "28 Mar",
                    fertileIn = "13 Days"
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FloatingActionButton(
                onClick = { viewModel.togglePredictorSheet() },
                modifier = Modifier.size(48.dp),
                containerColor = Color.White.copy(alpha = 0.9f),
                contentColor = Primary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                shape = CircleShape
            ) {
                Text("🔮", fontSize = 18.sp)
            }

            FloatingActionButton(
                onClick = { viewModel.showLogSheet() },
                modifier = Modifier.size(64.dp),
                containerColor = Color.Transparent,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(listOf(PrimaryFixed, PrimaryFixedDim)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Log",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Log",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showLogSheet) {
        LogEntryBottomSheet(
            selectedDate = selectedDate,
            onDismiss = { viewModel.hideLogSheet() },
            onSave = { flow, symptoms, mood, water, notes ->
                viewModel.logEntry(flow, symptoms, mood, water, notes)
            }
        )
    }

    if (showPredictorSheet) {
        SymptomPredictorSheet(
            selectedDate = selectedDate,
            onDismiss = { viewModel.togglePredictorSheet() }
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun DayCell(
    dayData: CalendarDayData,
    isSelected: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    // ── Breathing/Pulse Infinite Animations ─────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // ── Mood Glow Aura ──────────────────────────────────────────────────────
    val moodGlowBrush = remember(dayData.mood) {
        when (dayData.mood?.lowercase() ?: "") {
            "happy", "very happy", "excited" -> Brush.radialGradient(
                listOf(Color(0xFFFFD54F).copy(alpha = 0.45f), Color.Transparent)
            )
            "calm", "peaceful", "relaxed" -> Brush.radialGradient(
                listOf(Color(0xFFB39DDB).copy(alpha = 0.45f), Color.Transparent)
            )
            "energetic", "active", "productive" -> Brush.radialGradient(
                listOf(Color(0xFFFFF176).copy(alpha = 0.45f), Color.Transparent)
            )
            "tired", "sick", "lazy" -> Brush.radialGradient(
                listOf(Color(0xFF64B5F6).copy(alpha = 0.35f), Color.Transparent)
            )
            "sad", "lonely", "moody" -> Brush.radialGradient(
                listOf(Color(0xFF90A4AE).copy(alpha = 0.35f), Color.Transparent)
            )
            "stressed", "anxious", "annoyed" -> Brush.radialGradient(
                listOf(Color(0xFFFF8A65).copy(alpha = 0.4f), Color.Transparent)
            )
            else -> null
        }
    }

    // Jitter stress vibration logic
    val isStressed = dayData.mood?.lowercase() in listOf("stressed", "anxious", "annoyed")
    val jitterOffset = if (isStressed) (pulseScale * 2f - 2f).dp else 0.dp

    // ── Base Container Styles ───────────────────────────────────────────────
    val containerBorder = when {
        isSelected -> BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
        dayData.isToday -> BorderStroke(2.dp, Brush.sweepGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)))
        dayData.isPredicted -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer.copy(0.7f))
        else -> null
    }

    val containerBackground = when {
        // Heavy Flow Day (Intensity 3)
        dayData.isPeriodDay && dayData.periodIntensity == 3 -> Brush.verticalGradient(
            listOf(Color(0xFFE53935), Color(0xFFC62828))
        )
        // Medium Flow Day (Intensity 2)
        dayData.isPeriodDay && dayData.periodIntensity == 2 -> Brush.verticalGradient(
            listOf(Color(0xFFEF5350), Color(0xFFE53935))
        )
        // Light Flow Day (Intensity 1)
        dayData.isPeriodDay && dayData.periodIntensity == 1 -> Brush.verticalGradient(
            listOf(Color(0xFFFFCDD2), Color(0xFFEF9A9A))
        )
        // Predicted Period
        dayData.isPredicted -> Brush.verticalGradient(
            listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
        )
        // Ovulation / Fertile
        dayData.isFertile -> Brush.verticalGradient(
            listOf(Color(0xFFE2FAF0), Color(0xFFC8E6C9))
        )
        else -> Brush.verticalGradient(
            listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
        )
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .scale(scale.value)
            .offset(x = jitterOffset, y = jitterOffset)
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    coroutineScope.launch {
                        scale.animateTo(1.08f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                        scale.animateTo(1f, animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium))
                    }
                    onTap()
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient Glow Backdrops (Mood Glows)
        if (moodGlowBrush != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(4.dp)
                    .background(moodGlowBrush)
            )
        }

        // 2. Base Container Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(containerBackground)
                .then(
                    if (containerBorder != null) Modifier.border(containerBorder, RoundedCornerShape(12.dp))
                    else Modifier
                )
        )

        // 3. Flower/Fertile/Flow Ripple Animations
        if (dayData.isFertile) {
            // Blooming Flower Overlay
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(Color(0xFF81C784).copy(alpha = 0.25f))
            )
        }

        if (dayData.isPeriodDay && dayData.periodIntensity == 3) {
            // Strong Ripple Ring for Heavy Flow
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .scale(pulseScale)
                    .border(BorderStroke(1.5.dp, Color.White.copy(pulseAlpha)), CircleShape)
            )
        }

        // 4. Center Number Text
        Text(
            text = dayData.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (dayData.isToday || isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = when {
                dayData.isPeriodDay && dayData.periodIntensity >= 2 -> Color.White
                dayData.isPeriodDay && dayData.periodIntensity == 1 -> Color(0xFFC62828)
                dayData.isFertile -> Color(0xFF2E7D32)
                !dayData.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        // 5. Flow Droplet Indicator Icon (Light/Medium/Heavy droplet sizes)
        if (dayData.isPeriodDay && !dayData.isPredicted) {
            val dropScale = when (dayData.periodIntensity) {
                3 -> 1.0f
                2 -> 0.8f
                else -> 0.6f
            }
            Text(
                text = "🌸", // Elegant wellness flower petal icon instead of generic drops
                fontSize = (9 * dropScale).sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 3.dp).padding(end = 3.dp)
                    .scale(pulseScale)
            )
        }

        // 6. Logged Badges / Saathi Sparkle Pop
        val hasLoggedData = dayData.hasLoggedSymptoms || dayData.hasLoggedWater || dayData.hasLoggedNotes || dayData.hasLoggedExercise
        if (hasLoggedData) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp).padding(end = 2.dp)
                    .clip(CircleShape)
                    .background(
                        if (dayData.hasDailyGoalMet) Color(0xFFFFD54F) // Golden Bloom Coin
                        else MaterialTheme.colorScheme.primaryContainer
                    )
            )
        }
    }
}

@Composable
private fun GoalToggleChips(selectedGoal: String?, onGoalSelected: (String?) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val goals = listOf("Planning Baby 🌱", "Avoiding Pregnancy 🧡")
        goals.forEach { goal ->
            FilterChip(
                selected = goal == selectedGoal,
                onClick = {
                    onGoalSelected(if (goal == selectedGoal) null else goal)
                },
                label = { Text(goal) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlushPink,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun CycleHealthCard(nextPeriodDate: String, fertileIn: String) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SoftLavender.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌸", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Cycle Health",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = OnSurface
                    )
                    Text(
                        "Your pattern looks normal and consistent.",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "NEXT PERIOD",
                            fontSize = 10.sp,
                            color = OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            nextPeriodDate,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlushPink
                        )
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "FERTILE IN",
                            fontSize = 10.sp,
                            color = OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            fertileIn,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Tertiary
                        )
                    }
                    }
                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SymptomPredictorSheet(
    selectedDate: LocalDate?,
    onDismiss: () -> Unit
) {
    val bloatingProgress by animateFloatAsState(
        targetValue = 0.78f,
        animationSpec = tween(durationMillis = 1000),
        label = "bloatingProgress"
    )
    val crampsProgress by animateFloatAsState(
        targetValue = 0.65f,
        animationSpec = tween(durationMillis = 1000),
        label = "crampsProgress"
    )
    val moodProgress by animateFloatAsState(
        targetValue = 0.55f,
        animationSpec = tween(durationMillis = 1000),
        label = "moodProgress"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White.copy(alpha = 0.85f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(OutlineVariant.copy(alpha = 0.6f))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "What to expect",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = PrimaryContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📅", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedDate?.format(DateTimeFormatter.ofPattern("MMM d")) ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Bloating",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        "78% likely",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = bloatingProgress)
                            .clip(RoundedCornerShape(50))
                            .background(Primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Cramps",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        "65% likely",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = crampsProgress)
                            .clip(RoundedCornerShape(50))
                            .background(Primary.copy(alpha = 0.8f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Mood changes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        "55% likely",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = moodProgress)
                            .clip(RoundedCornerShape(50))
                            .background(Primary.copy(alpha = 0.6f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Want to prepare?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.4f)
                    )
                ) {
                    Text("Add ginger tea reminder 🍵")
                }
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.4f)
                    )
                ) {
                    Text("Mark rest day 🛏")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ACCURACY TRACKER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                letterSpacing = 0.36.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { }
                ) {
                    Text(
                        "Spot on ✓",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { }
                ) {
                    Text(
                        "Somewhat ~",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { }
                ) {
                    Text(
                        "Way off ✗",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CalendarEmptyState(onStartTracking: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "📅", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No cycles tracked yet",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start logging your period to see\nyour cycle calendar here",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onStartTracking,
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Start tracking your cycle")
            }
        }
    }
}

@Composable
    private fun LogEntryBottomSheet(
        selectedDate: LocalDate?,
        onDismiss: () -> Unit,
        onSave: (Int?, List<String>, String?, Int, String?) -> Unit
    ) {
        var selectedFlow by remember { mutableStateOf<Int?>(null) }
        var notes by remember { mutableStateOf("") }
        var selectedSymptoms by remember { mutableStateOf<List<String>>(emptyList()) }

        val allSymptoms = listOf("Cramps", "Bloating", "Headache", "Mood Swings", "Back Pain", "Fatigue", "Acne")
        val dateText = selectedDate?.format(DateTimeFormatter.ofPattern("MMMM d")) ?: "Today"

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(onClick = onDismiss)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .background(
                        SurfaceContainerLow,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer.copy(alpha = 0.7f))
                        .rotate(12f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌸", fontSize = 18.sp)
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(OutlineVariant.copy(alpha = 0.5f))
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Log Symptoms",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Text(
                                text = "Today, $dateText",
                                fontSize = 14.sp,
                                color = OnSecondaryFixedVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.4f))
                                .clickable(onClick = onDismiss),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", color = OnSurfaceVariant)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 100.dp)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Flow Level",
                            fontSize = 14.sp,
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Light", "Medium", "Heavy").forEachIndexed { index, label ->
                                val isSelected = selectedFlow == index + 1
                                val shape = RoundedCornerShape(12.dp)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(
                                            if (isSelected) Modifier.background(
                                                Brush.verticalGradient(listOf(Color(0xFFFFD9DE), Color(0xFFFCB3BE))),
                                                shape
                                            )
                                            else Modifier.border(1.dp, OutlineVariant, shape)
                                        )
                                        .clickable { selectedFlow = if (isSelected) null else index + 1 }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else OnSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "How are you feeling?",
                            fontSize = 14.sp,
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            allSymptoms.forEach { symptom ->
                                val isSelected = selectedSymptoms.contains(symptom)
                                val bgColor = when {
                                    isSelected && symptom == "Cramps" -> SecondaryContainer
                                    isSelected && symptom == "Mood Swings" -> TertiaryContainer
                                    isSelected -> PrimaryContainer
                                    else -> Color.Transparent
                                }
                                val textColor = when {
                                    isSelected && symptom == "Cramps" -> OnSecondaryContainer
                                    isSelected && symptom == "Mood Swings" -> OnTertiaryContainer
                                    isSelected -> OnPrimaryContainer
                                    else -> OnSurfaceVariant
                                }
                                Box(
                                    modifier = Modifier
                                        .then(
                                            if (isSelected) Modifier.background(bgColor, RoundedCornerShape(50))
                                            else Modifier.border(1.dp, OutlineVariant, RoundedCornerShape(50))
                                        )
                                        .clickable {
                                            selectedSymptoms = if (isSelected) {
                                                selectedSymptoms.filter { it != symptom }
                                            } else {
                                                selectedSymptoms + symptom
                                            }
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = symptom,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Notes",
                            fontSize = 14.sp,
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("How's your day going?", color = OnSurfaceVariant.copy(alpha = 0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SecondaryContainer,
                                unfocusedBorderColor = OutlineVariant.copy(alpha = 0.5f),
                                focusedContainerColor = Color.White.copy(alpha = 0.2f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 4
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow)
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(listOf(Primary, PrimaryContainer)))
                                .clickable { onSave(selectedFlow, selectedSymptoms, null, 0, notes.ifBlank { null }) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Save Log",
                                color = OnPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
