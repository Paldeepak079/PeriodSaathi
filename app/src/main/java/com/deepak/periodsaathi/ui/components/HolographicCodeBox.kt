package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.theme.Primary
import com.deepak.periodsaathi.ui.theme.PrimaryContainer
import com.deepak.periodsaathi.ui.theme.Secondary

/**
 * Holographic-style invite code display box that pulses with light.
 *
 * Features:
 * - Gradient border that shifts hue continuously (holographic shimmer)
 * - Subtle pulsing glow behind the box
 * - Each character displayed in its own glass cell
 * - Inner rainbow shimmer overlay
 */
@Composable
fun HolographicCodeBox(
    code: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "holo")

    // Pulsing glow alpha
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse),
        label = "glowAlpha"
    )

    // Shimmer sweep angle
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f, targetValue = 300f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "shimmer"
    )

    // Scale pulse
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOut), RepeatMode.Reverse),
        label = "scale"
    )

    // Border gradient colors — shift over time
    val borderColor1 by infiniteTransition.animateColor(
        initialValue = Color(0xFFFF6B9D),
        targetValue = Color(0xFF9B8FFF),
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOut), RepeatMode.Reverse),
        label = "bc1"
    )
    val borderColor2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF86CFFF),
        targetValue = Color(0xFFFFD700),
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOut), RepeatMode.Reverse),
        label = "bc2"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Glow halo behind box
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(20.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            borderColor1.copy(alpha = glowAlpha * 0.6f),
                            Color.Transparent
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
        )

        // Main holographic box
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    2.dp,
                    Brush.linearGradient(
                        colors = listOf(borderColor1, borderColor2, borderColor1),
                        start = Offset(shimmerOffset, 0f),
                        end = Offset(shimmerOffset + 200f, 200f)
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🔗 Your Invite Code",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                // Character cells
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    code.toList().forEach { char ->
                        CodeCharCell(char = char, shimmerOffset = shimmerOffset)
                    }
                }

                Text(
                    text = "Share this with your partner",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CodeCharCell(char: Char, shimmerOffset: Float) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .drawBehind {
                // Rainbow shimmer inside each cell
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x40FF6B9D),
                            Color(0x409B8FFF),
                            Color(0x4086CFFF)
                        ),
                        start = Offset(shimmerOffset, 0f),
                        end = Offset(shimmerOffset + 100f, 100f)
                    )
                )
            }
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            color = Color.White
        )
    }
}
