package com.deepak.periodsaathi.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.domain.model.CyclePhase
import com.deepak.periodsaathi.domain.HealthQueryResult
import com.deepak.periodsaathi.domain.QueryType
import com.deepak.periodsaathi.ui.components.*
import com.deepak.periodsaathi.ui.theme.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.deepak.periodsaathi.ui.components.MascotEmotion as ComponentMascotEmotion

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
    onNavigateToPartner: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToPayment: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToFriend: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showPhaseCoachSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var ttsEngine by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        var tts: android.speech.tts.TextToSpeech? = null
        tts = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                tts?.language = java.util.Locale.getDefault()
            }
        }
        ttsEngine = tts
        onDispose {
            tts?.stop()
            tts?.shutdown()
            ttsEngine = null
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = {
                Text(
                    text = "Sign Out \uD83C\uDF38",
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out? Your data will remain safely on this device, but you will need to log in again to sync online.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSignOutDialog = false }
                ) {
                    Text("Cancel", color = Primary.copy(0.7f), fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            tonalElevation = 6.dp
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Background,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(Modifier.height(32.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            scope.launch { drawerState.close() }
                            onNavigateToPayment()
                        }
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.userName.firstOrNull()?.toString() ?: "P",
                            color = OnPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = uiState.userName.ifBlank { "Guest User" },
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Primary
                    )
                    Text("Free Plan", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider(color = OutlineVariant)
                Spacer(Modifier.height(16.dp))
                NavigationDrawerItem(
                    icon = { Text("\uD83D\uDC6D", fontSize = 18.sp) },
                    label = { Text("Friend", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToFriend()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Text("\uD83D\uDC65", fontSize = 18.sp) },
                    label = { Text("Partner Mode", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToPartner()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Text("\u2699\uFE0F", fontSize = 18.sp) },
                    label = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = { Text("\u2B50", fontSize = 18.sp) },
                    label = { Text("Premium", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToPayment()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    badge = { Icon(Icons.Default.Favorite, "Premium", tint = BlushPink, modifier = Modifier.size(16.dp)) }
                )
                NavigationDrawerItem(
                    icon = { Text("\uD83D\uDCAC", fontSize = 18.sp) },
                    label = { Text("Ask Saathi", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToChat()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = OutlineVariant.copy(0.5f), modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Text("\uD83D\uDEAA", fontSize = 18.sp) },
                    label = { Text("Sign Out", fontWeight = FontWeight.SemiBold, color = Color(0xFFE53935)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showSignOutDialog = true
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            HomeMeshBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    HomeTopBar(
                        userName = uiState.userName,
                        onProfileClick = { scope.launch { drawerState.open() } },
                        onNotificationsClick = { onNavigateToNotifications() }
                    )
                }

                item {
                    PhaseTabRow(
                        selectedPhase = uiState.selectedTabPhase,
                        onPhaseSelected = { phase -> viewModel.onPhaseTabSelected(phase) },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (uiState.isFirstLaunch) {
                    item {
                        MedicalDisclaimerBanner(
                            onDismiss = { viewModel.dismissMedicalDisclaimer() },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item {
                    GreetingRow(
                        name = uiState.userName,
                        cycleDay = uiState.cycleDay,
                        phaseName = uiState.selectedTabPhase.displayName,
                        phaseDay = uiState.phaseDayInPhase,
                        todayFlow = uiState.todayFlow,
                        todayMood = uiState.todayMood,
                        wellnessPercent = ((uiState.waterGlasses / 8f) * 100).toInt().coerceAtMost(100),
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 8.dp, bottom = 16.dp)
                    )
                }

                item {
                    HeroCard(
                        cycleDay = uiState.cycleDay,
                        isRestDay = uiState.showRestDay,
                        mascotTip = uiState.mascotTipText,
                        mascotEmotion = uiState.mascotEmotion.toComponentEmotion(),
                        onMascotTap = { viewModel.onMascotTapped() },
                        onDismissTip = { viewModel.onMascotTapped() },
                        onShareTip = {
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, uiState.mascotTipText)
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Share tip"))
                        },
                        onSaveTip = {
                            android.widget.Toast.makeText(context, "Tip bookmarked! \uD83D\uDC97", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onDayClicked = { dayEpoch -> onNavigateToDayLog(dayEpoch) },
                        onRotateTip = { viewModel.rotateTip() },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    )
                }

                item {
                    // Audio tip button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            onClick = {
                                val engine = ttsEngine
                                if (engine != null) {
                                    if (isPlaying) {
                                        engine.stop()
                                        isPlaying = false
                                    } else {
                                        isPlaying = true
                                        engine.speak(
                                            uiState.mascotTipText,
                                            android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            "tip"
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isPlaying) "⏸ Pause" else "🔊 Hear Tip",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                item {
                    PhaseCoachBanner(
                        phaseName = uiState.selectedTabPhase.displayName,
                        phaseDay = uiState.phaseDayInPhase,
                        onViewCoachGuide = { showPhaseCoachSheet = true },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 4.dp)
                    )
                }

                item {
                    CycleInsightsHub(
                        selectedPhase = uiState.selectedTabPhase,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                item {
                    HealthQuerySection(
                        onQuerySelect = { queryType -> viewModel.assessHealth(queryType) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            uiState.healthQueryResult?.let { result ->
                HealthQueryResultModal(
                    result = result,
                    onDismiss = { viewModel.dismissHealthQuery() }
                )
            }

            if (showPhaseCoachSheet) {
                PhaseCoachBottomSheet(
                    phase = uiState.selectedTabPhase,
                    phaseDay = uiState.phaseDayInPhase,
                    tip = uiState.mascotTipText,
                    onDismiss = { showPhaseCoachSheet = false }
                )
            }
        }
    }
}

// ── Sub-composables ─────────────────────────────────────────────────────

@Composable
private fun HomeMeshBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-60).dp, y = (-20).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(0.4f), Color.Transparent)),
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
                    Brush.radialGradient(listOf(MaterialTheme.colorScheme.secondaryContainer.copy(0.3f), Color.Transparent)),
                    CircleShape
                )
                .blur(50.dp)
        )
    }
}

@Composable
private fun HomeTopBar(
    userName: String,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
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
                    .background(PrimaryContainer)
                    .clickable { onProfileClick() },
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

@Composable
private fun GreetingRow(
    name: String,
    cycleDay: Int,
    phaseName: String,
    phaseDay: Int,
    todayFlow: String?,
    todayMood: String?,
    wellnessPercent: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "Hey ${name.ifBlank { "there" }} \uD83C\uDF38",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    ),
                    color = OnSurface
                )
                if (cycleDay > 0) {
                    Text(
                        text = "Day $cycleDay \u2022 $phaseName (Day $phaseDay)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Start tracking today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
            }
            WellnessRing(percent = wellnessPercent)
        }

        if (todayFlow != null || todayMood != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (todayFlow != null) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = BlushPink.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "\uD83D\uDCA7 $todayFlow",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = BlushPink,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                if (todayMood != null) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SoftLavender.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "\uD83C\uDF70 $todayMood",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SoftLavender,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
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
            drawArc(
                color = Color.White.copy(0.5f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
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
    onDayClicked: (Long) -> Unit = {},
    onRotateTip: () -> Unit = {},
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

    LaunchedEffect(Unit) {
        while (true) {
            delay(12000)
            onRotateTip()
        }
    }

    val todayEpoch = LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

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
            WeekStrip(cycleDay = cycleDay, isRestDay = isRestDay, onDayClicked = onDayClicked, todayEpoch = todayEpoch)

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
private fun WeekStrip(
    cycleDay: Int,
    isRestDay: Boolean = false,
    onDayClicked: (Long) -> Unit = {},
    todayEpoch: Long = 0L
) {
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
            val dayEpoch = day.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { onDayClicked(dayEpoch) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhaseCoachBottomSheet(
    phase: CyclePhase,
    phaseDay: Int,
    tip: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coachContent = when (phase) {
        CyclePhase.MENSTRUAL -> listOf(
            "\uD83D\uDECC Prioritize rest \u2014 your uterus is working hard",
            "\uD83C\uDF75 Ginger or chamomile tea reduces cramp intensity",
            "\uD83E\uDDD8 Cat-Cow yoga pose eases lower back pain",
            "\uD83D\uDC8A Iron-rich foods: spinach, lentils, dark chocolate",
            "\uD83D\uDCA7 Stay warm and hydrated; avoid cold drinks"
        )
        CyclePhase.FOLLICULAR -> listOf(
            "\u26A1 Your energy is building \u2014 try a brisk walk or jog",
            "\uD83E\uDD57 Fermented foods boost gut health this phase",
            "\uD83D\uDCDA Great time to learn something new \u2014 brain fog lifts",
            "\uD83C\uDF38 Your skin is clearing up \u2014 less breakouts likely",
            "\uD83D\uDCAA Strength training works great in follicular phase"
        )
        CyclePhase.OVULATORY, CyclePhase.OVULATION -> listOf(
            "\u2728 Peak communication phase \u2014 schedule important talks",
            "\uD83C\uDFAF High energy, high motivation \u2014 tackle big goals",
            "\uD83E\uDD69 Eat cruciferous veg to help metabolize estrogen",
            "\uD83D\uDC83 Confidence is peaking \u2014 say yes to social plans",
            "\uD83C\uDF21\uFE0F Slight temp rise and clear discharge = ovulation signs"
        )
        CyclePhase.LUTEAL -> listOf(
            "\uD83D\uDCD3 Journal your emotions \u2014 luteal brings big feelings",
            "\uD83C\uDF6B Magnesium-rich foods: dark chocolate, almonds, bananas",
            "\uD83C\uDFC3 Moderate cardio > high intensity this phase",
            "\uD83D\uDE34 Prioritize 7-9 hours sleep; progesterone peaks here",
            "\uD83E\uDEB4 Deep breathing exercises calm the nervous system"
        )
        CyclePhase.PMS -> listOf(
            "\uD83D\uDC99 Your emotions are valid \u2014 hormones are real",
            "\uD83E\uDDE0 Evening Primrose Oil may reduce PMS symptoms",
            "\uD83D\uDEC1 Warm baths with Epsom salt ease bloating",
            "\uD83D\uDCF5 Reduce screen time 2 hours before bed",
            "\uD83E\uDD17 Reach out to someone you trust if feeling low"
        )
        else -> listOf(
            "\uD83D\uDCCA Start logging your cycle to unlock personalized tips",
            "\uD83D\uDCC5 Even basic period start/end dates help predictions",
            "\uD83D\uDCA1 Track symptoms daily for the best insights",
            "\uD83C\uDF19 Your body has wisdom \u2014 we help you understand it",
            "\uD83C\uDF38 Every cycle is unique, just like you"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            // Handle
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "${phase.emoji} Phase Coach Guide",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Day $phaseDay of ${phase.displayName} phase",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )
            Text(
                text = "\uD83D\uDCA1 Today's Tip",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 22.sp
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "\uD83C\uDF3F Phase Recommendations",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            coachContent.forEachIndexed { index, content ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "\u26A0\uFE0F Information is general and not medical advice.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MedicalDisclaimerBanner(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "\u26A0\uFE0F Medical Disclaimer",
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
                text = "Guides",
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
                val guideContext = LocalContext.current
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
                                .clickable {
                                    isPlaying = !isPlaying
                                    if (isPlaying) {
                                        android.widget.Toast.makeText(guideContext, "Audio guide coming soon \uD83C\uDFA7", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthQueryResultModal(
    result: HealthQueryResult,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = result.question,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = result.headline,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = result.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Action Items",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            result.actionItems.forEach { action ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("\u2022", modifier = Modifier.padding(end = 8.dp), color = Primary)
                    Text(text = action, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = result.disclaimer,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HealthQuerySection(
    onQuerySelect: (QueryType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Ask Period Saathi",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = OnSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HealthQueryChip(text = "Am I pregnant?", onClick = { onQuerySelect(QueryType.PREGNANCY_RISK) }, modifier = Modifier.weight(1f))
            HealthQueryChip(text = "Why is it late?", onClick = { onQuerySelect(QueryType.LATE_PERIOD) }, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HealthQueryChip(text = "Normal cramps?", onClick = { onQuerySelect(QueryType.CRAMPS_NORMAL) }, modifier = Modifier.weight(1f))
            HealthQueryChip(text = "Acne breakout?", onClick = { onQuerySelect(QueryType.ACNE_REASONS) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun HealthQueryChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.5f),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Primary,
            modifier = Modifier.padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}
