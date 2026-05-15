package com.example.periodsaathi.ui.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.SoftCoral
import kotlinx.coroutines.delay

enum class MascotEmotion {
    HAPPY, SAD, SLEEPING, EXCITED, PAIN, LISTENING, HUGGING
}

private val wellnessTips = listOf(
    "Staying hydrated reduces cramps by up to 40% 💧",
    "Ginger tea is nature's ibuprofen 🫚",
    "Iron-rich foods help combat fatigue during your cycle 🍎",
    "Light exercise like walking can ease period pain 🚶‍♀️",
    "Vitamin E may help reduce menstrual cramps 💊",
    "Sleep 7-9 hours to regulate hormones and mood 😴",
    "Avoid caffeine to reduce breast tenderness ☕",
    "Omega-3 fatty acids help reduce inflammation 🐟",
    "Keep a period diary to track patterns 📓",
    "Heat therapy is proven to relieve menstrual pain 🔥",
    "Magnesium helps with mood swings and bloating ⚡",
    "Dark chocolate can boost serotonin levels 🍫",
    "Chamomile tea soothes cramps and anxiety 🌼",
    "Practice deep breathing to manage stress 🧘",
    "Yoga poses like child’s pose help relieve tension 👶",
    "Vitamin C helps absorb iron more efficiently 🍊",
    "Compression shorts can ease lower back pain 👖",
    "Cranberry juice may help prevent UTIs during periods 🫐",
    "Eat small, frequent meals to stabilize energy 🍽️",
    "Self-care isn't selfish—take time for yourself today 🌸"
)

@Composable
fun SaathiMascot(
    emotion: MascotEmotion,
    size: Dp = 120.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "idle")
    val idleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleFloat"
    )

    var isClicked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.3f else 1f,
        animationSpec = spring(
            stiffness = if (isClicked) Spring.StiffnessMedium else Spring.StiffnessLow
        ),
        label = "clickScale",
        finishedListener = { if (isClicked) isClicked = false }
    )

    var showBubble by remember { mutableStateOf(false) }
    var tipIndex by remember { mutableIntStateOf(0) }
    var displayedText by remember { mutableStateOf("") }
    val currentTip = wellnessTips[tipIndex]

    LaunchedEffect(showBubble) {
        if (showBubble) {
            displayedText = ""
            currentTip.forEachIndexed { index, _ ->
                displayedText = currentTip.substring(0, index + 1)
                delay(35)
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showBubble) {
                SpeechBubble(
                    text = displayedText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            AnimatedContent(
                targetState = emotion,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f))
                        .togetherWith(fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f))
                },
                label = "emotion"
            ) { currentEmotion ->
                Box(
                    modifier = Modifier
                        .size(size)
                        .offset(y = (-idleOffset).dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .then(
                            if (onClick != null) {
                                Modifier.clickable {
                                    isClicked = true
                                    showBubble = !showBubble
                                    tipIndex = (tipIndex + 1) % wellnessTips.size
                                    onClick()
                                }
                            } else Modifier
                        )
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawMascot(
                            emotion = currentEmotion,
                            size = this.size
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedContent(
        targetState = visible,
        transitionSpec = {
            (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                    scaleIn(initialScale = 0.5f, animationSpec = spring(stiffness = Spring.StiffnessMedium)))
                .togetherWith(fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.5f))
        },
        label = "bubble"
    ) { isVisible ->
        if (isVisible) {
            Surface(
                modifier = modifier,
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF5D4037),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawMascot(emotion: MascotEmotion, size: Size) {
    val bodyWidth = size.width * 0.8f
    val bodyHeight = size.height * 0.8f
    val centerX = size.width / 2
    val centerY = size.height / 2

    val bodyGradient = Brush.verticalGradient(
        colors = listOf(BlushPink, SoftCoral),
        startY = centerY - bodyHeight / 2,
        endY = centerY + bodyHeight / 2
    )

    drawRoundRect(
        brush = bodyGradient,
        topLeft = Offset(centerX - bodyWidth / 2, centerY - bodyHeight / 2),
        size = Size(bodyWidth, bodyHeight),
        cornerRadius = CornerRadius(bodyWidth / 2, bodyHeight / 2)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val sparkleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    val earWiggle by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "earWiggle"
    )

    when (emotion) {
        MascotEmotion.HAPPY -> drawHappyFace(centerX, centerY, bodyWidth, sparkleOffset)
        MascotEmotion.SAD -> drawSadFace(centerX, centerY, bodyWidth)
        MascotEmotion.SLEEPING -> drawSleepingFace(centerX, centerY, bodyWidth)
        MascotEmotion.EXCITED -> drawExcitedFace(centerX, centerY, bodyWidth)
        MascotEmotion.PAIN -> drawPainFace(centerX, centerY, bodyWidth)
        MascotEmotion.LISTENING -> drawListeningFace(centerX, centerY, bodyWidth, earWiggle)
        MascotEmotion.HUGGING -> drawHuggingFace(centerX, centerY, bodyWidth)
    }
}

private fun DrawScope.drawHappyFace(centerX: Float, centerY: Float, bodyWidth: Float, sparkleOffset: Float) {
    val eyeY = centerY - bodyWidth * 0.1f
    val leftEyeX = centerX - bodyWidth * 0.2f
    val rightEyeX = centerX + bodyWidth * 0.2f
    val eyeWidth = bodyWidth * 0.12f
    val eyeHeight = bodyWidth * 0.15f

    drawOval(
        color = Color(0xFF4A4A4A),
        topLeft = Offset(leftEyeX - eyeWidth / 2, eyeY - eyeHeight / 2),
        size = Size(eyeWidth, eyeHeight)
    )
    drawOval(
        color = Color(0xFF4A4A4A),
        topLeft = Offset(rightEyeX - eyeWidth / 2, eyeY - eyeHeight / 2),
        size = Size(eyeWidth, eyeHeight)
    )

    drawCircle(
        color = Color.White,
        radius = eyeWidth * 0.25f,
        center = Offset(leftEyeX - eyeWidth * 0.15f, eyeY - eyeHeight * 0.2f)
    )
    drawCircle(
        color = Color.White,
        radius = eyeWidth * 0.25f,
        center = Offset(rightEyeX - eyeWidth * 0.15f, eyeY - eyeHeight * 0.2f)
    )

    val cheekY = centerY + bodyWidth * 0.05f
    val cheekXOffset = bodyWidth * 0.28f
    drawCircle(
        color = Color(0x33FF69B4),
        radius = bodyWidth * 0.08f,
        center = Offset(centerX - cheekXOffset, cheekY)
    )
    drawCircle(
        color = Color(0x33FF69B4),
        radius = bodyWidth * 0.08f,
        center = Offset(centerX + cheekXOffset, cheekY)
    )

    val smileY = centerY + bodyWidth * 0.15f
    drawArc(
        color = Color(0xFF4A4A4A),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(centerX - bodyWidth * 0.15f, smileY - bodyWidth * 0.08f),
        size = Size(bodyWidth * 0.3f, bodyWidth * 0.16f),
        style = Stroke(width = bodyWidth * 0.04f, cap = StrokeCap.Round)
    )

    val starY1 = centerY - bodyWidth * 0.35f + sparkleOffset * 10
    val starY2 = centerY - bodyWidth * 0.4f - sparkleOffset * 8

    drawStar(Offset(centerX - bodyWidth * 0.25f, starY1), bodyWidth * 0.06f, Color(0xFFFFD700))
    drawStar(Offset(centerX + bodyWidth * 0.25f, starY2), bodyWidth * 0.05f, Color(0xFFFFD700))
}

private fun DrawScope.drawSadFace(centerX: Float, centerY: Float, bodyWidth: Float) {
    val eyeY = centerY - bodyWidth * 0.1f
    val leftEyeX = centerX - bodyWidth * 0.2f
    val rightEyeX = centerX + bodyWidth * 0.2f

    drawArc(
        color = Color(0xFF4A4A4A),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(leftEyeX - bodyWidth * 0.08f, eyeY - bodyWidth * 0.04f),
        size = Size(bodyWidth * 0.16f, bodyWidth * 0.08f),
        style = Stroke(width = bodyWidth * 0.035f, cap = StrokeCap.Round)
    )
    drawArc(
        color = Color(0xFF4A4A4A),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(rightEyeX - bodyWidth * 0.08f, eyeY - bodyWidth * 0.04f),
        size = Size(bodyWidth * 0.16f, bodyWidth * 0.08f),
        style = Stroke(width = bodyWidth * 0.035f, cap = StrokeCap.Round)
    )

    val mouthY = centerY + bodyWidth * 0.12f
    drawArc(
        color = Color(0xFF4A4A4A),
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(centerX - bodyWidth * 0.1f, mouthY - bodyWidth * 0.02f),
        size = Size(bodyWidth * 0.2f, bodyWidth * 0.04f),
        style = Stroke(width = bodyWidth * 0.03f, cap = StrokeCap.Round)
    )

    val tearX = leftEyeX + bodyWidth * 0.05f
    val tearY = eyeY + bodyWidth * 0.08f
    val path = Path().apply {
        moveTo(tearX, tearY)
        quadraticBezierTo(tearX - bodyWidth * 0.03f, tearY + bodyWidth * 0.08f, tearX, tearY + bodyWidth * 0.12f)
        quadraticBezierTo(tearX + bodyWidth * 0.03f, tearY + bodyWidth * 0.08f, tearX, tearY)
        close()
    }
    drawPath(path, Color(0xFF87CEEB).copy(alpha = 0.7f))
}

private fun DrawScope.drawSleepingFace(centerX: Float, centerY: Float, bodyWidth: Float) {
    val eyeY = centerY - bodyWidth * 0.1f
    val leftEyeX = centerX - bodyWidth * 0.2f
    val rightEyeX = centerX + bodyWidth * 0.2f

    drawLine(
        color = Color(0xFF4A4A4A),
        start = Offset(leftEyeX - bodyWidth * 0.08f, eyeY),
        end = Offset(leftEyeX + bodyWidth * 0.08f, eyeY),
        strokeWidth = bodyWidth * 0.03f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF4A4A4A),
        start = Offset(rightEyeX - bodyWidth * 0.08f, eyeY),
        end = Offset(rightEyeX + bodyWidth * 0.08f, eyeY),
        strokeWidth = bodyWidth * 0.03f,
        cap = StrokeCap.Round
    )

    val smileY = centerY + bodyWidth * 0.1f
    drawArc(
        color = Color(0xFF4A4A4A),
        startAngle = 0f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(centerX - bodyWidth * 0.08f, smileY - bodyWidth * 0.02f),
        size = Size(bodyWidth * 0.16f, bodyWidth * 0.04f),
        style = Stroke(width = bodyWidth * 0.025f, cap = StrokeCap.Round)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "sleep")
    val zOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ),
        label = "zFloat"
    )

    val zX = centerX + bodyWidth * 0.35f
    val zY = centerY - bodyWidth * 0.35f

    for (i in 0..2) {
        val offset = i * bodyWidth * 0.08f
        val alpha = 1f - (i * 0.3f)
        val scale = 1f - (i * 0.2f)

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.save()
            canvas.nativeCanvas.translate(zX + offset, zY - zOffset * bodyWidth * 0.1f - offset * 0.5f)
            canvas.nativeCanvas.rotate(-45f)

            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb((alpha * 255).toInt(), 74, 74, 74)
                textSize = bodyWidth * (0.15f - i * 0.03f) * scale
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
            canvas.nativeCanvas.drawText("Z", 0f, 0f, paint)
            canvas.nativeCanvas.restore()
        }
    }
}

private fun DrawScope.drawExcitedFace(centerX: Float, centerY: Float, bodyWidth: Float) {
    val eyeY = centerY - bodyWidth * 0.1f
    val leftEyeX = centerX - bodyWidth * 0.2f
    val rightEyeX = centerX + bodyWidth * 0.2f

    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.1f,
        center = Offset(leftEyeX, eyeY)
    )
    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.1f,
        center = Offset(rightEyeX, eyeY)
    )

    drawCircle(
        color = Color(0xFF4A4A4A),
        radius = bodyWidth * 0.07f,
        center = Offset(leftEyeX, eyeY)
    )
    drawCircle(
        color = Color(0xFF4A4A4A),
        radius = bodyWidth * 0.07f,
        center = Offset(rightEyeX, eyeY)
    )

    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.025f,
        center = Offset(leftEyeX - bodyWidth * 0.02f, eyeY - bodyWidth * 0.02f)
    )
    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.025f,
        center = Offset(rightEyeX - bodyWidth * 0.02f, eyeY - bodyWidth * 0.02f)
    )

    val smileY = centerY + bodyWidth * 0.12f
    drawArc(
        color = Color(0xFF4A4A4A),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(centerX - bodyWidth * 0.18f, smileY - bodyWidth * 0.1f),
        size = Size(bodyWidth * 0.36f, bodyWidth * 0.2f),
        style = Stroke(width = bodyWidth * 0.04f, cap = StrokeCap.Round)
    )

    for (i in 0 until 8) {
        val angle = (i * 45f) - 135f
        val innerRadius = bodyWidth * 0.45f
        val outerRadius = bodyWidth * 0.55f

        val startX = centerX + innerRadius * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
        val startY = centerY + innerRadius * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
        val endX = centerX + outerRadius * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
        val endY = centerY + outerRadius * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()

        drawLine(
            color = BlushPink.copy(alpha = 0.8f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = bodyWidth * 0.025f,
            cap = StrokeCap.Round
        )
    }

    drawCircle(
        color = Color(0x33FF69B4),
        radius = bodyWidth * 0.08f,
        center = Offset(centerX - bodyWidth * 0.28f, centerY + bodyWidth * 0.05f)
    )
    drawCircle(
        color = Color(0x33FF69B4),
        radius = bodyWidth * 0.08f,
        center = Offset(centerX + bodyWidth * 0.28f, centerY + bodyWidth * 0.05f)
    )
}

private fun DrawScope.drawPainFace(centerX: Float, centerY: Float, bodyWidth: Float) {
    val eyeY = centerY - bodyWidth * 0.1f
    val leftEyeX = centerX - bodyWidth * 0.2f
    val rightEyeX = centerX + bodyWidth * 0.2f

    drawOval(
        color = Color(0xFF4A4A4A),
        topLeft = Offset(leftEyeX - bodyWidth * 0.06f, eyeY - bodyWidth * 0.03f),
        size = Size(bodyWidth * 0.12f, bodyWidth * 0.06f)
    )
    drawOval(
        color = Color(0xFF4A4A4A),
        topLeft = Offset(rightEyeX - bodyWidth * 0.06f, eyeY - bodyWidth * 0.03f),
        size = Size(bodyWidth * 0.12f, bodyWidth * 0.06f)
    )

    val mouthY = centerY + bodyWidth * 0.12f
    drawOval(
        color = Color(0xFF4A4A4A),
        topLeft = Offset(centerX - bodyWidth * 0.06f, mouthY - bodyWidth * 0.03f),
        size = Size(bodyWidth * 0.12f, bodyWidth * 0.06f)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "sweat")
    val sweatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweat"
    )

    val foreheadY = centerY - bodyWidth * 0.3f
    drawSweatDrop(Offset(centerX - bodyWidth * 0.15f, foreheadY + sweatOffset * bodyWidth * 0.1f), bodyWidth * 0.04f)
    drawSweatDrop(Offset(centerX + bodyWidth * 0.1f, foreheadY + sweatOffset * bodyWidth * 0.08f), bodyWidth * 0.035f)
}

private fun DrawScope.drawSweatDrop(center: Offset, size: Float) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        quadraticBezierTo(center.x - size * 0.8f, center.y, center.x, center.y + size)
        quadraticBezierTo(center.x + size * 0.8f, center.y, center.x, center.y - size)
        close()
    }
    drawPath(path, Color(0xFF87CEEB).copy(alpha = 0.8f))
}

private fun DrawScope.drawListeningFace(centerX: Float, centerY: Float, bodyWidth: Float, earWiggle: Float) {
    val eyeY = centerY - bodyWidth * 0.1f
    val leftEyeX = centerX - bodyWidth * 0.2f
    val rightEyeX = centerX + bodyWidth * 0.2f

    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.1f,
        center = Offset(leftEyeX, eyeY)
    )
    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.1f,
        center = Offset(rightEyeX, eyeY)
    )

    drawCircle(
        color = Color(0xFF4A4A4A),
        radius = bodyWidth * 0.05f,
        center = Offset(leftEyeX, eyeY)
    )
    drawCircle(
        color = Color(0xFF4A4A4A),
        radius = bodyWidth * 0.05f,
        center = Offset(rightEyeX, eyeY)
    )

    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.02f,
        center = Offset(leftEyeX - bodyWidth * 0.015f, eyeY - bodyWidth * 0.015f)
    )
    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.02f,
        center = Offset(rightEyeX - bodyWidth * 0.015f, eyeY - bodyWidth * 0.015f)
    )

    val smileY = centerY + bodyWidth * 0.1f
    drawArc(
        color = Color(0xFF4A4A4A),
        startAngle = 0f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(centerX - bodyWidth * 0.08f, smileY - bodyWidth * 0.02f),
        size = Size(bodyWidth * 0.16f, bodyWidth * 0.04f),
        style = Stroke(width = bodyWidth * 0.025f, cap = StrokeCap.Round)
    )

    rotate(earWiggle, pivot = Offset(centerX - bodyWidth * 0.4f, centerY)) {
        drawOval(
            color = SoftCoral,
            topLeft = Offset(centerX - bodyWidth * 0.5f, centerY - bodyWidth * 0.15f),
            size = Size(bodyWidth * 0.12f, bodyWidth * 0.2f)
        )
    }

    rotate(-earWiggle, pivot = Offset(centerX + bodyWidth * 0.4f, centerY)) {
        drawOval(
            color = SoftCoral,
            topLeft = Offset(centerX + bodyWidth * 0.38f, centerY - bodyWidth * 0.15f),
            size = Size(bodyWidth * 0.12f, bodyWidth * 0.2f)
        )
    }
}

private fun DrawScope.drawHuggingFace(centerX: Float, centerY: Float, bodyWidth: Float) {
    val eyeY = centerY - bodyWidth * 0.12f
    val leftEyeX = centerX - bodyWidth * 0.18f
    val rightEyeX = centerX + bodyWidth * 0.18f

    drawOval(
        color = Color(0xFF4A4A4A),
        topLeft = Offset(leftEyeX - bodyWidth * 0.06f, eyeY - bodyWidth * 0.04f),
        size = Size(bodyWidth * 0.12f, bodyWidth * 0.08f)
    )
    drawOval(
        color = Color(0xFF4A4A4A),
        topLeft = Offset(rightEyeX - bodyWidth * 0.06f, eyeY - bodyWidth * 0.04f),
        size = Size(bodyWidth * 0.12f, bodyWidth * 0.08f)
    )

    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.02f,
        center = Offset(leftEyeX - bodyWidth * 0.02f, eyeY - bodyWidth * 0.02f)
    )
    drawCircle(
        color = Color.White,
        radius = bodyWidth * 0.02f,
        center = Offset(rightEyeX - bodyWidth * 0.02f, eyeY - bodyWidth * 0.02f)
    )

    val cheekY = centerY + bodyWidth * 0.02f
    drawCircle(
        color = Color(0x44FF69B4),
        radius = bodyWidth * 0.07f,
        center = Offset(centerX - bodyWidth * 0.25f, cheekY)
    )
    drawCircle(
        color = Color(0x44FF69B4),
        radius = bodyWidth * 0.07f,
        center = Offset(centerX + bodyWidth * 0.25f, cheekY)
    )

    val mouthY = centerY + bodyWidth * 0.1f
    drawArc(
        color = Color(0xFF4A4A4A),
        startAngle = 0f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(centerX - bodyWidth * 0.1f, mouthY - bodyWidth * 0.015f),
        size = Size(bodyWidth * 0.2f, bodyWidth * 0.03f),
        style = Stroke(width = bodyWidth * 0.025f, cap = StrokeCap.Round)
    )

    val armY = centerY + bodyWidth * 0.05f
    val leftArmStart = Offset(centerX - bodyWidth * 0.35f, armY)
    val leftArmEnd = Offset(centerX - bodyWidth * 0.45f, armY + bodyWidth * 0.15f)
    drawLine(
        color = SoftCoral,
        start = leftArmStart,
        end = leftArmEnd,
        strokeWidth = bodyWidth * 0.08f,
        cap = StrokeCap.Round
    )

    val rightArmStart = Offset(centerX + bodyWidth * 0.35f, armY)
    val rightArmEnd = Offset(centerX + bodyWidth * 0.45f, armY + bodyWidth * 0.15f)
    drawLine(
        color = SoftCoral,
        start = rightArmStart,
        end = rightArmEnd,
        strokeWidth = bodyWidth * 0.08f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawStar(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        val outerRadius = size
        val innerRadius = size * 0.4f
        val points = 5

        for (i in 0 until points * 2) {
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val angle = Math.toRadians((i * 360.0 / (points * 2)) - 90)
            val x = center.x + (radius * kotlin.math.cos(angle)).toFloat()
            val y = center.y + (radius * kotlin.math.sin(angle)).toFloat()

            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path, color)
}