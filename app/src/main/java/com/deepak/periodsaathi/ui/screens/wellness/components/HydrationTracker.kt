package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.deepak.periodsaathi.ui.theme.BabyBlue
import com.deepak.periodsaathi.ui.theme.BlushPink
import com.deepak.periodsaathi.ui.theme.OnSurface
import com.deepak.periodsaathi.ui.theme.Primary
import kotlin.math.sin

@Composable
fun HydrationTracker(
    currentGlasses: Int,
    onAddWater: () -> Unit,
    onAdd500ml: () -> Unit
) {
    val goalGlasses = 8
    val percent = currentGlasses / goalGlasses.toFloat()

    // Wave animation offset
    val infiniteTransition = rememberInfiniteTransition(label = "waveOffset")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    val animatedFillLevel by animateFloatAsState(
        targetValue = percent.coerceIn(0f, 1f),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "fillLevel"
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Water Beaker progress
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.4f))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    val beakerPath = Path().apply {
                        addOval(androidx.compose.ui.geometry.Rect(0f, 0f, width, height))
                    }

                    clipPath(beakerPath) {
                        drawRect(Color(0xFFE1F5FE)) // Clean light blue base

                        val waveHeight = 6f
                        val waveLength = width
                        val waterLevelY = height - (animatedFillLevel * height)

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
                                    Color(0xFF81D4FA),
                                    Color(0xFF0288D1)
                                )
                            )
                        )
                    }
                }

                // Text overlay inside beaker
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentGlasses / $goalGlasses",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (percent > 0.45f) Color.White else Primary
                    )
                    Text(
                        text = "Glasses",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (percent > 0.45f) Color.White.copy(0.9f) else OnSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Hydro Logs
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hydration Tracker 💧",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OnSurface
                )
                Text(
                    text = "Drink plenty of water to naturally thin flow volumes and ease severe muscle spasms.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAddWater,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("+1 Glass", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onAdd500ml,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                    ) {
                        Text("+2 Glasses", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
