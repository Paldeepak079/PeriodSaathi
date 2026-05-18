package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.domain.model.CyclePhase
import com.deepak.periodsaathi.ui.theme.BabyBlue
import com.deepak.periodsaathi.ui.theme.BlushPink
import com.deepak.periodsaathi.ui.theme.DeepRose
import com.deepak.periodsaathi.ui.theme.SoftLavender
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min

private val phaseColorMap = mapOf(
    CyclePhase.MENSTRUAL to BlushPink,
    CyclePhase.FOLLICULAR to BabyBlue,
    CyclePhase.OVULATORY to SoftLavender,
    CyclePhase.LUTEAL to DeepRose,
    CyclePhase.PMS to Color(0xFFFFB5C8),
    CyclePhase.UNKNOWN to Color(0xFFCCCCCC)
)

@Composable
fun CycleRing(
    currentDay: Int,
    totalDays: Int,
    phase: CyclePhase,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (totalDays > 0) min(currentDay.toFloat() / totalDays, 1f) else 0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "progress"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val phaseColor = phaseColorMap[phase] ?: Color.Gray

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 16.dp.toPx()
            val arcSize = size.toPx() - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcRect = androidx.compose.ui.geometry.Rect(topLeft, Offset(topLeft.x + arcSize, topLeft.y + arcSize))
            val sweepAngle = 220f
            val startAngle = -160f

            // Background arc
            drawArc(
                color = Color(0xFFEEEEEE),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth)
            )

            // Progress arc with gradient
            val progressSweep = animatedProgress * sweepAngle
            val gradient = Brush.sweepGradient(
                colors = listOf(phaseColor, phaseColor.copy(alpha = 0.6f)),
                center = Offset(size.toPx() / 2, size.toPx() / 2)
            )
            drawArc(
                brush = gradient,
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth)
            )

            // Glow effect
            drawArc(
                color = phaseColor.copy(alpha = glowAlpha * 0.3f),
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth + 4.dp.toPx())
            )
        }

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$currentDay",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )
            Text(
                text = "Day",
                fontSize = 12.sp,
                color = Color(0xFF6B6B6B),
                modifier = Modifier.align(Alignment.BottomCenter).then(Modifier.graphicsLayer { translationY = 24f })
            )
            Text(
                text = phase.emoji,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.BottomCenter).then(Modifier.graphicsLayer { translationY = 46f })
            )
        }
    }
}

