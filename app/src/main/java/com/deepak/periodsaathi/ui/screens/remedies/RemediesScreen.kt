package com.deepak.periodsaathi.ui.screens.remedies

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun RemediesScreen(
    onNavigateToYoga: () -> Unit = {},
    viewModel: RemediesViewModel = hiltViewModel()
) {
    val flippedCards by viewModel.flippedCards.collectAsState()
    val hotBagPosition by viewModel.hotBagPosition.collectAsState()

    val tempC = (30 + hotBagPosition * 15).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Natural Remedies", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        Spacer(modifier = Modifier.height(20.dp))

        // Kitchen Remedies
        Text(text = "Kitchen Remedies", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(
                listOf(
                    Triple("🫚", "Ginger", "Reduces inflammation"),
                    Triple("🟤", "Jaggery", "Instant energy & iron"),
                    Triple("🌿", "Fennel", "Relieves bloating")
                )
            ) { (emoji, name, desc) ->
                GlassCard(
                    modifier = Modifier
                        .width(140.dp)
                        .height(180.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    Brush.verticalGradient(listOf(PrimaryContainer, Color.White)),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = desc, fontSize = 10.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tap to reveal", color = OnSurfaceVariant, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.remedies) { remedy ->
                FlipCard(
                    remedy = remedy,
                    isFlipped = remedy.id in flippedCards,
                    onFlip = { viewModel.flipCard(remedy.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Safe Heat Therapy
        Text(text = "Safe Heat Therapy", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Temperature: $tempC°C", color = OnSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = tempC.toFloat(),
                    onValueChange = { viewModel.updateHotBagPosition((it - 30f) / 15f) },
                    valueRange = 30f..45f,
                    colors = SliderDefaults.colors(
                        thumbColor = when {
                            tempC > 40 -> Color.Red
                            tempC > 35 -> ButterYellow
                            else -> MintGreen
                        },
                        activeTrackColor = when {
                            tempC > 40 -> Color.Red
                            tempC > 35 -> ButterYellow
                            else -> MintGreen
                        }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${tempC}°C", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = OnPrimaryContainer.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🔥", fontSize = 12.sp)
                            Text(text = "Soothing Range", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Primary)
                        }
                    }
                }

                if (tempC > 40) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⚠️ Warning: Too hot! Reduce temperature.",
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Breathing Circle
        BreathingCircle()

        Spacer(modifier = Modifier.height(32.dp))

        // Yoga Flow Section
        Text(text = "Yoga Flow", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToYoga() },
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "🧘 Cramp Relief Flow", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text(text = "Gentle stretches to ease cramps", color = OnSurfaceVariant, fontSize = 14.sp)
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PrimaryContainer.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "15 Mins / 5 Poses",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val infiniteTransition = rememberInfiniteTransition(label = "btnGlow")
                val glowAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glowAlpha"
                )

                Box {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Primary.copy(alpha = glowAlpha * 0.15f), RoundedCornerShape(12.dp))
                    )
                    Button(
                        onClick = onNavigateToYoga,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "START FLOW", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun BreathingCircle() {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )
    val phaseText = if (breathScale > 0.8f) "Inhale" else "Exhale"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val baseRadius = size.minDimension / 2 * breathScale
                drawCircle(
                    color = Primary.copy(alpha = 0.1f),
                    radius = baseRadius + 20f
                )
                drawCircle(
                    color = Primary.copy(alpha = 0.2f),
                    radius = baseRadius
                )
            }
            Text(
                text = phaseText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Breathe",
            fontSize = 14.sp,
            color = OnSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FlipCard(remedy: Remedy, isFlipped: Boolean, onFlip: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400),
        label = "flipRotation"
    )

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rotation <= 90f) DeepRose.copy(alpha = 0.2f) else BlushPink.copy(alpha = 0.2f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (rotation <= 90f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = remedy.emoji, fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = remedy.name, color = OnSurface, fontWeight = FontWeight.Medium)
                    Text(text = "Tap to reveal", color = OnSurfaceVariant, fontSize = 12.sp)
                }
            } else {
                Column(
                    modifier = Modifier
                        .graphicsLayer { rotationY = 180f }
                        .padding(12.dp)
                ) {
                    Text(text = remedy.emoji, fontSize = 28.sp)
                    Text(text = remedy.name, color = OnSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Ingredients: ${remedy.ingredients}", color = OnSurfaceVariant, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Steps: ${remedy.steps.take(50)}...", color = OnSurfaceVariant, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Helps: ${remedy.helpsWith}", color = BabyBlue, fontSize = 10.sp)
                }
            }
        }
    }
}
