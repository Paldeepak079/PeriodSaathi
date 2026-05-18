package com.deepak.periodsaathi.ui.screens.remedies

import androidx.compose.animation.core.*
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Natural Remedies", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

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

        // Hot Bag Safety Section
        Text(text = "Hot Bag Safety", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Temperature: ${(hotBagPosition * 100).toInt()}%", color = OnSurface)

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = hotBagPosition,
                    onValueChange = { viewModel.updateHotBagPosition(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = when {
                            hotBagPosition > 0.8f -> Color.Red
                            hotBagPosition > 0.5f -> ButterYellow
                            else -> MintGreen
                        },
                        activeTrackColor = when {
                            hotBagPosition > 0.8f -> Color.Red
                            hotBagPosition > 0.5f -> ButterYellow
                            else -> MintGreen
                        }
                    )
                )

                if (hotBagPosition > 0.8f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "âš ï¸ Warning: Too hot! Reduce temperature.",
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

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
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "ðŸ§˜ 4-Minute Flow", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Text(text = "Gentle stretches to ease cramps", color = OnSurfaceVariant, fontSize = 14.sp)
                }
                Button(
                    onClick = onNavigateToYoga,
                    colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Start")
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
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

