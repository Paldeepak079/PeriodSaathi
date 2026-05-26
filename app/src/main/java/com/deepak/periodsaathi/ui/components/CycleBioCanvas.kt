package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.deepak.periodsaathi.R

/**
 * Biological goal states that drive the CycleBioCanvas animation reactivity.
 */
enum class BioCanvasGoal {
    NONE,
    TRACK_CYCLE,
    GET_PREGNANT,
    TRACK_PREGNANCY
}

/**
 * The Biological Visualization Canvas — the central "Cycle Interaction Canvas"
 * that spans the upper portion of the onboarding screen and home dashboard.
 *
 * Features:
 * - Lottie-animated uterus with breathing loop
 * - Reactive background tint that changes based on [goal] (hormonal phase color mapping)
 * - Optional glow pulse overlay when a symptom is logged
 *
 * Goal → Visual State mapping:
 * - TRACK_CYCLE    → Soft pink (#FFE4EE), standard uterus breathe
 * - GET_PREGNANT   → Sky blue (#DCEEFF) + follicle growth (cycle_glow overlay)
 * - TRACK_PREGNANCY → Warm amber (#FFF0DC) + slower breathe
 * - NONE           → Neutral warm cream, gentle breathing
 */
@Composable
fun CycleBioCanvas(
    goal: BioCanvasGoal = BioCanvasGoal.NONE,
    modifier: Modifier = Modifier,
    showGlowPulse: Boolean = false,
    animationSpeed: Float = 1f
) {
    val bgTint by animateColorAsState(
        targetValue = when (goal) {
            BioCanvasGoal.GET_PREGNANT   -> Color(0xFFDCEEFF).copy(alpha = 0.45f)
            BioCanvasGoal.TRACK_PREGNANCY -> Color(0xFFFFF0DC).copy(alpha = 0.45f)
            BioCanvasGoal.TRACK_CYCLE    -> Color(0xFFFFE4EE).copy(alpha = 0.40f)
            BioCanvasGoal.NONE           -> Color(0xFFFFF8F2).copy(alpha = 0.30f)
        },
        animationSpec = tween(durationMillis = 700),
        label = "bioBgTint"
    )

    val breatheSpeed = when (goal) {
        BioCanvasGoal.TRACK_PREGNANCY -> 0.7f   // slower, more peaceful
        BioCanvasGoal.GET_PREGNANT    -> 1.3f   // slightly excited
        else                          -> 1.0f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(bgTint),
        contentAlignment = Alignment.Center
    ) {
        // Main animated uterus
        LottieAnimView(
            resId = R.raw.anim_uterus_breathe,
            loop = true,
            speed = breatheSpeed * animationSpeed,
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(200.dp)
        )

        // Glow pulse overlay — triggered when user logs a symptom
        if (showGlowPulse) {
            LottieAnimView(
                resId = R.raw.anim_cycle_glow,
                loop = false,
                speed = 1.2f,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(220.dp)
            )
        }

        // Goal-specific overlay indicator
        if (goal == BioCanvasGoal.GET_PREGNANT) {
            // Follicle growth indicator — small pulsing sphere top-right of canvas
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFADD8FF).copy(alpha = 0.7f))
            )
        }
    }
}
