package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.theme.BabyBlue
import com.deepak.periodsaathi.ui.theme.BlushPink
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun WaterRingWithWave(
    currentGlasses: Int,
    totalGlasses: Int = 8,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    onGoalReached: () -> Unit = {}
) {
    val fillLevel = if (totalGlasses > 0) min(currentGlasses.toFloat() / totalGlasses, 1f) else 0f

    val infiniteTransition = rememberInfiniteTransition(label = "waterWave")
    val phaseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing)
        ),
        label = "phase"
    )

    if (currentGlasses >= totalGlasses && totalGlasses > 0) {
        onGoalReached()
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 12.dp.toPx()
            val ringSize = size.toPx() - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val ringRect = androidx.compose.ui.geometry.Rect(topLeft, Offset(topLeft.x + ringSize, topLeft.y + ringSize))

            // Background ring
            drawArc(
                color = Color(0xFFEEEEEE),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(ringSize, ringSize),
                style = Stroke(width = strokeWidth)
            )

            // Progress ring
            val progressSweep = fillLevel * 360f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(BabyBlue, BlushPink),
                    center = Offset(size.toPx() / 2, size.toPx() / 2)
                ),
                startAngle = -90f,
                sweepAngle = progressSweep,
                useCenter = false,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(ringSize, ringSize),
                style = Stroke(width = strokeWidth)
            )

            // Wave inside
            val waveHeight = 20.dp.toPx()
            val waveTop = size.toPx() * (1f - fillLevel)
            val wavePath = Path().apply {
                moveTo(0f, waveTop + waveHeight)
                for (x in 0..size.toPx().toInt() step 2) {
                    val y = waveTop + sin((x * 0.05f) + phaseOffset) * waveHeight
                    lineTo(x.toFloat(), y)
                }
                lineTo(size.toPx(), size.toPx())
                lineTo(0f, size.toPx())
                close()
            }
            drawPath(
                path = wavePath,
                color = BabyBlue.copy(alpha = 0.6f)
            )

            // Second wave (offset)
            val wavePath2 = Path().apply {
                moveTo(0f, waveTop + waveHeight)
                for (x in 0..size.toPx().toInt() step 2) {
                    val y = waveTop + sin((x * 0.05f) + phaseOffset + PI.toFloat()) * waveHeight
                    lineTo(x.toFloat(), y)
                }
                lineTo(size.toPx(), size.toPx())
                lineTo(0f, size.toPx())
                close()
            }
            drawPath(
                path = wavePath2,
                color = BabyBlue.copy(alpha = 0.4f)
            )
        }

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$currentGlasses/$totalGlasses",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )
            Text(
                text = "💧",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .then(Modifier.graphicsLayer { translationY = 28f })
            )
        }
    }
}

