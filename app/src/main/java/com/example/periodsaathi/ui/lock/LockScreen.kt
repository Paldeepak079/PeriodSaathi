package com.example.periodsaathi.ui.lock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.periodsaathi.security.AppBiometricManager
import com.example.periodsaathi.security.BiometricStatus
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

private data class Star(
    val x: Float,
    val initialY: Float,
    val size: Float,
    val alpha: Float,
    val speedMultiplier: Float
)

@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    onPinFallback: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val density = LocalDensity.current
    val screenHeightDp = 800f
    val screenHeightPx = with(density) { screenHeightDp.dp.toPx() }

    val stars = remember {
        List(50) {
            Star(
                x = Random.nextFloat(),
                initialY = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 2f,
                alpha = Random.nextFloat() * 0.5f + 0.3f,
                speedMultiplier = Random.nextFloat() * 0.7f + 0.3f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val timeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = screenHeightPx,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starTime"
    )

    val mascotScale = remember { Animatable(1f) }
    val textAlpha = remember { Animatable(0f) }
    var unlockState by remember { mutableStateOf(UnlockState.Idle) }

    LaunchedEffect(Unit) {
        mascotScale.animateTo(
            1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(500)
        textAlpha.animateTo(1f, tween(500))
    }

    val shakeOffset = remember { Animatable(0f) }
    val brightenAlpha = remember { Animatable(0f) }

    val biometricManager = remember { AppBiometricManager(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A0E2E))
            .offset(x = with(density) { shakeOffset.value.dp })
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                val xPos = star.x * size.width
                val rawY = star.initialY * size.height + timeOffset * star.speedMultiplier
                val yPos = if (rawY > size.height) rawY - size.height else rawY
                val alphaAdjusted = star.alpha * (1f - (yPos / size.height) * 0.5f)

                drawCircle(
                    color = Color.White.copy(alpha = alphaAdjusted.coerceIn(0f, 1f)),
                    radius = star.size * density.density,
                    center = Offset(xPos, yPos)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(textAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            MascotSleepingSection(mascotScale.value)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Saathi is resting \uD83D\uDE34",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB8A9D4),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Verify to wake her up",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.3f))

            ZzzBubbles()

            Spacer(modifier = Modifier.weight(0.1f))

            UnlockButton(
                biometricStatus = biometricManager.isBiometricAvailable(),
                onClick = {
                    activity?.let { act ->
                        biometricManager.authenticate(
                            activity = act,
                            onSuccess = {
                                unlockState = UnlockState.Success
                            },
                            onFailed = {
                                unlockState = UnlockState.Failed
                            },
                            onError = { _ ->
                                onPinFallback()
                            }
                        )
                    } ?: onPinFallback()
                }
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        when (unlockState) {
            UnlockState.Success -> {
                var wakePhase by remember { mutableStateOf(WakePhase.Brightening) }

                LaunchedEffect(Unit) {
                    brightenAlpha.animateTo(1f, tween(600))
                    wakePhase = WakePhase.Waking
                    delay(400)
                    wakePhase = WakePhase.Speech
                    delay(1200)
                    onUnlocked()
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = brightenAlpha.value * 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    when (wakePhase) {
                        WakePhase.Brightening -> {}
                        WakePhase.Waking -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFFFFD9DE),
                                                    Color(0xFFFDF6F0)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "\uD83C\uDF38",
                                        fontSize = 48.sp,
                                        modifier = Modifier.scale(1.2f)
                                    )
                                }
                            }
                        }
                        WakePhase.Speech -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Good Morning! \uD83C\uDF38",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            UnlockState.Failed -> {
                LaunchedEffect(Unit) {
                    repeat(3) {
                        shakeOffset.animateTo(8f, tween(60))
                        shakeOffset.animateTo(-8f, tween(60))
                    }
                    shakeOffset.animateTo(0f, tween(60))
                    unlockState = UnlockState.Idle
                }
            }
            UnlockState.Idle -> {}
        }
    }
}

enum class UnlockState { Idle, Success, Failed }
enum class WakePhase { Brightening, Waking, Speech }

@Composable
private fun MascotSleepingSection(currentScale: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(currentScale)
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2D1B4E).copy(alpha = 0.6f),
                            Color(0xFF1A0E2E)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8D5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", fontSize = 10.sp, color = Color(0xFF1A0E2E))
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8D5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", fontSize = 10.sp, color = Color(0xFF1A0E2E))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp, 20.dp)
                        .clip(RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
                        .background(Color(0xFFD4B8E8))
                )
            }
        }
    }
}

private data class ZzzData(
    val text: String,
    val fontSize: TextUnit,
    val xOffset: Float,
    val yOffset: Float
)

@Composable
private fun ZzzBubbles() {
    val zzzData = remember {
        listOf(
            ZzzData("Z", 24.sp, 0f, 0f),
            ZzzData("z", 18.sp, -16f, 0f),
            ZzzData("\u02FB", 14.sp, 16f, 0f)
        )
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.size(80.dp)) {
            zzzData.forEachIndexed { index, data ->
                val infiniteTransition = rememberInfiniteTransition(label = "zzz_$index")
                val yOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = -60f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, delayMillis = index * 700, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "zzzY_$index"
                )
                val zAlpha by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, delayMillis = index * 700, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "zzzAlpha_$index"
                )
                val zScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, delayMillis = index * 700, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "zzzScale_$index"
                )

                Text(
                    text = data.text,
                    fontSize = data.fontSize,
                    color = Color(0xFFB8A9D4).copy(alpha = zAlpha),
                    modifier = Modifier
                        .offset { IntOffset((data.xOffset + yOffset * 0.3f).roundToInt(), yOffset.roundToInt()) }
                        .graphicsLayer { scaleX = zScale; scaleY = zScale }
                )
            }
        }
    }
}

@Composable
private fun UnlockButton(
    biometricStatus: BiometricStatus,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "unlockPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .alpha(pulseAlpha),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (biometricStatus == BiometricStatus.Available) "\uD83D\uDD13" else "\uD83D\uDD11",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = when (biometricStatus) {
                    BiometricStatus.Available -> "Unlock with Biometrics"
                    BiometricStatus.NotEnrolled -> "Set up biometrics"
                    BiometricStatus.NotAvailable -> "Use PIN to unlock"
                },
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
