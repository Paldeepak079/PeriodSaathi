package com.example.periodsaathi.ui.screens.yoga

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun YogaFlowScreen(
    viewModel: YogaFlowViewModel = hiltViewModel(),
    onExit: () -> Unit = {}
) {
    val currentPoseIndex by viewModel.currentPoseIndex.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val timeRemaining by viewModel.timeRemaining.collectAsStateWithLifecycle()
    val breathingPhase by viewModel.breathingPhase.collectAsStateWithLifecycle()

    val currentPose = viewModel.poses[currentPoseIndex]

    // Timer countdown
    LaunchedEffect(isPlaying, currentPoseIndex) {
        if (isPlaying) {
            var remaining = timeRemaining
            while (remaining > 0 && isPlaying) {
                delay(1000)
                remaining--
                viewModel.setTimeRemaining(remaining)
            }
            if (remaining == 0 && currentPoseIndex < viewModel.poses.size - 1) {
                viewModel.nextPose()
            } else if (remaining == 0) {
                viewModel.togglePlay()
            }
        }
    }

    // Breathing animation
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            viewModel.setBreathingPhase(BreathingPhase.INHALE)
            delay(4000)
            viewModel.setBreathingPhase(BreathingPhase.HOLD)
            delay(2000)
            viewModel.setBreathingPhase(BreathingPhase.EXHALE)
            delay(6000)
            viewModel.setBreathingPhase(BreathingPhase.REST)
            delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0A14))
    ) {
        // Exit button
        IconButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Exit",
                tint = Color.White.copy(alpha = 0.7f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Pose info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = currentPose.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentPose.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 ${currentPose.benefits}",
                    fontSize = 12.sp,
                    color = SoftLavender.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            // Breathing circle
            BreathingCircle(
                phase = breathingPhase,
                isPlaying = isPlaying
            )

            // Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Timer
                Text(
                    text = formatTime(timeRemaining),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Play/Pause button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Primary)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, _ -> }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { viewModel.togglePlay() }
                    ) {
                        Text(
                            text = if (isPlaying) "⏸" else "▶️",
                            fontSize = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pose navigation dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.poses.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentPoseIndex) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentPoseIndex) Primary
                                    else Color.White.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation arrows
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    TextButton(
                        onClick = { viewModel.previousPose() },
                        enabled = currentPoseIndex > 0
                    ) {
                        Text("← Previous", color = Color.White.copy(alpha = if (currentPoseIndex > 0) 1f else 0.3f))
                    }
                    TextButton(
                        onClick = { viewModel.nextPose() },
                        enabled = currentPoseIndex < viewModel.poses.size - 1
                    ) {
                        Text("Next →", color = Color.White.copy(alpha = if (currentPoseIndex < viewModel.poses.size - 1) 1f else 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
private fun BreathingCircle(
    phase: BreathingPhase,
    isPlaying: Boolean
) {
    val targetRadius = when (phase) {
        BreathingPhase.INHALE -> 170f
        BreathingPhase.HOLD -> 170f
        BreathingPhase.EXHALE -> 120f
        BreathingPhase.REST -> 120f
    }

    val animatedRadius = animateFloatAsState(
        targetValue = if (isPlaying) targetRadius else 120f,
        animationSpec = tween(
            durationMillis = when (phase) {
                BreathingPhase.INHALE -> 4000
                BreathingPhase.HOLD -> 2000
                BreathingPhase.EXHALE -> 6000
                BreathingPhase.REST -> 2000
            },
            easing = FastOutSlowInEasing
        ),
        label = "breathingRadius"
    )

    val circleColor = when (phase) {
        BreathingPhase.INHALE -> BabyBlue
        BreathingPhase.HOLD -> SoftLavender
        BreathingPhase.EXHALE -> BlushPink
        BreathingPhase.REST -> Color.Gray.copy(alpha = 0.5f)
    }

    val phaseText = when (phase) {
        BreathingPhase.INHALE -> "Breathe In..."
        BreathingPhase.HOLD -> "Hold..."
        BreathingPhase.EXHALE -> "Breathe Out..."
        BreathingPhase.REST -> "Rest"
    }

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Glow effect
            drawCircle(
                color = circleColor.copy(alpha = 0.3f),
                radius = animatedRadius.value.dp.toPx() + 20.dp.toPx()
            )

            // Main circle
            drawCircle(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(
                        circleColor.copy(alpha = 0.8f),
                        circleColor.copy(alpha = 0.4f)
                    )
                ),
                radius = animatedRadius.value.dp.toPx()
            )
        }

        AnimatedContent(
            targetState = phaseText,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            label = "breathingText"
        ) { text ->
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}