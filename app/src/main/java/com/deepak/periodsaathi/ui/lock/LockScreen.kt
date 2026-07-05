package com.deepak.periodsaathi.ui.lock

import androidx.biometric.BiometricPrompt
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
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import com.deepak.periodsaathi.security.AppBiometricManager
import com.deepak.periodsaathi.security.BiometricStatus
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.SaathiMascot
import com.deepak.periodsaathi.ui.components.springClickable
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val sakuraPink = Color(0xFFFFB6C1)
private val lavenderMist = Color(0xFFE6E6FA)
private val roseGold = Color(0xFFE8B4B8)
private val pearlWhite = Color(0xFFFFF0F5)
private val deepRose = Color(0xFF874E58)
private val softGold = Color(0xFFFFD700)

private data class LockPetal(
    var x: Float, var y: Float, val size: Float, val rotation: Float,
    val rotSpeed: Float, val speedY: Float, val speedX: Float,
    val color: Color, val alpha: Float
)
private data class LockSparkle(val x: Float, val y: Float, val size: Float, val phase: Float, val speed: Float)
private data class LockGlow(val x: Float, val y: Float, val size: Float, val phase: Float, val speed: Float, val driftX: Float)

private fun generateLockPetals() = List(14) {
    LockPetal(Random.nextFloat(), Random.nextFloat() * 1.2f - 0.1f, Random.nextFloat() * 10f + 6f, Random.nextFloat() * 360f,
        (Random.nextFloat() - 0.5f) * 30f, Random.nextFloat() * 15f + 8f, (Random.nextFloat() - 0.5f) * 10f,
        if (Random.nextFloat() > 0.5f) sakuraPink else lavenderMist, Random.nextFloat() * 0.3f + 0.15f)
}
private fun generateLockSparkles() = List(20) { LockSparkle(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 2f + 1f, Random.nextFloat() * 2f * PI.toFloat(), Random.nextFloat() * 0.5f + 0.3f) }
private fun generateLockGlows() = List(12) { LockGlow(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 4f + 2f, Random.nextFloat() * 2f * PI.toFloat(), Random.nextFloat() * 0.3f + 0.2f, (Random.nextFloat() - 0.5f) * 20f) }

@Composable
private fun LockScreenBackground() {
    val petals = remember { mutableStateOf(generateLockPetals()) }
    val sparkles = remember { mutableStateOf(generateLockSparkles()) }
    val glows = remember { mutableStateOf(generateLockGlows()) }
    val inf = rememberInfiniteTransition(label = "bg")
    val gradTime by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse), label = "g")
    val partTime by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart), label = "p")
    val sparkTime by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart), label = "s")
    val glowTime by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart), label = "gl")

    Canvas(Modifier.fillMaxSize()) {
        val cx = size.width * (0.5f + 0.08f * sin(gradTime * 6.28f))
        val cy = size.height * (0.5f + 0.08f * cos(gradTime * 6.28f * 0.7f))
        drawRect(Brush.radialGradient(listOf(sakuraPink.copy(alpha = 0.75f), lavenderMist.copy(alpha = 0.7f), roseGold.copy(alpha = 0.65f), pearlWhite), Offset(cx, cy), size.width * 0.85f), size = size)
        drawRect(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent), Offset(cx - size.width * 0.1f, cy - size.height * 0.1f), size.width * 0.6f), size = size)

        glows.value.forEach { g ->
            val p = (glowTime + g.phase / (2f * PI.toFloat())) % 1f
            val pulse = sin(p * 6.28f * g.speed) * 0.5f + 0.5f
            drawCircle(Color.White.copy(alpha = 0.03f + pulse * 0.05f), g.size * density * (0.5f + pulse * 0.5f), Offset(g.x * size.width + sin(p * 2f) * g.driftX, g.y * size.height))
        }

        val step = partTime * size.height
        petals.value.forEach { p ->
            val py = (p.y * size.height + step * p.speedY / 100f) % (size.height + 40f) - 20f
            drawLockPetal(p.color.copy(alpha = p.alpha * 0.6f), Offset(p.x * size.width + sin(p.rotation * PI.toFloat() / 180f) * 15f, py), p.rotation + partTime * p.rotSpeed, p.size, p.size * 0.6f)
        }

        sparkles.value.forEach { s ->
            val tw = sin(sparkTime * 6.28f * s.speed + s.phase) * 0.5f + 0.5f
            drawCircle(Color.White.copy(alpha = (0.15f + tw * 0.5f).coerceIn(0f, 1f)), s.size * density, Offset(s.x * size.width, s.y * size.height))
        }
    }
}

private fun DrawScope.drawLockPetal(color: Color, center: Offset, rotationDeg: Float, length: Float, width: Float) {
    drawContext.canvas.save()
    drawContext.canvas.translate(center.x, center.y)
    drawContext.canvas.rotate(rotationDeg)
    drawOval(color.copy(alpha = color.alpha.coerceIn(0f, 1f)), Offset(-width / 2f, 0f), Size(width, length))
    drawContext.canvas.restore()
}

private fun isFatalBiometricError(errorCode: Int): Boolean = errorCode in listOf(
    BiometricPrompt.ERROR_HW_NOT_PRESENT, BiometricPrompt.ERROR_HW_UNAVAILABLE,
    BiometricPrompt.ERROR_NO_BIOMETRICS, BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL,
    BiometricPrompt.ERROR_LOCKOUT_PERMANENT
)

private fun android.content.Context.findActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is ContextWrapper) { if (ctx is FragmentActivity) return ctx; ctx = ctx.baseContext }
    return null
}

@Composable
fun LockScreen(onUnlocked: () -> Unit, onPinFallback: () -> Unit) {
    val ctx = LocalContext.current
    val activity = ctx.findActivity()
    var unlockState by remember { mutableStateOf(UnlockState.Idle) }
    var bioErrorMsg by remember { mutableStateOf<String?>(null) }
    var mascotEmotion by remember { mutableStateOf(MascotEmotion.SLEEPING) }
    val textAlpha = remember { Animatable(0f) }
    val shakeOffset = remember { Animatable(0f) }
    val brightenAlpha = remember { Animatable(0f) }
    val bioManager = remember { AppBiometricManager(ctx) }

    fun triggerBiometric() {
        activity?.let { act ->
            bioManager.authenticate(
                activity = act,
                onSuccess = { bioErrorMsg = null; unlockState = UnlockState.Success; mascotEmotion = MascotEmotion.HAPPY },
                onFailed = { unlockState = UnlockState.Failed },
                onError = { msg, code ->
                    if (isFatalBiometricError(code)) onPinFallback()
                    else { bioErrorMsg = msg; unlockState = UnlockState.Idle }
                }
            )
        } ?: onPinFallback()
    }

    LaunchedEffect(Unit) { delay(300); textAlpha.animateTo(1f, tween(600)) }

    LaunchedEffect(Unit) {
        delay(600)
        when (bioManager.isBiometricAvailable()) {
            BiometricStatus.Available -> triggerBiometric()
            BiometricStatus.NotEnrolled -> onPinFallback()
            BiometricStatus.NotAvailable -> {}
        }
    }

    Box(Modifier.fillMaxSize()) {
        LockScreenBackground()

        Box(modifier = Modifier.fillMaxSize().alpha(textAlpha.value), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Spacer(Modifier.weight(0.3f))

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.graphicsLayer { translationX = shakeOffset.value }) {
                    SaathiMascot(emotion = mascotEmotion, size = 130.dp)
                }

                Spacer(Modifier.height(20.dp))

                Text("Saathi is resting", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = deepRose, textAlign = TextAlign.Center, letterSpacing = 0.5.sp)
                Spacer(Modifier.height(8.dp))
                Text("Your caring companion is keeping watch", fontSize = 13.sp, color = deepRose.copy(alpha = 0.55f), textAlign = TextAlign.Center)

                Spacer(Modifier.weight(0.3f))

                UnlockButton(
                    status = bioManager.isBiometricAvailable(),
                    errorMessage = bioErrorMsg,
                    onClick = {
                        bioErrorMsg = null
                        when (bioManager.isBiometricAvailable()) {
                            BiometricStatus.Available -> triggerBiometric()
                            BiometricStatus.NotEnrolled, BiometricStatus.NotAvailable -> onPinFallback()
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))

                Text("Or use PIN", fontSize = 12.sp, color = deepRose.copy(alpha = 0.45f), textAlign = TextAlign.Center, modifier = Modifier.clickable { onPinFallback() })

                Spacer(Modifier.weight(0.1f))
            }
        }

        when (unlockState) {
            UnlockState.Success -> {
                LaunchedEffect(Unit) {
                    brightenAlpha.animateTo(1f, tween(800))
                    delay(600)
                    onUnlocked()
                }
                Box(
                    Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color.White.copy(alpha = (0.8f * brightenAlpha.value).coerceIn(0f, 0.8f)), Color(0xFFFFF0F5).copy(alpha = (0.5f * brightenAlpha.value).coerceIn(0f, 0.5f)), Color.Transparent))),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(brightenAlpha.value)) {
                        SaathiMascot(emotion = MascotEmotion.EXCITED, size = 140.dp)
                        Spacer(Modifier.height(20.dp))
                        Text("Good Morning! 🌸", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = deepRose, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        Text("Saathi is happy to see you", fontSize = 14.sp, color = deepRose.copy(alpha = 0.6f), textAlign = TextAlign.Center)
                    }
                }
            }

            UnlockState.Failed -> {
                LaunchedEffect(Unit) {
                    mascotEmotion = MascotEmotion.SAD
                    repeat(3) { shakeOffset.animateTo(10f, tween(60)); shakeOffset.animateTo(-10f, tween(60)) }
                    shakeOffset.animateTo(0f, tween(60))
                    delay(400)
                    mascotEmotion = MascotEmotion.SLEEPING
                    unlockState = UnlockState.Idle
                }
            }

            UnlockState.Idle -> {}
        }
    }
}

@Composable
private fun UnlockButton(status: BiometricStatus, errorMessage: String? = null, onClick: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "pulse")
    val pulse by inf.animateFloat(0.7f, 1f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "p")
    val scanP by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart), label = "scan")
    val avail = status == BiometricStatus.Available

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp)
                .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.15f * pulse), Color.White.copy(alpha = 0.06f))), RoundedCornerShape(28.dp))
                .border(1.dp, Color.White.copy(alpha = 0.25f * pulse), RoundedCornerShape(28.dp))
                .alpha(pulse)
                .springClickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Canvas(Modifier.size(24.dp)) {
                    val cx = size.width / 2f; val cy = size.height / 2f; val r = size.width * 0.35f; val sweep = scanP * 360f
                    for (i in 0 until 4) {
                        val a = i * 90f; val alpha = if (sweep > a && sweep < a + 60f) ((sweep - a) / 60f).coerceIn(0.3f, 0.9f) else 0.2f
                        drawArc(deepRose.copy(alpha = alpha), a.toFloat(), 60f, false, Offset(cx - r, cy - r), Size(r * 2, r * 2), style = Stroke(1.8f))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    when { avail -> "Unlock with Biometrics"; status == BiometricStatus.NotEnrolled -> "Set up biometrics"; else -> "Use PIN to unlock" },
                    fontSize = 15.sp, fontWeight = FontWeight.Medium, color = deepRose.copy(alpha = 0.85f), letterSpacing = 0.3.sp
                )
            }
        }

        if (avail) {
            Box(
                modifier = Modifier.size(80.dp, 24.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Text("Face ID / Fingerprint", fontSize = 11.sp, color = deepRose.copy(alpha = 0.4f), letterSpacing = 0.5.sp)
            }
        }

        errorMessage?.let {
            Text(it, color = Color(0xFFFFB74D), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(0.8f).padding(horizontal = 8.dp))
        }
    }
}

private enum class UnlockState { Idle, Success, Failed }
