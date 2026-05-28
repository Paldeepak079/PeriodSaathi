package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.deepak.periodsaathi.R

enum class MascotEmotion { HAPPY, SAD, SLEEPING, EXCITED, PAIN, LISTENING, HUGGING }

@Composable
fun SaathiMascot(
    emotion: MascotEmotion = MascotEmotion.HAPPY,
    size: Dp = 120.dp,
    onTap: () -> Unit = {},
    showTip: Boolean = false,
    tipText: String = ""
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(animation = tween(3000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "float"
    )
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(animation = tween(600, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "bounce"
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.pslogo),
            contentDescription = "Period Saathi Logo",
            modifier = Modifier
                .size(size * 0.9f) // Slight padding so it bounces within the box
                .graphicsLayer {
                    translationY = if (emotion == MascotEmotion.EXCITED) bounceOffset else floatOffset
                }
        )
    }
}

