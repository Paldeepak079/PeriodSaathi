package com.example.periodsaathi.ui.screens.remedies

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun RemediesScreen(
    viewModel: RemediesViewModel = hiltViewModel()
) {
    val flippedCards by viewModel.flippedCards.collectAsStateWithLifecycle()
    val hotBagPosition by viewModel.hotBagPosition.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(WarmCream),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Natural Remedies",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary
            )
        }

        // Kitchen Remedies
        item {
            Text(
                text = "Kitchen Remedies",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.remedies) { remedy ->
                    FlipCard(
                        remedy = remedy,
                        isFlipped = remedy.id in flippedCards,
                        onFlip = { viewModel.flipCard(remedy.id) }
                    )
                }
            }
        }

        // Hot Bag Safety
        item {
            HotBagSection(
                position = hotBagPosition,
                onPositionChange = { viewModel.updateHotBagPosition(it) }
            )
        }

        // Yoga Flow
        item {
            YogaFlowCard()
        }
    }
}

@Composable
private fun FlipCard(
    remedy: RemedyCard,
    isFlipped: Boolean,
    onFlip: () -> Unit
) {
    val view = LocalView.current

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400),
        label = "flipRotation"
    )

    Box(
        modifier = Modifier
            .size(width = 150.dp, height = 200.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onFlip()
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // Front side
            GlassCard(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = remedy.emoji, fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = remedy.name,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = "Tap to reveal",
                        fontSize = 10.sp,
                        color = OnSurfaceVariant
                    )
                }
            }
        } else {
            // Back side (flipped)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                GlassCard(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = remedy.ingredients,
                            fontSize = 10.sp,
                            color = OnSurfaceVariant,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = remedy.steps,
                            fontSize = 9.sp,
                            color = OnSurface,
                            maxLines = 4
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 ${remedy.helpfulFor}",
                            fontSize = 9.sp,
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HotBagSection(
    position: Float,
    onPositionChange: (Float) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Safe Heat Therapy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "30°C", color = MintGreen, fontSize = 12.sp)
                Text(text = "45°C", color = SoftCoral, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Custom slider
            val sliderColors = listOf(MintGreen, ButterYellow, SoftCoral)
            Slider(
                value = position,
                onValueChange = onPositionChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = when {
                        position < 0.3f -> MintGreen
                        position < 0.6f -> ButterYellow
                        else -> SoftCoral
                    },
                    activeTrackColor = when {
                        position < 0.3f -> MintGreen
                        position < 0.6f -> ButterYellow
                        else -> SoftCoral
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            val temperature = (30 + (position * 15)).toInt()
            Text(
                text = "Current: $temperature°C",
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )

            if (position > 0.8f) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Warning: Above safe temperature limit",
                    color = Error,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun YogaFlowCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cramp Relief Flow",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = "4 minutes • 5 poses",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }

                Text(text = "🧘", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Child's Pose", "Cat-Cow", "Supine Twist", "Bridge", "Savasana").forEach { pose ->
                    Text(
                        text = pose.take(8) + "..",
                        fontSize = 10.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SpringBounceButton(
                text = "Start Flow ▶",
                onClick = { /* Navigate to yoga flow */ },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Tertiary
            )
        }
    }
}