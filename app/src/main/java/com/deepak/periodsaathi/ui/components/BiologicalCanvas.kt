package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.deepak.periodsaathi.R

/**
 * Represents the current phase or state the user is configuring/experiencing
 * to sync with the interactive Biological Visualization Engine.
 */
enum class BiologicalState {
    DEFAULT,
    FOLLICLE_GROWTH,   // "Get Pregnant"
    LINING_PHASE,      // "Track Cycle"
    OVULATION,
    MENSTRUATION
}

@Composable
fun CycleInteractionCanvas(
    biologicalState: BiologicalState,
    modifier: Modifier = Modifier
) {
    // Top 30% of screen calculation
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val canvasHeight = screenHeight * 0.3f

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bio_placeholder))
    
    // Animate between different segments or speeds based on state
    // In a real scenario with proper Lottie segments, you would use composition markers
    // Here we just change playback speed or bounds to simulate "reactivity"
    val isPlaying = biologicalState != BiologicalState.DEFAULT
    
    val speed = when (biologicalState) {
        BiologicalState.FOLLICLE_GROWTH -> 1.5f
        BiologicalState.LINING_PHASE -> 0.8f
        BiologicalState.OVULATION -> 2.0f
        BiologicalState.MENSTRUATION -> 1.0f
        else -> 0f
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .background(Color(0xFF0F0B1E)), // Deep background for contrast
        contentAlignment = Alignment.Center
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}
