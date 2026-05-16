package com.example.periodsaathi.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun HapticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            tryAwaitRelease()
                        },
                        onTap = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            onClick()
                        }
                    )
                }
            }
    ) {
        content()
    }
}

@Composable
fun SpringBounceButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Primary,
    textColor: Color = Color.White
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "springButtonScale"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(50.dp))
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f))
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onTap = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            onClick()
                        }
                    )
                }
            }
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun AnimatedGradientMesh(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(BlushPink, SoftLavender, BabyBlue, ButterYellow)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meshGradient")

    val orb1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb1"
    )

    val orb2Offset by infiniteTransition.animateFloat(
        initialValue = 180f,
        targetValue = 540f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb2"
    )

    val orb3Offset by infiniteTransition.animateFloat(
        initialValue = 90f,
        targetValue = 450f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb3"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Background base
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    WarmCream,
                    WarmCream.copy(alpha = 0.95f)
                )
            )
        )

        // Orb 1 - BlushPink
        val orb1X = width * 0.3f + width * 0.2f * cos(Math.toRadians(orb1Offset.toDouble())).toFloat()
        val orb1Y = height * 0.3f + height * 0.2f * sin(Math.toRadians(orb1Offset.toDouble())).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors[0].copy(alpha = 0.4f), colors[0].copy(alpha = 0f)),
                center = Offset(orb1X, orb1Y),
                radius = width * 0.4f
            ),
            radius = width * 0.4f,
            center = Offset(orb1X, orb1Y)
        )

        // Orb 2 - SoftLavender
        val orb2X = width * 0.7f + width * 0.15f * cos(Math.toRadians(orb2Offset.toDouble())).toFloat()
        val orb2Y = height * 0.5f + height * 0.2f * sin(Math.toRadians(orb2Offset.toDouble())).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors[1].copy(alpha = 0.35f), colors[1].copy(alpha = 0f)),
                center = Offset(orb2X, orb2Y),
                radius = width * 0.35f
            ),
            radius = width * 0.35f,
            center = Offset(orb2X, orb2Y)
        )

        // Orb 3 - BabyBlue
        val orb3X = width * 0.5f + width * 0.2f * cos(Math.toRadians(orb3Offset.toDouble())).toFloat()
        val orb3Y = height * 0.8f + height * 0.15f * sin(Math.toRadians(orb3Offset.toDouble())).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors[2].copy(alpha = 0.3f), colors[2].copy(alpha = 0f)),
                center = Offset(orb3X, orb3Y),
                radius = width * 0.3f
            ),
            radius = width * 0.3f,
            center = Offset(orb3X, orb3Y)
        )
    }
}

@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 25
) {
    val particles = remember {
        List(particleCount) {
            FloatingParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 6f + 2f,
                alpha = Random.nextFloat() * 0.5f + 0.2f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                color = listOf(BlushPink, SoftLavender, BabyBlue, ButterYellow).random()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    particles.forEachIndexed { index, particle ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (3000 / particle.speed).toInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle$index"
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val x = particle.x * size.width
            val y = ((particle.y + offsetY) % 1f) * size.height

            drawCircle(
                color = particle.color.copy(alpha = particle.alpha),
                radius = particle.size.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

private data class FloatingParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val alpha: Float,
    val speed: Float,
    val color: Color
)

@Composable
fun BouncingMascot(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val mascotOffset = remember { Animatable(-300f) }

    LaunchedEffect(Unit) {
        mascotOffset.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleBounce"
    )

    Box(
        modifier = modifier
            .offset(y = (mascotOffset.value + bounce).dp)
            .then(
                if (onClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(onTap = { onClick() })
                    }
                } else Modifier
            )
    ) {
        Text(text = "🌸", fontSize = 100.sp)
    }
}

@Composable
fun ConfettiEffect(
    visible: Boolean,
    onComplete: () -> Unit = {}
) {
    if (!visible) return

    val particles = remember {
        List(60) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = -Random.nextFloat() * 0.2f,
                velocityX = (Random.nextFloat() - 0.5f) * 0.3f,
                velocityY = Random.nextFloat() * 0.5f + 0.2f,
                color = listOf(BlushPink, SoftLavender, BabyBlue, ButterYellow, SoftCoral, MintGreen).random(),
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

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gravity = 0.002f

        particles.forEach { particle ->
            val currentX = (particle.x + particle.velocityX * animationProgress) * size.width
            val currentY = (particle.y + particle.velocityY * animationProgress + gravity * animationProgress * animationProgress) * size.height

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

@Composable
fun AnimatedDotsIndicator(
    totalDots: Int,
    currentDot: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Primary,
    inactiveColor: Color = Color.Gray.copy(alpha = 0.3f)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            val isActive = index == currentDot
            val width by animateDpAsState(
                targetValue = if (isActive) 20.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "dotWidth$index"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isActive) activeColor else inactiveColor)
            )
        }
    }
}

@Composable
fun PulsingDot(
    color: Color = Error,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .size(12.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shimmerColors = listOf(
        Color.Gray.copy(alpha = 0.3f),
        Color.Gray.copy(alpha = 0.1f),
        Color.Gray.copy(alpha = 0.3f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    Box(modifier = modifier) {
        content()
    }
}