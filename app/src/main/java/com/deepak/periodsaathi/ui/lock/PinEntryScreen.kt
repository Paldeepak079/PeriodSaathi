package com.deepak.periodsaathi.ui.lock

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.security.AppLockManager
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.SaathiMascot
import com.deepak.periodsaathi.ui.components.springClickable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val PIN_LENGTH = 4

private val sakuraPink = Color(0xFFFFB6C1)
private val lavenderMist = Color(0xFFE6E6FA)
private val roseGold = Color(0xFFE8B4B8)
private val pearlWhite = Color(0xFFFFF0F5)
private val deepRose = Color(0xFF874E58)
private val softGold = Color(0xFFFFD700)

data class Petal(
    var x: Float, var y: Float, val size: Float, val rotation: Float,
    val rotSpeed: Float, val speedY: Float, val speedX: Float,
    val color: Color, val alpha: Float
)

data class Sparkle(
    val x: Float, val y: Float, val size: Float,
    val phase: Float, val speed: Float
)

data class GlowParticle(
    val x: Float, val y: Float, val size: Float,
    val phase: Float, val speed: Float, val driftX: Float
)

data class HeartBubble(
    var x: Float, var y: Float, val size: Float,
    val speed: Float, val wobble: Float, val alpha: Float
)

private fun generatePetals() = List(18) {
    Petal(
        x = Random.nextFloat(), y = Random.nextFloat() * 1.2f - 0.1f,
        size = Random.nextFloat() * 10f + 6f,
        rotation = Random.nextFloat() * 360f,
        rotSpeed = (Random.nextFloat() - 0.5f) * 30f,
        speedY = Random.nextFloat() * 15f + 8f,
        speedX = (Random.nextFloat() - 0.5f) * 10f,
        color = if (Random.nextFloat() > 0.5f) sakuraPink else lavenderMist,
        alpha = Random.nextFloat() * 0.3f + 0.15f
    )
}

private fun generateSparkles() = List(24) {
    Sparkle(
        x = Random.nextFloat(), y = Random.nextFloat(),
        size = Random.nextFloat() * 2f + 1f,
        phase = Random.nextFloat() * 2f * PI.toFloat(),
        speed = Random.nextFloat() * 0.5f + 0.3f
    )
}

private fun generateGlowParticles() = List(16) {
    GlowParticle(
        x = Random.nextFloat(), y = Random.nextFloat(),
        size = Random.nextFloat() * 4f + 2f,
        phase = Random.nextFloat() * 2f * PI.toFloat(),
        speed = Random.nextFloat() * 0.3f + 0.2f,
        driftX = (Random.nextFloat() - 0.5f) * 20f
    )
}

private fun generateHeartBubbles() = List(8) {
    HeartBubble(
        x = Random.nextFloat(), y = Random.nextFloat() * 1.2f + 0.8f,
        size = Random.nextFloat() * 6f + 4f,
        speed = Random.nextFloat() * 10f + 6f,
        wobble = Random.nextFloat() * 4f + 2f,
        alpha = Random.nextFloat() * 0.2f + 0.1f
    )
}

@Composable
private fun PremiumLockBackground(
    showSuccess: Boolean = false,
    successProgress: Float = 0f,
    burstTrigger: Int = 0
) {
    val petals = remember { mutableStateOf(generatePetals()) }
    val sparkles = remember { mutableStateOf(generateSparkles()) }
    val glows = remember { mutableStateOf(generateGlowParticles()) }
    val hearts = remember { mutableStateOf(generateHeartBubbles()) }
    val burstParticles = remember { mutableStateOf(listOf<Petal>()) }

    val inf = rememberInfiniteTransition(label = "bg")
    val gradTime by inf.animateFloat(0f, 1f,
        infiniteRepeatable(animation = tween(12000, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "grad")
    val particleTime by inf.animateFloat(0f, 1f,
        infiniteRepeatable(animation = tween(8000, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "part")
    val sparkleTime by inf.animateFloat(0f, 1f,
        infiniteRepeatable(animation = tween(3000, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "spark")
    val glowTime by inf.animateFloat(0f, 1f,
        infiniteRepeatable(animation = tween(5000, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "glow")

    LaunchedEffect(burstTrigger) {
        if (burstTrigger > 0) {
            burstParticles.value = List(8) {
                Petal(
                    x = 0.5f, y = 0.5f,
                    size = Random.nextFloat() * 6f + 3f,
                    rotation = Random.nextFloat() * 360f,
                    rotSpeed = (Random.nextFloat() - 0.5f) * 60f,
                    speedY = (Random.nextFloat() - 0.5f) * 50f - 30f,
                    speedX = (Random.nextFloat() - 0.5f) * 50f,
                    color = if (Random.nextFloat() > 0.5f) sakuraPink else softGold.copy(alpha = 0.6f),
                    alpha = 0.8f
                )
            }
            delay(800)
            burstParticles.value = emptyList()
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width * (0.5f + 0.08f * sin(gradTime * 6.28f))
        val cy = size.height * (0.5f + 0.08f * cos(gradTime * 6.28f * 0.7f))
        val gradColors = if (showSuccess) {
            listOf(sakuraPink.copy(alpha = 0.9f + successProgress * 0.1f), lavenderMist.copy(alpha = 0.8f), roseGold.copy(alpha = 0.7f + successProgress * 0.2f), pearlWhite)
        } else {
            listOf(sakuraPink, lavenderMist, roseGold, pearlWhite)
        }

        drawRect(brush = Brush.radialGradient(colors = gradColors.map { it.copy(alpha = it.alpha.coerceAtMost(0.85f)) }, center = Offset(cx, cy), radius = size.width * 0.85f), size = size)
        drawRect(brush = Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent), center = Offset(cx - size.width * 0.1f, cy - size.height * 0.1f), radius = size.width * 0.6f), size = size)

        glows.value.forEach { glow ->
            val p = (glowTime + glow.phase / (2f * PI.toFloat())) % 1f
            val pulse = sin(p * 6.28f * glow.speed) * 0.5f + 0.5f
            drawCircle(Color.White.copy(alpha = 0.04f + pulse * 0.06f), glow.size * density * (0.5f + pulse * 0.5f), Offset(glow.x * size.width + sin(p * 2f) * glow.driftX, glow.y * size.height))
        }

        val step = particleTime * size.height
        petals.value.forEach { p ->
            val py = (p.y * size.height + step * p.speedY / 100f) % (size.height + 40f) - 20f
            drawPetal(p.color.copy(alpha = p.alpha), Offset(p.x * size.width + sin(p.rotation * PI.toFloat() / 180f) * 15f, py), p.rotation + particleTime * p.rotSpeed, p.size, p.size * 0.6f)
        }

        sparkles.value.forEach { s ->
            val tw = sin(sparkleTime * 6.28f * s.speed + s.phase) * 0.5f + 0.5f
            val sa = (0.2f + tw * 0.6f).coerceIn(0f, 1f)
            drawCircle(Color.White.copy(alpha = sa), s.size * density, Offset(s.x * size.width, s.y * size.height))
            if (tw > 0.8f) drawCircle(softGold.copy(alpha = (tw - 0.8f) * 2f * 0.3f), s.size * density * 2f, Offset(s.x * size.width, s.y * size.height))
        }

        hearts.value.forEachIndexed { i, h ->
            val p = (particleTime + i * 0.12f) % 1f
            val hy = h.y * size.height - p * size.height * h.speed / 100f
            if (hy >= -20f) drawHeart(Color(0xFFFF6B9D).copy(alpha = h.alpha * (1f - p).coerceIn(0f, 1f)), Offset(h.x * size.width + sin(p * 6.28f * 0.5f) * h.wobble, hy + 20f), h.size * density)
        }

        burstParticles.value.forEach { p ->
            val bp = particleTime * 2f
            if (bp <= 1f) drawPetal(p.color.copy(alpha = (p.alpha * (1f - bp)).coerceIn(0f, 1f)), Offset(p.x * size.width + p.speedX * density * bp, p.y * size.height + p.speedY * density * bp), p.rotation + bp * p.rotSpeed, p.size * (1f - bp * 0.3f), p.size * 0.6f * (1f - bp * 0.3f))
        }
    }
}

private fun DrawScope.drawPetal(color: Color, center: Offset, rotationDeg: Float, length: Float, width: Float) {
    translate(center.x, center.y) {
        rotate(rotationDeg) {
            drawOval(color = color.copy(alpha = color.alpha.coerceIn(0f, 1f)), topLeft = Offset(-width / 2f, 0f), size = Size(width, length))
        }
    }
}

private fun DrawScope.drawHeart(color: Color, center: Offset, size: Float) {
    translate(center.x, center.y) {
        rotate(-45f) {
            drawCircle(color, radius = size * 0.5f, center = Offset(size * 0.25f, 0f))
            drawCircle(color, radius = size * 0.5f, center = Offset(-size * 0.25f, 0f))
            val tri = Path().apply { moveTo(-size * 0.4f, 0f); lineTo(size * 0.4f, 0f); lineTo(0f, size * 0.5f); close() }
            drawPath(tri, color.copy(alpha = color.alpha * 0.7f))
        }
    }
}

@Composable
private fun PetalPinIndicator(
    pinLength: Int,
    isError: Boolean,
    onBurst: () -> Unit
) {
    var prevLen by remember { mutableStateOf(0) }
    val bloomScales = remember { List(PIN_LENGTH) { Animatable(0.8f) } }

    LaunchedEffect(pinLength) {
        if (pinLength > prevLen && pinLength in 1..PIN_LENGTH) {
            bloomScales[pinLength - 1].animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 300f))
            onBurst()
        }
        if (isError) bloomScales.forEach { it.snapTo(0.8f) }
        prevLen = pinLength
    }

    Box(Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(130.dp, 48.dp)) {
            val cx = size.width / 2f; val cy = size.height / 2f + 6f
            for (i in 0 until PIN_LENGTH) {
                val angle = i * 90f - 45f
                val filled = i < pinLength
                val s = bloomScales[i].value
                val base = if (filled) { if (isError) Color(0xFFE53935) else sakuraPink } else Color.White.copy(alpha = 0.2f)
                val pl = size.width * 0.16f * s; val pw = size.width * 0.08f * s
                val rad = angle * PI.toFloat() / 180f
                val px = cx + cos(rad) * size.width * 0.1f; val py = cy + sin(rad) * size.width * 0.1f

                if (filled && !isError) drawCircle(sakuraPink.copy(alpha = 0.08f * s), size.width * 0.12f * s, Offset(cx, cy))

                translate(px, py) {
                    rotate(angle) {
                        drawOval(base, Offset(-pw / 2f, 0f), Size(pw, pl))
                        if (filled && !isError) drawOval(Color.White.copy(alpha = 0.3f * s), Offset(-pw / 4f, pl * 0.1f), Size(pw / 2f, pl * 0.4f))
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassNumpadButton(label: String, onClick: () -> Unit, isDelete: Boolean = false) {
    var pressed by remember { mutableStateOf(false) }
    val bgAlpha by animateFloatAsState(if (pressed) 0.2f else 0.08f, tween(100), label = "bg")

    Box(modifier = Modifier.size(68.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.size(68.dp)
                .background(Brush.radialGradient(listOf(Color.White.copy(alpha = bgAlpha + 0.08f), Color.White.copy(alpha = bgAlpha))), CircleShape)
                .border(1.dp, Color.White.copy(alpha = if (pressed) 0.4f else 0.2f), CircleShape)
                .springClickable { pressed = true; onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isDelete) Text("⌫", fontSize = 22.sp, color = Color.White.copy(alpha = if (pressed) 0.9f else 0.7f), fontWeight = FontWeight.Light)
            else Text(label, fontSize = 28.sp, fontWeight = FontWeight.Normal, color = Color(0xFF1A0E2E).copy(alpha = if (pressed) 0.9f else 0.7f), textAlign = TextAlign.Center)
        }
        if (pressed) LaunchedEffect(Unit) { delay(150); pressed = false }
    }
}

@Composable
private fun GlassNumpad(onDigit: (String) -> Unit, onDelete: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        for (row in listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("", "0", "del"))) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                for (lbl in row) when (lbl) {
                    "" -> Box(Modifier.size(68.dp))
                    "del" -> GlassNumpadButton(label = "⌫", onClick = onDelete, isDelete = true)
                    else -> GlassNumpadButton(label = lbl, onClick = { onDigit(lbl) })
                }
            }
        }
    }
}

@Composable
private fun BiometricUnlockCard(onClick: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "bio")
    val scan by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart), label = "s")
    val pulse by inf.animateFloat(0.6f, 1f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "p")

    Box(
        modifier = Modifier.fillMaxWidth(0.7f).height(48.dp)
            .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.15f * pulse), Color.White.copy(alpha = 0.06f))), RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f * pulse), RoundedCornerShape(24.dp))
            .springClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Canvas(Modifier.size(20.dp)) {
                val r = size.width * 0.38f; val sweep = scan * 360f
                for (i in 0 until 4) {
                    val a = i * 90f; val alpha = if (sweep > a && sweep < a + 60f) ((sweep - a) / 60f).coerceIn(0.3f, 0.9f) else 0.25f
                    drawArc(deepRose.copy(alpha = alpha), a.toFloat(), 60f, false, Offset(size.width / 2f - r, size.height / 2f - r), Size(r * 2, r * 2), style = Stroke(1.5f))
                }
            }
            Spacer(Modifier.width(10.dp))
            Text("Unlock instantly", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = deepRose.copy(alpha = 0.8f), letterSpacing = 0.3.sp)
        }
    }
}

@Composable
private fun ErrorMsg(text: String?, isSuccess: Boolean) {
    Box(Modifier.height(36.dp), contentAlignment = Alignment.Center) {
        text?.let { Text(it, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
            color = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFE53935)) }
    }
}

@Composable
fun PinEntryScreen(onPinVerified: () -> Unit, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val mgr = remember { AppLockManager(ctx) }
    val hasPin = remember { mgr.hasPin() }
    val scope = rememberCoroutineScope()

    var emotion by remember { mutableStateOf(MascotEmotion.HAPPY) }
    var showSuccess by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(0) }
    var step by remember { mutableStateOf(SetupStep.Create) }
    var firstPin by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var errMsg by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }
    val titleFade = remember { Animatable(0f) }
    val successP = remember { Animatable(0f) }

    fun shakeAndReset(msg: String) {
        emotion = MascotEmotion.SAD; isError = true; errMsg = msg; isSuccess = false
        scope.launch {
            repeat(3) { shake.animateTo(10f, tween(50)); shake.animateTo(-10f, tween(50)) }
            shake.animateTo(0f, tween(50)); delay(500); pin = ""; isError = false; emotion = MascotEmotion.HAPPY
        }
    }

    fun onDigit(d: String) {
        if (pin.length >= PIN_LENGTH) return
        burst++
        val np = pin + d
        if (np.length == PIN_LENGTH) {
            pin = np
            if (!hasPin) {
                when (step) {
                    SetupStep.Create -> { firstPin = np; step = SetupStep.Confirm; pin = ""; errMsg = "✓ First PIN entered"; isSuccess = true; emotion = MascotEmotion.EXCITED }
                    SetupStep.Confirm -> {
                        if (np == firstPin) {
                            mgr.setPin(np); emotion = MascotEmotion.EXCITED; showSuccess = true
                            scope.launch { successP.animateTo(1f, tween(800)); delay(400); onPinVerified() }
                        } else { step = SetupStep.Create; firstPin = ""; shakeAndReset("PINs didn't match — try again") }
                    }
                }
            } else {
                if (mgr.verifyPin(np)) { emotion = MascotEmotion.EXCITED; showSuccess = true; scope.launch { successP.animateTo(1f, tween(800)); delay(400); onPinVerified() } }
                else shakeAndReset("Incorrect PIN")
            }
        } else pin = np
    }

    LaunchedEffect(Unit) { delay(300); titleFade.animateTo(1f, tween(600)) }

    Box(Modifier.fillMaxSize()) {
        PremiumLockBackground(showSuccess = showSuccess, successProgress = successP.value, burstTrigger = burst)

        Column(Modifier.fillMaxSize().padding(horizontal = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))

            if (hasPin) Box(Modifier.align(Alignment.Start).size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape).border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape).springClickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Text("←", fontSize = 18.sp, color = deepRose.copy(alpha = 0.7f))
            } else Spacer(Modifier.height(48.dp))

            Spacer(Modifier.weight(0.08f))

            Column(Modifier.graphicsLayer { translationX = shake.value }, horizontalAlignment = Alignment.CenterHorizontally) {
                SaathiMascot(emotion = emotion, size = 110.dp)
            }

            Spacer(Modifier.height(12.dp))

            AnimatedContent(targetState = if (!hasPin) step else SetupStep.Confirm, transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(300)) }, label = "t", modifier = Modifier.graphicsLayer { alpha = titleFade.value }) { s ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        when { !hasPin && s == SetupStep.Create -> "🌸 Protect Your Safe Space"; !hasPin && s == SetupStep.Confirm -> "✨ One More Time"; else -> "🌸 Welcome Back" },
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = deepRose, textAlign = TextAlign.Center, letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        when { !hasPin && s == SetupStep.Create -> "Your private wellness journey stays only with you"; !hasPin && s == SetupStep.Confirm -> "Re-enter your secret PIN"; else -> "Enter your secret PIN to continue" },
                        fontSize = 13.sp, color = deepRose.copy(alpha = 0.55f), textAlign = TextAlign.Center, letterSpacing = 0.2.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            PetalPinIndicator(pinLength = pin.length, isError = isError, onBurst = { burst++ })
            ErrorMsg(text = errMsg, isSuccess = isSuccess)
            Spacer(Modifier.height(16.dp))

            GlassNumpad(onDigit = { onDigit(it) }, onDelete = { if (pin.isNotEmpty()) { pin = pin.dropLast(1); errMsg = null; isSuccess = false } })
            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(visible = !showSuccess, enter = fadeIn(tween(500)), exit = fadeOut(tween(200))) {
                BiometricUnlockCard(onClick = onBack)
            }

            Spacer(Modifier.height(8.dp))
            if (hasPin && !showSuccess) Text("Forgot PIN?", fontSize = 12.sp, color = deepRose.copy(alpha = 0.5f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.weight(0.05f))
        }

        if (showSuccess) {
            Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color.White.copy(alpha = (0.7f * successP.value).coerceIn(0f, 0.7f)), Color(0xFFFFF0F5).copy(alpha = (0.4f * successP.value).coerceIn(0f, 0.4f)), Color.Transparent))), contentAlignment = Alignment.Center) {
                if (successP.value > 0.3f) Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SaathiMascot(emotion = MascotEmotion.EXCITED, size = 130.dp)
                    Spacer(Modifier.height(16.dp))
                    Text("🌸 Welcome back!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = deepRose, textAlign = TextAlign.Center, modifier = Modifier.graphicsLayer { alpha = ((successP.value - 0.3f) / 0.3f).coerceIn(0f, 1f) })
                    Spacer(Modifier.height(8.dp))
                    Text("Your safe space is ready", fontSize = 14.sp, color = deepRose.copy(alpha = 0.6f), textAlign = TextAlign.Center, modifier = Modifier.graphicsLayer { alpha = ((successP.value - 0.5f) / 0.3f).coerceIn(0f, 1f) })
                }
            }
        }
    }
}

private enum class SetupStep { Create, Confirm }
