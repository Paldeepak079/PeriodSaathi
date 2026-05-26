package com.deepak.periodsaathi.ui.lock

import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.deepak.periodsaathi.security.AppBiometricManager
import com.deepak.periodsaathi.security.BiometricStatus
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.SaathiMascot
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Star(
    val x: Float,
    val initialY: Float,
    val size: Float,
    val alpha: Float,
    val speedMultiplier: Float
)

private data class SleepParticle(
    val startX: Float,
    val startY: Float,
    val size: Float,
    val speed: Float,
    val phase: Float
)

/**
 * Returns true for hardware-level / permanent errors that cannot be retried
 * with biometrics and require a PIN fallback.
 */
private fun isFatalBiometricError(errorCode: Int): Boolean = errorCode in listOf(
    BiometricPrompt.ERROR_HW_NOT_PRESENT,
    BiometricPrompt.ERROR_HW_UNAVAILABLE,
    BiometricPrompt.ERROR_NO_BIOMETRICS,
    BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL,
    BiometricPrompt.ERROR_LOCKOUT_PERMANENT
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

    val sleepParticles = remember {
        List(12) {
            SleepParticle(
                startX = Random.nextFloat() * 0.6f + 0.2f,
                startY = Random.nextFloat() * 0.3f + 0.6f,
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.3f + 0.15f,
                phase = Random.nextFloat() * 6.28f
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

    val gradientTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientTime"
    )

    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )

    val mascotScale = remember { Animatable(1f) }
    val textAlpha = remember { Animatable(0f) }
    var unlockState by remember { mutableStateOf(UnlockState.Idle) }
    var biometricErrorMessage by remember { mutableStateOf<String?>(null) }

    val shakeOffset = remember { Animatable(0f) }
    val brightenAlpha = remember { Animatable(0f) }
    val failGlowAlpha = remember { Animatable(0f) }

    val biometricManager = remember { AppBiometricManager(context) }

    // Reusable function to trigger the native biometric dialog
    fun triggerBiometric() {
        activity?.let { act ->
            biometricManager.authenticate(
                activity = act,
                onSuccess = {
                    biometricErrorMessage = null
                    unlockState = UnlockState.Success
                },
                onFailed = {
                    // A single biometric scan failed — animate the button but keep screen open
                    unlockState = UnlockState.Failed
                },
                onError = { errMsg, errorCode ->
                    if (isFatalBiometricError(errorCode)) {
                        // Hardware unavailable / permanently locked — go to PIN
                        onPinFallback()
                    } else {
                        // User cancelled (ERROR_NEGATIVE_BUTTON / ERROR_USER_CANCELED)
                        // or temporary lockout — show message and let them retry
                        biometricErrorMessage = errMsg
                        unlockState = UnlockState.Idle
                    }
                }
            )
        } ?: onPinFallback() // Context is not a FragmentActivity — go straight to PIN
    }

    // Mascot breathing animation
    LaunchedEffect(Unit) {
        mascotScale.animateTo(
            1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Fade in text
    LaunchedEffect(Unit) {
        delay(500)
        textAlpha.animateTo(1f, tween(500))
    }

    // Auto-trigger biometric prompt once the screen has settled
    LaunchedEffect(Unit) {
        delay(600) // Let the entrance animation finish first
        when (biometricManager.isBiometricAvailable()) {
            BiometricStatus.Available -> triggerBiometric()
            BiometricStatus.NotEnrolled -> {
                // No biometrics enrolled — go straight to PIN (which will run setup if needed)
                onPinFallback()
            }
            BiometricStatus.NotAvailable -> Unit // nothing — show button only
        }
    }

    val nightColors = listOf(
        Color(0xFF0D0A1A), Color(0xFF1A0E2E), Color(0xFF2D1B4E), Color(0xFF1A0E2E)
    )
    val gradientProgress = gradientTime * (nightColors.size - 1)
    val colorIndex = gradientProgress.toInt().coerceAtMost(nightColors.size - 2)
    val colorFraction = gradientProgress - colorIndex
    val bgColor = nightColors[colorIndex].let { c1 ->
        nightColors[colorIndex + 1].let { c2 ->
            Color(
                red = c1.red * (1f - colorFraction) + c2.red * colorFraction,
                green = c1.green * (1f - colorFraction) + c2.green * colorFraction,
                blue = c1.blue * (1f - colorFraction) + c2.blue * colorFraction,
                alpha = 1f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Animated star / particle canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradientCenterX = size.width * 0.5f
            val gradientCenterY = size.height * 0.5f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3A1F6A).copy(alpha = 0.3f),
                        Color(0xFF1A0E2E).copy(alpha = 0.5f),
                        bgColor.copy(alpha = 0.8f)
                    ),
                    center = Offset(
                        gradientCenterX + sin(gradientTime * 6.28f) * size.width * 0.05f,
                        gradientCenterY + cos(gradientTime * 6.28f) * size.height * 0.05f
                    ),
                    radius = size.width * 0.7f
                ),
                radius = size.width
            )

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

            sleepParticles.forEach { p ->
                val progress = (particleTime + p.phase) % 1f
                val yOff = -progress * size.height * p.speed * 1.5f
                val xOff = sin(progress * 6.28f * 0.5f) * 20f
                val alpha = (1f - progress).coerceIn(0f, 1f) * 0.6f
                val radius = p.size * density.density * (1f - progress * 0.4f)
                drawCircle(
                    color = Color(0xFFB8A9D4).copy(alpha = alpha),
                    radius = radius,
                    center = Offset(
                        p.startX * size.width + xOff,
                        p.startY * size.height + yOff
                    )
                )
            }
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(textAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // Shake offset applied via padding trick on the mascot column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = shakeOffset.value.dp)
            ) {
                MascotSleepingSection(mascotScale.value)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Saathi is resting 😴",
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

            SleepParticlesCanvas()

            Spacer(modifier = Modifier.weight(0.1f))

            UnlockButton(
                biometricStatus = biometricManager.isBiometricAvailable(),
                glowAlpha = failGlowAlpha.value,
                errorMessage = biometricErrorMessage,
                onClick = {
                    biometricErrorMessage = null
                    when (biometricManager.isBiometricAvailable()) {
                        BiometricStatus.Available -> triggerBiometric()
                        BiometricStatus.NotEnrolled,
                        BiometricStatus.NotAvailable -> onPinFallback()
                    }
                }
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        // Success / Failure overlay states
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
                            SaathiMascot(
                                emotion = MascotEmotion.EXCITED,
                                size = 120.dp
                            )
                        }
                        WakePhase.Speech -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Good Morning! 🌸",
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
                    failGlowAlpha.animateTo(1f, tween(200))
                    repeat(3) {
                        shakeOffset.animateTo(8f, tween(60))
                        shakeOffset.animateTo(-8f, tween(60))
                    }
                    shakeOffset.animateTo(0f, tween(60))
                    failGlowAlpha.animateTo(0f, tween(400))
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

@Composable
private fun SleepParticlesCanvas() {
    val particles = remember {
        List(8) { index ->
            SleepParticle(
                startX = Random.nextFloat() * 0.5f + 0.25f,
                startY = 0f,
                size = Random.nextFloat() * 3f + 2f,
                speed = Random.nextFloat() * 0.3f + 0.2f,
                phase = index.toFloat() * 0.8f
            )
        }
    }

    val localDensity = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition(label = "sleepParticles")
    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleProg"
    )

    Canvas(modifier = Modifier.size(100.dp, 60.dp)) {
        particles.forEach { p ->
            val progress = (particleTime + p.phase) % 1f
            val y = size.height * (1f - progress)
            val xOff = sin(progress * 6.28f * 2f) * 15f
            val alpha = (1f - progress).coerceIn(0f, 1f) * 0.5f
            val radius = p.size * localDensity.density * (1f - progress * 0.3f)
            drawCircle(
                color = Color(0xFFB8A9D4).copy(alpha = alpha),
                radius = radius,
                center = Offset(p.startX * size.width + xOff, y)
            )
        }
    }
}

@Composable
private fun UnlockButton(
    biometricStatus: BiometricStatus,
    glowAlpha: Float = 0f,
    errorMessage: String? = null,
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

    val buttonBg = if (glowAlpha > 0.01f) {
        Color(0xFFE53935).copy(alpha = 0.2f + glowAlpha * 0.3f)
    } else {
        Color.White.copy(alpha = 0.08f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(buttonBg)
                .alpha(pulseAlpha + glowAlpha * 0.3f)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (glowAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFE53935).copy(alpha = glowAlpha * 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (biometricStatus == BiometricStatus.Available) "🔓" else "🔑",
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

        // Show transient error message (e.g. "Try again" after user cancelled)
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color(0xFFFFB74D),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
