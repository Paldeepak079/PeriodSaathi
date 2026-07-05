package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.deepak.periodsaathi.ui.theme.Lavender
import com.deepak.periodsaathi.ui.theme.OnSurface
import com.deepak.periodsaathi.ui.theme.OnSurfaceVariant
import com.deepak.periodsaathi.ui.theme.Primary
import com.deepak.periodsaathi.ui.theme.MintGreen
import androidx.compose.ui.draw.scale
import kotlin.math.sin

@Composable
fun HydrationTracker(
    currentGlasses: Int,
    waterReminderEnabled: Boolean = false,
    onAddWater: () -> Unit,
    onAdd500ml: () -> Unit,
    onToggleReminder: () -> Unit = {}
) {
    val goalGlasses = 8
    val percent = currentGlasses / goalGlasses.toFloat()

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            drawRect(Color(0xFFE1F5FE))

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
                                    colors = listOf(Color(0xFF81D4FA), Color(0xFF0288D1))
                                )
                            )
                        }
                    }

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

            Spacer(Modifier.height(12.dp))

            // Water Reminder Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (waterReminderEnabled) BabyBlue.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        if (waterReminderEnabled) BabyBlue.copy(0.5f) else Color.White.copy(0.7f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onToggleReminder() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (waterReminderEnabled) "🔔" else "🔕",
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (waterReminderEnabled) "Reminder Active" else "Set Reminder",
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (waterReminderEnabled) "Every 2 hours, 9AM - 9PM" else "Tap to enable water reminders",
                            fontSize = 10.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = waterReminderEnabled,
                    onCheckedChange = { onToggleReminder() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BlushPink,
                        checkedTrackColor = BlushPink.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.scale(scaleX = 0.8f, scaleY = 0.8f)
                )
            }
        }
    }
}


