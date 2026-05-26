package com.deepak.periodsaathi.ui.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * Reusable Lottie animation wrapper.
 *
 * @param resId    Raw resource ID of the Lottie JSON (e.g. R.raw.anim_uterus_breathe)
 * @param loop     Whether to loop the animation indefinitely.
 * @param speed    Playback speed multiplier (default 1.0).
 * @param modifier Compose modifier.
 * @param isPlaying Whether animation is currently playing.
 */
@Composable
fun LottieAnimView(
    @RawRes resId: Int,
    modifier: Modifier = Modifier,
    loop: Boolean = true,
    speed: Float = 1f,
    isPlaying: Boolean = true
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = if (loop) LottieConstants.IterateForever else 1,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

/**
 * One-shot Lottie animation that calls [onFinished] when it completes.
 * Perfect for celebrations (partner connect, onboarding complete).
 */
@Composable
fun LottieOneShotView(
    @RawRes resId: Int,
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    onFinished: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = speed,
        isPlaying = true
    )
    if (progress == 1f && composition != null) onFinished()
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}
