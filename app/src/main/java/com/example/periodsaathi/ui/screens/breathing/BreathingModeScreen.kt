package com.example.periodsaathi.ui.screens.breathing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.DeepRose
import com.example.periodsaathi.ui.theme.SoftLavender

@Composable
fun BreathingModeScreen(
    onExit: () -> Unit = {},
    viewModel: BreathingModeViewModel = hiltViewModel()
) {
    val phase by viewModel.phase.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val cycleCount by viewModel.cycleCount.collectAsState()

    val circleScale by animateFloatAsState(
        targetValue = when (phase) {
            BreathingPhase.INHALE -> 1.4f
            BreathingPhase.HOLD_IN -> 1.4f
            BreathingPhase.EXHALE -> 0.8f
            BreathingPhase.HOLD_OUT -> 0.8f
        },
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 100f),
        label = "circleScale"
    )

    val phaseText = when (phase) {
        BreathingPhase.INHALE -> "Breathe In"
        BreathingPhase.HOLD_IN -> "Hold"
        BreathingPhase.EXHALE -> "Breathe Out"
        BreathingPhase.HOLD_OUT -> "Hold"
    }

    val phaseColor = when (phase) {
        BreathingPhase.INHALE -> BabyBlue
        BreathingPhase.HOLD_IN -> SoftLavender
        BreathingPhase.EXHALE -> BlushPink
        BreathingPhase.HOLD_OUT -> DeepRose
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B2838))
                )
            )
    ) {
        // Background particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(20) {
                val x = (0..size.width.toInt()).random().toFloat()
                val y = (0..size.height.toInt()).random().toFloat()
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = (2..6).random().toFloat(),
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Breathing Exercise",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            // Breathing circle
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(circleScale)
                    .clip(CircleShape)
                    .background(phaseColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(phaseColor.copy(alpha = if (isRunning) 0.4f else 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        color = phaseColor,
                        trackColor = phaseColor.copy(alpha = 0.1f),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(200.dp)
                    )

                    Text(
                        text = phaseText,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cycle $cycleCount / 10",
                color = SoftLavender,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Start/Stop button
            Button(
                onClick = {
                    if (isRunning) viewModel.stopBreathing()
                    else viewModel.startBreathing()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) DeepRose else BlushPink
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (isRunning) "Stop" else "Start Breathing",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = {
                viewModel.stopBreathing()
                onExit()
            }) {
                Text("Exit", color = SoftLavender)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
