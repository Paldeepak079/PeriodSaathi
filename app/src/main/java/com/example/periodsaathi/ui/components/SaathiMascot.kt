package com.example.periodsaathi.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.DeepRose
import com.example.periodsaathi.ui.theme.MintGreen
import com.example.periodsaathi.ui.theme.SoftLavender
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class MascotEmotion { HAPPY, SAD, SLEEPING, EXCITED, PAIN, LISTENING, HUGGING }

@Composable
fun SaathiMascot(
    emotion: MascotEmotion = MascotEmotion.HAPPY,
    size: Dp = 120.dp,
    onTap: () -> Unit = {},
    showTip: Boolean = false,
    tipText: String = ""
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(animation = tween(3000, easing = LinearEasing)),
        label = "float"
    )
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(animation = tween(600, easing = LinearEasing)),
        label = "bounce"
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    translationY = if (emotion == MascotEmotion.EXCITED) bounceOffset else floatOffset
                }
        ) {
            val cx = size.toPx() / 2
            val cy = size.toPx() / 2
            val bodyRadius = size.toPx() * 0.35f
            val eyeRadius = bodyRadius * 0.12f

            // Body - round rect
            drawRoundRect(
                color = Color(0xFFFFD1DC),
                topLeft = Offset(cx - bodyRadius, cy - bodyRadius * 0.5f),
                size = Size(bodyRadius * 2, bodyRadius * 1.8f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(bodyRadius * 0.5f)
            )

            // Eyes
            val eyeY = cy - bodyRadius * 0.15f
            val eyeSpacing = bodyRadius * 0.3f

            when (emotion) {
                MascotEmotion.HAPPY, MascotEmotion.EXCITED -> {
                    // Large round eyes
                    drawCircle(Color(0xFF2D2D2D), eyeRadius, Offset(cx - eyeSpacing, eyeY))
                    drawCircle(Color(0xFF2D2D2D), eyeRadius, Offset(cx + eyeSpacing, eyeY))
                    // Highlights
                    drawCircle(Color.White, eyeRadius * 0.4f, Offset(cx - eyeSpacing + 2f, eyeY - 2f))
                    drawCircle(Color.White, eyeRadius * 0.4f, Offset(cx + eyeSpacing + 2f, eyeY - 2f))
                }
                MascotEmotion.SAD -> {
                    val sadW = eyeRadius * 1.5f
                    val sadH = eyeRadius * 0.8f
                    drawOval(Color(0xFF2D2D2D), topLeft = Offset(cx - eyeSpacing - sadW/2, eyeY - sadH/2), size = Size(sadW, sadH))
                    drawOval(Color(0xFF2D2D2D), topLeft = Offset(cx + eyeSpacing - sadW/2, eyeY - sadH/2), size = Size(sadW, sadH))
                }
                MascotEmotion.SLEEPING -> {
                    // Closed eyes - curved lines
                    drawArc(Color(0xFF2D2D2D), 0f, 180f, false, Offset(cx - eyeSpacing - eyeRadius, eyeY - 2f), Size(eyeRadius * 2, eyeRadius * 0.5f), style = Stroke(2f))
                    drawArc(Color(0xFF2D2D2D), 0f, 180f, false, Offset(cx + eyeSpacing - eyeRadius, eyeY - 2f), Size(eyeRadius * 2, eyeRadius * 0.5f), style = Stroke(2f))
                }
                MascotEmotion.PAIN -> {
                    val painW = eyeRadius * 1.2f
                    val painH = eyeRadius * 0.5f
                    drawOval(Color(0xFF2D2D2D), topLeft = Offset(cx - eyeSpacing - painW/2, eyeY - painH/2), size = Size(painW, painH))
                    drawOval(Color(0xFF2D2D2D), topLeft = Offset(cx + eyeSpacing - painW/2, eyeY - painH/2), size = Size(painW, painH))
                }
                MascotEmotion.LISTENING -> {
                    drawCircle(Color(0xFF2D2D2D), eyeRadius, Offset(cx - eyeSpacing, eyeY))
                    drawCircle(Color(0xFF2D2D2D), eyeRadius, Offset(cx + eyeSpacing, eyeY))
                }
                MascotEmotion.HUGGING -> {
                    drawCircle(Color(0xFF2D2D2D), eyeRadius, Offset(cx - eyeSpacing, eyeY))
                    drawCircle(Color(0xFF2D2D2D), eyeRadius, Offset(cx + eyeSpacing, eyeY))
                }
            }

            // Mouth
            val mouthY = cy + bodyRadius * 0.3f
            val mouthWidth = bodyRadius * 0.3f
            when (emotion) {
                MascotEmotion.HAPPY, MascotEmotion.EXCITED -> {
                    drawArc(Color(0xFF2D2D2D), 0f, 180f, false, Offset(cx - mouthWidth, mouthY - 2f), Size(mouthWidth * 2, mouthWidth * 0.6f), style = Stroke(2.5f))
                }
                MascotEmotion.SAD, MascotEmotion.PAIN -> {
                    drawArc(Color(0xFF2D2D2D), 180f, 180f, false, Offset(cx - mouthWidth, mouthY - 2f), Size(mouthWidth * 2, mouthWidth * 0.6f), style = Stroke(2.5f))
                }
                MascotEmotion.SLEEPING -> {
                    drawArc(Color(0xFF2D2D2D), 0f, 120f, false, Offset(cx - mouthWidth * 0.5f, mouthY), Size(mouthWidth, mouthWidth * 0.5f), style = Stroke(2f))
                }
                else -> {
                    drawArc(Color(0xFF2D2D2D), 0f, 180f, false, Offset(cx - mouthWidth, mouthY - 2f), Size(mouthWidth * 2, mouthWidth * 0.6f), style = Stroke(2.5f))
                }
            }

            // Cheek blush
            if (emotion == MascotEmotion.HAPPY || emotion == MascotEmotion.EXCITED) {
                drawCircle(Color(0xFFFFB5C8).copy(alpha = 0.4f), bodyRadius * 0.12f, Offset(cx - bodyRadius * 0.55f, cy + bodyRadius * 0.15f))
                drawCircle(Color(0xFFFFB5C8).copy(alpha = 0.4f), bodyRadius * 0.12f, Offset(cx + bodyRadius * 0.55f, cy + bodyRadius * 0.15f))
            }
        }
    }
}
