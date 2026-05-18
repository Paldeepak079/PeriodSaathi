package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val shimmerColors = listOf(
    Color.White.copy(alpha = 0.2f),
    Color.White.copy(alpha = 0.5f),
    Color.White.copy(alpha = 0.2f)
)

@Composable
fun ShimmerBox(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearEasing)),
        label = "shimmerTranslate"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun ShimmerCard(height: Dp = 120.dp) {
    ShimmerBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    )
}

@Composable
fun ShimmerHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(28.dp)
        )
        Spacer(Modifier.height(16.dp))
        Row {
            repeat(4) {
                ShimmerBox(
                    modifier = Modifier
                        .size(64.dp),
                    shape = CircleShape
                )
                if (it < 3) Spacer(Modifier.width(12.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
        Spacer(Modifier.height(12.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
    }
}

@Composable
fun ShimmerCalendarGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(7) {
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        // Day cells - 5 rows
        repeat(5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) {
                    ShimmerBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .padding(4.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}

