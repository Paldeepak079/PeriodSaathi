package com.deepak.periodsaathi.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieAnim(
    resName: String,
    modifier: Modifier = Modifier,
    iterations: Int = Int.MAX_VALUE
) {
    val context = LocalContext.current
    val composition = rememberLottieComposition(
        LottieCompositionSpec.JsonString(
            try {
                context.assets.open("lottie/$resName.json").bufferedReader().use { it.readText() }
            } catch (e: Exception) { "{}" }
        )
    )
    LottieAnimation(
        composition = composition.value,
        iterations = iterations,
        modifier = modifier
    )
}
