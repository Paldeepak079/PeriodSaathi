package com.deepak.periodsaathi.ui.screens.yoga

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.deepak.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
    val poseElapsed by viewModel.poseElapsed.collectAsState()
    val breathingPhase by viewModel.breathingPhase.collectAsState()
    val currentPose = viewModel.poses[currentPoseIndex]
    val poseRemaining = (currentPose.duration - poseElapsed).coerceAtLeast(0)
    val nextPoses = viewModel.poses.drop(currentPoseIndex + 1).take(2)

    val breathingRadius = remember { Animatable(120f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                breathingRadius.animateTo(170f, tween(4000))
                delay(2000)
                breathingRadius.animateTo(120f, tween(6000))
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

            // Up Next suggestions
            if (nextPoses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Up Next",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftLavender.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                nextPoses.forEachIndexed { index, pose ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pose.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = pose.description.take(40) + if (pose.description.length > 40) "..." else "",
                                    color = SoftLavender.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "❯",
                                color = SoftLavender.copy(alpha = 0.4f),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Breathing circle
            BreathingCircle(
                radius = breathingRadius.value,
                phase = if (isPlaying) breathingPhase else BreathingPhase.IDLE
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Timer - per-pose remaining
            Text(
                text = formatTime(poseRemaining),
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
                                shape = MaterialTheme.shapes.small
                            )
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
                    Text("Previous", color = SoftLavender)
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
                    Text("Next", color = SoftLavender)
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
            BreathingPhase.IDLE -> SoftLavender
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
                BreathingPhase.IDLE -> "Press Play"
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
