package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.SaathiMascot
import com.deepak.periodsaathi.ui.theme.*
import kotlin.math.sin

@Composable
fun WellnessScoreCard(
    score: Int,
    streak: Int,
    onConfigureClick: () -> Unit
) {
    // Wave animation offset
    val infiniteTransition = rememberInfiniteTransition(label = "waveOffset")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    // Animated score transition
    val animatedScore by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "score"
    )

    val mascotEmotion = when {
        score <= 30 -> MascotEmotion.SAD
        score <= 70 -> MascotEmotion.LISTENING
        else -> MascotEmotion.HAPPY
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Liquid Beaker Score Meter
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.4f))
                    .border(2.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    val circlePath = Path().apply {
                        addOval(androidx.compose.ui.geometry.Rect(0f, 0f, width, height))
                    }

                    clipPath(circlePath) {
                        // Background wash
                        drawRect(Color(0xFFFFF0F5))

                        // Wave Path drawing
                        val waveHeight = 8f // wave amplitude
                        val waveLength = width // wavelength equal to width
                        val waterLevelY = height - (animatedScore * height) // Y coord for liquid top level

                        val wavePath = Path().apply {
                            moveTo(0f, height)
                            lineTo(0f, waterLevelY)

                            for (x in 0..width.toInt()) {
                                val y = waterLevelY + waveHeight * sin(
                                    (2f * Math.PI * x / waveLength) + waveOffset
                                ).toFloat()
                                lineTo(x.toFloat(), y)
                            }

                            lineTo(width, height)
                            close()
                        }

                        drawPath(
                            path = wavePath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BlushPink,
                                    DeepRose
                                )
                            )
                        )
                    }
                }

                // Score text overlay
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (score > 40) Color.White else Primary
                    )
                    Text(
                        text = "Score",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (score > 40) Color.White.copy(alpha = 0.9f) else OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Mascot & Quest Summary
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SaathiMascot(
                        emotion = mascotEmotion,
                        size = 56.dp
                    )

                    Column {
                        val message = when {
                            score <= 30 -> "I'm here for you. Let's do some self-care. 🌸"
                            score <= 70 -> "You're getting stronger! Keep going. ✨"
                            else -> "Incredible! Your body is glowing! 🎉"
                        }
                        Text(
                            text = message,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥 $streak Day Streak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SoftCoral
                    )

                    Button(
                        onClick = onConfigureClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Log logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
