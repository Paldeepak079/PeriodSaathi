package com.example.periodsaathi.ui.wellness

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.components.SaathiMascot
import com.example.periodsaathi.ui.components.MascotEmotion
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.Lavender
import com.example.periodsaathi.ui.theme.SoftCoral
import com.example.periodsaathi.ui.theme.SoftLavender
import kotlin.math.sin

@Composable
fun WellnessScreen(
    viewModel: WellnessViewModel = hiltViewModel()
) {
    val todayWater by viewModel.todayWater.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val totalPoints by viewModel.totalPoints.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val streakCount by viewModel.streakCount.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()

    var showCustomWaterDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    text = "Wellness Tracker 🌸",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                WaterIntakeHero(
                    currentGlasses = todayWater,
                    goalGlasses = WellnessViewModel.WATER_GOAL,
                    onAddWater = { glasses -> viewModel.addWater(glasses) },
                    onCustomTap = { showCustomWaterDialog = true }
                )
            }

            item {
                HabitsChecklist(
                    habits = habits,
                    onHabitToggle = { habitId -> viewModel.toggleHabit(habitId) }
                )
            }

            item {
                RewardsProgress(
                    totalPoints = totalPoints,
                    rewards = rewards,
                    onRewardClick = { rewardId -> viewModel.unlockReward(rewardId) },
                    onApplyTheme = { themeId -> viewModel.applyTheme(themeId) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showConfetti) {
            ConfettiOverlay()
        }
    }

    if (showCustomWaterDialog) {
        CustomWaterDialog(
            onDismiss = { showCustomWaterDialog = false },
            onConfirm = { amount ->
                viewModel.addWater(amount)
                showCustomWaterDialog = false
            }
        )
    }
}

@Composable
fun WaterIntakeHero(
    currentGlasses: Int,
    goalGlasses: Int,
    onAddWater: (Int) -> Unit,
    onCustomTap: () -> Unit
) {
    val view = LocalView.current
    val progress = (currentGlasses.toFloat() / goalGlasses).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "waterProgress"
    )

    val isFull = currentGlasses >= goalGlasses

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        CircularWaterRing(
            progress = animatedProgress,
            currentGlasses = currentGlasses,
            goalGlasses = goalGlasses,
            isFull = isFull,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PastelChip(
                text = "+1 Glass",
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onAddWater(1)
                },
                modifier = Modifier.weight(1f)
            )
            PastelChip(
                text = "+500ml",
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onAddWater(1)
                },
                modifier = Modifier.weight(1f)
            )
            PastelChip(
                text = "Custom",
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onCustomTap()
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CircularWaterRing(
    progress: Float,
    currentGlasses: Int,
    goalGlasses: Int,
    isFull: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleRotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2 - 16.dp.toPx()
            val strokeWidth = 16.dp.toPx()

            drawCircle(
                color = Color(0xFFE0E0E0),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = strokeWidth)
            )

            val sweepAngle = progress * 360f
            drawArc(
                color = if (isFull) BabyBlue else BabyBlue.copy(alpha = 0.7f),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            if (isFull) {
                val glowX = centerX + radius * kotlin.math.cos(Math.toRadians((-90 + sweepAngle).toDouble())).toFloat()
                val glowY = centerY + radius * kotlin.math.sin(Math.toRadians((-90 + sweepAngle).toDouble())).toFloat()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(BabyBlue.copy(alpha = 0.6f), Color.Transparent),
                        center = Offset(glowX, glowY),
                        radius = strokeWidth * 2
                    ),
                    radius = strokeWidth * 2,
                    center = Offset(glowX, glowY)
                )

                for (i in 0 until 6) {
                    val angle = (sparkleRotation + i * 60) * (Math.PI / 180)
                    val starRadius = radius * 0.3f
                    val starX = centerX + starRadius * kotlin.math.cos(angle).toFloat()
                    val starY = centerY + starRadius * kotlin.math.sin(angle).toFloat()
                    drawStar(Offset(starX, starY), 4.dp.toPx(), Color.White)
                }
            }

            val waterLevel = radius * progress
            drawWaveInsideCircle(
                centerX = centerX,
                centerY = centerY,
                radius = radius - strokeWidth / 2,
                waterLevel = waterLevel,
                wavePhase = wavePhase
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$currentGlasses",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = if (isFull) BabyBlue else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "/ $goalGlasses",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun DrawScope.drawWaveInsideCircle(
    centerX: Float,
    centerY: Float,
    radius: Float,
    waterLevel: Float,
    wavePhase: Float
) {
    val amplitude = 8.dp.toPx()
    val frequency = 0.05f

    val path1 = Path().apply {
        val baseY = centerY + radius - waterLevel
        moveTo(centerX - radius, centerY + radius)

        for (x in (centerX - radius).toInt()..(centerX + radius).toInt() step 2) {
            val y = baseY + amplitude * sin(frequency * x + wavePhase)
            lineTo(x.toFloat(), y)
        }

        lineTo(centerX + radius, centerY + radius)
        close()
    }

    drawPath(path1, BabyBlue.copy(alpha = 0.4f))

    val path2 = Path().apply {
        val baseY = centerY + radius - waterLevel + 5.dp.toPx()
        moveTo(centerX - radius, centerY + radius)

        for (x in (centerX - radius).toInt()..(centerX + radius).toInt() step 2) {
            val y = baseY + (amplitude * 0.7f) * sin(frequency * x + wavePhase + Math.PI.toFloat())
            lineTo(x.toFloat(), y)
        }

        lineTo(centerX + radius, centerY + radius)
        close()
    }

    drawPath(path2, BabyBlue.copy(alpha = 0.25f))
}

private fun DrawScope.drawStar(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        val outerRadius = size
        val innerRadius = size * 0.4f
        val points = 5

        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val angle = Math.toRadians((i * 360.0 / (points * 2)) - 90)
            val x = center.x + (r * kotlin.math.cos(angle)).toFloat()
            val y = center.y + (r * kotlin.math.sin(angle)).toFloat()

            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path, color)
}

@Composable
fun PastelChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chipScale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                isPressed = true
                onClick()
            },
        color = SoftLavender.copy(alpha = 0.3f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
fun HabitsChecklist(
    habits: List<HabitItem>,
    onHabitToggle: (String) -> Unit
) {
    Column {
        Text(
            text = "Today's Self-Care 🌸",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        habits.forEach { habit ->
            HabitRow(
                habit = habit,
                onToggle = { onHabitToggle(habit.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HabitRow(
    habit: HabitItem,
    onToggle: () -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rowScale"
    )

    var showPointsToast by remember { mutableStateOf(false) }
    var toastOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(showPointsToast) {
        if (showPointsToast) {
            kotlinx.coroutines.delay(1500)
            showPointsToast = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (habit.isCompleted) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            habit.color.copy(alpha = 0.15f),
                            habit.color.copy(alpha = 0.05f)
                        )
                    )
                } else Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .clickable {
                if (!habit.isCompleted) {
                    isAnimating = true
                    showPointsToast = true
                    onToggle()
                }
            }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = habit.emoji,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = habit.label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textDecoration = if (habit.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (habit.isCompleted) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            AnimatedCheckbox(
                isChecked = habit.isCompleted,
                color = habit.color
            )

            if (showPointsToast && !habit.isCompleted) {
                Text(
                    text = "+${habit.pointValue} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = BlushPink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .graphicsLayer {
                            alpha = if (toastOffset < -20f) 0f else 1f - (toastOffset / -40f)
                        }
                )
            }
        }
    }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            kotlinx.coroutines.delay(200)
            isAnimating = false
        }
    }

    LaunchedEffect(showPointsToast) {
        if (showPointsToast) {
            kotlinx.coroutines.delay(50)
            toastOffset = -40f
        } else {
            toastOffset = 0f
        }
    }
}

@Composable
fun AnimatedCheckbox(
    isChecked: Boolean,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "checkbox")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashRotation"
    )

    val checkProgress by animateFloatAsState(
        targetValue = if (isChecked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkProgress"
    )

    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!isChecked) {
                val dashLength = 8.dp.toPx()
                val gapLength = 4.dp.toPx()
                val pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(dashLength, gapLength),
                    rotation
                )

                drawCircle(
                    color = color,
                    radius = size.minDimension / 2 - 2.dp.toPx(),
                    style = Stroke(width = 2.dp.toPx(), pathEffect = pathEffect)
                )
            } else {
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(color, color.copy(alpha = 0.8f))
                    ),
                    radius = size.minDimension / 2 - 2.dp.toPx()
                )

                val checkPath = Path().apply {
                    moveTo(size.width * 0.25f, size.height * 0.5f)
                    lineTo(size.width * 0.45f, size.height * 0.7f)
                    lineTo(size.width * 0.75f, size.height * 0.3f)
                }
                drawPath(
                    path = checkPath,
                    color = Color.White,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun RewardsProgress(
    totalPoints: Int,
    rewards: List<Reward>,
    onRewardClick: (String) -> Unit,
    onApplyTheme: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Unlock Rewards ✨",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val nextLockedReward = rewards.firstOrNull { !it.isUnlocked }
            if (nextLockedReward != null) {
                val progress = (totalPoints.toFloat() / nextLockedReward.requiredPoints).coerceIn(0f, 1f)

                CustomProgressBar(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$totalPoints / ${nextLockedReward.requiredPoints} pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (totalPoints >= nextLockedReward.requiredPoints) {
                        Text(
                            text = "🎉 Unlocked!",
                            style = MaterialTheme.typography.labelMedium,
                            color = BlushPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rewards",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rewards) { reward ->
                    RewardCard(
                        reward = reward,
                        onClick = { onRewardClick(reward.id) },
                        onApply = { onApplyTheme(reward.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progressAnim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Canvas(modifier = modifier) {
        val cornerRadius = size.height / 2

        drawRoundRect(
            color = Color(0xFFE0E0E0),
            size = Size(size.width, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        val progressWidth = size.width * animatedProgress
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(BlushPink, Lavender),
                start = Offset(0f, 0f),
                end = Offset(progressWidth, 0f)
            ),
            size = Size(progressWidth, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        if (animatedProgress > 0.05f) {
            drawCircle(
                color = BlushPink.copy(alpha = glowAlpha),
                radius = size.height * 0.8f,
                center = Offset(progressWidth, size.height / 2)
            )
        }
    }
}

@Composable
fun RewardCard(
    reward: Reward,
    onClick: () -> Unit,
    onApply: () -> Unit
) {
    var isApplying by remember { mutableStateOf(false) }
    var showUnlockAnimation by remember { mutableStateOf(false) }

    val rotationY by animateFloatAsState(
        targetValue = if (isApplying) 180f else 0f,
        animationSpec = tween(400),
        label = "flip"
    )

    val scale by animateFloatAsState(
        targetValue = if (showUnlockAnimation) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    LaunchedEffect(showUnlockAnimation) {
        if (showUnlockAnimation) {
            kotlinx.coroutines.delay(500)
            showUnlockAnimation = false
        }
    }

    Card(
        modifier = Modifier
            .size(120.dp, 160.dp)
            .scale(scale)
            .graphicsLayer {
                rotationY = rotationY
                cameraDistance = 12f * density
            }
            .clickable {
                if (!reward.isUnlocked) {
                    onClick()
                }
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reward.isUnlocked) {
                reward.themeColors?.firstOrNull()?.copy(alpha = 0.2f)
                    ?: MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (reward.isUnlocked) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotationY < 90f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    if (reward.isUnlocked) {
                        Text(
                            text = if (reward.themeColors != null) "🎨" else "✨",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = reward.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                isApplying = true
                                showUnlockAnimation = true
                                onApply()
                            },
                            modifier = Modifier.height(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlushPink
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Apply",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    } else {
                        Text(
                            text = "🔒",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = reward.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${reward.requiredPoints} pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🎉",
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unlocked!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BlushPink
                    )
                }
            }
        }
    }
}

@Composable
fun CustomWaterDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Amount") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() } },
                label = { Text("Glasses") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount.toIntOrNull()?.let { onConfirm(it.coerceAtLeast(1)) }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfettiOverlay() {
    val particles = remember {
        List(50) {
            ConfettiParticle(
                x = (Math.random() * 1000).toFloat(),
                y = (Math.random() * -200 - 100).toFloat(),
                color = listOf(BlushPink, SoftLavender, BabyBlue, SoftCoral).random(),
                size = (Math.random() * 8 + 4).toFloat(),
                velocityY = (Math.random() * 5 + 3).toFloat(),
                velocityX = (Math.random() * 4 - 2).toFloat()
            )
        }
    }

    var animationProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = animationProgress }
    ) {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(
                    particle.x + particle.velocityX * animationProgress * 20,
                    particle.y + particle.velocityY * animationProgress * 30
                )
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val velocityY: Float,
    val velocityX: Float
)