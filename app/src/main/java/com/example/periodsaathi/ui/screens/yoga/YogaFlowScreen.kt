package com.example.periodsaathi.ui.screens.yoga

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun YogaFlowScreen(
    onExit: () -> Unit = {},
    viewModel: YogaFlowViewModel = hiltViewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPoseIndex by viewModel.currentPoseIndex.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val breathingPhase by viewModel.breathingPhase.collectAsState()
    val currentPose = viewModel.poses[currentPoseIndex]

    val breathingRadius = remember { Animatable(120f) }

    LaunchedEffect(isPlaying, breathingPhase) {
        if (isPlaying) {
            when (breathingPhase) {
                BreathingPhase.INHALE -> breathingRadius.animateTo(170f, tween(4000))
                BreathingPhase.HOLD -> {}
                BreathingPhase.EXHALE -> breathingRadius.animateTo(120f, tween(6000))
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0A14))
    ) {
        // Close button
        IconButton(
            onClick = {
                viewModel.exit()
                onExit()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pose name
            Text(
                text = currentPose.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentPose.description,
                color = SoftLavender,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Benefits: ${currentPose.benefits}",
                color = BabyBlue,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Breathing circle
            BreathingCircle(
                radius = breathingRadius.value,
                phase = breathingPhase
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Timer
            Text(
                text = formatTime(timeRemaining),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = BlushPink
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pose dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.poses.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPoseIndex) 12.dp else 8.dp)
                            .background(
                                if (index == currentPoseIndex) BlushPink else SoftLavender.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.small)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { viewModel.previousPose() }) {
                    Text("← Previous", color = SoftLavender)
                }

                FloatingActionButton(
                    onClick = { viewModel.togglePlay() },
                    containerColor = BlushPink
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                TextButton(onClick = { viewModel.nextPose() }) {
                    Text("Next →", color = SoftLavender)
                }
            }
        }
    }
}

@Composable
private fun BreathingCircle(radius: Float, phase: BreathingPhase) {
    val animatedColor by animateColorAsState(
        targetValue = when (phase) {
            BreathingPhase.INHALE -> BabyBlue
            BreathingPhase.HOLD -> SoftLavender
            BreathingPhase.EXHALE -> BlushPink
            else -> SoftLavender
        },
        animationSpec = tween(1000),
        label = "breathColor"
    )

    Canvas(modifier = Modifier.size(300.dp)) {
        // Glow effect
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(animatedColor.copy(alpha = 0.3f), Color.Transparent)
            ),
            radius = radius * 1.5f
        )

        // Main circle
        drawCircle(
            color = animatedColor.copy(alpha = 0.8f),
            radius = radius,
            center = Offset(size.width / 2, size.height / 2)
        )
    }

    // Breathing text
    AnimatedContent(
        targetState = phase,
        label = "breathText"
    ) { phaseText ->
        Text(
            text = when (phaseText) {
                BreathingPhase.INHALE -> "Breathe In..."
                BreathingPhase.HOLD -> "Hold..."
                BreathingPhase.EXHALE -> "Breathe Out..."
                else -> "Press Play"
            },
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}