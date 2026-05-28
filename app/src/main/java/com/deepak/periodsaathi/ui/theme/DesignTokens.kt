package com.deepak.periodsaathi.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Centralized design tokens for Period Saathi.
 * Use these constants instead of ad-hoc magic numbers throughout the UI.
 *
 * To swap a Lottie mascot animation file, update the constants in [MascotAssets].
 */
object Spacing {
    val xxs  = 2.dp
    val xs   = 4.dp
    val sm   = 8.dp
    val md   = 12.dp
    val lg   = 16.dp
    val xl   = 24.dp
    val xxl  = 32.dp
    val xxxl = 48.dp
    val pageHorizontal = 16.dp
    val contentHorizontal = 24.dp
    val cardInner = 20.dp
    val bottomNavHeight = 80.dp
}

object Radius {
    val xs     = 6.dp
    val sm     = 8.dp
    val md     = 12.dp
    val lg     = 16.dp
    val xl     = 20.dp
    val xxl    = 24.dp
    val card   = 24.dp
    val chip   = 50.dp
    val button = 50.dp
    val bottom = 28.dp
}

object Elevation {
    val none  = 0.dp
    val xs    = 1.dp
    val sm    = 2.dp
    val md    = 4.dp
    val lg    = 8.dp
    val modal = 16.dp
}

/**
 * Mascot Lottie animation asset paths.
 *
 * All assets live in app/src/main/assets/lottie/.
 * To swap animations, replace the JSON files here — no code changes needed.
 *
 * States:
 *  - IDLE: breathing / blinking loop while waiting
 *  - HAPPY: on greeting or positive event
 *  - EXCITED: on achievement / ovulation phase
 *  - SLEEPING: follicular low-energy / sleep card
 *  - HUGGING: luteal / supportive
 *  - THINKING: loading / analysing state
 *
 * Phase mappings:
 *  - MENSTRUAL  → HUGGING (warm, comforting)
 *  - FOLLICULAR → HAPPY (energised)
 *  - OVULATORY  → EXCITED (peak energy)
 *  - LUTEAL     → SLEEPING (wind-down)
 */
object MascotAssets {
    const val IDLE      = "lottie/mascot_idle.json"
    const val HAPPY     = "lottie/mascot_happy.json"
    const val EXCITED   = "lottie/mascot_excited.json"
    const val SLEEPING  = "lottie/mascot_sleeping.json"
    const val HUGGING   = "lottie/mascot_hugging.json"
    const val THINKING  = "lottie/mascot_thinking.json"
    // Reaction overlays (short one-shot bursts)
    const val REACTION_WATER     = "lottie/reaction_water.json"
    const val REACTION_STREAK    = "lottie/reaction_streak.json"
    const val REACTION_CONFETTI  = "lottie/reaction_confetti.json"
}

object AnimDuration {
    const val fastest  = 100
    const val fast     = 200
    const val normal   = 300
    const val slow     = 500
    const val verySlow = 800
    const val page     = 350
    const val spring   = 400
}
