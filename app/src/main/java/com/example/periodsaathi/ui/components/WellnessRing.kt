package com.example.periodsaathi.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.periodsaathi.ui.theme.Primary
import com.example.periodsaathi.ui.theme.SurfaceContainerHigh

@Composable
fun WellnessRing(
    percentage: Int,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    strokeWidth: Dp = 6.dp,
    progressColor: Color = Primary,
    trackColor: Color = SurfaceContainerHigh,
    label: @Composable (() -> Unit)? = null
) {
    Canvas(modifier = modifier.size(size)) {
        val canvasSize = size.toPx()
        val stroke = strokeWidth.toPx()
        val arcSize = canvasSize - stroke
        val topLeft = Offset(stroke / 2, stroke / 2)

        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(arcSize, arcSize),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        val sweep = (percentage / 100f) * 360f
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = Size(arcSize, arcSize),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}
