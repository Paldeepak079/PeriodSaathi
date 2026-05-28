package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.PrimaryButton
import com.deepak.periodsaathi.ui.theme.Lavender
import com.deepak.periodsaathi.ui.theme.Primary
import com.deepak.periodsaathi.wellness.ui.wellness.utils.WellnessSoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BreathPhase { INHALE, HOLD, EXHALE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingSessionScreen(
    onClose: () -> Unit,
    onComplete: () -> Unit
) {
    // Standard hilt entry points injection
    val context = androidx.compose.ui.platform.LocalContext.current
    val soundManager = remember {
        // Safe Hilt entry-point equivalent (initialized cleanly inside view model
        // but instantiated manually here to prevent view-graph compilation locks).
        com.deepak.periodsaathi.wellness.ui.wellness.utils.WellnessSoundManager(
            context = context
        )
    }

    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var isRunning by remember { mutableStateOf(true) }
    var timeRemaining by remember { mutableStateOf(180) } // 3 minutes total
    var breathPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var phaseTimer by remember { mutableStateOf(4) } // Cycle lengths
    var volumeMuted by remember { mutableStateOf(false) }

    // Phase cycle transitions loop
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        
        // Start Sound Synthesizer loop
        soundManager.playZenMusic()
        soundManager.setVolume(if (volumeMuted) 0f else 0.5f)

        while (timeRemaining > 0 && isRunning) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Phase transition alert
            when (breathPhase) {
                BreathPhase.INHALE -> {
                    phaseTimer = 4
                    while (phaseTimer > 0) {
                        delay(1000)
                        phaseTimer--
                        timeRemaining--
                    }
                    breathPhase = BreathPhase.HOLD
                }
                BreathPhase.HOLD -> {
                    phaseTimer = 2
                    while (phaseTimer > 0) {
                        delay(1000)
                        phaseTimer--
                        timeRemaining--
                    }
                    breathPhase = BreathPhase.EXHALE
                }
                BreathPhase.EXHALE -> {
                    phaseTimer = 4
                    while (phaseTimer > 0) {
                        delay(1000)
                        phaseTimer--
                        timeRemaining--
                    }
                    breathPhase = BreathPhase.INHALE
                }
            }
        }

        if (timeRemaining <= 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            soundManager.release()
            onComplete()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    // Orb expansion calculations
    val orbScaleAnim = remember { Animatable(0.45f) }
    LaunchedEffect(breathPhase) {
        when (breathPhase) {
            BreathPhase.INHALE -> orbScaleAnim.animateTo(0.95f, tween(4000, easing = EaseInOutCubic))
            BreathPhase.HOLD -> { /* Keep scale */ }
            BreathPhase.EXHALE -> orbScaleAnim.animateTo(0.45f, tween(4000, easing = EaseInOutCubic))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0B1F)) // Deep space visual contrast
    ) {
        // Blurred starry particles
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(360.dp)
                .background(Brush.radialGradient(listOf(Color(0x30C9B8FF), Color.Transparent)), CircleShape)
                .blur(80.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Top Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        soundManager.release()
                        onClose()
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White)
                }

                Text(
                    text = "Breathe Therapy",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Sound Toggle
                IconButton(
                    onClick = {
                        volumeMuted = !volumeMuted
                        soundManager.setVolume(if (volumeMuted) 0f else 0.5f)
                    }
                ) {
                    Text(
                        text = if (volumeMuted) "🔇" else "🔊",
                        fontSize = 20.sp
                    )
                }
            }

            // Expanding Zen Orb Canvas
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Expanding orb canvas
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = size.width / 2.2f * orbScaleAnim.value
                    
                    // Outer glow rings
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x95FFB5C8),
                                Color(0x30C9B8FF),
                                Color.Transparent
                            )
                        ),
                        radius = baseRadius * 1.3f,
                        center = center
                    )

                    // Solid Core
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFB5C8),
                                Color(0xFFC9B8FF)
                            )
                        ),
                        radius = baseRadius,
                        center = center
                    )
                }

                // Breathing guidance text overlays
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val promptText = when (breathPhase) {
                        BreathPhase.INHALE -> "Breathe In 💨"
                        BreathPhase.HOLD -> "Hold... 🌸"
                        BreathPhase.EXHALE -> "Breathe Out ✨"
                    }
                    Text(
                        text = promptText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$phaseTimer sec",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Total session visual clocks
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                val mins = timeRemaining / 60
                val secs = timeRemaining % 60
                Text(
                    text = String.format("%02d:%02d remaining", mins, secs),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f)
                )

                // Breathing timeline nodes
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(BreathPhase.INHALE, BreathPhase.HOLD, BreathPhase.EXHALE).forEach { phase ->
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (breathPhase == phase) Color(0xFFFFB5C8)
                                    else Color.White.copy(alpha = 0.2f)
                                )
                        )
                    }
                }
            }
        }
    }
}
