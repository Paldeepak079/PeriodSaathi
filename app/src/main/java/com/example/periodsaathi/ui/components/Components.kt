package com.example.periodsaathi.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.domain.model.CyclePhase
import com.example.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

// ==================== DESIGN SYSTEM ====================
private val CardShape = RoundedCornerShape(28.dp)
private val ButtonShape = RoundedCornerShape(50.dp)
private val ChipShape = RoundedCornerShape(20.dp)
private val BannerShape = RoundedCornerShape(24.dp)

// ==================== GLASSCARD ====================
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "glassCardScale"
    )

    val contentModifier = modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )

    Box(contentModifier = contentModifier) {
        Card(
            modifier = Modifier.matchParentSize(),
            shape = CardShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.45f)
            ),
            tonalElevation = 0.dp,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
            ) {
                content()
            }
        }

        // Custom soft pink shadow
        Canvas(modifier = Modifier.matchParentSize()) {
            drawSoftShadow(
                color = BlushPink.copy(alpha = 0.3f),
                blurRadius = 16.dp.toPx(),
                offsetY = 4.dp.toPx()
            )
        }
    }
}

private fun DrawScope.drawSoftShadow(color: Color, blurRadius: Float, offsetY: Float) {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = color
            maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.Normal)
        }
        canvas.drawRoundRect(
            left = 0f + blurRadius / 2,
            top = 0f + blurRadius / 2 + offsetY,
            right = size.width - blurRadius / 2,
            bottom = size.height - blurRadius / 2 + offsetY,
            cornerRadius = 28.dp.toPx(),
            paint = paint
        )
    }
}

// ==================== PRIMARY BUTTON ====================
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            else -> 1.02f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale",
        finishedListener = {
            // Spring back to 1.0 after reaching 1.02
        }
    )

    // Animate to 1.0 after the bounce
    LaunchedEffect(scale) {
        if (scale >= 1.01f) {
            delay(50)
        }
    }

    val finalScale = if (scale > 1.01f) 1f else scale

    val buttonModifier = modifier
        .fillMaxWidth()
        .height(56.dp)
        .graphicsLayer {
            scaleX = finalScale
            scaleY = finalScale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled && !isLoading,
            onClick = onClick
        )

    Button(
        onClick = {},
        modifier = buttonModifier,
        shape = ButtonShape,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = BlushPink.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (enabled) listOf(BlushPink, DeepRose)
                        else listOf(BlushPink.copy(alpha = 0.5f), DeepRose.copy(alpha = 0.5f))
                    ),
                    shape = ButtonShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ==================== SECONDARY BUTTON ====================
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "secondaryButtonScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(ButtonShape)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = if (enabled) listOf(BlushPink, BlushPink)
                    else listOf(BlushPink.copy(alpha = 0.5f), BlushPink.copy(alpha = 0.5f))
                ),
                shape = ButtonShape
            )
            .background(
                color = Color.Transparent,
                shape = ButtonShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) BlushPink else BlushPink.copy(alpha = 0.5f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

// ==================== PASTEL CHIP ====================
@Composable
fun PastelChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = BlushPink,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chipScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) color else Color.White,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipBackground"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.3f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipBorder"
    )

    Box(
        modifier = modifier
            .height(36.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(ChipShape)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = ChipShape
            )
            .background(
                color = backgroundColor,
                shape = ChipShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
            Text(
                text = label,
                color = if (selected) Color.White else Color.Gray,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

// ==================== CYCLE RING ====================
@Composable
fun CycleRing(
    currentDay: Int,
    totalDays: Int,
    phase: CyclePhase,
    modifier: Modifier = Modifier
) {
    val progress = currentDay.toFloat() / totalDays.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cycleProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glowPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 24.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background ring
            drawCircle(
                color = Color.Gray.copy(alpha = 0.15f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Glow effect
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        phase.color().copy(alpha = glowAlpha),
                        phase.color().copy(alpha = glowAlpha * 0.5f),
                        phase.color().copy(alpha = glowAlpha)
                    ),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth + 8.dp.toPx())
            )

            // Progress arc with gradient
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(BlushPink, SoftCoral, SoftLavender),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentDay.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = phase.color()
            )
            Text(
                text = "days",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// ==================== WATER RING ====================
@Composable
fun WaterRing(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    onFull: () -> Unit = {}
) {
    val progress = current.toFloat() / total.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "waterProgress"
    )

    var hasTriggeredConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(current, total) {
        if (current >= total && !hasTriggeredConfetti) {
            hasTriggeredConfetti = true
            onFull()
        }
        if (current < total) {
            hasTriggeredConfetti = false
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "waveAnimation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background ring
            drawCircle(
                color = BabyBlue.copy(alpha = 0.15f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Water progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(BabyBlue, BabyBlue.copy(alpha = 0.7f), BabyBlue),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Wave effect inside
            if (animatedProgress > 0.1f) {
                val waveRadius = radius * animatedProgress
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BabyBlue.copy(alpha = 0.3f),
                            BabyBlue.copy(alpha = 0.1f)
                        )
                    ),
                    radius = waveRadius - strokeWidth / 2,
                    center = center
                )
            }
        }

        Text(
            text = "$current/$total",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BabyBlue
        )
    }
}

// ==================== PHASE INDICATOR BANNER ====================
@Composable
fun PhaseIndicatorBanner(
    phase: CyclePhase,
    cycleDay: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bannerGradient")
    val hueShift by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hueShift"
    )

    val backgroundColor = phase.color()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(BannerShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.8f)
                    )
                )
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = phase.emoji(),
                fontSize = 32.sp
            )
            Column {
                Text(
                    text = phase.displayName(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "Day $cycleDay",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ==================== CONFETTI OVERLAY ====================
@Composable
fun ConfettiOverlay(
    visible: Boolean,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val particles = remember {
        List(60) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = -Random.nextFloat() * 0.2f,
                velocityX = (Random.nextFloat() - 0.5f) * 0.3f,
                velocityY = Random.nextFloat() * 0.5f + 0.2f,
                color = listOf(BlushPink, SoftLavender, BabyBlue, ButterYellow, SoftCoral, MintGreen)
                    .random(),
                size = Random.nextFloat() * 8f + 4f,
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    var animationProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            animationProgress = 0f
            val startTime = System.nanoTime()
            while (animationProgress < 1f) {
                val elapsed = (System.nanoTime() - startTime) / 1_000_000_000f
                animationProgress = (elapsed / 2f).coerceAtMost(1f)
                delay(16)
            }
            onComplete()
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val gravity = 0.002f

        particles.forEach { particle ->
            val currentX = (particle.x + particle.velocityX * animationProgress) * size.width
            val currentY = (particle.y + particle.velocityY * animationProgress + gravity * animationProgress * animationProgress) * size.height
            val currentRotation = particle.rotation + animationProgress * 720f

            drawCircle(
                color = particle.color.copy(alpha = (1f - animationProgress * 0.5f)),
                radius = particle.size,
                center = Offset(currentX, currentY)
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val size: Float,
    val rotation: Float
)

// ==================== MASCOT TIP BUBBLE ====================
@Composable
fun MascotTipBubble(
    tip: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bubbleScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bubbleAlpha"
    )

    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(tip, visible) {
        if (visible) {
            displayedText = ""
            tip.forEachIndexed { index, char ->
                delay(30)
                displayedText = tip.substring(0, index + 1)
            }
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable(onClick = onDismiss)
            .padding(16.dp)
    ) {
        Column {
            // Bubble body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = displayedText,
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
            }

            // Triangle tail
            Canvas(
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = 40.dp)
            ) {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(10f, 15f)
                        lineTo(20f, 0f)
                        close()
                    },
                    color = Color.White
                )
            }
        }
    }
}

// ==================== REST DAY BANNER ====================
@Composable
fun RestDayBanner(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "restBannerScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "heartParticles")
    val heartOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heartOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SoftCoral.copy(alpha = 0.3f),
                                BlushPink.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🛌 REST DAY",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepRose
                        )
                        Text(
                            text = "Take it easy today. Your body deserves rest.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // Animated hearts
                    Canvas(modifier = Modifier.size(40.dp)) {
                        val heartY = heartOffset * 20 - 10
                        drawCircle(
                            color = DeepRose.copy(alpha = 0.6f),
                            radius = 8f,
                            center = Offset(10f, heartY)
                        )
                        drawCircle(
                            color = DeepRose.copy(alpha = 0.4f),
                            radius = 6f,
                            center = Offset(30f, heartY + 15f)
                        )
                    }
                }
            }
        }
    }
}

// Helper extension function for phase color
private fun CyclePhase.color(): Color = when (this) {
    CyclePhase.MENSTRUAL -> BlushPink
    CyclePhase.FOLLICULAR -> SoftLavender
    CyclePhase.OVULATION -> BabyBlue
    CyclePhase.LUTEAL -> Color(0xFFC9B8FF)
}

private fun CyclePhase.displayName(): String = when (this) {
    CyclePhase.MENSTRUAL -> "Menstrual Phase"
    CyclePhase.FOLLICULAR -> "Follicular Phase"
    CyclePhase.OVULATION -> "Ovulation Phase"
    CyclePhase.LUTEAL -> "Luteal Phase"
}

private fun CyclePhase.emoji(): String = when (this) {
    CyclePhase.MENSTRUAL -> "🌸"
    CyclePhase.FOLLICULAR -> "✨"
    CyclePhase.OVULATION -> "💫"
    CyclePhase.LUTEAL -> "🌙"
}